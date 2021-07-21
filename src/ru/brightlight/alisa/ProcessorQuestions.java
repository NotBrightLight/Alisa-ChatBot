package ru.brightlight.alisa;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;

public class ProcessorQuestions implements IProcessor {
    private BrightAlisa context;
    private ArrayList<QuestionResponse> questionResponses;

    public ProcessorQuestions(BrightAlisa context) {
        this.context = context;
        this.fillQuestionsResponses();
    }

    public boolean processMessage(Player player, String message) {
        message = message.toLowerCase();
        for (QuestionResponse qr : this.questionResponses) {
            if (!this.questionResponsePassed(qr, message) || !this.questionCooldownExpired(qr)) continue;
            this.answerQuestionResponse(player, qr);
            this.triggerQuestionCooldown(qr);
            return false;
        }
        return false;
    }

    public int getQuestionsCount() {
        return this.questionResponses.size();
    }

    private boolean questionCooldownExpired(QuestionResponse qr) {
        return this.context.alisa.cooldownsHandler.questionsCooldowns.isExpired(this.questionResponses.indexOf(qr));
    }

    private void triggerQuestionCooldown(QuestionResponse qr) {
        this.context.alisa.cooldownsHandler.questionsCooldowns.trigger(this.questionResponses.indexOf(qr));
    }

    private void answerQuestionResponse(Player player, QuestionResponse qr) {
        this.context.alisa.say(String.format("#c2%s#c1, %s", player.getName(), qr.response));
        ++this.context.alisa.statistics.answers;
    }

    private boolean questionResponsePassed(QuestionResponse qr, String message) {
        for (ArrayList<String> block : qr.questions) {
            if (this.questionBlockPassed(block, message)) continue;
            return false;
        }
        return true;
    }

    private boolean questionBlockPassed(ArrayList<String> block, String message) {
        for (String word : block) {
            if (!message.contains(word)) continue;
            return true;
        }
        return false;
    }

    private boolean questionBlockPassedOld(ArrayList<String> block, String message) {
        for (String word : block) {
            if (!message.contains(word)) continue;
            return true;
        }
        return false;
    }

    protected void fillQuestionsResponses() {
        this.questionResponses = new ArrayList<>();
        ArrayList<String> temp = mf.readProjectFileLines("questions.txt");
        ArrayList<Integer> questionIndexes = new ArrayList<>();
        questionIndexes.add(0);

        for (int i = 0; i < temp.size(); ++i) {
            if (temp.get(i).equalsIgnoreCase("***")) {
                questionIndexes.add(i + 1);
            }
        }

        for (Integer index : questionIndexes) {
            QuestionResponse qr = this.getNextQuestion(temp, index);
            if (qr != null) {
                this.questionResponses.add(qr);
            }
        }
    }

    private QuestionResponse getNextQuestion(ArrayList<String> arr, int startingIndex) {
        if (startingIndex >= arr.size()) {
            return null;
        } else {
            QuestionResponse result = new QuestionResponse();
            result.response = arr.get(startingIndex);

            for (int i = startingIndex + 1; i < arr.size() && !arr.get(i).contains("***"); ++i) {
                result.questions.add(new ArrayList<>(Arrays.asList(arr.get(i).split(","))));
            }

            return result;
        }
    }
}