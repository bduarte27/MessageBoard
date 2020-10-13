package com.example.messageBoard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyAdapter extends RecyclerView.Adapter {
    ArrayList<Message> messages;
    private Context context;
    private DatabaseReference myRef;

    public MyAdapter(Context context, DatabaseReference myRef) {
        this.context = context;
        this.myRef = myRef;
        messages = new ArrayList<Message>();
    }

    public void sortAllMessages() {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.upvotes - o1.upvotes;
            }
        });
    }

    public void loadAllMessages() {
        // read a message and create object
        // use getChildren()
        final ArrayList<Message> loadedMessages = new ArrayList<Message>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    int upies1 = Integer.valueOf((String)(d.child("msgUpvotes").getValue()));
                    String hash1 = d.getKey();
                    String message1 = (String)(d.child("msg").getValue());
                    MyRepliesAdapter ra = new MyRepliesAdapter(context, myRef, hash1);
                    Message mm = new Message(message1, upies1, ra, hash1);
                    for (DataSnapshot d2 : d.child("replies").getChildren()) {
                        String h2 = d2.getKey();
                        String reply2 = (String)(d2.child("reply").getValue());
                        int upies2 = Integer.valueOf((String)(d2.child("upsReply").getValue()));
                        mm.replyAdapter.repsAll.add(new replyData(reply2, upies2));
                        mm.replyAdapter.hashes.add(h2);
                    }

                    loadedMessages.add(mm);
                    i++;
                }
                messages.addAll(loadedMessages);
                sortAllMessages();
                for (Message m: messages) {
                    //m.replyAdapter.fillReps();
                    m.replyAdapter.sortAllReplies();
                }
                notifyDataSetChanged();
/*                for (int p=0; p < 10000; p++) {
                }
                for (String s: messageHashes)
                    System.out.println(s);
                for (String s: msgs)
                    System.out.println(s);
                for (int s: messageUpvotes22)
                    System.out.println(s);*/
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
        for (Message m: loadedMessages) {
            System.out.println(m.message);
            System.out.println(m.upvotes);
        }
    }

    public void add(String message) {
        DatabaseReference messageKeyRef = myRef.push();
        messageKeyRef.child("msg").setValue(message);
        messageKeyRef.child("msgUpvotes").setValue(String.valueOf(0));

        String hash = messageKeyRef.getKey();
        MyRepliesAdapter ra = new MyRepliesAdapter(context, myRef, hash);
        messages.add(new Message(message, 0, ra, hash));

        notifyDataSetChanged();
    }

    public void incrementMessageUpvotes(int position) {
        String hash = messages.get(position).hash;
        DatabaseReference messageKeyRef = myRef.child(hash);
        messages.get(position).upvotes++;
        messageKeyRef.child("msgUpvotes").setValue(String.valueOf(messages.get(position).upvotes));
        sortAllMessages();
        notifyDataSetChanged();
    }

    public void addReply(String reply, int position) {
        if (reply.equals("")) {
            return;
        }
        String hash = messages.get(position).hash;
        DatabaseReference messageKeyRef = myRef.child(hash);
        DatabaseReference repliesKeyRef = messageKeyRef.child("replies");

        MyRepliesAdapter ra = messages.get(position).replyAdapter;
        ra.addo(reply, repliesKeyRef);
    }

    public void deleteItem(int position) {
        String hash = messages.get(position).hash;
        DatabaseReference messageKeyRef = myRef.child(hash);
        messageKeyRef.removeValue();

        messages.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent,false);

         MyViewHolder viewHolder = new MyViewHolder(v, this);

         return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder)holder).setValues(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

class replyData {
    String rep;
    int repUps;
    replyData(String rep, int repUps) {
        this.rep = rep;
        this.repUps = repUps;
    }
}

class MyRepliesAdapter extends RecyclerView.Adapter {
    //ArrayList things;
    private Context context;
    //public ArrayList<Integer> upvotesPerReply;
    DatabaseReference myRef;
    String hash;
    ArrayList<String> hashes;
    ArrayList<replyData> repsAll;

    public MyRepliesAdapter(Context context, DatabaseReference myRef, String hash) {
        this.context = context;
        //things = new ArrayList<String>();
        //upvotesPerReply = new ArrayList<Integer>();
        this.myRef = myRef;
        this.hash = hash;
        hashes = new ArrayList<String>();
        repsAll = new ArrayList<replyData>();
    }

    /*public void fillReps() {
        for (int i = 0 ; i < things.size(); i++) {
            replyData rD = new replyData((String)things.get(i), upvotesPerReply.get(i));
            repsAll.add(rD);
        }
    }*/

    public void sortAllReplies() {
        Collections.sort(repsAll, new Comparator<replyData>() {
            @Override
            public int compare(replyData o1, replyData o2) {
                return o2.repUps - o1.repUps;
            }
        });
    }

    public void addo(String message, DatabaseReference repliesKeyRef) {
        DatabaseReference testK = repliesKeyRef.push();
        hashes.add(testK.getKey());
        testK.child("reply").setValue(message);
        testK.child("upsReply").setValue(String.valueOf(0));

//        things.add(message);
//        upvotesPerReply.add(0);
        repsAll.add(new replyData(message, 0));
        notifyDataSetChanged();
    }

    public void incrementMessageUpvotes(int position) {
        repsAll.get(position).repUps = repsAll.get(position).repUps + 1;
        DatabaseReference upReply = myRef.child(hash).child("replies").child(hashes.get(position)).child("upsReply");
        upReply.setValue(String.valueOf(repsAll.get(position).repUps));
        sortAllReplies();
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        // gota delete respective upvote index and reply index
        // gota make hashes -> AND REFACTOR TO BE ALL IN SAME OBJECT reply1hash: {rep: x, up: y}
        DatabaseReference remMe = myRef.child(hash).child("replies").child(hashes.get(position));
        remMe.removeValue();
        hashes.remove(position);

        //things.remove(position);
        //upvotesPerReply.remove(position);
        repsAll.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.replies_view, parent,false);
        MyRepliesViewHolder viewHolder = new MyRepliesViewHolder(v, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyRepliesViewHolder)holder).item2.setText(repsAll.get(position).rep);
        ((MyRepliesViewHolder)holder).number2.setText("" + repsAll.get(position).repUps);
        ((MyRepliesViewHolder)holder).adapter2 = this;
    }

    @Override
    public int getItemCount() {
        return repsAll.size();
    }
}























