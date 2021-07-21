package ru.brightlight.alisa;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private BrightAlisa context;
    private FileConfiguration config;

    public Config(BrightAlisa context) {
        this.context = context;
        this.config = context.getConfig();
    }

    public String getString(String key) {
        return this.config.getString(key);
    }

    public float getFloat(String key) {
        return (float) this.config.getDouble(key);
    }

    public int getInt(String key) {
        return this.config.getInt(key);
    }

    public boolean getBoolean(String key) {
        return this.config.getBoolean(key, false);
    }

    public ArrayList<String> getList(String key) {
        return (ArrayList<String>) this.config.getStringList(key);
    }

    public void set(String key, Object value) {
        this.config.set(key, value);
        this.context.saveConfig();
    }

    public Object getObject(String key) {
        return this.config.get(key);
    }

    public boolean exists(String key) {
        return this.config.contains(key);
    }
}