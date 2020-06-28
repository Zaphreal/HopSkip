package me.zaphreal.hopskip;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
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
    private DatabaseReference database;
        //private final SharedPreferences prefs;

    Player(String userID, boolean firstLogin) {
        //this.prefs = prefs;
        database = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(userID));
        if (firstLogin) {
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putInt("balance", 0);
//            editor.putString("chars", "Frog");
//            editor.putInt("drawableID", R.drawable.frog);
//            editor.apply();

            database.child("balance").setValue((int)0);
            database.child("chars").setValue("Frog");
            database.child("drawableID").setValue(R.drawable.frog);
            //storageRef.child("unlockedCharacters").setValue(new ArrayList<>(Collections.singletonList("Frog")));

            balance = 0;
            chars = "Frog";
            drawableID = R.drawable.frog;
            unlockedCharacters = new ArrayList<>(Collections.singletonList("Frog"));
        } else {
//            balance = prefs.getInt("balance", 0);
//            drawableID = prefs.getInt("drawableID", R.drawable.frog);
//            chars = prefs.getString("chars", "Frog");
//            unlockedCharacters = new ArrayList<>(Arrays.asList(chars.split(",")));
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("");
                    System.out.println("THIS IS CALLED");
                    System.out.println("");
                    balance = (long) snapshot.child("balance").getValue();
                    drawableID = Integer.parseInt(String.valueOf(snapshot.child("drawableID").getValue()));
                    chars = (String) snapshot.child("chars").getValue();
                    unlockedCharacters = new ArrayList<>(Arrays.asList(chars.split(",")));

//                    System.out.println("Balance: " + balance + ", drawableID: " + drawableID);
//                    System.out.println("Characters: " + chars);
//                    System.out.println("unlockedCharacters: " + unlockedCharacters.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("CANCELED???? " + error.toException());
                }
            });

        }
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
