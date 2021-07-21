package ru.brightlight.alisa;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAseen implements CommandExecutor {
    private BrightAlisa context;

    public CommandAseen(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            if (args.length >= 1) {
                this.context.alisa.whisper(sender, this.getSeenString(args[0]));
            } else {
                this.context.alisa.whisper(sender, "похоже, ты забыл указать имя");
            }
        }
        return true;
    }

    private String getSeenString(String playerName) {
        Player onlinePlayer = Bukkit.getPlayerExact(playerName);
        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            return !this.context.alisa.moderatorsHandler.isModerator(onlinePlayer.getName()) ? String.format("игрок #c2%s#c3 #cgонлайн", onlinePlayer.getName()) : "прости, <persik>, это секретная инфа :(";
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                if (!this.context.alisa.moderatorsHandler.isModerator(offlinePlayer.getName())) {
                    long diff = System.currentTimeMillis() - offlinePlayer.getLastPlayed();
                    long days = diff / 86400000L;
                    long hours = diff / 3600000L % 24L;
                    long minutes = diff / 60000L % 60L;
                    long seconds = diff / 1000L % 60L;
                    if (days > 1000L) {
                        return String.format("не могу найти игрока с именем #c2%s#c3 :(", playerName);
                    } else {
                        StringBuilder sb = new StringBuilder(String.format("игрок #c2%s#c3 #crоффлайн#c3", offlinePlayer.getName()));
                        if (days != 0L) {
                            sb.append(String.format(" #c2%02d#c3 дней,", days));
                        }

                        sb.append(String.format(" #c2%02d#c3 часов,", hours));
                        sb.append(String.format(" #c2%02d#c3 минут", minutes));
                        sb.append(String.format(" #c2%02d#c3 секунд", seconds));
                        return sb.toString();
                    }
                } else {
                    return "прости, <persik>, это секретная инфа :(";
                }
            } else {
                return String.format("не могу найти игрока с именем #c2%s#c3 :(", playerName);
            }
        }
    }
}