/*
package com.example.reddit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends RecyclerView.Adapter {
    ArrayList<Message> messages;
    //private ArrayList things;
    private Context context;
    //public ArrayList<Integer> upvotesPerMessage;
    private DatabaseReference myRef;

    //ArrayList<MyRepliesAdapter> replyAdapters;
    //ArrayList<String> msgHashes;

    public MyAdapter(Context context, DatabaseReference myRef) {
        this.context = context;
        things = new ArrayList<String>();
        upvotesPerMessage = new ArrayList<Integer>();
        this.myRef = myRef;
        // Recycler view setup
        replyAdapters = new ArrayList<MyRepliesAdapter>();
        msgHashes = new ArrayList<String>();
    }

    public void add(String message) {
        DatabaseReference messageKeyRef = myRef.push();
        msgHashes.add(messageKeyRef.getKey());
        messageKeyRef.child("msg").setValue(message);
        messageKeyRef.child("msgUpvotes").setValue(0);
        things.add(message);
        upvotesPerMessage.add(0);
        replyAdapters.add(new MyRepliesAdapter(context, replyAdapters.size(), myRef, msgHashes));

        notifyDataSetChanged();
    }

    public void incrementMessageUpvotes(int position) {
        String hash = msgHashes.get(position);
        DatabaseReference messageKeyRef = myRef.child(hash);
        int i = upvotesPerMessage.get(position);
        upvotesPerMessage.set(position, i + 1);
        messageKeyRef.child("msgUpvotes").setValue(upvotesPerMessage.get(position));
        notifyDataSetChanged();
    }

    public void addReply(String reply, int position) {
        String hash = msgHashes.get(position);
        DatabaseReference messageKeyRef = myRef.child(hash);
        DatabaseReference repliesKeyRef = messageKeyRef.child("replies");

        MyRepliesAdapter ra = replyAdapters.get(position);
        ra.addo(reply, repliesKeyRef);
    }

    public void deleteItem(int position) {
        String hash = msgHashes.get(position);
        DatabaseReference messageKeyRef = myRef.child(hash);
        messageKeyRef.removeValue();

        if (things.size() == 0)
            return;
        msgHashes.remove(position);
        things.remove(position);
        upvotesPerMessage.remove(position);
        replyAdapters.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent,false);

         MyViewHolder viewHolder = new MyViewHolder(v, this);

         return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder)holder).item.setText((String)things.get(position));
        ((MyViewHolder)holder).number.setText("" + upvotesPerMessage.get(position));

        ((MyViewHolder)holder).repliesAdapter = replyAdapters.get(position);
        ((MyViewHolder)holder).repliesAdapter.msgNumber = position;
        ((MyViewHolder)holder).recyclerView2.setAdapter(((MyViewHolder) holder).repliesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete2(((MyViewHolder)holder).repliesAdapter));
        itemTouchHelper.attachToRecyclerView(((MyViewHolder)holder).recyclerView2);
        ((MyViewHolder)holder).repliesAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return things.size();
    }
}

class MyRepliesAdapter extends RecyclerView.Adapter {
    private ArrayList things;
    private Context context;
    public ArrayList<Integer> upvotesPerReply;
    int msgNumber;
    DatabaseReference myRef;
    ArrayList<String> msgHashes;

    public MyRepliesAdapter(Context context, int msgNumber, DatabaseReference myRef, ArrayList<String> msgHashes) {
        this.context = context;
        things = new ArrayList<String>();
        upvotesPerReply = new ArrayList<Integer>();
        this.msgNumber = msgNumber;
        this.myRef = myRef;
        this.msgHashes = msgHashes;
    }

    public void addo(String message, DatabaseReference repliesKeyRef) {
        DatabaseReference repsKeyRef = repliesKeyRef.child("reps");
        DatabaseReference upsKeyRef = repliesKeyRef.child("ups");
        DatabaseReference replyKeyRef = repsKeyRef.child(String.valueOf(things.size()));
        DatabaseReference upKeyRef = upsKeyRef.child(String.valueOf(things.size()));
        replyKeyRef.setValue(message);
        upKeyRef.setValue(0);

        things.add(message);
        upvotesPerReply.add(0);
        notifyDataSetChanged();
    }

    public void incrementMessageUpvotes(int position) {
        String hash = msgHashes.get(msgNumber);
        DatabaseReference upKeyRef = myRef.child(hash).child("replies").child("ups").child(String.valueOf(position));
        upKeyRef.setValue(upvotesPerReply.get(position) + 1);

        int i = upvotesPerReply.get(position);
        upvotesPerReply.set(position, i + 1);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        things.remove(position);
        upvotesPerReply.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.replies_view, parent,false);
        MyRepliesViewHolder viewHolder = new MyRepliesViewHolder(v, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyRepliesViewHolder)holder).item2.setText((String)things.get(position));
        ((MyRepliesViewHolder)holder).number2.setText("" + upvotesPerReply.get(position));
        ((MyRepliesViewHolder)holder).adapter2 = this;
    }

    @Override
    public int getItemCount() {
        return things.size();
    }
}
 */