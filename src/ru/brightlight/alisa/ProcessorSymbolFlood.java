package ru.brightlight.alisa;

import ru.brightlight.alisa.Alisa.AnswerReason;
import ru.brightlight.alisa.Alisa.PunishmentType;
import org.bukkit.entity.Player;

public class ProcessorSymbolFlood implements IProcessor {
    private BrightAlisa context;
    private int minimumMessageLengthForSymbolCheck = 12;

    public ProcessorSymbolFlood(BrightAlisa context) {
        this.context = context;
    }

    public boolean processMessage(Player player, String message) {
        if (message.length() >= this.minimumMessageLengthForSymbolCheck && this.getSymbolRatioOfMessage(message) > this.getAllowedSymbolRatio()) {
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationSymbolFlood(), "Флуд символами", AnswerReason.FLOOD);
            return true;
        } else {
            return false;
        }
    }

    public float getSymbolRatioOfMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            int count = 0;
            String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.№;:+=-'";

            for (int i = 0; i < message.length(); ++i) {
                if (specialChars.contains(message.substring(i, i + 1))) {
                    ++count;
                }
            }
            return (float) count / (float) message.length();
        } else {
            return 0.0F;
        }
    }

    private float getAllowedSymbolRatio() {
        return this.context.config.getFloat("symbol-ratio");
    }

    private int getTempmuteDurationSymbolFlood() {
        return this.context.config.getInt("tempmute.symbol-flood");
    }
}