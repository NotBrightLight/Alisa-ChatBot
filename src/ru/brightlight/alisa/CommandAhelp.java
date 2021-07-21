package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandAhelp implements CommandExecutor {
    private BrightAlisa context;
    private ArrayList<String> help;

    public CommandAhelp(BrightAlisa context) {
        this.context = context;
        this.help = mf.readProjectFileLines("command-help.txt");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            StringBuilder sb = new StringBuilder("#c2Список доступных команд#c3:\n");

            for (String str : this.help) {
                sb.append(str).append("\n");
            }

            this.context.alisa.whisper(player, sb.toString());
        }
        return true;
    }
}