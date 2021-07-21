package ru.brightlight.alisa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class mf {
    public static ArrayList<String> readProjectFileLines(String name) {
        ArrayList<String> result = new ArrayList<>();
        File f1 = new File(BrightAlisa.getInstance().getDataFolder(), name);
        if (f1.exists()) {
            return mf.readFileFromDataFolderToArray(name);
        }
        BufferedReader reader = null;
        try {
            String line;
            InputStream in = BrightAlisa.getInstance().getClass().getResourceAsStream("/" + name);
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) result.add(line);
            }
        } catch (Exception e) {
            BrightAlisa.getInstance().log("error reading project file");
            e.printStackTrace();
            return result;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String readFileFromJarToString(String name) throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;
        try {
            String line;
            InputStream in = BrightAlisa.getInstance().getClass().getResourceAsStream("/" + name);
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) result.append(line);
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String readFile(String filename) throws Exception {
        File f1 = new File(BrightAlisa.getInstance().getDataFolder(), filename);
        return f1.exists() ? readFileFromDataFolderToString(filename) : readFileFromJarToString(filename);
    }

    public static boolean removeFile(String filename) {
        File f1 = new File(BrightAlisa.getInstance().getDataFolder(), filename);
        return f1.exists() ? f1.delete() : false;
    }

    public static String readFileFromDataFolderToString(String filename) {
        File f1 = new File(BrightAlisa.getInstance().getDataFolder(), filename);
        String result = null;

        try {
            result = FileUtils.readFileToString(f1, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean fileExistsInDataFolder(String filename) {
        File f1 = new File(BrightAlisa.getInstance().getDataFolder(), filename);
        return f1.exists();
    }

    public static ArrayList<String> readFileFromDataFolderToArray(String filename) {
        ArrayList<String> result = new ArrayList<>();
        BufferedReader reader = null;
        try {
            String line;
            FileInputStream in = new FileInputStream(new File(BrightAlisa.getInstance().getDataFolder(), filename));
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) result.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean arrayListContainsIgnoreCase(ArrayList<String> arr, String s) {
        for (String str : arr) {
            if (str.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public static int getPlayerPlaytime(String name) {
        return BrightAlisa.getInstance().getPlaytime().getDataManager().getDataHandler().getValue("playtime", name);
    }

    public static int getDayOfWeek() {
        Date date = new Date(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(7);
    }
}