package l.rq;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import qianyan.rc.R;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectActivity extends Activity {
    EditText ipET;
    EditText socketET;
    Button button;
    //DatagramSocket socket = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ipET = (EditText)findViewById(R.id.IpEditText);
        socketET = (EditText)findViewById(R.id.SocketEditText);
        button = (Button)findViewById(R.id.ConnectButton);
        
        String ipnum = ipET.getText().toString(); 
		int socketnum = Integer.parseInt(socketET.getText().toString());
		Settings.ipnum =ipnum;
		Settings.socketnum = socketnum;
        
        button.setOnClickListener(new OnClickListener() { 
        	@Override 
        	public void onClick(View v) { 
        		
        		try {
        			sendMessage("connect:hello" );
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        	} 
        	
        	private void sendMessage(String str) {
        		try {     
        			DatagramSocket socket =new DatagramSocket();  
        			InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
        			byte data[] = str.getBytes();
        		
        			DatagramPacket outPacket = new DatagramPacket(data, data.length,
        					serverAddress, Settings.socketnum);
        			socket.send(outPacket);	
        			System.out.println(new String (outPacket.getData()));
        			byte[] buffer =  "ACK".getBytes();
        			DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);  
        		    try {  
        		        //设置超时时间,3秒  
        		    	socket.setSoTimeout(3000);  
        		    	socket.receive(inPacket);
        		    	System.out.println(new String (inPacket.getData()));
        		    } catch (Exception e) {  
        		    	Toast.makeText(ConnectActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
        		    	throw e;  
        		    } finally{
        		    	socket.close();
        		    }
        		    Intent intent = new Intent(ConnectActivity.this,ScreenActivity.class);
        			ConnectActivity.this.startActivity(intent);
        			ConnectActivity.this.finish();
        			Toast.makeText(ConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
        		    
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	
        	
        }); 
    }
}