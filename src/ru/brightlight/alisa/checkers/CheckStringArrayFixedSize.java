package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public class CheckStringArrayFixedSize implements IArgumentChecker {
    int size;

    public CheckStringArrayFixedSize(int size) {
        this.size = size;
    }

    @Override
    public boolean check(ArrayList<String> args) {
        return args.size() == this.size;
    }

    @Override
    public Object getFixedClassedArg(ArrayList<String> args) {
        return args;
    }
}