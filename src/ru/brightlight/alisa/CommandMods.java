package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMods implements CommandExecutor {
    private BrightAlisa context;

    public CommandMods(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ++this.context.alisa.statistics.modsAndInfCommands;
        this.context.alisa.whisper(sender, this.context.alisa.moderatorsHandler.getOnlineModsString());
        return true;
    }
}