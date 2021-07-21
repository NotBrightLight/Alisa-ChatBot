package ru.brightlight.alisa;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcessorExtra implements IProcessor {
    private BrightAlisa context;
    private String template;
    private ArrayList<String> immunes;

    public ProcessorExtra(BrightAlisa context) {
        this.context = context;
        this.template = " уничтожь ";
        this.immunes = context.config.getList("annihilation-immunity");
    }

    public boolean processMessage(final Player player, String message) {
        if (player.isOp()) {
            String[] words = message.split(",", 2);
            if (words.length > 1 && this.context.alisa.getName().contains(words[0])) {
                message = words[1];
                if (message.startsWith(this.template)) {
                    String targetName = message.substring(this.template.length());
                    if (this.isPlayerImmune(targetName)) {
                        (new BukkitRunnable() {
                            public void run() {
                                ProcessorExtra.this.context.alisa.say("Офигел?");
                                ProcessorExtra.this.annihilate(player);
                            }
                        }).runTaskLater(this.context, 30L);
                        return true;
                    }

                    final Player target = Bukkit.getPlayerExact(targetName);
                    if (target != null && target.isOnline()) {
                        (new BukkitRunnable() {
                            public void run() {
                                ProcessorExtra.this.context.alisa.say("Прощай, #c3" + target.getName() + " #c1:(");
                                ProcessorExtra.this.annihilate(target);
                            }
                        }).runTaskLater(this.context, 30L);
                    }
                }
            }
        }
        return false;
    }

    private boolean isPlayerImmune(String name) {
        for (String imm : this.immunes) {
            if (!imm.equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    private void annihilate(final Player p) {
        for (int i = 0; i <= 12; ++i) {
            final int finalI = i;
            BukkitRunnable br = new BukkitRunnable() {
                public void run() {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.damage(2.0);
                    if (finalI == 12) {
                        p.setHealth(0.0);
                    }
                }
            };
            br.runTaskLater(this.context, 20 + i * 2);
        }
    }
}