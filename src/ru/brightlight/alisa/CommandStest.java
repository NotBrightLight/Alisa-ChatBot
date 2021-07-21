package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStest implements CommandExecutor {
    private BrightAlisa context;

    public CommandStest(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.context.alisa.test((Player) sender, args);
        return true;
    }
}