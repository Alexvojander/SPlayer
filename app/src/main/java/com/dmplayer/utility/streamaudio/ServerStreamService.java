package com.dmplayer.utility.streamaudio;

/**
 * Created by Alexvojander on 03.10.2016.
 */


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ServerStreamService extends Service {

    MyRun mr;
    final String LOG_TAG = "myLogs";
    ExecutorService es;
    ServerUDPThread serverUDPThread=null;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "ServerStreamService onCreate");
        es = Executors.newFixedThreadPool(1);
    }

    public void onDestroy() {

        //mr.stop();
        stop();
        super.onDestroy();
        Log.d(LOG_TAG, "ServerStreamService onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "ServerStreamService onStartCommand");

        // int time = intent.getIntExtra(MainActivity.PARAM_TIME, 1);
        //int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);

        Log.d(LOG_TAG, "serverUDPThread started");
        if (serverUDPThread != null) {
            serverUDPThread.refresh();
            serverUDPThread.sockett.close();
            Thread dummy = serverUDPThread;
            serverUDPThread = null;
            dummy.interrupt();
        }
        serverUDPThread = new ServerUDPThread(getBaseContext());
        serverUDPThread.start();

//          mr = new MyRun();
//         es.execute(mr);

        return super.onStartCommand(intent, flags, startId);
    }
    void stop() {
            if (serverUDPThread != null) {
                if(serverUDPThread.sockett!=null){serverUDPThread.sockett.close();}
                Thread dummy = serverUDPThread;
                serverUDPThread = null;
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

        public MyRun() {

            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {
//            Log.d(LOG_TAG, "serverUDPThread started");
//            if (serverUDPThread != null) {
//                serverUDPThread.refresh();
//                serverUDPThread.sockett.close();
//                Thread dummy = serverUDPThread;
//                serverUDPThread = null;
//                dummy.interrupt();
//            }
//            serverUDPThread = new ServerUDPThread();
//            serverUDPThread.start();

//
            //stop();
        }

        void stop() {
//            if (serverUDPThread != null) {
//                if(serverUDPThread.sockett!=null){serverUDPThread.sockett.close();}
//                Thread dummy = serverUDPThread;
//                serverUDPThread = null;
//                dummy.interrupt();
//            }
//            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
//                    + startId + ") = " + stopSelfResult(startId));
        }
    }
}