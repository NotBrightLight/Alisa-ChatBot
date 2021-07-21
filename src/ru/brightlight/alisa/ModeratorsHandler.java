package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ModeratorsHandler {
    BrightAlisa context;
    public ArrayList<ModeratorsEntry> groups = new ArrayList<>();
    protected ArrayList<Player> hiddenModerators = new ArrayList<>();

    public ModeratorsHandler(BrightAlisa context) {
        this.context = context;
        this.loadModeratorsConfig();
    }

    protected SuccessReport createGroup(ModeratorsEntry moderatorsEntry) {
        this.groups.add(moderatorsEntry);
        Collections.sort(this.groups);
        this.saveModeratorsConfig();
        return new SuccessReport(true, String.format("#cgУспешно#c3: создана группа #c2%s#c3 (ID #c2%d#c3)", moderatorsEntry.groupName, moderatorsEntry.ID));
    }

    protected String getAllModsListString() {
        StringBuilder sb = new StringBuilder("#c2Список модераторов по группам#c3:\n");
        ArrayList<String> responses = new ArrayList<>();

        for (ModeratorsEntry group : this.groups) {
            responses.add(group.getModListString());
        }

        sb.append(String.join("\n", responses));
        return sb.toString();
    }

    protected SuccessReport addPlayerToGroup(int ID, String playerName0) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerName0);
        if (op != null && op.getName() != null) {
            String playerName = op.getName();
            this.removePlayerFromAllGroupsExceptOneSilent(playerName, ID);
            ModeratorsEntry group = this.getGroupById(ID);
            if (group != null) {
                SuccessReport sr = group.addPlayerByName(playerName);
                this.saveModeratorsConfig();
                return sr;
            } else {
                return new SuccessReport(false, String.format("#crОшибка#c3: группа с ID #c2%d#c3 не существует", ID));
            }
        } else {
            return new SuccessReport(false, String.format("#crОшибка#c3: игрок с именем #c2%s#c3 не найден", playerName0));
        }
    }

    protected SuccessReport removePlayerFromAllGroups(String playername) {
        int removedGroupsCount = 0;

        for (ModeratorsEntry group : this.groups) {
            SuccessReport sr = group.removePlayerByName(playername);
            if (sr.success) {
                ++removedGroupsCount;
            }
        }

        if (removedGroupsCount > 0) {
            this.saveModeratorsConfig();
            return new SuccessReport(true, String.format("#cgУспешно#c3: игрок #c2%s#c3 удален из списка модераторов", playername));
        } else {
            return new SuccessReport(true, String.format("#crОшибка#c3: игрок #c2%s#c3 не находится в списках модераторов", playername));
        }
    }

    private void removePlayerFromAllGroupsExceptOneSilent(String playername, int exceptionGroupID) {
        for (ModeratorsEntry group : this.groups) {
            if (group.ID != exceptionGroupID) {
                group.removePlayerByName(playername);
            }
        }
    }

    private ModeratorsEntry getGroupById(int ID) {
        for (ModeratorsEntry me : this.groups) {
            if (me.ID == ID) return me;
        }
        return null;
    }

    protected String getOnlineModsString() {
        StringBuilder sb = new StringBuilder("#c2Список модераторов онлайн#c3:\n");
        ArrayList<String> responses = new ArrayList<>();

        for (int i = 0; i < this.groups.size(); ++i) {
            responses.add(this.groups.get(i).getOnlinePlayersString());
        }

        sb.append(String.join("\n", responses));
        return sb.toString();
    }

    protected SuccessReport addModsGroup(String groupName, int ID, String prefixColor, String nameColor) {
        if (this.getGroupById(ID) == null && !this.groupWithThisNameExists(groupName)) {
            ModeratorsEntry me = new ModeratorsEntry(ID, groupName, prefixColor, nameColor);
            return this.createGroup(me);
        } else {
            return new SuccessReport(false, String.format("#crОшибка#c3: группа с именем #c2%s#c3 или ID #c2%s#c3 уже существует", groupName, ID));
        }
    }

    private boolean groupWithThisNameExists(String name) {
        for (ModeratorsEntry me : this.groups) {
            if (me.groupName.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    protected SuccessReport removeModsGroup(int ID) {
        ModeratorsEntry toRemove = this.getGroupById(ID);
        if (toRemove != null) {
            this.groups.remove(toRemove);
            this.saveModeratorsConfig();
            return new SuccessReport(true, String.format("#cgУспешно#c3: группа с ID #c2%d#c3 (#c2%s#c3) была удалена", ID, toRemove.groupName));
        } else {
            return new SuccessReport(true, String.format("#crОшибка#c3: группы с ID #c2%d#c3 не существует", ID));
        }
    }

    protected SuccessReport editGroup(int ID, String newName, String newPrefixColor, String newNameColor) {
        ModeratorsEntry me = this.getGroupById(ID);
        if (me != null) {
            me.groupName = newName;
            me.prefixColor = newPrefixColor;
            me.playernameColor = newNameColor;
            this.saveModeratorsConfig();
            Collections.sort(this.groups);
            return new SuccessReport(true, String.format("#cgУспешно#c3: группа с ID #c2%d#c3 (#c2%s#c3) была обновлена", ID, me.groupName));
        } else {
            return new SuccessReport(false, String.format("#crОшибка#c3: группы с ID #c2%d#c3 не существует", ID));
        }
    }

    protected void saveModeratorsConfig() {
        this.context.config.set("moderators", this.groups);
    }

    protected void loadModeratorsConfig() {
        this.groups = (ArrayList) this.context.getConfig().getList("moderators", new ArrayList());
    }

    protected boolean isModerator(Player p) {
        for (ModeratorsEntry me : this.groups) {
            for (String name : me.playerNames) {
                if (name.equalsIgnoreCase(p.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void toggleDetect(Player player) {
        if (this.isModerator(player)) {
            if (this.hiddenModerators.contains(player)) {
                this.hiddenModerators.remove(player);
                this.context.alisa.whisper(player, "#cgУспешно#c3: теперь тебя #cgвидно#c3 в списке онлайн модераторов (до рестарта)");
            } else {
                this.hiddenModerators.add(player);
                this.context.alisa.whisper(player, "#cgУспешно#c3: теперь тебя #crне видно#c3 в списке онлайн модераторов (до рестарта)");
            }
        } else {
            this.context.alisa.whisper(player, "#crОшибка#c3: тебя нет в списках модераторов");
        }

    }

    protected boolean isModeratorHidden(String name) {
        for (Player p : this.hiddenModerators) {
            if (p.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    protected boolean isModerator(String playerName) {
        for (ModeratorsEntry me : this.groups) {
            for (String name : me.playerNames) {
                if (name.equalsIgnoreCase(playerName)) return true;
            }
        }
        return false;
    }
}