package ru.brightlight.alisa;

import java.util.HashMap;

public class CooldownIndexBased {
    private HashMap<Integer, Cooldown> cooldowns = new HashMap<>();
    private long duration;

    public CooldownIndexBased(int separateDurationSeconds) {
        this.duration = separateDurationSeconds * 1000L;
    }

    protected boolean isExpired(int index) {
        return !this.cooldowns.containsKey(index) || this.cooldowns.get(index).isExpired();
    }

    protected void trigger(int index) {
        if (!this.cooldowns.containsKey(index)) {
            this.cooldowns.put(index, new Cooldown(this.duration / 1000L));
        }
        
        this.cooldowns.get(index).trigger();
    }
}