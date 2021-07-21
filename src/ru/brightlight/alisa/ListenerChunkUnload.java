package ru.brightlight.alisa;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ListenerChunkUnload implements Listener {
    private BrightAlisa context;

    public ListenerChunkUnload(BrightAlisa context) {
        this.context = context;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (this.context.alisa.chunkLoader.shouldChunkBeKeptLoaded(event.getChunk())) {
            event.setCancelled(true);
        }
    }
}