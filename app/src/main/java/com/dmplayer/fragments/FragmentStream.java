/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.dmplayer.R;
import com.dmplayer.manager.MediaController;
import com.dmplayer.utility.streamaudio.ClientStreamService;
import com.dmplayer.utility.streamaudio.Observer.SingleObserverContainer;
import com.dmplayer.utility.streamaudio.ReciveAudioSocket;
import com.dmplayer.utility.streamaudio.SendAudioSocket;
import com.dmplayer.utility.streamaudio.ServerStreamService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentStream extends BaseFragment {

	Button stopStreamButton;
	@BindView(R.id.switchServer)
	Switch switcher;
	@BindView(R.id.connect_to_UDPServer)
	Button connectToServerButton;

	public ReciveAudioSocket reciveAudioSocket;

	SendAudioSocket sendAudioSocket;
	@BindView(R.id.server_progress_bar)
	ProgressBar pbServer;
	@BindView(R.id.client_progress_bar)
	ProgressBar pbClient;
	Handler handler;
	BroadcastReceiver brClient;
	BroadcastReceiver brServer;
	public final static String BROADCAST_ACTION_CLIENT = "com.dmplayer.utility.streamaudio.ClientStreamService";
	public final static String BROADCAST_ACTION_SERVER = "com.dmplayer.utility.streamaudio.ServerStreamService";

	final String LOG_TAG = "myLogs";

	public final static String PARAM_TIME = "time";
	public final static String PARAM_TASK = "task";
	public final static String PARAM_RESULT = "result";
	public final static String PARAM_STATUS = "status";


	public final static int STATUS_START = 100;
	public final static int STATUS_FINISH = 200;

	final int TASK1_CODE = 1;
	final int TASK2_CODE = 2;
	final int TASK3_CODE = 3;

	View view1;
	Intent intentServer;
	Intent intentClient;

	final String ATTRIBUTE_NAME = "text";
	final String ATTRIBUTE_IP = "value";
	final String ATTRIBUTE_IMAGE = "image";


	@Override
	protected int getLayoutId() {
		return R.layout.fragment_stream;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupInitialViews(view);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(brClient);
		getActivity().unregisterReceiver(brServer);
		//sendAudioSocket.refresh();
		super.onDestroy();
	}

	private void setupInitialViews(View view) {


		view1=view;

		switcher.setOnCheckedChangeListener(serverListener);
		connectToServerButton.setOnClickListener(connectToServerListener);
		final ListView listView= ButterKnife.findById(view1, R.id.streamListView);

		//listView.setOnItemClickListener(itemListener);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> obj = (HashMap<String, Object>)listView.getAdapter().getItem(position);
				final String ip_adress = (String) obj.get(ATTRIBUTE_IP);
				//Log.d("Yourtag ", name);
				reciveAudioSocket=new ReciveAudioSocket();
				reciveAudioSocket.start();

				if(MediaController.getInstance().getPlayingSongDetail()!=null){

					new Thread()
					{
						public void run() {
							DatagramSocket datagramSocket = null;
							DatagramPacket sendPacket;
							try {
								datagramSocket = new DatagramSocket();
								datagramSocket.setBroadcast(true);
								byte[]sendData = "START_STREAMING_MUSIC".getBytes();
								sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip_adress), 8888);
								datagramSocket.send(sendPacket);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							datagramSocket.close();
						}
					}.start();

					//sendAudioSocket= new SendAudioSocket(name,MediaController.getInstance().getPlayingSongDetail());
					//sendAudioSocket.start();
				}
			}
		});
		SingleObserverContainer.getInstance().update(pbClient,pbServer);

		SingleObserverContainer.getInstance().setListView(listView);

		SingleObserverContainer.getInstance().setContext(view.getContext());

		brClient = new BroadcastReceiver() {
			// действия при получении сообщений
			public void onReceive(Context context, Intent intent) {
				//int task = intent.getIntExtra(PARAM_TASK, 0);
				//int status = intent.getIntExtra(PARAM_STATUS, 0);
				//Log.d(LOG_TAG, "onReceive: task = " + task + ", status = " + status);

				// Ловим сообщения о старте задач
				//if (status  == STATUS_START) {
				//	switch (task) {

//						case TASK1_CODE:
//							tvTask1.setText("Task1 start");
//							//break;
//						case TASK2_CODE:
//							tvTask2.setText("Task2 start");
//							//break;
//						case TASK3_CODE:
//							//tvTask3.setText("Task3 start");
//							break;
				//	}
				//}

				// Ловим сообщения об окончании задач
				//if (status == STATUS_FINISH) {
				//	int result = intent.getIntExtra(PARAM_RESULT, 0);
				//	switch (task) {

//						case TASK1_CODE:
//							tvTask1.setText("Task1 finish, result = " + result);
//							break;
//						case TASK2_CODE:
//							tvTask2.setText("Task2 finish, result = " + result);
//							break;
//						case TASK3_CODE:
//							tvTask3.setText("Task3 finish, result = " + result);
//							break;
				//	}
				//}
			}
		};
		brServer = new BroadcastReceiver() {
			// действия при получении сообщений
			public void onReceive(Context context, Intent intent) {

			}
		};
		// создаем фильтр для BroadcastReceiver
		IntentFilter intFiltClient = new IntentFilter(BROADCAST_ACTION_CLIENT);
		IntentFilter intFiltServer = new IntentFilter(BROADCAST_ACTION_SERVER);
		// регистрируем (включаем) BroadcastReceiver
		getActivity().registerReceiver(brClient, intFiltClient);
		getActivity().registerReceiver(brServer, intFiltServer);

	}
	private CompoundButton.OnCheckedChangeListener serverListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			if(switcher.isChecked()){

				//sendButton.setEnabled(false);
				switcher.setText("Server ON");

				// Создаем Intent для вызова сервиса,
				// кладем туда параметр времени и код задачи
				intentServer = new Intent(getActivity(), ServerStreamService.class);
				// стартуем сервис
				getActivity().startService(intentServer);
			}
			else{
				switcher.setText("Server OFF");
				getActivity().stopService(intentServer);

			}
		}
	};

	private  View.OnClickListener connectToServerListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// Создаем Intent для вызова сервиса,

			intentClient = new Intent(getActivity(), ClientStreamService.class);
			// стартуем сервис
			getActivity().startService(intentClient);
			//startSendAudioSocket(pbClient);
		}
	};


}
