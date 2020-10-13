package com.example.messageBoard;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

public class Message {
    String message;
    int upvotes;
    MyRepliesAdapter replyAdapter;
    String hash;

    Message(String message, int upvotes, MyRepliesAdapter replyAdapter, String hash) {
        this.message = message;
        this.upvotes = upvotes;
        this.hash = hash;
        this.replyAdapter = replyAdapter;
    }
}
