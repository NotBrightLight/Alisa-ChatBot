package ru.brightlight.alisa;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerPlayerLogin implements Listener {
    private BrightAlisa context;

    public ListenerPlayerLogin(BrightAlisa context) {
        this.context = context;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joinedPlayer = event.getPlayer();
        this.context.alisa.addKnownPlayer(joinedPlayer.getName());
        String toRemove = null;

        for (String name : this.context.alisa.utilCommandsHandler.toSpawnPlayerNames) {
            if (joinedPlayer.getName().equalsIgnoreCase(name)) {
                toRemove = name;
                joinedPlayer.teleport(joinedPlayer.getWorld().getSpawnLocation().add(0.0D, 0.5D, 0.0D));
                this.context.config.set("tospawn-playernames", this.context.alisa.utilCommandsHandler.toSpawnPlayerNames);
            }
        }

        if (toRemove != null) {
            this.context.alisa.utilCommandsHandler.toSpawnPlayerNames.remove(toRemove);
            this.context.config.set("tospawn-playernames", this.context.alisa.utilCommandsHandler.toSpawnPlayerNames);
        }
    }
}