package com.dmplayer.utility.streamaudio;

/**
 * Created by Alexvojander on 03.10.2016.
 */


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class ClientStreamService extends Service {

    ClientUDPThread clientUDPThread;
    final String LOG_TAG = "myLogs";
    ExecutorService es;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "ClientStreamService onCreate");
        es = Executors.newFixedThreadPool(1);
    }

    public void onDestroy() {
        stop();
        super.onDestroy();
        Log.d(LOG_TAG, "ClientStreamService onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "ClientStreamService onStartCommand");

        // int time = intent.getIntExtra(MainActivity.PARAM_TIME, 1);
        //int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);

        if (clientUDPThread != null) {
            clientUDPThread.refresh();
            Thread dummy = clientUDPThread;
            clientUDPThread = null;
            dummy.interrupt();
        }
        clientUDPThread = new ClientUDPThread();
        clientUDPThread.start();

       // MyRun mr = new MyRun();
       // es.execute(mr);

       // stop();
        //Log.e("Player3456789", "fdsdhgfhgjhegdhjgkhljk;ljhgfdsdfghjkl;lkjhgfd");
        return super.onStartCommand(intent, flags, startId);
    }
    void stop(){
        if (clientUDPThread != null) {
			clientUDPThread.refresh();
			Thread dummy = clientUDPThread;
			clientUDPThread = null;
			dummy.interrupt();
		}
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {


        int time;
        int startId;
        int task;
       // ProgressBar progressBarClient;

        public MyRun() {
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {

        }

        void stop() {

            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
        }
    }
}