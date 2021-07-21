package ru.brightlight.alisa;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class CooldownPlayerBased {
    private HashMap<Player, Cooldown> cooldowns = new HashMap<>();
    private long duration;

    public CooldownPlayerBased(int separateDurationSeconds) {
        this.duration = separateDurationSeconds * 1000L;
    }

    protected boolean isExpired(Player player) {
        return !this.cooldowns.containsKey(player) || this.cooldowns.get(player).isExpired();
    }

    protected void trigger(Player player) {
        if (!this.cooldowns.containsKey(player)) {
            this.cooldowns.put(player, new Cooldown(this.duration / 1000L));
        }

        this.cooldowns.get(player).trigger();
    }

    protected long getSecondsLeft(Player player) {
        return this.cooldowns.containsKey(player) ? this.cooldowns.get(player).getSecondsLeft() : 0L;
    }
}