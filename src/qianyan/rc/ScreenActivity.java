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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ScreenActivity extends Activity{	
	private Button startButton;
	private DatagramSocket socket;
	byte[] sendData =  "ACK".getBytes(); 
    byte[] buff = new byte[1024];
    int getLength = 1024;
	DatagramPacket inPacket = new DatagramPacket(buff , buff.length);
	DatagramPacket outPacket = null; 
	int [] images = new int [] {
			R.drawable.windows,
			R.drawable.screen
	};
	private int currentImg = 0;
	
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
			inPacket = new DatagramPacket(data, data.length,
					serverAddress, Settings.scoketnum);
			socket.send(inPacket);
			receivePic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void receivePic() throws IOException {
		FileOutputStream fos = null;
		File file = new File("new.png");    
		if(!file.exists())
		{
			System.out.println("file do not exit\n"); 
			//create file
			if(!file.createNewFile())
				System.out.println("file do not create\n"); 			
		}
			
        fos = new FileOutputStream(file);    
        BufferedOutputStream bos = new BufferedOutputStream(fos);        
		while(getLength == 1024){
        	System.out.println("recv ...\n");           
            socket.receive(inPacket);
            System.out.println(new String (inPacket.getData()));
            System.out.println("getLength" + inPacket.getLength());
            getLength = inPacket.getLength();
            bos.write(buff, 0, getLength);
            
            //System.out.println("send ACK\n");
            //sendMessage("screen:message," + "ACK");
        }   
		bos.close();
        fos.close();
        System.out.println("recv done!\n");
        //socket.close();  
	}
	
	private void showView() {	
		RelativeLayout screen = (RelativeLayout) findViewById(R.id.screen);
//		TextView screen = (TextView) findViewById(R.id.screen);
//		LayoutParams params = new LayoutParams(null
//				);
//		params.gravity = Gravity.CENTER;
//		keyboard.setLayoutParams(params);
		final ImageView image = new ImageView(this);
		screen.addView(image);
		image.setImageResource(images[0]);
		image.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				image.setImageResource(images[++currentImg % images.length]);
			}
		});
	}
}
