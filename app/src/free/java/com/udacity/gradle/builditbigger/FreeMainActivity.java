package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.jokeuilib.JokeActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static com.udacity.gradle.builditbigger.JokeEndpointRetreiverTask.MESSAGE_JOKE_RECEIVED;


public class FreeMainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private InterstitialAd mInterstitialAd;
    private String mJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                showJoke();
            }
        });

        requestNewInterstitial();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {

//

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("LOADING...");
        mProgressDialog.show();
        new JokeEndpointRetreiverTask().execute(new Pair<Handler, String>(handler, "Manfred"));
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_JOKE_RECEIVED) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                mJoke = (String) msg.obj;
                //Error happened in retrieval
                if (mJoke.contains("timed out")) {
                    Toast.makeText(getApplicationContext(),
                            "Error retirieving Joke. Please try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                } else
                    showJoke();

            }


        }

    };

    private void showJoke() {
        Intent i = new Intent(FreeMainActivity.this, JokeActivity.class);
        i.putExtra(JokeActivity.EXTRA_JOKE, mJoke);
        startActivity(i);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("E1AFF5759729456715563D4D0BFE2747")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
