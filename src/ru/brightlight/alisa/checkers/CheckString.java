package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public class CheckString implements IArgumentChecker {

    @Override
    public boolean check(ArrayList<String> args) {
        return true;
    }

    @Override
    public Object getFixedClassedArg(ArrayList<String> args) {
        return args.get(0);
    }
}