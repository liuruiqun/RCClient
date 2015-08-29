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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DOSActivity extends Activity {
	private EditText inputET;
	private Button dosButton;
	private String volumedownkey =  "leftButton";
	private String volumeupkey =  "leftButton";
	int [] images = new int [] {
			R.drawable.windows,
			R.drawable.screen
	};
	int currentImg = 0;
	private DatagramSocket socket;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dos);
		inputET = (EditText) findViewById(R.id.InputEditText);
		dosButton = (Button) findViewById(R.id.dosButton);
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		dosButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String s = inputET.getText().toString();
				if (s == null || s.equals("")) {
					Toast.makeText(DOSActivity.this, "信息为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				sendMessage("keyboard:dosmessage," + s);
        
			}

		});		
	}

	/*
	 * public void onclick(View v){ switch(v.getId()){ case R.id.UP:
	 * 
	 * break;
	 * 
	 * case R.id.DOWN: if(isUSLR) sendMessage("keyboard:key,Down"); else
	 * sendMessage("keyboard:key,S"); break;
	 * 
	 * case R.id.LEFT: if(isUSLR) sendMessage("keyboard:key,Left"); else
	 * sendMessage("keyboard:key,A"); break; case R.id.RIGHT: if(isUSLR)
	 * sendMessage("keyboard:key,Right"); else sendMessage("keyboard:key,D");
	 * break; }
	 * 
	 * }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if(volumedownkey.equals("leftButton"))
				sendMessage("leftButton:down");
			else if(volumedownkey.equals("rightButton"))
				sendMessage("rightButton:down");
			else
				sendMessage("keyboard:key,"+volumedownkey+",down");
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if(volumeupkey.equals("leftButton"))
				sendMessage("leftButton:down");
			else if(volumeupkey.equals("rightButton"))
				sendMessage("rightButton:down");
			else
				sendMessage("keyboard:key,"+volumeupkey+",down");
			return true;

		} else if( keyCode== KeyEvent.KEYCODE_HOME){
			return true;
		} else if( keyCode== KeyEvent.KEYCODE_BACK){
			return true;
		} 
		
		return super.onKeyDown(keyCode, event);

	}
	
	
	 @Override  
	 public void onAttachedToWindow() {  
	        
	     this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
	        
	     super.onAttachedToWindow();  
	 } 
	
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if(volumedownkey.equals("leftButton"))
				sendMessage("leftButton:release");
			else if(volumedownkey.equals("rightButton"))
				sendMessage("rightButton:release");
			else
				sendMessage("keyboard:key,"+volumedownkey+",up");
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if(volumeupkey.equals("leftButton"))
				sendMessage("leftButton:release");
			else if(volumeupkey.equals("rightButton"))
				sendMessage("rightButton:release");
			else
				sendMessage("keyboard:key,"+volumeupkey+",up");
			return true;

		} 
		
		return super.onKeyUp(keyCode, event);
	}

	private void sendMessage(String str) {
		try {
			// 首先创建一个DatagramSocket对象
			
			// 创建一个InetAddree
			InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
			byte data[] = str.getBytes();
			// 创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
			DatagramPacket packet = new DatagramPacket(data, data.length,
					serverAddress, Settings.scoketnum);
			// 调用socket对象的send方法，发送数据
			socket.send(packet);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.dosmenu, menu);
		return true;
	}
	
	/**
	 * 捕捉菜单事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			help();
			return true;
		
		case R.id.reback:
			doBack();
			return true;
		}
		return false;
	}
	
	private void help(){
		new AlertDialog.Builder(DOSActivity.this).setTitle("使用帮助")
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
		 Intent intent = new Intent(DOSActivity.this,ScreenActivity.class);
		 DOSActivity.this.startActivity(intent);
		 this.finish();
	}
	
	
}
