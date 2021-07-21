package ru.brightlight.alisa;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Vote {
    protected long duration;
    protected float successRatio;
    protected VoteHandler voteHandler;
    protected HashSet<Player> votedFor = new HashSet<>();
    protected HashSet<Player> votedAgainst = new HashSet<>();
    protected Player playerStarter;
    protected int successAdvantage;
    protected String positiveOutcome;
    protected String negativeOutcome;

    protected Vote(int durationSeconds, float successRatio, int successAdvantage, VoteHandler voteHandler, Player starter) {
        this.duration = durationSeconds * 1000L;
        this.successRatio = successRatio;
        this.voteHandler = voteHandler;
        this.playerStarter = starter;
        this.successAdvantage = successAdvantage;
        this.start();
    }

    abstract void start();

    protected SuccessReport tryToCastVote(Player player, boolean YES) {
        if (!this.votedFor.contains(player) && !this.votedAgainst.contains(player)) {
            if (YES) {
                this.votedFor.add(player);
                return new SuccessReport(true, "ты проголосовал #cg\"за\"");
            } else {
                this.votedAgainst.add(player);
                return new SuccessReport(true, "ты проголосовал #cr\"против\"");
            }
        } else {
            return new SuccessReport(true, "ты уже участвовал в этом голосовании, <persik> :*");
        }
    }

    protected void start0() {
        this.votedFor.add(this.playerStarter);
        BukkitRunnable br = new BukkitRunnable() {
            public void run() {
                Vote.this.finish();
            }
        };
        br.runTaskLater(this.voteHandler.context, this.duration / 1000L * 20L);
    }

    protected SuccessReport castVote(Player player, boolean FOR) {
        if (!this.votedFor.contains(player) && !this.votedAgainst.contains(player)) {
            if (FOR) {
                this.votedFor.add(player);
                return new SuccessReport(true, "ты проголосовал #cg\"за\"#c3");
            } else {
                this.votedAgainst.add(player);
                return new SuccessReport(true, "ты проголосовал #cr\"против\"#c3");
            }
        } else {
            return new SuccessReport(true, "ты уже принимал участие в этом голосовании, <persik> :*");
        }
    }

    abstract void finish();
}