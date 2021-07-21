package ru.brightlight.alisa;

public class ServerRule {
    String par;
    String description;
    String punishment;

    public ServerRule(String paragraph, String description, String punishment) {
        this.par = paragraph;
        this.description = description;
        this.punishment = punishment;
    }
}
