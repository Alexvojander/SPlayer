package com.dmplayer.utility.streamaudio.WifiProfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Alexvojander on 06.10.2016.
 */

public class WifiProfileObject implements Serializable {

    private String ip;
    private String name;
    private byte[] imageByteArray;

    public  void  setIp(String ip){this.ip=ip;}
    public  void  setName(String name){this.name=name;}
    public  void  setImageByteArray(byte[] imageByteArray){this.imageByteArray=imageByteArray.clone();}

    public  String getIp(){return  ip;}
    public  String getName(){return  name;}
    public  byte[] getImageByteArray(){return imageByteArray;}

    public WifiProfileObject(){}
    public WifiProfileObject(String ip,String name,byte[] imageByteArray){
        this.ip=ip;
        this.name=name;
        this.imageByteArray=imageByteArray;
    }
    public WifiProfileObject(String ip,String name){
        this.ip=ip;
        this.name=name;
        //this.imageByteArray=imageByteArray;
    }
     public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        return out.toByteArray();
    }

    public static byte[] serialize(WifiProfileObject obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static WifiProfileObject deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (WifiProfileObject)is.readObject();
    }
}
