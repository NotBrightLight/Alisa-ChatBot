package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStest2 implements CommandExecutor {
    private BrightAlisa context;

    public CommandStest2(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.context.alisa.test2((Player) sender, args);
        return true;
    }
}