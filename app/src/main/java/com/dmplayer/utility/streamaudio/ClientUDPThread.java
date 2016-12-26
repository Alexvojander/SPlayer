package com.dmplayer.utility.streamaudio;

import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.dmplayer.R;
import com.dmplayer.utility.streamaudio.Observer.SingleObserverContainer;
import com.dmplayer.utility.streamaudio.WifiProfile.WifiProfileObject;
import com.dmplayer.ui.ServerItemAdapter;
import com.dmplayer.utility.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ClientUDPThread extends Thread {
    DatagramSocket datagramSocket;
    ServerSocket serverSocket;
    public ReciveAudioSocket reciveAudioSocket;
    ArrayList<WifiProfileObject> serversList=new ArrayList<>();

    final String ATTRIBUTE_NAME = "text";
    final String ATTRIBUTE_IP = "value";
    final String ATTRIBUTE_IMAGE = "image";

    public ClientUDPThread() {

    }


    public void refresh()
    {
        if(datagramSocket!=null)
        {
            datagramSocket.close();
        }
        if (  reciveAudioSocket != null) {
            reciveAudioSocket.refresh();
            Thread dummy =  reciveAudioSocket;
            reciveAudioSocket = null;
            dummy.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            //Open a random port to send the package
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            String str= Utils.getIPAddress(true);

            byte[] sendData = str.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                datagramSocket.send(sendPacket);
                System.out.println( ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {

            }
            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    try {
                        serverSocket = new ServerSocket(5010);
                        WifiProfileObject clientObject;
                        sendData=null;
                        sendData = "GET_SERVER_OBJECT".getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        //System.out.println(  ">>>CLIENTTTTT111111111111111TTT: " );
                        datagramSocket.send(sendPacket);
                        System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());


                        datagramSocket.setSoTimeout(100);

                        System.out.println(  ">>>CLIENTTTTT222222222222222TTT: " );
                        boolean keepGoing =true;
                        int count=0;
                        int timeouts=0;

                        serverSocket.setSoTimeout(500);
                        while (keepGoing)
                        {

                            try{
                                if(serverSocket==null)
                                serverSocket = new ServerSocket(5010);
                                Socket socket = serverSocket.accept();

                                InputStream in = socket.getInputStream();
                                DataInputStream dis = new DataInputStream(in);

                                OutputStream out = socket.getOutputStream();
                                DataOutputStream dos = new DataOutputStream(out);

                                int len = dis.readInt();
                                dos.writeBoolean(true);
                                byte[] bytes=new byte[len];
                                dis.readFully(bytes,0,len);

                                dos.close();
                                dis.close();
                                in.close();
                                out.close();
                                socket.close();

                                clientObject=WifiProfileObject.deserialize(bytes);

                                serversList.add(clientObject);
                            }
                            catch (SocketTimeoutException ste){
                                Log.d("TIMEOUT", "timeout " + timeouts);
                                timeouts++;
                            }

                            if(timeouts>=10){
                                Log.d("TIMEOUT", "end");
                                keepGoing=false;
                            }
                        }
                        serverSocket.close();

                    } catch (Exception e) {
                        Log.e("LOG_E:",e.toString());
                        System.out.println(e.toString());
                    }
                    System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(  ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //reciveAudioSocket=new ReciveAudioSocket();
            //reciveAudioSocket.start();
            datagramSocket.close();

            SingleObserverContainer.getInstance().setServersList(serversList);

            if(SingleObserverContainer.getInstance().getContext()!=null && SingleObserverContainer.getInstance().getListView()!=null){

                Handler mainHandler = new Handler(SingleObserverContainer.getInstance().getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        List<WifiProfileObject> serversList=SingleObserverContainer.getInstance().getServersList();
                        ListView listView=SingleObserverContainer.getInstance().getListView();
                       // List<String> countries=new ArrayList<>();

                        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(serversList.size());
                        Map<String, Object> m;
                        int img = 0;


                        for (WifiProfileObject serverinfo: serversList ) {
                            m = new HashMap<String, Object>();
                            m.put(ATTRIBUTE_NAME, serverinfo.getName());
                            m.put(ATTRIBUTE_IP, serverinfo.getIp());

                                img = R.drawable.default_server_adapter_icon;
                            m.put(ATTRIBUTE_IMAGE, img);
                            data.add(m);

                            //countries.add(serverinfo.getIp());
                            System.out.println(serverinfo.getName());
                            System.out.println(serverinfo.getIp());
                        }

                        // массив имен атрибутов, из которых будут читаться данные
                        String[] from = { ATTRIBUTE_NAME, ATTRIBUTE_IP,
                                ATTRIBUTE_IMAGE };
                        // массив ID View-компонентов, в которые будут вставлять данные
                        int[] to = { R.id.name_server_item_adapter, R.id.ip_server_item_adapter, R.id.image_server_item_adapter };


                        // создаем адаптер
                        ServerItemAdapter sAdapter = new ServerItemAdapter(listView.getContext(), data,
                                R.layout.server_item_adapter, from, to);

                        // определяем список и присваиваем ему адаптер

                        listView.setAdapter(null);
                        listView.setAdapter(sAdapter);

                        //ArrayAdapter<String> adapter = new ArrayAdapter<>(SingleObserverContainer.getInstance().getContext(),android.R.layout.simple_list_item_1, countries);


                       // listView.setAdapter(null);
                        //listView.setAdapter(new ServerItemAdapter(serversList,SingleObserverContainer.getInstance().getContext() ));
                        // устанавливаем для списка адаптер
                        //listView.setAdapter(adapter);

                    }
                };
                mainHandler.post(myRunnable);
            }
        } catch (IOException ex) {

        }
    }
//    private WifiProfileObject getModel(int position,ListView listView) {
//        return((listView.getAdapter()).getItem(position));
//    }
//    public class ServerItemAdapter  extends ArrayAdapter<WifiProfileObject> {
//
//
//        private LayoutInflater mInflater;
//
//        private ListView listView;
//        public ServerItemAdapter(WifiProfileObject[] list,Context context,ListView listView) {
//            this.listView=listView;
//            super(context, R.layout.server_item_adapter,  list);
//            mInflater = LayoutInflater.from(context);
//        }
//        public View getView(int position, View convertView,
//                            ViewGroup parent) {
//            ViewHolder holder;
//            View row=convertView;
//            if(row==null){
//
//                row = mInflater.inflate(R.layout.server_item_adapter, parent, false);
//                holder = new ViewHolder();
//                holder.imageView = (ImageView) row.findViewById(R.id.image);
//                holder.nameView = (TextView) row.findViewById(R.id.name_server_item_adapter);
//                holder.ipView = (TextView) row.findViewById(R.id.ip_server_item_adapter);
//                row.setTag(holder);
//            }
//            else{
//
//                holder = (ViewHolder)row.getTag();
//            }
//
//            WifiProfileObject wifiProfileObject = getModel(position,listView);
//
//            //holder.imageView.setImageResource((wifiProfileObject.getImageByteArray()));
//            holder.imageView.setImageResource(R.drawable.default_server_adapter_icon);
//            holder.nameView.setText(wifiProfileObject.getName());
//            holder.ipView.setText(wifiProfileObject.getIp());
//
//            return row;
//        }
//
//        class ViewHolder {
//            public ImageView imageView;
//            public TextView nameView, ipView;
//        }
//    }


}