package ru.brightlight.alisa;

import java.lang.management.ManagementFactory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandServer implements CommandExecutor {
    private BrightAlisa context;

    public CommandServer(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
            long toRestart = ((long) this.context.config.getInt("restart-period") * 60 * 1000) - jvmUpTime;
            int seconds = (int) (toRestart / 1000L) % 60;
            int minutes = (int) (toRestart / 60000L % 60L);
            int hours = (int) (toRestart / 3600000L % 24L);
            StringBuilder sb = new StringBuilder("#c2До рестарта#c3: ");
            if (hours > 0) {
                sb.append(hours).append(" час(а), ");
            }

            sb.append(minutes).append(" минут, ");
            sb.append(seconds).append(" секунд");
            this.context.alisa.whisper(player, sb.toString());
        }

        return true;
    }
}