package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public class CheckFloat implements IArgumentChecker {
    private float rangeMin;
    private float rangeMax;

    public CheckFloat(float rangeMin, float rangeMax) {
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    @Override
    public boolean check(ArrayList<String> args) {
        Float parsed = this.getFloatFromString(args.get(0));
        return parsed != null && !(parsed < this.rangeMin) && !(parsed > this.rangeMax);
    }

    @Override
    public Object getFixedClassedArg(ArrayList<String> args) {
        return this.getFloatFromString(args.get(0));
    }

    private Float getFloatFromString(String s) {
        float result;
        try {
            result = Float.parseFloat(s);
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}