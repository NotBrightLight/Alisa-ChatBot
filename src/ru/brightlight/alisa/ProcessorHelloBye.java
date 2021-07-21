package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

public class ProcessorHelloBye implements IProcessor {
    private BrightAlisa context;
    private Pattern vsem;
    private ArrayList<String> helloSecondaryWords;
    private ArrayList<String> byeSecondaryWords;

    public boolean processMessage(Player player, String message) {
        if (this.context.alisa.cooldownsHandler.helloByeCooldown.isExpired()) {
            message = message.toLowerCase();
            Matcher m = this.vsem.matcher(message);
            if (message.contains("всем") && !m.find()) {
                if (this.containsWord(message, this.helloSecondaryWords)) {
                    this.context.alisa.sayHello(player);
                    this.context.alisa.cooldownsHandler.helloByeCooldown.trigger();
                    return false;
                }

                if (this.containsWord(message, this.byeSecondaryWords)) {
                    this.context.alisa.sayBye(player);
                    this.context.alisa.cooldownsHandler.helloByeCooldown.trigger();
                    return false;
                }
            }
        }

        return false;
    }

    private boolean containsWord(String message, ArrayList<String> words) {
        for (String s : words) {
            if (!message.contains(s)) continue;
            return true;
        }
        return false;
    }

    public ProcessorHelloBye(BrightAlisa context) {
        this.context = context;
        this.vsem = Pattern.compile("\\Sвсем");
        this.helloSecondaryWords = mf.readProjectFileLines("hello-secondary-words.txt");
        this.byeSecondaryWords = mf.readProjectFileLines("bye-secondary-words.txt");
    }
}