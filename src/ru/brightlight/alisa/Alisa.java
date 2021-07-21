package ru.brightlight.alisa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

public class Alisa {
    private BrightAlisa context;
    private String muteCommand;
    private String warnCommand;
    private Random rand = new Random();
    private HashMap<Alisa.AnswerReason, Alisa.Answer> answers = new HashMap<>();
    private HashMap<Alisa.AnswerReason, Alisa.Answer> moderatorAnswers = new HashMap<>();
    private HashMap<Alisa.PunishmentType, String> punishments = new HashMap<>();
    protected HashMap<String, ServerRule> serverRules = new HashMap<>();
    protected HashMap<String, ServerRule> serverRulesExtra;
    protected ModeratorsHandler moderatorsHandler;
    private ArrayList<String> helloAnswers;
    private ArrayList<String> byeAnswers;
    private ArrayList<String> persiki;
    protected CooldownsHandler cooldownsHandler;
    protected VoteHandler voteHandler;
    protected UtilCommandsHandler utilCommandsHandler;
    protected Statistics statistics;
    private long lastSavedStatisticsTimeStampMillis;
    protected ChunkLoader chunkLoader;
    protected HashSet<String> knownPlayerNames = new HashSet<>();

    protected Alisa(BrightAlisa context) {
        this.context = context;
        this.createAnswers();
        this.createModeratorAnswers();
        this.createPunishments();
        this.fillServerRules();
        this.fillExtraServerRules();
        this.cooldownsHandler = new CooldownsHandler(this.context);
        this.moderatorsHandler = new ModeratorsHandler(this.context);
        this.helloAnswers = mf.readProjectFileLines("hello-answers.txt");
        this.byeAnswers = mf.readProjectFileLines("bye-answers.txt");
        this.persiki = mf.readProjectFileLines("persiki.txt");
        this.voteHandler = new VoteHandler(this.context);
        this.utilCommandsHandler = new UtilCommandsHandler(this.context);
        this.initializeStatistics();
        this.runStatisticsSaverTask();
        this.chunkLoader = new ChunkLoader(context);
    }

    private void runStatisticsSaverTask() {
        BukkitRunnable br = new BukkitRunnable() {
            public void run() {
                Alisa.this.saveStatistics();
            }
        };
        br.runTaskTimerAsynchronously(this.context, 2400L, 2400L);
    }

    protected void saveStatistics() {
        this.statistics.onlineSeconds = (int) ((long) this.statistics.onlineSeconds + (System.currentTimeMillis() - this.lastSavedStatisticsTimeStampMillis) / 1000L);
        this.lastSavedStatisticsTimeStampMillis = System.currentTimeMillis();
        this.context.config.set("statistics", this.statistics);
    }

