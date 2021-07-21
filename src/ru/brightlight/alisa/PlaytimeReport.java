package ru.brightlight.alisa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PlaytimeReport implements ConfigurationSerializable {
    protected long time;
    protected HashMap<String, Integer> entries;

    public PlaytimeReport(long time, HashMap<String, Integer> entries) {
        this.time = time;
        this.entries = entries;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("time", this.time);
        result.put("entries", this.entries);
        return result;
    }

    public static PlaytimeReport deserialize(Map<String, Object> args) {
        long time = -1L;
        HashMap entries = new HashMap();
        if (args.containsKey("time")) {
            time = (Long) args.get("time");
        }

        if (args.containsKey("entries")) {
            entries = (HashMap) args.get("entries");
        }

        return new PlaytimeReport(time, entries);
    }
}