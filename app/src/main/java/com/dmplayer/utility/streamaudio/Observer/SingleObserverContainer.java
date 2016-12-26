package com.dmplayer.utility.streamaudio.Observer;

import android.content.Context;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dmplayer.utility.streamaudio.WifiProfile.WifiProfileObject;

import java.util.ArrayList;

/**
 * Created by Alexvojander on 05.10.2016.
 */

public class SingleObserverContainer implements Observer {
    private static volatile SingleObserverContainer instance;

    //List<ProgressBar> progressBarList=null;

    ProgressBar progressBarClient=null;
    ProgressBar progressBarServer=null;
    ArrayList<WifiProfileObject> serversList=new ArrayList<>();
    ListView listView;
    Context context;
//    View playPauseView;
//    public void setPlayPauseView(View playPauseView){this.playPauseView=playPauseView;}
//    public View getPlayPauseView(){return this.playPauseView;}
    public  void setContext(Context context){this.context=context;}
    public Context getContext(){return this.context;}
    public void setServersList(ArrayList<WifiProfileObject> serversList){
        this.serversList=null;
        this.serversList=serversList;
    }

    public void setListView(ListView listView){this.listView=listView;}
    public ListView getListView(){return this.listView;}
    public  ArrayList<WifiProfileObject> getServersList(){return this.serversList;}
    private SingleObserverContainer() {}

    public static SingleObserverContainer getInstance() {
        SingleObserverContainer localInstance = instance;
        if (localInstance == null) {
            synchronized (SingleObserverContainer.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SingleObserverContainer();
                }
            }
        }
        return localInstance;
    }

    public void setProgressBarClient(ProgressBar progressBarClient){this.progressBarClient=progressBarClient;}
    public void setProgressBarServer(ProgressBar progressBarServer){this.progressBarServer=progressBarServer;}

    public  ProgressBar getProgressBarClient(){return progressBarClient;}
    public  ProgressBar getProgressBarServer(){return progressBarServer;}
    
    @Override
    public void update(ProgressBar progressBarClient, ProgressBar progressBarServer) {
        // this.progressBarList.add(progressBar);
        this.progressBarClient=progressBarClient;
        this.progressBarServer=progressBarServer;
    }
}
