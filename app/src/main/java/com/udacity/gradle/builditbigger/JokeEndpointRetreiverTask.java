package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Pair;

import com.example.admin.myapplication.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by Pedram on 2/12/2017.
 */

public class JokeEndpointRetreiverTask extends AsyncTask<Pair<Handler, String>, Void, String> {

    public static final int MESSAGE_JOKE_RECEIVED = 100;

    private static MyApi myApiService = null;
    private Handler handler;

    @Override
    protected String doInBackground(Pair<Handler, String>... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        handler = params[0].first;
        String name = params[0].second;

        try {
            return myApiService.getJoke(name).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        handler.obtainMessage(MESSAGE_JOKE_RECEIVED,result).sendToTarget();
    }
}