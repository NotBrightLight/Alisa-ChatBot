package ru.brightlight.alisa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandYes implements CommandExecutor {
    private BrightAlisa context;

    public CommandYes(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            SuccessReport sr = this.context.alisa.voteHandler.castVote(player, true);
            this.context.alisa.whisper(player, sr.message);
        }
        return true;
    }
}