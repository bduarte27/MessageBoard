package com.example.messageBoard;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    Message message;
    TextView item;
    Button number;
    ImageButton replyButton;
    MyAdapter adapter;
    RecyclerView recyclerView2;
    Context context;

    public MyViewHolder(View view, final MyAdapter adapter) {
        super(view);
        context = view.getContext();
        item = (TextView) view.findViewById(R.id.textViewItem);
        number = (Button) view.findViewById(R.id.textViewNumber);
        replyButton = (ImageButton) view.findViewById(R.id.replyButton);
        this.adapter = adapter;

        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerViewReplies);

        number.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int position = getAdapterPosition();
                adapter.incrementMessageUpvotes(position);
            }
        });

        replyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeToReplyActivity(v);
            }
        });
    }

    public void setValues(Message message) {
        this.message = message;
        item.setText(message.message);
        number.setText("" + message.upvotes);
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setAdapter(this.message.replyAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete2(this.message.replyAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView2);
    }


    public void changeToReplyActivity(View view) {
        Intent intent = new Intent(context, replyActivity.class);
        intent.putExtra("pos", getAdapterPosition());
        ((MainActivity)context).startActivityForResult(intent,1);
    }

}

class MyRepliesViewHolder extends RecyclerView.ViewHolder{
    TextView item2;
    Button number2;
    MyRepliesAdapter adapter2;


    public MyRepliesViewHolder(View view, final MyRepliesAdapter adapter2) {
        super(view);
        item2 = (TextView) view.findViewById(R.id.textViewItem2);
        number2 = (Button) view.findViewById(R.id.textViewNumber2);
        this.adapter2 = adapter2;

        number2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int position = getAdapterPosition();
                adapter2.incrementMessageUpvotes(position);
            }
        });

    }
}