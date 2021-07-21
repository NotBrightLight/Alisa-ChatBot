package ru.brightlight.alisa;

import java.util.HashSet;

import org.bukkit.Chunk;

public class ChunkLoader {
    boolean isActive;
    protected HashSet<Chunk> loadedChunks;
    BrightAlisa context;

    public ChunkLoader(BrightAlisa context) {
        this.context = context;
        this.isActive = false;
        this.loadedChunks = new HashSet<>();
    }

    protected void setActive(boolean setActive) {
        if (setActive) {
            this.context.registerListenerChunkUnload();
            this.isActive = true;
        } else {
            this.context.unregisterListenerChunkUnload();
            this.loadedChunks = new HashSet<>();
            this.isActive = false;
        }

    }

    protected void addChunk(Chunk chunk) {
        this.loadedChunks.add(chunk);
        if (!this.isActive) {
            this.setActive(true);
        }
    }

    protected boolean shouldChunkBeKeptLoaded(Chunk testedChunk) {
        return this.loadedChunks.contains(testedChunk);
    }
}