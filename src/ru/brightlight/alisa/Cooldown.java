package ru.brightlight.alisa;

public class Cooldown {
    private long lastTriggered;
    private long duration;

    protected Cooldown(long durationSeconds) {
        this.duration = durationSeconds * 1000L;
        this.lastTriggered = 0L;
    }

    protected boolean isExpired() {
        return System.currentTimeMillis() - this.lastTriggered >= this.duration;
    }

    protected void trigger() {
        this.lastTriggered = System.currentTimeMillis();
    }

    protected long getSecondsLeft() {
        return (this.lastTriggered + this.duration - System.currentTimeMillis()) / 1000L;
    }
}