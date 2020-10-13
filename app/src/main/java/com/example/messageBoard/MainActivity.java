package com.example.messageBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewItems);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(this, myRef);
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete((MyAdapter)myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // now do load  -> then do sort
        // Needs:
        // get messages+upvotes, replies+ups
        //   load message then its replies
        ((MyAdapter) myAdapter).loadAllMessages();

    }

    public void add (View view) {
        EditText item = (EditText)findViewById(R.id.editTextItem);
        if ((item.getText().toString()).equals("")) {
            item.setText("");
            return;
        }
        ((MyAdapter) myAdapter).add(item.getText().toString());

        item.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                EditText item = (EditText)findViewById(R.id.editTextItem);
                item.setText(null);

                String result = data.getStringExtra("test");
                int position = data.getIntExtra("pos", -1);
                ((MyAdapter) myAdapter).addReply(result, position);

                item.setEnabled(false);
                item.setFocusable(false);
                item.setFocusableInTouchMode(false);

                item.setEnabled(true);
                item.setFocusable(true);
                item.setFocusableInTouchMode(true);
            }
        }
    }

}
