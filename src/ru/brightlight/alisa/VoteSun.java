package ru.brightlight.alisa;

import org.bukkit.entity.Player;

public class VoteSun extends Vote {
    protected VoteSun(int durationSeconds, float successRatio, int successAdvantage, VoteHandler voteHandler, Player starter) {
        super(durationSeconds, successRatio, successAdvantage, voteHandler, starter);
    }

    void start() {
        this.voteHandler.context.alisa.sayRaw(this.voteHandler.context.alisa.textComponent(String.format("#c3%s#c1 начал голосование за #c2отключение дождя#c1", this.playerStarter.getName())));
        this.voteHandler.context.alisa.sayRawDelayed(this.voteHandler.context.alisa.textComponent("#c1Голосуй: "), this.voteHandler.context.alisa.clickComponent("#cgотключить", "/yes"), this.voteHandler.context.alisa.textComponent(" "), this.voteHandler.context.alisa.clickComponent("#crоставить", "/no"));
        super.start0();
    }

    void finish() {
        int FOR = this.votedFor.size();
        int AGAINST = this.votedAgainst.size();
        boolean succeeded = FOR - AGAINST >= this.successAdvantage && (AGAINST == 0 || (float) FOR / ((float) FOR + (float) AGAINST) >= this.successRatio);
        String append = succeeded ? "Дождь #cgвыключен" : "Дождь #crне выключен";
        this.voteHandler.context.alisa.say(String.format("Голосование окончено: #cg\"за\"#c1 - #cg%d#c1, #cr\"против\"#c1 - #cr%d#c1. %s", FOR, AGAINST, append));
        if (succeeded) {
            this.voteHandler.context.getMainWorld().setStorm(false);
            ++this.voteHandler.context.alisa.statistics.successfulVotes;
        }

        this.voteHandler.currentVote = null;
    }
}