package ru.brightlight.alisa;

import org.bukkit.entity.Player;

public interface IProcessor {
    boolean processMessage(Player player, String message);
}
