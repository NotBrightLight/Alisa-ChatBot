package ru.brightlight.alisa;

import org.bukkit.World;

public class MyChunk {
    public World world;
    public int x;
    public int z;

    public MyChunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }
}
