package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("ModeratorsEntry")
public class ModeratorsEntry implements Comparable<ModeratorsEntry>, ConfigurationSerializable {
    public String groupName;
    public String prefixColor;
    public String playernameColor;
    public ArrayList<String> playerNames = new ArrayList<>();
    public int ID;

    public int compareTo(ModeratorsEntry o) {
        return o.ID - this.ID;
    }

    protected ModeratorsEntry(int ID, String groupName, String prefixColor, String playernameColor) {
        this.ID = ID;
        this.groupName = groupName;
        this.prefixColor = prefixColor;
        this.playernameColor = playernameColor;
    }

    protected ModeratorsEntry(int ID, String groupName, ArrayList<String> playerNames, String prefixColor, String playernameColor) {
        this.ID = ID;
        this.groupName = groupName;
        this.prefixColor = prefixColor;
        this.playernameColor = playernameColor;
        this.playerNames = playerNames;
    }

    public String getModListString() {
        return String.format("[%d] %s[%s%s%s]#c3: %s", this.ID, ChatColor.GRAY, '§' + this.prefixColor, this.groupName, ChatColor.GRAY, this.getPlayerListString());
    }

    private String getPlayerListString() {
        ArrayList<String> coloredMods = new ArrayList<>();

        for (String name : this.playerNames) {
            coloredMods.add(String.format("%s%s#c3", '§' + this.playernameColor, name));
        }

        return String.join(", ", coloredMods);
    }

    protected SuccessReport addPlayerByName(String playerName) {
        if (!this.hasPlayer(playerName)) {
            this.playerNames.add(playerName);
            return new SuccessReport(true, String.format("#cgУспешно#c3: игрок #c2%s#c3 добавлен в группу #c2%s", playerName, this.groupName));
        } else {
            return new SuccessReport(false, String.format("#crОшибка#c3: игрок #c2%s#c3 уже находится в группе #c2%s", playerName, this.groupName));
        }
    }

    protected SuccessReport removePlayerByName(String playerName) {
        if (this.hasPlayer(playerName)) {
            this.removeIgnoreCase(playerName, this.playerNames);
            return new SuccessReport(true, String.format("#cgУспешно#c3: игрок #c2%s#c3 удален из группы #c2%s", playerName, this.groupName));
        } else {
            return new SuccessReport(false, String.format("#crОшибка#c3: игрок #c2%s#c3 не находится в группе #c2%s", playerName, this.groupName));
        }
    }

    private void removeIgnoreCase(String playerName, ArrayList<String> arr) {
        String toRemove = null;
        for (String s : arr) {
            if (!s.equalsIgnoreCase(playerName)) continue;
            toRemove = s;
            break;
        }
        if (toRemove != null) {
            arr.remove(toRemove);
        }
    }

    protected boolean hasPlayer(String playerName) {
        return mf.arrayListContainsIgnoreCase(this.playerNames, playerName);
    }

    protected String getColoredGroupName() {
        return String.format("%s%s%s", '§' + this.prefixColor, this.groupName, ChatColor.RESET);
    }

    protected String getFormattedGroupName() {
        return String.format("%s[%s%s%s]", ChatColor.DARK_GRAY, '§' + this.prefixColor, this.groupName, ChatColor.DARK_GRAY);
    }

    protected String getOnlinePlayersString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> responses = new ArrayList<>();

        for (String playerName : this.playerNames) {
            if (!BrightAlisa.getInstance().alisa.moderatorsHandler.isModeratorHidden(playerName) && this.isPlayerNameOnline(playerName)) {
                responses.add(String.format("#cgонлайн %s>> %s%s %s", ChatColor.GRAY, this.getFormattedGroupName(), '§' + this.playernameColor, playerName));
            }
        }

        sb.append(String.join("\n", responses));
        return sb.toString();
    }

    private boolean isPlayerNameOnline(String playerName) {
        Player p = Bukkit.getServer().getPlayer(playerName);
        return p != null && p.isOnline();
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("groupName", this.groupName);
        result.put("prefixColor", this.prefixColor);
        result.put("playernameColor", this.playernameColor);
        result.put("ID", this.ID);
        result.put("playerNames", this.playerNames);
        return result;
    }

    public static ModeratorsEntry deserialize(Map<String, Object> args) {
        String groupName = "";
        String prefixColor = "";
        String playernameColor = "";
        ArrayList<String> playerNames = new ArrayList<>();
        int ID = 0;
        if (args.containsKey("groupName")) {
            groupName = (String) args.get("groupName");
        }

        if (args.containsKey("prefixColor")) {
            prefixColor = (String) args.get("prefixColor");
        }

        if (args.containsKey("playernameColor")) {
            playernameColor = (String) args.get("playernameColor");
        }

        if (args.containsKey("playerNames")) {
            playerNames = (ArrayList) args.get("playerNames");
        }

        if (args.containsKey("ID")) {
            ID = (Integer) args.get("ID");
        }

        return new ModeratorsEntry(ID, groupName, playerNames, prefixColor, playernameColor);
    }

}