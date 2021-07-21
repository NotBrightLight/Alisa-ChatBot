package ru.brightlight.alisa;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerChatEssChat implements MyChatListener {
    private BrightAlisa context;

    public ListenerChatEssChat(BrightAlisa context) {
        this.context = context;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            this.context.messageHandler.handleMessage(event.getPlayer(), event.getMessage());
        }
    }
}