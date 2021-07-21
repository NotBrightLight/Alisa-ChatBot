package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAlisa implements CommandExecutor {
    private BrightAlisa context;

    public CommandAlisa(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("players only");
        } else {
            Player player = (Player) sender;
            if (player.isOp()) {
                if (args.length == 0) {
                    this.context.alisa.utilCommandsHandler.handleAlisaHelpCommand(player);
                } else if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("set")) {
                        this.context.settings.handleSetCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("read")) {
                        this.context.settings.handleReadCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("mods")) {
                        this.context.settings.handleModsCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("getname")) {
                        this.context.alisa.utilCommandsHandler.handleGetNameCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("getuuid")) {
                        this.context.alisa.utilCommandsHandler.handleGetUUIDCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("tospawn")) {
                        this.context.alisa.utilCommandsHandler.handleToSpawnCommand(player, cmd, args);
                    } else if (args[0].equalsIgnoreCase("reloadfiles")) {
                        this.context.alisa.utilCommandsHandler.handleReloadQuestionsAndRulesFilesCommand(player);
                    } else if (args[0].equalsIgnoreCase("playtimereport")) {
                        this.context.alisa.utilCommandsHandler.handlePlaytimeReportCommand(player);
                    } else if (args[0].equalsIgnoreCase("stats")) {
                        this.context.alisa.utilCommandsHandler.handleStatsCommand(player);
                    } else if (!args[0].equalsIgnoreCase("toggledetect") && !args[0].equalsIgnoreCase("td")) {
                        if (args[0].equalsIgnoreCase("reloadconfig")) {
                            this.context.alisa.utilCommandsHandler.handleReloadConfigCommand(player);
                        } else if (args[0].equalsIgnoreCase("==")) {
                            this.context.alisa.utilCommandsHandler.handleRetranslateCommand(player, args);
                        } else if (args[0].equalsIgnoreCase("tp")) {
                            this.context.alisa.utilCommandsHandler.handleTpCommand(player, args);
                        } else if (args[0].equalsIgnoreCase("loadchunks")) {
                            this.context.alisa.utilCommandsHandler.handleLoadChunksCommand(player, args);
                        } else if (args[0].equalsIgnoreCase("unloadchunks")) {
                            this.context.alisa.utilCommandsHandler.handleUnloadChunksCommand(player);
                        } else {
                            this.context.alisa.whisper(player, "#crОшибка#c3: такой команды нет");
                        }
                    } else {
                        this.context.alisa.moderatorsHandler.toggleDetect(player);
                    }
                } else {
                    this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
                }
            }
        }
        return true;
    }
}