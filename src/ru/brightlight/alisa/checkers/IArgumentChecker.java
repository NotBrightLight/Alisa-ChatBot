package ru.brightlight.alisa.checkers;

import java.util.ArrayList;

public interface IArgumentChecker {
    boolean check(ArrayList<String> args);

    Object getFixedClassedArg(ArrayList<String> args);
}
