package ru.brightlight.alisa;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandInf implements CommandExecutor {
    private BrightAlisa context;

    public CommandInf(BrightAlisa context) {
        this.context = context;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ++this.context.alisa.statistics.modsAndInfCommands;
        if (args.length >= 1) {
            String paragraph = args[0].toLowerCase().replace(",", ".");
            String message;
            if (this.context.alisa.serverRules.containsKey(paragraph)) {
                message = this.getAnswerFromServerRulesMap(paragraph, this.context.alisa.serverRules);
                this.context.alisa.whisper(sender, message);
                return true;
            } else if (this.context.alisa.serverRulesExtra.containsKey(paragraph)) {
                message = this.getAnswerFromServerRulesMap(paragraph, this.context.alisa.serverRulesExtra);
                this.context.alisa.whisper(sender, message);
                return true;
            } else {
                this.context.alisa.whisper(sender, "#c3Прости, <persik>, но такого пункта правил нет :(");
                return true;
            }
        } else {
            this.context.alisa.whisper(sender, "#c3Похоже, ты забыл ввести пункт правил, <persik> :*");
            return true;
        }
    }

    private String getAnswerFromServerRulesMap(String paragraph, HashMap<String, ServerRule> map) {
        String result = "#cr" + paragraph + "#c3: " + map.get(paragraph).description + "\n";
        if (!map.get(paragraph).punishment.isEmpty()) {
            result = result + "#crНаказание: #c3" + map.get(paragraph).punishment;
        }
        return result;
    }
}