package ru.brightlight.alisa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class UtilCommandsHandler {
    protected BrightAlisa context;
    protected ArrayList<String> toSpawnPlayerNames;
    private ArrayList<String> alisaHelpCommandStrings;

    protected UtilCommandsHandler(BrightAlisa context) {
        this.context = context;
        this.toSpawnPlayerNames = context.config.getList("tospawn-playernames");
        this.alisaHelpCommandStrings = mf.readProjectFileLines("command-alisa-help.txt");
    }

    protected void handleGetNameCommand(Player player, Command cmd, String[] args) {
        if (args.length >= 2) {
            String targetString = args[1];
            OfflinePlayer op;

            try {
                UUID uuid = UUID.fromString(targetString);
                op = Bukkit.getOfflinePlayer(uuid);
            } catch (Exception e) {
                this.context.alisa.whisper(player, "#crОшибка#c3: неверный UUID");
                return;
            }

            if (op != null) {
                this.context.alisa.whisper(player, String.format("#cgУспешно#c3: игрок с этим UUID: #c2%s", op.getName()));
            } else {
                this.context.alisa.whisper(player, "#crОшибка#c3: игрок с этим UUID не найден");
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    protected void handleGetUUIDCommand(Player player, Command cmd, String[] args) {
        if (args.length >= 2) {
            String targetName = args[1];
            OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
            if (op != null) {
                this.context.alisa.whisper(player, String.format("#cgУспешно#c3: UUID игрока %s: #c2%s", op.getName(), op.getUniqueId().toString()));
            } else {
                this.context.alisa.whisper(player, String.format("#crОшибка#c3: игрок с именем #c2%s#c3 не найден (имя чувствительно к регистру)", targetName));
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    protected void handleToSpawnCommand(Player player, Command cmd, String[] args) {
        if (args.length >= 2) {
            String targetName = args[1];
            if (!mf.arrayListContainsIgnoreCase(this.toSpawnPlayerNames, targetName)) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
                if (op != null) {
                    this.toSpawnPlayerNames.add(targetName);
                    this.context.config.set("tospawn-playernames", this.toSpawnPlayerNames);
                    this.context.alisa.whisper(player, String.format("#cgУспешно#c3: игрок #c2%s#c3 будет отправлен на спавн при следующем заходе в игру", op.getName()));
                } else {
                    this.context.alisa.whisper(player, String.format("#crОшибка#c3: игрок #c2%s#c3 не найден (имя чувствительно к регистру)", targetName));
                }
            } else {
                this.context.alisa.whisper(player, String.format("#crОшибка#c3: игрок #c2%s#c3 уже находится в списке на телепортацию", targetName));
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    protected void handleReloadQuestionsAndRulesFilesCommand(Player player) {
        this.context.messageHandler.reloadQuestions();

        try {
            this.context.alisa.fillExtraServerRules();
        } catch (Exception e) {
            e.printStackTrace();
            this.context.alisa.whisper(player, "#crОшибка#c3: произошла ошибка при перезагрузке файла с доп. правилами сервера");
        }

        int totalQuestions = this.context.messageHandler.processorQuestions.getQuestionsCount();
        this.context.alisa.whisper(player, "#cgУспешно#c3: вопросы и доп. правила перезагружены. Всего вопросов: " + totalQuestions);
    }

    protected void handlePlaytimeReportCommand(Player player) {
        PlaytimeReport report1 = (PlaytimeReport) this.context.config.getObject("report1");
        PlaytimeReport report2 = (PlaytimeReport) this.context.config.getObject("report2");
        if (report1 != null && report2 != null) {
            StringBuilder sb = new StringBuilder("Отчет по онлайну модераторов:\n");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report1.time);
            String date1 = formatter.format(calendar.getTime());
            calendar.setTimeInMillis(report2.time);
            String date2 = formatter.format(calendar.getTime());
            float daysPassed = (float) (report2.time - report1.time) / 1000.0F / 60.0F / 60.0F / 24.0F;
            sb.append(ChatColor.GRAY);
            sb.append(String.format("%s -> %s (период %.2f дней)\n", date1, date2, daysPassed));
            sb.append("[должность] имя : общий - за период - средний\n");

            for (ModeratorsEntry me : this.context.alisa.moderatorsHandler.groups) {
                if (me.ID >= 100) continue;

                for (String name : me.playerNames) {
                    Integer totalMinutes = null;
                    Integer lastWeekMinutes = null;
                    Float averageMinutes = null;
                    if (report2.entries.containsKey(name)) {
                        totalMinutes = report2.entries.get(name);
                        if (report1.entries.containsKey(name)) {
                            lastWeekMinutes = report2.entries.get(name) - report1.entries.get(name);
                            averageMinutes = (float) lastWeekMinutes / daysPassed;
                        }
                    }

                    String totalHours = totalMinutes != null ? String.format("%.2f", (float) totalMinutes / 60.0F) : "?";
                    String lastWeekHours = lastWeekMinutes != null ? String.format("%.2f", (float) lastWeekMinutes / 60.0F) : "?";
                    String averageHours = averageMinutes != null ? String.format("%.2f", averageMinutes / 60.0F) : "?";
                    sb.append(String.format("%s[%s%s] %s#c3 : #cw%s#c3 - #cw%s#c3 - #cw%s#c3\n", ChatColor.DARK_GRAY, me.getColoredGroupName(), ChatColor.DARK_GRAY, '§' + me.playernameColor + name, totalHours, lastWeekHours, averageHours));
                }
                this.context.alisa.whisper(player, sb.toString());
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: прошло недостаточно времени для составления отчетов об онлайне");
        }
    }

    protected void handleStatsCommand(Player player) {
        this.context.alisa.saveStatistics();
        String sb = "Статистика:\n" + String.format("#c3сообщений в чат: #cw%d\n", this.context.alisa.statistics.chatMessages) +
                String.format("#c3ответов на вопросы: #cw%d\n", this.context.alisa.statistics.answers) +
                String.format("#c3варнов: #cw%d\n", this.context.alisa.statistics.warns) +
                String.format("#c3мутов: #cw%d\n", this.context.alisa.statistics.mutes) +
                String.format("#c3общая длительность мутов: #cw%.2f #c3минут\n", (float) this.context.alisa.statistics.mutesDuration / 60.0F) +
                String.format("#c3начато голосований: #cw%d\n", this.context.alisa.statistics.totalVotesStarted) +
                String.format("#c3успешных голосований: #cw%d\n", this.context.alisa.statistics.successfulVotes) +
                String.format("#c3команд /mods и /inf: #cw%d\n", this.context.alisa.statistics.modsAndInfCommands) +
                String.format("#c3плейтайм %s: #cw%.2f #c3часов\n", this.context.config.getString("name2"), (float) this.context.alisa.statistics.onlineSeconds / 60.0F / 60.0F);
        this.context.alisa.whisper(player, sb);
    }

    protected void handleReloadConfigCommand(Player player) {
        this.context.reloadConfig();
        this.context.alisa.whisper(player, "#cgУспешно#c3: конфиг перезагружен. Некоторые изменения вступят в силу только после рестарта");
    }

    protected void handleAlisaHelpCommand(Player player) {
        StringBuilder sb = new StringBuilder("#c2Список админских команд#c3:\n");

        for (String s : this.alisaHelpCommandStrings) {
            sb.append(s).append("\n");
        }

        this.context.alisa.whisper(player, sb.toString());
    }

    protected void handleRetranslateCommand(Player player, String[] args) {
        if (args.length >= 2) {
            if (!args[1].isEmpty()) {
                StringBuilder msg = new StringBuilder();
                for (int i = 1; i < args.length; ++i) {
                    msg.append(" ").append(args[i]);
                }
                msg.toString().replaceFirst(" ", "");
                this.context.alisa.say(msg.toString().replaceAll("&", Character.toString('§')));
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: сообщение пусто");
        }
    }

    protected void handleTpCommand(Player player, String[] args) {
        if (args.length >= 2) {
            if (!args[1].isEmpty()) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    player.teleport(target.getLocation());
                    this.context.alisa.whisper(player, String.format("#cgУспешно#c3: телепорт к игроку #c2%s", target.getName()));
                } else {
                    this.context.alisa.whisper(player, "#crОшибка#c3: игрок не онлайн");
                }
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: не указано имя");
        }

    }

    protected void handleLoadChunksCommand(Player player, String[] args) {
        int r;

        try {
            r = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
            this.context.alisa.whisper(player, "#crОшибка#c3: неверно указан параметр радиуса");
            return;
        }

        if (r < 1) {
            this.context.alisa.whisper(player, "#crОшибка#c3: радиус не может быть меньше 1");
        } else if (r > 6) {
            this.context.alisa.whisper(player, "#crОшибка#c3: радиус не может быть больше 6");
        } else {
            Chunk c = player.getWorld().getChunkAt(player.getLocation());
            int chunkX = c.getX();
            int chunkZ = c.getZ();
            ArrayList<String> res = new ArrayList<>();

            for (int x = chunkX - (r - 1); x <= chunkX + (r - 1); ++x) {
                for (int z = chunkZ - (r - 1); z <= chunkZ + (r - 1); ++z) {
                    this.context.alisa.chunkLoader.addChunk(c.getWorld().getChunkAt(x, z));
                    res.add(String.format("[#c2%d#c3,#c2%d#c3]", x, z));
                }
            }

            this.context.alisa.whisper(player, "#cgУспешно#c3: добавлено в прогрузку #c2" + res.size() + "#c3 чанков: " + String.join(", ", res) + " в мире #c2" + c.getWorld().getName());
            this.context.alisa.whisper(player, String.format("#cgУспешно#c3: всего сейчас прогружается #c2%d#c3 чанков", this.context.alisa.chunkLoader.loadedChunks.size()));
        }
    }

    protected void handleUnloadChunksCommand(Player player) {
        if (!this.context.alisa.chunkLoader.isActive) {
            this.context.alisa.whisper(player, "#crОшибка#c3: никакие чанки в данный момент не прогружаются");
        } else {
            int num = this.context.alisa.chunkLoader.loadedChunks.size();
            this.context.alisa.chunkLoader.setActive(false);
            this.context.alisa.whisper(player, String.format("#cgУспешно#c3: прогрузка #c2%d#c3 чанков отключена", num));
        }
    }

}