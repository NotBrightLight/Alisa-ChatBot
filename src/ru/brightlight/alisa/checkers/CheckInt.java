package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public class CheckInt implements IArgumentChecker {
    private int rangeMin;
    private int rangeMax;

    public CheckInt(int rangeMin, int rangeMax) {
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    @Override
    public boolean check(ArrayList<String> args) {
        Integer parsed = this.getIntFromString(args.get(0));
        return parsed != null && parsed >= this.rangeMin && parsed <= this.rangeMax;
    }

    @Override
    public Object getFixedClassedArg(ArrayList<String> args) {
        return this.getIntFromString(args.get(0));
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