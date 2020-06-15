package me.zaphreal.hopskip;

import java.util.ArrayList;

public class Player {
    private int balance = 0;
    private int drawableID;
    private final ArrayList<String> unlockedCharacters = new ArrayList<>();

    Player(int drawableID) {
        this.drawableID = drawableID;
        unlockedCharacters.add("Frog");
    }

    public boolean hasCharacter(String character) {
        return unlockedCharacters.contains(character);
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public void unlockCharacter(String character) {
        unlockedCharacters.add(character);
    }

    public void modifyBalanceBy(int change) {
        this.balance += change;
    }

    public int getBalance() {
        return balance;
    }

    public int getDrawableID() {
        return drawableID;
    }
}
