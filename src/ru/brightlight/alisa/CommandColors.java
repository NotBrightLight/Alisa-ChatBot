package ru.brightlight.alisa;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandColors implements CommandExecutor {
    private BrightAlisa context;
    private ArrayList<String> colors;

    public CommandColors(BrightAlisa context) {
        this.context = context;
        this.colors = mf.readProjectFileLines("colors.txt");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        StringBuilder sb = new StringBuilder("#c2Цветовые коды чата#c3:\n");

        for (String colorString : this.colors) {
            sb.append(colorString).append("\n");
        }

        this.context.alisa.whisper(commandSender, sb.toString());
        return true;
    }
}