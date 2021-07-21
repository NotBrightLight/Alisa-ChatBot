package ru.brightlight.alisa;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class MessageHandler {
    protected BrightAlisa context;
    private ArrayList<IProcessor> globalMessageProcessors = new ArrayList<>();
    private ArrayList<IProcessor> localMessageProcessors = new ArrayList<>();
    protected ProcessorQuestions processorQuestions;

    public MessageHandler(BrightAlisa context) {
        this.context = context;
        this.globalMessageProcessors.add(new ProcessorExtra(context));
        this.globalMessageProcessors.add(new ProcessorProfanity(context));
        this.globalMessageProcessors.add(new ProcessorFlood(context));
        this.globalMessageProcessors.add(new ProcessorSymbolFlood(context));
        this.globalMessageProcessors.add(new ProcessorCaps(context));

        try {
            this.processorQuestions = new ProcessorQuestions(context);
            this.globalMessageProcessors.add(this.processorQuestions);
        } catch (Exception var3) {
            var3.printStackTrace();
            context.log("error creating QuestionProcessor, disabling plugin");
            context.disable();
        }

        this.globalMessageProcessors.add(new ProcessorHelloBye(context));
    }

    protected void reloadQuestions() {
        this.processorQuestions.fillQuestionsResponses();
    }

    public void handleMessage(Player player, String msg0) {
        if (this.context.config.getBoolean("debug")) {
            this.context.log("[debug] [chat] " + player.getName() + ": " + msg0);
        }

        boolean isGlobal = this.isGlobalMessage(msg0);
        String message = this.deColor(msg0);
        if (isGlobal) {
            message = message.substring(1);

            for (IProcessor processor : this.globalMessageProcessors) {
                if (processor.processMessage(player, message)) {
                    return;
                }
            }
        }

    }

    private boolean isGlobalMessage(String message) {
        return message.indexOf("!") == 0;
    }

    private String deColor(String msg) {
        return msg.replaceAll("§.", "");
    }
}