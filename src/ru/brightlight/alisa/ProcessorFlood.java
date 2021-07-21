package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import ru.brightlight.alisa.Alisa.AnswerReason;
import ru.brightlight.alisa.Alisa.PunishmentType;
import org.bukkit.entity.Player;

public class ProcessorFlood implements IProcessor {
    protected BrightAlisa context;
    private ArrayList<String> tradeFilters1;
    private ArrayList<String> tradeFilters2;
    private HashMap<Player, ProcessorFlood.MessageData> messages = new HashMap<>();
    private int flood_any_trade_messages_trigger = 3;
    private int flood_identical_trade_messages_trigger = 2;
    private int flood_timeout = 60;
    private int flood_normal_messages_trigger = 3;
    private int flood_any_trade_messages_period = 300;

    protected ProcessorFlood(BrightAlisa context) {
        this.context = context;
        this.tradeFilters1 = mf.readProjectFileLines("trade-filters-1.txt");
        this.tradeFilters2 = mf.readProjectFileLines("trade-filters-2.txt");
    }

    private int getTempmuteDurationFlood() {
        return this.context.config.getInt("tempmute.flood");
    }

    private int getTempmuteDurationAdvertisement() {
        return this.context.config.getInt("tempmute.advertisement");
    }

    public boolean processMessage(Player player, String message) {
        if (!this.messages.containsKey(player)) {
            this.messages.put(player, new ProcessorFlood.MessageData(this.flood_any_trade_messages_trigger));
        }

        boolean isTrade = this.isTradeMessage(message);
        ProcessorFlood.MessageData md = this.messages.get(player);
        if (!md.previousMessage.isEmpty() && md.previousMessage.equalsIgnoreCase(message) && System.currentTimeMillis() - md.previousMessageTime <= (this.flood_timeout * 1000L)) {
            ++md.subsequentMessagesCount;
        } else {
            md.subsequentMessagesCount = 1;
        }
        md.previousMessage = message;
        md.previousMessageTime = System.currentTimeMillis();

        if (isTrade) {
            md.tradeMessageTimers.remove();
            md.tradeMessageTimers.add(System.currentTimeMillis());
        }

        if (isTrade && md.subsequentMessagesCount >= this.flood_identical_trade_messages_trigger) {
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationAdvertisement(), "Флуд рекламой", AnswerReason.ADVERTISEMENT);
            return true;
        } else if (isTrade && md.tradeMessageTimers.get(0) != 0L && System.currentTimeMillis() - md.tradeMessageTimers.get(0) <= (this.flood_any_trade_messages_period * 1000L)) {
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationAdvertisement(), "Флуд рекламой", AnswerReason.ADVERTISEMENT);
            return true;
        } else if (md.subsequentMessagesCount >= this.flood_normal_messages_trigger) {
            this.context.alisa.punish(PunishmentType.MUTE, player, this.getTempmuteDurationFlood(), "Флуд", AnswerReason.FLOOD);
            return true;
        } else {
            return false;
        }
    }

    private boolean isTradeMessage(String message) {
        if ((message = message.toLowerCase()).length() > 12) {
            for (String word : this.tradeFilters1) {
                if (!message.contains(word)) continue;
                return true;
            }
        }
        if (message.length() > 30) {
            for (String word : this.tradeFilters2) {
                if (!message.contains(word)) continue;
                return true;
            }
        }
        return false;
    }

    class MessageData {
        String previousMessage = "";
        int subsequentMessagesCount = 1;
        long previousMessageTime = 0L;
        LinkedList<Long> tradeMessageTimers = new LinkedList<>();

        MessageData(int anyMessagesTrigger) {
            for (int i = 0; i < anyMessagesTrigger; ++i) {
                this.tradeMessageTimers.add(0L);
            }
        }
    }
}