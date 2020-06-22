package me.zaphreal.hopskip;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Player {
    private int balance;
    private int drawableID;
    private String chars;
    private final ArrayList<String> unlockedCharacters;
    private final SharedPreferences prefs;

    Player(SharedPreferences prefs, boolean firstLogin) {
        this.prefs = prefs;
        if (firstLogin) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("balance", 0);
            editor.putString("chars", "Frog");
            editor.putInt("drawableID", R.drawable.frog);
            editor.apply();

            balance = 0;
            drawableID = R.drawable.frog;
            chars = "Frog";
            unlockedCharacters = new ArrayList<>(Collections.singletonList("Frog"));
        } else {
            balance = prefs.getInt("balance", 0);
            drawableID = prefs.getInt("drawableID", R.drawable.frog);
            chars = prefs.getString("chars", "Frog");
            unlockedCharacters = new ArrayList<>(Arrays.asList(chars.split(",")));
        }
        System.out.println("Balance: " + balance + ", drawableID: " + drawableID);
        System.out.println("Characters: " + chars);
        System.out.println("unlockedCharacters: " + unlockedCharacters.toString());
    }

    public boolean hasCharacter(String character) {
        return unlockedCharacters.contains(character);
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("drawableID", drawableID);
        editor.apply();
    }

    public void unlockCharacter(String character) {
        unlockedCharacters.add(character);
        chars += ("," + character);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("chars", chars);
        editor.apply();
    }

    public void modifyBalanceBy(int change) {
        this.balance += change;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("balance", balance);
        editor.apply();
    }

    public int getBalance() {
        return balance;
    }

    public int getDrawableID() {
        return drawableID;
    }
}
