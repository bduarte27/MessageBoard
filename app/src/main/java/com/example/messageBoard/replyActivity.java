package com.example.messageBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class replyActivity extends AppCompatActivity {
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("pos");
        EditText eTR = (EditText) findViewById(R.id.editTextReply);
        eTR.setEnabled(true);
        eTR.setFocusable(true);
    }

    public void returnToMainActivity(View view) {
        Intent intent = getIntent();
        EditText eTR = (EditText) findViewById(R.id.editTextReply);
        intent.putExtra("test", eTR.getText().toString());
        eTR.setText(null);
        eTR.setEnabled(false);
        eTR.setFocusable(false);
        setResult(RESULT_OK, intent);
        finish();
    }
}
