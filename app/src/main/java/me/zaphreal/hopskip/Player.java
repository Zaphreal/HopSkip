package me.zaphreal.hopskip;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Player {
    private long balance;
    private int drawableID;
    private String chars;
    private ArrayList<String> unlockedCharacters;
    private final DatabaseReference database;

    Player(String userID) {
        database = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(userID));

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("chars").getValue() == null) {
                    System.out.println("Chars for this account is null, new account detected!");
                    database.child("balance").setValue((int)0);
                    database.child("chars").setValue("Frog");
                    database.child("drawableID").setValue(R.drawable.frog);
                    //storageRef.child("unlockedCharacters").setValue(new ArrayList<>(Collections.singletonList("Frog")));

                    balance = 0;
                    chars = "Frog";
                    drawableID = R.drawable.frog;
                    unlockedCharacters = new ArrayList<>(Collections.singletonList("Frog"));
                } else {
                    System.out.println("Chars not null, existing account detected!");
                    balance = (long) snapshot.child("balance").getValue();
                    drawableID = Integer.parseInt(String.valueOf(snapshot.child("drawableID").getValue()));
                    chars = (String) snapshot.child("chars").getValue();
                    unlockedCharacters = new ArrayList<>(Arrays.asList(chars.split(",")));

//                    System.out.println("Balance: " + balance + ", drawableID: " + drawableID);
//                    System.out.println("Characters: " + chars);
//                    System.out.println("unlockedCharacters: " + unlockedCharacters.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("CANCELED???? " + error.toException());
            }
        });
    }

    public boolean hasCharacter(String character) {
        return unlockedCharacters.contains(character);
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("drawableID", drawableID);
//        editor.apply();
        database.child("drawableID").setValue(drawableID);
    }

    public void unlockCharacter(String character) {
        unlockedCharacters.add(character);
        chars += ("," + character);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("chars", chars);
//        editor.apply();
        database.child("chars").setValue(chars);
    }

    public void modifyBalanceBy(int change) {
        this.balance += change;
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("balance", balance);
//        editor.apply();
        database.child("balance").setValue(balance);
    }

    public long getBalance() {
        return balance;
    }

    public int getDrawableID() {
        return drawableID;
    }
}
