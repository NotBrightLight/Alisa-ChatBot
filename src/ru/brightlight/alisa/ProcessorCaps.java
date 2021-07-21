package ru.brightlight.alisa;

import java.util.ArrayList;

import ru.brightlight.alisa.Alisa.AnswerReason;
import ru.brightlight.alisa.Alisa.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProcessorCaps implements IProcessor {
    private BrightAlisa context;
    private int minimumMessageLengthForCapsCheck = 6;
    private int minimumLettersForCapsCheck = 3;
    private ArrayList<String> ignoredWords;

    public ProcessorCaps(BrightAlisa context) {
        this.context = context;
        this.ignoredWords = mf.readProjectFileLines("ignored-words-caps.txt");
    }

    public boolean processMessage(Player player, String message0) {
        String message = this.removeIgnoredWords(message0);
        message = this.removePlayerNames(message);
        message = this.removeDigitsAndSpaces(message);
        if (message.length() > this.minimumMessageLengthForCapsCheck && this.getCapsRatioOfMessage(message) > this.getAllowedCapsRatio()) {
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationCaps(), "Капс", AnswerReason.CAPS);
            this.context.debug("mute/caps: " + player.getName() + ": '" + message0 + "' (" + message + ")");
            return true;
        } else {
            return false;
        }
    }

    private String removeDigitsAndSpaces(String message) {
        message = message.replaceAll("\\d", "");
        message = message.replaceAll("\\s", "");
        return message;
    }

    private String removeIgnoredWords(String message) {
        for (String word : this.ignoredWords) {
            message = message.replaceAll(word, "");
        }
        return message;
    }

    private String removePlayerNames0(String message) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            message = message.replaceFirst(p.getName(), "");
        }
        return message;
    }

    private String removePlayerNames(String message) {
        for (String playername : this.context.alisa.knownPlayerNames) {
            message = message.replaceFirst(playername, "");
        }
        return message;
    }

    private int getTempmuteDurationCaps() {
        return this.context.config.getInt("tempmute.caps");
    }

    private float getAllowedCapsRatio() {
        return this.context.config.getFloat("caps-ratio");
    }

    private float getCapsRatioOfMessage(String message) {
        int total = 0;
        int uppercase = 0;

        for (int i = 0; i < message.length(); ++i) {
            if (Character.isLetter(message.charAt(i))) {
                ++total;
                if (Character.isUpperCase(message.charAt(i))) {
                    ++uppercase;
                }
            }
        }

        if (total < this.minimumLettersForCapsCheck) {
            return 0.0F;
        } else {
            return (float) uppercase / (float) total;
        }
    }
}