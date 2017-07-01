package com.wso2.glass.picavi.picaviglass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wso2.glass.picavi.picaviglass.constants.Constants;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        TextView messageView = (TextView) findViewById(R.id.textView);
        String message = getIntent().getStringExtra(Constants.MESSAGE);
        messageView.setText(message);
    }
}
