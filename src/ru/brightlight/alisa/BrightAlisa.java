package ru.brightlight.alisa;

import com.rogue.playtime.Playtime;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class BrightAlisa extends JavaPlugin {
    private Logger log = this.getLogger();
    private static BrightAlisa instance;
    private MyChatListener listenerChat;
    private ListenerPlayerLogin listenerPlayerLogin;
    protected MessageHandler messageHandler;
    protected Config config;
    protected Alisa alisa;
    protected Settings settings;
    protected Playtime pluginPlaytime;
    protected ListenerChunkUnload listenerChunkUnload;
    long startTime;

    public void onEnable() {
        instance = this;
        this.log.info("Alisa enabled");
        this.registerConfig();
        this.config = new Config(this);

        try {
            this.alisa = new Alisa(this);
        } catch (Exception e) {
            this.log("ERROR CREATING ALISA INSTANCE, disabling plugin");
            this.log(e.toString());
            e.printStackTrace();
            this.disable();
            return;
        }

        this.messageHandler = new MessageHandler(this);
        this.registerListeners();
        this.registerCommands();
        this.settings = new Settings(this);
        this.pluginPlaytime = (Playtime) Bukkit.getPluginManager().getPlugin("Playtime");
        if (this.needNewPlaytimeReport()) {
            this.pushNewPlaytimeReport();
            this.log("new playtime report generated");
        }
        this.startTime = System.currentTimeMillis();
    }

    public void onDisable() {
        this.alisa.saveStatistics();
    }

    public static BrightAlisa getInstance() {
        return instance;
    }

    public void registerCommands() {
        this.getCommand("inf").setExecutor(new CommandInf(this));
        this.getCommand("mods").setExecutor(new CommandMods(this));
        this.getCommand("alisa").setExecutor(new CommandAlisa(this));
        this.getCommand("votesun").setExecutor(new CommandVotesun(this));
        this.getCommand("voteday").setExecutor(new CommandVoteday(this));
        this.getCommand("yes").setExecutor(new CommandYes(this));
        this.getCommand("no").setExecutor(new CommandNo(this));
        this.getCommand("colors").setExecutor(new CommandColors(this));
        this.getCommand("ahelp").setExecutor(new CommandAhelp(this));
        this.getCommand("server").setExecutor(new CommandServer(this));
        this.getCommand("aseen").setExecutor(new CommandAseen(this));
    }

    public void log(String msg) {
        this.log.info(msg);
    }

    protected void registerListenerChunkUnload() {
        this.listenerChunkUnload = new ListenerChunkUnload(this);
        Bukkit.getServer().getPluginManager().registerEvents(this.listenerChunkUnload, this);
    }

    protected void unregisterListenerChunkUnload() {
        HandlerList.unregisterAll(this.listenerChunkUnload);
        this.listenerChunkUnload = null;
    }

    private void registerListeners() {
        if (this.config.getBoolean("essChat")) {
            this.listenerChat = new ListenerChatEssChat(this);
            this.log("using essentials chat");
        } else {
            this.listenerChat = new ListenerChat(this);
            this.log("not using essentials chat");
        }

        Bukkit.getServer().getPluginManager().registerEvents(this.listenerChat, this);
        this.listenerPlayerLogin = new ListenerPlayerLogin(this);
        Bukkit.getServer().getPluginManager().registerEvents(this.listenerPlayerLogin, this);
    }

    protected void say(String msg) {
        Bukkit.broadcastMessage(msg);
    }

    protected void d(String msg) {
        Bukkit.broadcastMessage(msg);
    }

    protected void debug(String msg) {
        this.log("(debug): " + msg);
    }

    private void registerConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public World getMainWorld() {
        return Bukkit.getWorld(this.config.getString("main-world"));
    }

    public World getWorld(String name) {
        return Bukkit.getWorld(name);
    }

    public Playtime getPlaytime() {
        return this.pluginPlaytime;
    }

    private void pushNewPlaytimeReport() {
        PlaytimeReport oldReport = (PlaytimeReport) this.config.getObject("report2");
        this.config.set("report1", oldReport);
        this.config.set("report2", this.newPlaytimeReport());
    }

    private PlaytimeReport newPlaytimeReport() {
        HashMap<String, Integer> entries = new HashMap<>();
        for (ModeratorsEntry me : this.alisa.moderatorsHandler.groups) {
            for (String playerName : me.playerNames) {
                int tempPlaytime = mf.getPlayerPlaytime(playerName);
                entries.put(playerName, tempPlaytime);
            }
        }

        return new PlaytimeReport(System.currentTimeMillis(), entries);
    }

    private boolean needNewPlaytimeReport() {
        PlaytimeReport oldReport = (PlaytimeReport) this.config.getObject("report2");
        if (mf.getDayOfWeek() == 2) {
            if (oldReport != null) {
                if (System.currentTimeMillis() - oldReport.time > 172800000L) {
                    this.log("need new playtime report: true");
                    return true;
                } else {
                    this.log("need new playtime report: false (less than 2 days passed since last report)");
                    return false;
                }
            } else {
                this.log("need new playtime report: true (old report == null)");
                return true;
            }
        } else {
            this.log("need new playtime report: false (wrong day of the week)");
            return false;
        }
    }

    static {
        ConfigurationSerialization.registerClass(ModeratorsEntry.class, "ModeratorsEntry");
        ConfigurationSerialization.registerClass(PlaytimeReport.class, "PlaytimeReport");
        ConfigurationSerialization.registerClass(Statistics.class, "Statistics");
    }
}