package ru.brightlight.alisa;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface MyChatListener extends Listener {
    void onChat(AsyncPlayerChatEvent event);
}
