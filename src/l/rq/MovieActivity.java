package l.rq;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import qianyan.rc.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MovieActivity extends Activity{	
	private Button startButton;
	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;
	
	private DatagramSocket socket;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie);
//		startButton = (Button) findViewById(R.id.startbutton);
//		upButton = (Button) findViewById(R.id.volumeupbutton);
//		downButton = (Button) findViewById(R.id.volumedownbutton);
//		leftButton = (Button) findViewById(R.id.backbutton);
//		rightButton = (Button) findViewById(R.id.forbutton);
//		
//		try {
//			socket = new DatagramSocket();
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		upButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				sendMessage("keyboard:key,Up,click");
//			}
//
//		});
//		
//		upButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				sendMessage("keyboard:key,Up,click");
//			}
//
//		});
		
		downButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendMessage("keyboard:key,Down,click");
			}

		});
		
		leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendMessage("keyboard:key,Left,click");
			}

		});
		
		rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendMessage("keyboard:key,Right,click");
			}

		});
	}
	
	private void sendMessage(String str) {
		try {			
			InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
			byte data[] = str.getBytes();
			DatagramPacket packet = new DatagramPacket(
					data, 
					data.length,
					serverAddress, 
					Settings.scoketnum
					);
			socket.send(packet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.moviemenu, menu);
		return true;
	}
	
	/**
	 * 捕捉菜单事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.doshelp:
			help();
			return true;
		
		case R.id.reback:
			doBack();
			return true;
		}
		return false;
	}
	
	private void help(){
		new AlertDialog.Builder(MovieActivity.this).setTitle("使用帮助")
		.setMessage("本页面可进行信息的发送 其中DOS发送是在DOS窗口下 信息的发送   \n使用设置 可设置音量键的操作 以方便你的使用和操作").setIcon(R.drawable.icon)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// finish();
			}
		}).setNegativeButton("返回",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}

				}).show();
	}
	
	private void doBack(){
		 Intent intent = new Intent(MovieActivity.this,ScreenActivity.class);
		 MovieActivity.this.startActivity(intent);
		 this.finish();
	}
	
}
