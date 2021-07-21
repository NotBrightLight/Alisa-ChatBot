package ru.brightlight.alisa;

import org.bukkit.entity.Player;

public class VoteHandler {
    protected BrightAlisa context;
    protected Vote currentVote;

    protected VoteHandler(BrightAlisa context) {
        this.context = context;
    }

    protected SuccessReport castVote(Player player, boolean YES) {
        return this.currentVote == null ? new SuccessReport(true, "но ведь сейчас не идет голосование, <persik> :*") : this.currentVote.tryToCastVote(player, YES);
    }

    protected SuccessReport tryToStartVotesun(Player player) {
        if (this.currentVote != null) {
            return new SuccessReport(true, "прости, <persik>, но сейчас идет другое голосование :(");
        } else if (!this.context.getMainWorld().hasStorm()) {
            return new SuccessReport(true, "но ведь сейчас не идет дождь, <persik> :*");
        } else if (!this.context.alisa.cooldownsHandler.votesunGlobalCooldown.isExpired()) {
            return new SuccessReport(true, String.format("извини, <persik>, но глобальный кулдаун на это голосование еще не прошел :( Осталось #c2%d#c3 секунд", this.context.alisa.cooldownsHandler.votesunGlobalCooldown.getSecondsLeft()));
        } else if (!this.context.alisa.cooldownsHandler.votesunPersonalCooldowns.isExpired(player)) {
            return new SuccessReport(true, String.format("прости, <persik>, но твой кулдаун на это голосование еще не прошел :( Осталось всего лишь #c2%d#c3 секунд", this.context.alisa.cooldownsHandler.votesunPersonalCooldowns.getSecondsLeft(player)));
        } else {
            this.context.alisa.voteHandler.startVotesun(player);
            return new SuccessReport(false, "");
        }
    }

    private boolean isNight() {
        long time = this.context.getMainWorld().getTime();
        return time > 12700L && time < 23000L;
    }

    protected SuccessReport tryToStartVoteday(Player player) {
        if (this.currentVote != null) {
            return new SuccessReport(true, "прости, <persik>, но сейчас идет другое голосование :(");
        } else if (!this.isNight()) {
            return new SuccessReport(true, "но ведь сейчас не ночь, <persik> :*");
        } else if (!this.context.alisa.cooldownsHandler.votedayGlobalCooldown.isExpired()) {
            return new SuccessReport(true, String.format("извини, <persik>, но глобальный кулдаун на это голосование еще не прошел :( Осталось #c2%d#c3 секунд", this.context.alisa.cooldownsHandler.votedayGlobalCooldown.getSecondsLeft()));
        } else if (!this.context.alisa.cooldownsHandler.votedayPersonalCooldowns.isExpired(player)) {
            return new SuccessReport(true, String.format("прости, <persik>, но твой кулдаун на это голосование еще не прошел :( Осталось всего лишь #c2%d#c3 секунд", this.context.alisa.cooldownsHandler.votedayPersonalCooldowns.getSecondsLeft(player)));
        } else {
            this.context.alisa.voteHandler.startVoteday(player);
            return new SuccessReport(false, "");
        }
    }

    private void startVotesun(Player player) {
        int duration = this.context.config.getInt("duration.votesun");
        float successRatio = this.context.config.getFloat("success-ratio.votesun");
        int successAdvantage = this.context.config.getInt("success-advantage.votesun");
        this.currentVote = new VoteSun(duration, successRatio, successAdvantage, this, player);
        this.context.alisa.cooldownsHandler.votesunGlobalCooldown.trigger();
        this.context.alisa.cooldownsHandler.votesunPersonalCooldowns.trigger(player);
        ++this.context.alisa.statistics.totalVotesStarted;
    }

    private void startVoteday(Player player) {
        int duration = this.context.config.getInt("duration.voteday");
        float successRatio = this.context.config.getFloat("success-ratio.voteday");
        int successAdvantage = this.context.config.getInt("success-advantage.voteday");
        this.currentVote = new VoteDay(duration, successRatio, successAdvantage, this, player);
        this.context.alisa.cooldownsHandler.votedayGlobalCooldown.trigger();
        this.context.alisa.cooldownsHandler.votedayPersonalCooldowns.trigger(player);
        ++this.context.alisa.statistics.totalVotesStarted;
    }
}