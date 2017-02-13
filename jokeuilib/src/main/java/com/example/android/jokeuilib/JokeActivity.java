package com.example.android.jokeuilib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    public static final String EXTRA_JOKE = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        TextView tv = (TextView) findViewById(R.id.tv_joke);

        String joke = getIntent().getStringExtra(EXTRA_JOKE);
        if (joke == null)
            finish();
        tv.setText(joke);
    }
}
