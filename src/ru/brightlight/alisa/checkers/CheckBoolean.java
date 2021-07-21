package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public class CheckBoolean implements IArgumentChecker {

    @Override
    public boolean check(ArrayList<String> args) {
        return args.get(0).equalsIgnoreCase("true") || args.get(0).equalsIgnoreCase("false");
    }

    @Override
    public Object getFixedClassedArg(ArrayList<String> args) {
        return this.getBooleanFromString(args.get(0));
    }

    private Boolean getBooleanFromString(String s) {
        if (s.equalsIgnoreCase("true")) {
            return true;
        }
        if (s.equalsIgnoreCase("false")) {
            return false;
        }
        return null;
    }
}