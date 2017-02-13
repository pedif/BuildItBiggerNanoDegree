package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.example.android.jokeuilib.*;
import com.example.android.jokeuilib.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.udacity.gradle.builditbigger.JokeEndpointRetreiverTask.MESSAGE_JOKE_RECEIVED;
import static org.junit.Assert.*;

/**
 * Created by Pedram on 2/13/2017.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class JokeEndpointRetreiverTaskTest {

    @Test
    public void testJokeTask() throws InterruptedException {

        /**
         * Lock object to prevent the test to be done before we handle a message from joke task
         */
        final Object syncObject = new Object();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_JOKE_RECEIVED) {
                    /*
                        We get either a joke or a timed out message from joke task
                        therefore if the message does not contain a time out substring
                        the joke was successfully retrieved
                     */
                    assertNotEquals(((String) msg.obj).contains("timed out"), true);

                    /*
                        Release the thread
                     */
                    synchronized (syncObject) {
                        syncObject.notify();
                    }

                }
            }
        };

        new JokeEndpointRetreiverTask().execute(new Pair<Handler, String>(handler, ""));

        /*
            Wait for Handler
         */
        synchronized (syncObject) {
            syncObject.wait();
        }

    }

}