package ru.brightlight.alisa;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Statistics implements ConfigurationSerializable {
    int chatMessages;
    int answers;
    int warns;
    int mutes;
    int mutesDuration;
    int totalVotesStarted;
    int successfulVotes;
    int modsAndInfCommands;
    int onlineSeconds;

    public Statistics(int chatMessages, int answers, int warns, int mutes, int mutesDuration, int totalVotesStarted, int successfulVotes, int modsAndInfCommands, int onlineSeconds) {
        this.chatMessages = chatMessages;
        this.answers = answers;
        this.warns = warns;
        this.mutes = mutes;
        this.mutesDuration = mutesDuration;
        this.totalVotesStarted = totalVotesStarted;
        this.successfulVotes = successfulVotes;
        this.modsAndInfCommands = modsAndInfCommands;
        this.onlineSeconds = onlineSeconds;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("chatMessages", this.chatMessages);
        result.put("answers", this.answers);
        result.put("warns", this.warns);
        result.put("mutes", this.mutes);
        result.put("mutesDuration", this.mutesDuration);
        result.put("totalVotesStarted", this.totalVotesStarted);
        result.put("successfulVotes", this.successfulVotes);
        result.put("modsAndInfCommands", this.modsAndInfCommands);
        result.put("onlineSeconds", this.onlineSeconds);
        return result;
    }

    public static Statistics deserialize(Map<String, Object> args) {
        int chatMessages = 0;
        int answers = 0;
        int warns = 0;
        int mutes = 0;
        int mutesDuration = 0;
        int totalVotesStarted = 0;
        int successfulVotes = 0;
        int modsAndInfCommands = 0;
        int onlineSeconds = 0;
        if (args.containsKey("chatMessages")) {
            chatMessages = (Integer) args.get("chatMessages");
        }

        if (args.containsKey("answers")) {
            answers = (Integer) args.get("answers");
        }

        if (args.containsKey("warns")) {
            warns = (Integer) args.get("warns");
        }

        if (args.containsKey("mutes")) {
            mutes = (Integer) args.get("mutes");
        }

        if (args.containsKey("mutesDuration")) {
            mutesDuration = (Integer) args.get("mutesDuration");
        }

        if (args.containsKey("totalVotesStarted")) {
            totalVotesStarted = (Integer) args.get("totalVotesStarted");
        }

        if (args.containsKey("successfulVotes")) {
            successfulVotes = (Integer) args.get("successfulVotes");
        }

        if (args.containsKey("modsAndInfCommands")) {
            modsAndInfCommands = (Integer) args.get("modsAndInfCommands");
        }

        if (args.containsKey("onlineSeconds")) {
            onlineSeconds = (Integer) args.get("onlineSeconds");
        }

        return new Statistics(chatMessages, answers, warns, mutes, mutesDuration, totalVotesStarted, successfulVotes, modsAndInfCommands, onlineSeconds);
    }
}