    private void initializeStatistics() {
        Statistics st = (Statistics) this.context.config.getObject("statistics");
        this.lastSavedStatisticsTimeStampMillis = System.currentTimeMillis();
        if (st != null) {
            this.context.log("statistics restored");
            this.statistics = st;
        } else {
            this.context.log("statistics cant be restored, creating new");
            this.statistics = new Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    private String randomPersik() {
        return this.persiki.get(this.rand.nextInt(this.persiki.size()));
    }

    protected void sayHello(Player player) {
        this.say(this.helloAnswers.get(this.rand.nextInt(this.helloAnswers.size())));
    }

    protected void sayBye(Player player) {
        this.say(this.byeAnswers.get(this.rand.nextInt(this.byeAnswers.size())));
    }

    private void createAnswers() {
        this.answers.put(Alisa.AnswerReason.CAPS, new Alisa.Answer("answers-caps.txt", this));
        this.answers.put(Alisa.AnswerReason.FLOOD, new Alisa.Answer("answers-flood.txt", this));
        this.answers.put(Alisa.AnswerReason.ADVERTISEMENT, new Alisa.Answer("answers-advertisement.txt", this));
        this.answers.put(Alisa.AnswerReason.PROFANITY, new Alisa.Answer("answers-profanity.txt", this));
        this.answers.put(Alisa.AnswerReason.DOUBLEWARN, new Alisa.Answer("answers-doublewarn.txt", this));
        this.answers.put(Alisa.AnswerReason.WARN, new Alisa.Answer("answers-warn.txt", this));
    }

    private void createModeratorAnswers() {
        this.moderatorAnswers.put(Alisa.AnswerReason.CAPS, new Alisa.Answer("moderator-answers-caps.txt", this));
        this.moderatorAnswers.put(Alisa.AnswerReason.FLOOD, new Alisa.Answer("moderator-answers-flood.txt", this));
        this.moderatorAnswers.put(Alisa.AnswerReason.ADVERTISEMENT, new Alisa.Answer("moderator-answers-advertisement.txt", this));
        this.moderatorAnswers.put(Alisa.AnswerReason.PROFANITY, new Alisa.Answer("moderator-answers-profanity.txt", this));
    }

    private void createPunishments() {
        this.punishments.put(Alisa.PunishmentType.MUTE, this.context.config.getString("mute-command"));
        this.punishments.put(Alisa.PunishmentType.WARN, this.context.config.getString("warn-command"));
    }

    protected String getName() {
        return this.context.config.getString("name1").replace("&", "§");
    }

    protected String getName2() {
        return this.context.config.getString("name2").replace("&", "§");
    }

    private ArrayList<String> getChatColors() {
        return this.context.config.getList("chat-colors");
    }

    private String getNamePrefix() {
        return this.context.config.getString("prefix");
    }

    private String getNameColor() {
        return '§' + this.context.config.getString("name-color");
    }

    private String getPrefixColor() {
        return '§' + this.context.config.getString("prefix-color");
    }

    private String getGlobalChatPrefix() {
        return this.context.config.getString("global-chat-prefix");
    }

    private String getGlobalChatPrefixColor() {
        return '§' + this.context.config.getString("global-chat-prefix-color");
    }

    private int getDoubleWarnTempmuteDuration() {
        return this.context.config.getInt("tempmute.double-warn");
    }

    protected void whisper(Player player, String message) {
        player.sendMessage(this.formatMessageStringForWhisper(message));
    }

    protected void whisper(CommandSender sender, String message) {
        sender.sendMessage(this.formatMessageStringForWhisper(message));
    }

    private String formatMessageStringForWhisper(String message) {
        message = this.replaceChatColorCodes(message);
        message = ChatColor.GOLD + "[" + this.getNameColor() + this.getName() + ChatColor.GOLD + " -> " + ChatColor.RED + "Я" + ChatColor.GOLD + "] " + message;
        message = message.replaceAll("<persik>", this.randomPersik());
        return message;
    }

    private String formatMessageStringForBroadcast(String message) {
        message = "#c1" + message;
        message = this.replaceChatColorCodes(message);
        message = ChatColor.DARK_GRAY + "[" + this.getGlobalChatPrefixColor() + this.getGlobalChatPrefix() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_GRAY + "[" + this.getPrefixColor() + this.getNamePrefix() + ChatColor.DARK_GRAY + "] " + this.getNameColor() + this.getName() + ChatColor.GREEN + ": " + message;
        message = message.replaceAll("<persik>", this.randomPersik());
        return message;
    }

    public String replaceChatColorCodes(String message) {
        for (int i = 0; i < this.getChatColors().size(); ++i) {
            message = message.replace("#c" + (i + 1), '§' + this.getChatColors().get(i));
        }

        message = message.replace("#cr", '§' + this.context.config.getString("chat-color-warning"));
        message = message.replace("#cg", '§' + this.context.config.getString("chat-color-ok"));
        message = message.replace("#c0", ChatColor.GRAY + "");
        message = message.replace("#cw", ChatColor.WHITE + "");
        return message;
    }

    public void test(Player p, String[] args) {
    }

    public void test2(Player p, String[] args) {
    }

    public void punish(Alisa.PunishmentType punishmentType, Player player, int durationSeconds, String reason, Alisa.AnswerReason answerReason) {
        switch (punishmentType) {
            case MUTE:
                if (!this.moderatorsHandler.isModerator(player)) {
                    this.mute(player, durationSeconds, reason);
                    this.say("#c2" + player.getName() + "#c1, " + this.answers.get(answerReason).getRandomAnswer(player));
                } else {
                    this.say(this.moderatorAnswers.get(answerReason).getRandomAnswer(player));
                }
                break;
            case WARN:
                if (!this.moderatorsHandler.isModerator(player)) {
                    if (this.cooldownsHandler.warnCooldowns.isExpired(player)) {
                        this.warn(player, reason);
                        this.say("#c2" + player.getName() + "#c1, " + this.answers.get(AnswerReason.WARN).getRandomAnswer(player));
                    } else {
                        this.punish(Alisa.PunishmentType.MUTE, player, this.getDoubleWarnTempmuteDuration(), reason + " (рецидив)", Alisa.AnswerReason.DOUBLEWARN);
                    }
                    this.cooldownsHandler.warnCooldowns.trigger(player);
                } else {
                    this.say(this.moderatorAnswers.get(answerReason).getRandomAnswer(player));
                }
        }
    }

    public void say(final String message) {
        if (this.context.config.getBoolean("debug")) {
            this.context.log("[debug] [say] " + message);
        }

        if (!this.context.config.getBoolean("silent")) {
            (new BukkitRunnable() {
                public void run() {
                    Bukkit.broadcastMessage(Alisa.this.formatMessageStringForBroadcast(message));
                    ++Alisa.this.statistics.chatMessages;
                }
            }).runTaskLater(this.context, 2L);
        }

    }

    public void sayRaw(TextComponent... args) {
        this.sayRawImplement(2, args);
    }

   /* public void sayRawJson(final String json) {
      if (!this.context.config.getBoolean("silent")) {
         new BukkitRunnable(){

            public void run() {
               Alisa.this.context.log(json);
               IChatBaseComponent comp = ChatSerializer.a((String)json);
               PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
               for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                  ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
               }
            }
         }.runTaskLater(this.context, 2L);
      }
   } */

    public void sayRawDelayed(TextComponent... args) {
        this.sayRawImplement(5, args);
    }

    private void sayRawImplement(int delay, final TextComponent... args) {
        if (!this.context.config.getBoolean("silent")) {
            new BukkitRunnable() {
                public void run() {
                    TextComponent res = Alisa.this.joinComponentsGlobal(args);
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        p.spigot().sendMessage(res);
                    }
                    ++Alisa.this.statistics.chatMessages;
                }
            }.runTaskLater(this.context, delay);
        }
    }

    public TextComponent textComponent(String text, boolean beginning) {
        return new TextComponent(beginning ? this.formatMessageStringForBroadcast(text) : this.replaceChatColorCodes(text));
    }

    public TextComponent textComponent(String text) {
        return this.textComponent(text, false);
    }

    public TextComponent clickComponent(String text, String command) {
        TextComponent t1 = new TextComponent(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + this.replaceChatColorCodes(text) + ChatColor.GRAY + "]");
        t1.setClickEvent(new ClickEvent(Action.RUN_COMMAND, command.replaceAll(Pattern.quote("#c") + ".", "")));
        return t1;
    }

    public TextComponent linkComponent(String text, String command) {
        TextComponent t1 = new TextComponent(ChatColor.GRAY + "[" + ChatColor.GOLD + this.replaceChatColorCodes(text) + ChatColor.GRAY + "]");
        t1.setClickEvent(new ClickEvent(Action.OPEN_URL, command));
        return t1;
    }

    public TextComponent joinComponentsFormatted(int reach, TextComponent... args) {
        this.context.log("joincomponents formatted: " + reach);
        TextComponent result;
        if (reach == -1) {
            result = this.textComponent(this.formatMessageStringForWhisper(""));
        } else if (reach == 1) {
            result = this.textComponent(this.formatMessageStringForBroadcast(""));
        } else {
            result = this.textComponent("");
        }

        for (TextComponent c : args) {
            result.addExtra(c);
        }

        return result;
    }

    public TextComponent joinComponents(TextComponent... args) {
        return this.joinComponentsFormatted(0, args);
    }

    public TextComponent joinComponentsGlobal(TextComponent... args) {
        return this.joinComponentsFormatted(1, args);
    }

    public TextComponent joinComponentsWhisper(TextComponent... args) {
        return this.joinComponentsFormatted(-1, args);
    }

    public void whisperRaw(Player p, TextComponent... args) {
        TextComponent res = this.joinComponentsWhisper(args);
        p.spigot().sendMessage(res);
    }

    public TextComponent addClickCommand(TextComponent com, String command) {
        com.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + command));
        return com;
    }

    private String getPunishmentCommand(Alisa.PunishmentType punishmentType) {
        return this.punishments.get(punishmentType);
    }

    private void executeCommand(final String command) {
        (new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }).runTaskLater(this.context, 1L);
    }

    private void mute(final Player player, final int durationSeconds, final String reason) {
        (new BukkitRunnable() {
            public void run() {
                Alisa.this.executeCommand(String.format(Alisa.this.getPunishmentCommand(Alisa.PunishmentType.MUTE), player.getName(), durationSeconds + " sec", reason));
                ++Alisa.this.statistics.mutes;
                Alisa.this.statistics.mutesDuration += durationSeconds;
            }
        }).runTaskLater(this.context, 1L);
    }

    private void warn(final Player player, final String reason) {
        (new BukkitRunnable() {
            public void run() {
                Alisa.this.executeCommand(String.format(Alisa.this.getPunishmentCommand(Alisa.PunishmentType.WARN), player.getName(), reason));
                ++Alisa.this.statistics.warns;
            }
        }).runTaskLater(this.context, 1L);
    }

    protected void fillServerRules() {
        ArrayList<String> temp = mf.readProjectFileLines("server-rules.txt");
        ArrayList<Integer> rulesIndexes = new ArrayList<>();
        rulesIndexes.add(0);

        for (int i = 0; i < temp.size(); ++i) {
            if (temp.get(i).equalsIgnoreCase("***")) {
                rulesIndexes.add(i + 1);
            }
        }

        for (Integer index : rulesIndexes) {
            ServerRule sr = this.getNextServerRule(temp, index);
            if (sr != null) {
                this.serverRules.put(sr.par, sr);
            }
        }
    }

    private ServerRule getNextServerRule(ArrayList<String> arr, int startingIndex) {
        if (startingIndex >= arr.size()) {
            return null;
        } else {
            String par = arr.get(startingIndex);
            String description = arr.size() > startingIndex + 1 ? arr.get(startingIndex + 1) : "---";
            description = description.replaceAll("<n>", "\n");
            String punishment = arr.size() > startingIndex + 2 ? arr.get(startingIndex + 2) : "";
            if (punishment.contains("***")) {
                punishment = "";
            }
            return new ServerRule(par, description, punishment);
        }
    }

    protected void fillExtraServerRules() {
        this.serverRulesExtra = new HashMap<>();
        if (mf.fileExistsInDataFolder("server-rules-extra.txt")) {
            ArrayList<String> temp = mf.readFileFromDataFolderToArray("server-rules-extra.txt");
            ArrayList<Integer> rulesIndexes = new ArrayList<>();
            rulesIndexes.add(0);

            for (int i = 0; i < temp.size(); ++i) {
                if (temp.get(i).equalsIgnoreCase("***")) {
                    rulesIndexes.add(i + 1);
                }
            }

            for (Integer index : rulesIndexes) {
                ServerRule sr = this.getNextServerRule(temp, index);
                if (sr != null) {
                    this.serverRulesExtra.put(sr.par, sr);
                }
            }
        }
    }

    protected void addKnownPlayer(String playername) {
        this.knownPlayerNames.add(playername);
    }

    private class Answer {
        ArrayList<String> answerStrings;
        Alisa context;

        private Answer(String answersFilePath, Alisa context) {
            this.context = context;
            this.answerStrings = mf.readProjectFileLines(answersFilePath);
        }

        protected String getRandomAnswer(Player player) {
            return String.format(this.answerStrings.get(Alisa.this.rand.nextInt(this.answerStrings.size())), "#c2" + player.getName() + "#c1");
        }
    }

    public enum AnswerReason {
        CAPS,
        PROFANITY,
        FLOOD,
        ADVERTISEMENT,
        WARN,
        DOUBLEWARN;
    }

    public enum PunishmentType {
        MUTE,
        WARN,
        BAN;
    }
}