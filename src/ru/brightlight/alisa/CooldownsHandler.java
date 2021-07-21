package ru.brightlight.alisa;

public class CooldownsHandler {
    protected BrightAlisa context;
    protected Cooldown helloByeCooldown;
    protected Cooldown votesunGlobalCooldown;
    protected Cooldown votedayGlobalCooldown;
    protected CooldownIndexBased questionsCooldowns;
    protected CooldownPlayerBased warnCooldowns;
    protected CooldownPlayerBased votesunPersonalCooldowns;
    protected CooldownPlayerBased votedayPersonalCooldowns;

    protected CooldownsHandler(BrightAlisa context) {
        this.context = context;
        this.helloByeCooldown = new Cooldown(context.config.getInt("cooldown.hello"));
        this.questionsCooldowns = new CooldownIndexBased(context.config.getInt("cooldown.answers"));
        this.warnCooldowns = new CooldownPlayerBased(context.config.getInt("cooldown.warn"));
        this.votesunGlobalCooldown = new Cooldown(context.config.getInt("cooldown.votesun-global"));
        this.votedayGlobalCooldown = new Cooldown(context.config.getInt("cooldown.voteday-global"));
        this.votesunPersonalCooldowns = new CooldownPlayerBased(context.config.getInt("cooldown.votesun-personal"));
        this.votedayPersonalCooldowns = new CooldownPlayerBased(context.config.getInt("cooldown.voteday-personal"));
    }
}