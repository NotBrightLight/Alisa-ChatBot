package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ru.brightlight.alisa.checkers.IArgumentChecker;
import ru.brightlight.alisa.checkers.CheckBoolean;
import ru.brightlight.alisa.checkers.CheckFloat;
import ru.brightlight.alisa.checkers.CheckInt;
import ru.brightlight.alisa.checkers.CheckString;
import ru.brightlight.alisa.checkers.CheckStringArrayFixedSize;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Settings {
    private BrightAlisa context;
    private HashMap<String, IArgumentChecker> mcps = new HashMap<>();

    public Settings(BrightAlisa context) {
        this.context = context;
        this.registerMCPs();
    }

    protected void handleSetCommand(Player sender, Command cmd, String[] args) {
        ArrayList<String> providedArgs = new ArrayList<>(Arrays.asList(args));
        providedArgs.remove(0);
        if (providedArgs.size() > 0) {
            String propertyName = providedArgs.get(0).toLowerCase();
            providedArgs.remove(0);
            if (this.mcps.containsKey(propertyName)) {
                IArgumentChecker requiredArg = this.mcps.get(propertyName);
                if (providedArgs.size() > 0) {
                    if (this.checkArg(requiredArg, providedArgs)) {
                        Object resultingArg = requiredArg.getFixedClassedArg(providedArgs);
                        this.setConfigPropertyWithReport(propertyName, resultingArg, requiredArg, sender);
                    } else {
                        this.context.alisa.whisper(sender, String.format("#crОшибка#c3: неправильно введен(ы) аргумент(ы) к настройке #c2%s#c3", propertyName));
                    }
                } else {
                    this.context.alisa.whisper(sender, String.format("#crОшибка#c3: недостаточно аргументов к настройке #c2%s#c3", propertyName));
                }
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: настройки #c2%s#c3 не существует", propertyName));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: неправильно введен(ы) аргумент(ы) к команде");
        }
    }

    protected void handleReadCommand(Player sender, Command cmd, String[] args) {
        if (args.length >= 2) {
            if (this.context.config.exists(args[1])) {
                String result = this.context.config.getString(args[1]);
                this.context.alisa.whisper(sender, String.format("Текущее значение настройки #c2%s#c3: #c2%s", args[1], result));
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: настройки #c2%s#c3 не существует", args[1]));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    protected void handleModsCommand(Player sender, Command cmd, String[] args) {
        ArrayList<String> providedArgs = new ArrayList<>(Arrays.asList(args));
        providedArgs.remove(0);
        if (providedArgs.size() >= 1) {
            if (providedArgs.get(0).equalsIgnoreCase("add")) {
                providedArgs.remove(0);
                this.handleModsAddCommand(sender, providedArgs);
            } else if (providedArgs.get(0).equalsIgnoreCase("remove")) {
                providedArgs.remove(0);
                this.handleModsRemoveCommand(sender, providedArgs);
            } else if (providedArgs.get(0).equalsIgnoreCase("list")) {
                this.context.alisa.whisper(sender, this.context.alisa.moderatorsHandler.getAllModsListString());
            } else if (providedArgs.get(0).equalsIgnoreCase("creategroup")) {
                providedArgs.remove(0);
                this.handleModsCreateGroupCommand(sender, providedArgs);
            } else if (providedArgs.get(0).equalsIgnoreCase("removegroup")) {
                providedArgs.remove(0);
                this.handleModsRemoveGroupCommand(sender, providedArgs);
            } else if (providedArgs.get(0).equalsIgnoreCase("editgroup")) {
                providedArgs.remove(0);
                this.handleEditGroupCommand(sender, providedArgs);
            } else {
                this.context.alisa.whisper(sender, "#crОшибка#c3: такой команды не существует");
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void handleEditGroupCommand(Player sender, ArrayList<String> providedArgs) {
        if (providedArgs.size() >= 4) {
            String newName = providedArgs.get(1).replace("_", " ");
            Integer ID;
            if ((ID = this.getIntFromString(providedArgs.get(0))) != null) {
                SuccessReport sr = this.context.alisa.moderatorsHandler.editGroup(ID, newName, providedArgs.get(2), providedArgs.get(3));
                this.context.alisa.whisper(sender, sr.message);
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: #c2%s#c3 не является корректным ID группы", providedArgs.get(0)));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void handleModsRemoveGroupCommand(Player sender, ArrayList<String> providedArgs) {
        if (providedArgs.size() >= 1) {
            Integer ID;
            if ((ID = this.getIntFromString(providedArgs.get(0))) != null) {
                SuccessReport sr = this.context.alisa.moderatorsHandler.removeModsGroup(ID);
                this.context.alisa.whisper(sender, sr.message);
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: #c2%s#c3 не является корректным ID для группы", providedArgs.get(0)));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void handleModsCreateGroupCommand(Player sender, ArrayList<String> providedArgs) {
        if (providedArgs.size() >= 2) {
            String groupName = providedArgs.get(1).replace("_", " ");
            String prefixColor;
            String nameColor;
            if (providedArgs.size() >= 4) {
                prefixColor = providedArgs.get(2);
                nameColor = providedArgs.get(3);
            } else {
                prefixColor = "f";
                nameColor = "f";
            }

            Integer ID;
            if ((ID = this.getIntFromString(providedArgs.get(0))) != null) {
                SuccessReport sr = this.context.alisa.moderatorsHandler.addModsGroup(groupName, ID, prefixColor, nameColor);
                this.context.alisa.whisper(sender, sr.message);
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: #c2%s#c3 не является корректным ID для группы", providedArgs.get(0)));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void handleModsRemoveCommand(Player sender, ArrayList<String> providedArgs) {
        if (providedArgs.size() >= 1) {
            SuccessReport sr = this.context.alisa.moderatorsHandler.removePlayerFromAllGroups(providedArgs.get(0));
            this.context.alisa.whisper(sender, sr.message);
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void handleModsAddCommand(Player sender, ArrayList<String> providedArgs) {
        if (providedArgs.size() >= 2) {
            Integer ID;
            if ((ID = this.getIntFromString(providedArgs.get(0))) != null) {
                SuccessReport sr = this.context.alisa.moderatorsHandler.addPlayerToGroup(ID, providedArgs.get(1));
                this.context.alisa.whisper(sender, sr.message);
            } else {
                this.context.alisa.whisper(sender, String.format("#crОшибка#c3: #c2%s#c3 не является корректным ID группы", providedArgs.get(0)));
            }
        } else {
            this.context.alisa.whisper(sender, "#crОшибка#c3: недостаточно аргументов к команде");
        }
    }

    private void setConfigPropertyWithReport(String key, Object value, IArgumentChecker requiredArg, Player player) {
        this.setConfigProperty(key, value, requiredArg);
        this.context.alisa.whisper(player, String.format("#cgУспешно#c3: настройка #c2%s#c3 установлена в значение #c2%s", key, value));
    }

    private void setConfigProperty(String key, Object value, IArgumentChecker requiredArg) {
        this.context.config.set(key, value);
    }

    private boolean checkArg(IArgumentChecker requiredArg, ArrayList<String> providedArgs) {
        return requiredArg.check(providedArgs);
    }

    private void registerMCPs() {
        this.registerMCP("tempmute.flood", new CheckInt(1, 9000));
        this.registerMCP("tempmute.symbol-flood", new CheckInt(1, 9000));
        this.registerMCP("tempmute.advertisement", new CheckInt(1, 9000));
        this.registerMCP("tempmute.caps", new CheckInt(1, 9000));
        this.registerMCP("tempmute.profanity", new CheckInt(1, 9000));
        this.registerMCP("tempmute.double-warn", new CheckInt(1, 9000));
        this.registerMCP("cooldown.votesun-global", new CheckInt(1, 90000));
        this.registerMCP("cooldown.votesun-personal", new CheckInt(1, 90000));
        this.registerMCP("cooldown.voteday-global", new CheckInt(1, 90000));
        this.registerMCP("cooldown.voteday-personal", new CheckInt(1, 90000));
        this.registerMCP("success-ratio.votesun", new CheckFloat(1.0F, 90000.0F));
        this.registerMCP("success-ratio.voteday", new CheckFloat(1.0F, 90000.0F));
        this.registerMCP("success-advantage.voteday", new CheckInt(1, 90000));
        this.registerMCP("success-advantage.votesun", new CheckInt(1, 90000));
        this.registerMCP("chat-colors", new CheckStringArrayFixedSize(3));
        this.registerMCP("name1", new CheckString());
        this.registerMCP("name2", new CheckString());
        this.registerMCP("name-color", new CheckString());
        this.registerMCP("prefix", new CheckString());
        this.registerMCP("prefix-color", new CheckString());
        this.registerMCP("silent", new CheckBoolean());
        this.registerMCP("main-world", new CheckString());
        this.registerMCP("debug", new CheckBoolean());
    }

    protected void registerMCP(String propertyKey, IArgumentChecker argumentChecker) {
        this.mcps.put(propertyKey, argumentChecker);
    }

    private Integer getIntFromString(String s) {
        int result;
        try {
            result = Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}