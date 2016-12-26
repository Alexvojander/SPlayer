package com.dmplayer.utility.streamaudio.Observer;

import android.widget.ProgressBar;

/**
 * Created by Alexvojander on 05.10.2016.
 */

interface Observer {


    void update(ProgressBar progressBarClient, ProgressBar progressBarServer);
}
