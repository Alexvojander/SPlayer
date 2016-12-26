package com.dmplayer.utility.streamaudio;

import android.util.Log;

import com.dmplayer.models.SongDetail;
import com.dmplayer.utility.streamaudio.Observer.SingleObserverContainer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Alexvojander on 09.09.2016.
 */

public class SendAudioSocket extends Thread {
    String LOG_TAG="SendAudioSocketLOG_TAG:";
    final String clientIPAdress;
    final String port="5005";
    final String pathToFileToStream;
    Socket socket;
    int totalRead;
    SongDetail audioInfo;

    public SendAudioSocket(String clientIPAdress, SongDetail audioInfo) {
        this.clientIPAdress = clientIPAdress;
        this.audioInfo=audioInfo;
        this.pathToFileToStream = audioInfo.getPath();
    }
    public void refresh()  {
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SingleObserverContainer.getInstance().getProgressBarServer().setMax(0);
        SingleObserverContainer.getInstance().getProgressBarServer().setProgress(0);
    }

    @Override
    public void run() {
        try {

            SingleObserverContainer.getInstance().getProgressBarServer().setMax(0);
            SingleObserverContainer.getInstance().getProgressBarServer().setMax((int)new File(pathToFileToStream).length());
            SingleObserverContainer.getInstance().getProgressBarServer().setProgress(0);

            InputStream is =  new FileInputStream(pathToFileToStream);
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] buffer = new byte[32384];
            byte[] buffer2 = new byte[32384];
            int numRead;
            boolean started = false;

            InetAddress address = InetAddress.getByName(clientIPAdress);
            totalRead=0;
            while ((numRead = bis.read(buffer)) != -1) {
                socket = new Socket(address,Integer.parseInt(port));
                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);

                InputStream in=socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);

                label:
                {
                    dos.writeInt((int)new File(pathToFileToStream).length());
                    dis.readBoolean();
                    dos.writeInt(numRead);
                    dis.readBoolean();
                    dos.write(buffer, 0, numRead);

                    int len = dis.readInt();
                    Log.d(LOG_TAG, "len " +len);

                    dos.writeBoolean(false);
                    if(len>32800){
                        Log.d(LOG_TAG, "so big value of socket pack:" +len);
                    }
                    if (len > 0) {
                        dis.readFully(buffer2,0,len);
                    }
                    if(Arrays.equals(buffer,buffer2)){
                        dos.writeBoolean(true);
                        totalRead += numRead;
                        Log.d(LOG_TAG, "totalSend : " + totalRead);
                        SingleObserverContainer.getInstance().getProgressBarServer().setProgress(totalRead);
                        Log.d(LOG_TAG, "file size : " + new File(pathToFileToStream).length());

                        if (totalRead >=new File(pathToFileToStream).length() && !started) {
                            Log.e("Player", "BufferHIT:StartPlayServer");
                            Log.d(LOG_TAG, "file size: " + (int)new File(pathToFileToStream).length());
                            Log.d(LOG_TAG, "recive2 pack: " + buffer.length);
                            Log.d(LOG_TAG, "bytes_count : " + totalRead);
                            totalRead=0;
                        }
                    }
                    else{
                        dos.writeBoolean(false);
                        break label;
                    }
                }
                out.close();
                dos.close();
                in.close();
                dis.close();
            }
            totalRead=0;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
