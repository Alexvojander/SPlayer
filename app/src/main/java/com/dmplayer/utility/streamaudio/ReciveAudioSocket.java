package com.dmplayer.utility.streamaudio;

import android.media.MediaPlayer;
import android.util.Log;

import com.dmplayer.manager.MediaController;
import com.dmplayer.utility.streamaudio.Observer.SingleObserverContainer;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ReciveAudioSocket extends Thread {
    String LOG_TAG="RecAudioSocketLOG_TAG:";
    public java.net.ServerSocket serverSocket;
    static final int SocketServerPORT = 5005;
    public MediaPlayer mediaPlayer=new MediaPlayer();

    int totalRead;
    public ReciveAudioSocket(){    }

    public void refresh()
    {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        if(serverSocket!=null)
        {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SingleObserverContainer.getInstance().getProgressBarClient().setProgress(0);
        SingleObserverContainer.getInstance().getProgressBarClient().setMax(0);

    }
    @Override
    public void run() {
        Boolean bool=false;

        try {
            serverSocket = new ServerSocket(SocketServerPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[32384];
        try {
            int maxValueForProgressBar=0;
            SingleObserverContainer.getInstance().getProgressBarClient().setProgress(0);
            SingleObserverContainer.getInstance().getProgressBarClient().setMax(0);

            File bufferFile = File.createTempFile("test", "mp3");
            BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));

            while(true)
            {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);

                label:{
                    maxValueForProgressBar=dis.readInt();
                    dos.writeBoolean(false);
                    SingleObserverContainer.getInstance().getProgressBarClient().setMax(maxValueForProgressBar);
                    int len = dis.readInt();
                    dos.writeBoolean(false);
                    if(len>32800){
                        Log.d(LOG_TAG, "so big value of socket pack: " +len);
                        continue  ;
                    }
                    Log.d(LOG_TAG, "len " +len);
                    try {
                        if (len > 0) {
                            dis.readFully(buffer,0,len);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue ;
                    }

                    dos.writeInt(len);
                    dis.readBoolean();
                    dos.write(buffer, 0, len);
                    bool=dis.readBoolean();

                    if(bool==true){
                        bufferOS.write(buffer, 0,len);
                        bufferOS.flush();
                        totalRead+=len;

                        Log.d(LOG_TAG, "totalRecive : " + totalRead);
                        SingleObserverContainer.getInstance().getProgressBarClient().setProgress(totalRead);
                        if (len<32384 ) {
                            Log.e("Player", "BufferHIT:StartPlayClient");
                            Log.d(LOG_TAG, "file size: " + maxValueForProgressBar);
                            Log.d(LOG_TAG, "totalRecive : " + totalRead);
                            mediaPlayer=new MediaPlayer();

                            bool=false;
                            setSourceAndStartPlay(mediaPlayer,bufferFile, totalRead);
                            totalRead=0;
                            dos.close();
                            dis.close();
                            in.close();
                            out.close();

                            bufferFile = File.createTempFile("test", "mp3");
                            bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));
                        }
                    }
                    else{
                        break label;
                    }
                }
                dos.close();
                dis.close();
                in.close();
                out.close();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void setSourceAndStartPlay(MediaPlayer mediaPlayer,File bufferFile, long size) {
        try {

//            mPlayerPosition = videoView.getCurrentPosition();
//            videoView.setVideoPath(bufferFile.getAbsolutePath());

            FileInputStream fis = new FileInputStream(bufferFile);
            FileDescriptor fileDescriptor = fis.getFD();
            mediaPlayer.setDataSource(fileDescriptor, 0, size);

           // MediaController.getInstance().playNextSongFromStream(true,mediaPlayer);
//            if (!MediaController.getInstance().isPlayingAudio(MediaController.getInstance().getPlayingSongDetail())) {
//
//                MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//
//                //((PlayPauseView) SingleObserverContainer.getInstance().getPlayPauseView()).Pause();
//            }

            MediaController.getInstance().playAudioFromStream(mediaPlayer);

       //     mediaPlayer.prepare();
         //   mediaPlayer.start();


//            videoView.start();

        } catch (Exception e) {
           // e.printStackTrace();
        }
    }
}
