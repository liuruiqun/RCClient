package qianyan.rc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ScreenActivity extends Activity{	
	final String FILE_NAME = "screen.png";
	private Button startButton;
	private DatagramSocket socket;
	//private Context context = null; 
	byte[] sendData =  "ACK".getBytes(); 
    byte[] buff = new byte[8192];
	DatagramPacket inPacket = new DatagramPacket(buff , buff.length);
	DatagramPacket outPacket = null; 
//	int [] images = new int [] {
//			R.drawable.windows,
//			R.drawable.screen
//	};
//	private int currentImg = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen);
		startButton = (Button) findViewById(R.id.startButton);
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		startButton.setOnClickListener(new OnClickListener() {
			String s = "Start";
			@Override
			public void onClick(View arg0) {
				sendMessage("screen:message," + s);
			}

		});		
		
	}
	
	private void sendMessage(String str) {
		try {
			InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
			byte data[] = str.getBytes();
			outPacket = new DatagramPacket(data, data.length,
					serverAddress, Settings.scoketnum);
			socket.send(outPacket);
			receivePic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void receivePic() throws IOException {
		InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
		int getLength = 8192;
		byte data[] = "ACK".getBytes();
		DatagramPacket ackPacket = new DatagramPacket(data, data.length,
				serverAddress, Settings.scoketnum - 1);
		
		//添加判断逻辑
                deleteFile("screen.png"); 
                System.out.println("delete old file !\n"); 
         
		
		FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND);
		//System.out.println(getFilesDir());
        BufferedOutputStream bos = new BufferedOutputStream(fos);        
		while(getLength == 8192){
        	//System.out.println("recv ...\n");           
            socket.receive(inPacket);
            //System.out.println(new String (inPacket.getData()));
            //System.out.println("getLength" + inPacket.getLength());
            getLength = inPacket.getLength();
            bos.write(buff, 0, getLength);
            
            //System.out.println("send ACK\n");
            socket.send(ackPacket);
        }   
		bos.close();
        fos.close();
        //System.out.println("recv done!\n");
        
        showView();
        
        //socket.close();  
	}
	
	private void showView() {	
		RelativeLayout screen = (RelativeLayout) findViewById(R.id.screen);
		//final ImageView image = new ImageView(this);
		final ImageView image = (ImageView) findViewById(R.id.image); 
		//screen.addView(image);
		//image.setImageResource(images[0]);

		String fileName = getFilesDir().getPath() + "/screen.png" ; 
		//System.out.println(fileName);
		Bitmap bm = BitmapFactory.decodeFile(fileName); 
		image.setImageBitmap(bm); 
		//System.out.println("show done!\n");		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.keyboardmenu, menu);
		return true;
	}
	
	/**
	 * 捕捉菜单事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mouse:
			toMouse();
			return true;
			
		case R.id.keyboard:
			toKeyboard();
			return true;
						
		
		case R.id.keyboardhelp:
			help();
			return true;
		
		case R.id.reback:
			doBack();
			return true;
		case R.id.exit:
			doExit();
			return true;
		}
		return false;
	}

	private void toMouse(){
		Intent intent = new Intent(ScreenActivity.this,ControlActivity.class);
		 ScreenActivity.this.startActivity(intent);
		 this.finish();
	}
	
	private void toKeyboard(){
		Intent intent = new Intent(ScreenActivity.this,KeyBoardActivity.class);
		 ScreenActivity.this.startActivity(intent);
		 this.finish();
	}
	
	private void help(){
		new AlertDialog.Builder(ScreenActivity.this).setTitle("使用帮助")
		.setMessage("本页面可实时查看电脑画面，截屏按钮点击一次将会返回一次实时图片").setIcon(R.drawable.icon)
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
		 Intent intent = new Intent(ScreenActivity.this,RemoteControlActivity.class);
		 ScreenActivity.this.startActivity(intent);
		 this.finish();
	}
	
	protected void doExit() {
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.exit_message))
				.setPositiveButton(getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(
								DialogInterface dialoginterface, int i) {
								finish();
							}
						})
				.setNeutralButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}

						}).show();

	}
}
