package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.brightlight.alisa.Alisa.AnswerReason;
import ru.brightlight.alisa.Alisa.PunishmentType;
import org.bukkit.entity.Player;

public class ProcessorProfanity implements IProcessor {
    protected BrightAlisa context;
    ArrayList<Pattern> badWords1;
    ArrayList<Pattern> badWords2;

    public ProcessorProfanity(BrightAlisa context) {
        this.context = context;
        ArrayList<String> temp = mf.readProjectFileLines("profanity1.txt");
        this.badWords1 = new ArrayList<>(temp.size());

        for (String word : temp) {
            Pattern pat = Pattern.compile(word.replace("|", "\\b"));
            this.badWords1.add(pat);
        }

        ArrayList<String> temp2 = mf.readProjectFileLines("profanity2.txt");
        this.badWords2 = new ArrayList<>(temp2.size());

        for (String word : temp2) {
            Pattern pat = Pattern.compile(word.replace("|", "\\b"));
            this.badWords2.add(pat);
        }
    }

    public boolean processMessage(Player player, String message0) {
        Matcher matcher;
        String message = message0.toLowerCase();
        for (Pattern p : this.badWords1) {
            matcher = p.matcher(message);
            if (!matcher.find()) continue;
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationProfanity(), "Ненормативная лексика", AnswerReason.PROFANITY);
            this.context.debug("mute/profanity: " + player.getName() + ": '" + message + "' (" + message0 + ")");
            return true;
        }
        for (Pattern p : this.badWords2) {
            matcher = p.matcher(message);
            if (!matcher.find()) continue;
            this.context.alisa.punish(PunishmentType.WARN, player, 0, "3.3", AnswerReason.PROFANITY);
            return true;
        }
        return false;
    }

    private int getTempmuteDurationProfanity() {
        return this.context.config.getInt("tempmute.profanity");
    }
}