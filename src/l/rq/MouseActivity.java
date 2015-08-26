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
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MouseActivity extends Activity {
	private static float mx = 0; // 发送的鼠标移动的差值
	private static float my = 0;
	private static float lx; // 记录上次鼠标的位置
	private static float ly;
	private static float fx; // 手指第一次接触屏幕时的坐标
	private static float fy;
	private static float lbx = 0; // 鼠标左键移动初始化坐标
	private static float lby = 0;
	Handler handler = new Handler();
	Runnable leftButtonDown;
	Runnable leftButtonRealease;
	Runnable rightButtonDown;
	Runnable rightButtonRealease;

	private FrameLayout leftButton;
	private FrameLayout rightButton;
	
	private String volumedownkey =  "leftButton";
	private String volumeupkey =  "leftButton";

	private boolean flag= true;//屏蔽home键
	private DatagramSocket socket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initTouch();
	}
	
	@Override  
	public void onAttachedToWindow() {  
	    if(flag) {  
	        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
	    }  
	    super.onAttachedToWindow();  
	} 

	private void initTouch() {
		FrameLayout touch = (FrameLayout) this.findViewById(R.id.touch);

		// let's set up a touch listener
		touch.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_MOVE)
					onMouseMove(ev);
				if (ev.getAction() == MotionEvent.ACTION_DOWN)
					onMouseDown(ev);
				if (ev.getAction() == MotionEvent.ACTION_UP)
					onMouseUp(ev);
				return true;
			}
		});

		leftButton = (FrameLayout) this.findViewById(R.id.leftButton);
		leftButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_DOWN) {
					onLeftButton("down");
					handler.post(leftButtonDown);
				}
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					onLeftButton("release");
					lbx = 0;
					lby = 0;
					handler.post(leftButtonRealease);
				}
				if (ev.getAction() == MotionEvent.ACTION_MOVE)
					moveMouseWithSecondFinger(ev);
				return true;
			}
		});

		rightButton = (FrameLayout) this.findViewById(R.id.rightButton);
		rightButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_DOWN) {
					onRightButton("down");
					handler.post(rightButtonDown);
				}
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					onRightButton("release");
					handler.post(rightButtonRealease);
				}
				return true;
			}
		});

		FrameLayout middleButton = (FrameLayout) this
				.findViewById(R.id.middleButton);
		middleButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_DOWN)
					onMiddleButtonDown(ev);
				if (ev.getAction() == MotionEvent.ACTION_MOVE)
					onMiddleButtonMove(ev);
				return true;
			}

		});

		this.leftButtonDown = new Runnable() {
			@Override
			public void run() {
				drawLeftButtonDown(leftButton);
			}

			private void drawLeftButtonDown(FrameLayout fl) {
				fl.setBackgroundResource(R.drawable.zuoc);
			}
		};

		this.rightButtonDown = new Runnable() {
			@Override
			public void run() {
				drawButtonDown(rightButton);
			}

			private void drawButtonDown(FrameLayout fl) {
				fl.setBackgroundResource(R.drawable.youc);
			}
		};

		this.leftButtonRealease = new Runnable() {
			@Override
			public void run() {
				drawLeftButtonRealease(leftButton);
			}

			private void drawLeftButtonRealease(FrameLayout fl) {
				fl.setBackgroundResource(R.drawable.zuo);

			}
		};

		this.rightButtonRealease = new Runnable() {
			@Override
			public void run() {
				drawButtonRealease(rightButton);
			}

			private void drawButtonRealease(FrameLayout fl) {
				fl.setBackgroundResource(R.drawable.you);

			}
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.mousemenu, menu);
		return true;
	}

	/**
	 * 捕捉菜单事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			about();
			return true;
		case R.id.keyboard:
			keyboard();
			return true;
		case R.id.reback:
			doBack();
			return true;
		}
		return false;
	}

	private void moveMouseWithSecondFinger(MotionEvent event) {
		int count = event.getPointerCount();
		if (count == 2) {
			if (lbx == 0 && lby == 0) {
				lbx = event.getX(1);
				lby = event.getY(1);
				return;
			}
			float x = event.getX(1);
			float y = event.getY(1);
			sendMouseEvent("mouse", x - lbx, y - lby);
			lbx = x;
			lby = y;
		}
		if (count == 1) {
			lbx = 0;
			lby = 0;
		}

	}

	private void onMouseDown(MotionEvent ev) {
		lx = ev.getX(); // 当手机第一放入时 把当前坐标付给lx
		ly = ev.getY();
		fx = ev.getX();
		fy = ev.getY();
	}

	private void onMouseMove(MotionEvent ev) {
		float x = ev.getX();
		mx = x - lx; // 当前鼠标位置 - 上次鼠标的位置
		lx = x; // 把当前鼠标的位置付给lx 以备下次使用
		float y = ev.getY();
		my = y - ly;
		ly = y;
		if (mx != 0 && my != 0)
			this.sendMouseEvent("mouse", mx, my);

	}

	private void onMouseUp(MotionEvent ev) {
		if (fx == ev.getX() && fy == ev.getY()) {
			sendMessage("leftButton:down");
			sendMessage("leftButton:release");
		}

	}

	private void sendMouseEvent(String type, float x, float y) {
		String str = type + ":" + x + "," + y;
		sendMessage(str);
	}

	private void onLeftButton(String type) {
		String str = "leftButton" + ":" + type;
		sendMessage(str);

	}

	private void onRightButton(String type) {
		String str = "rightButton" + ":" + type;
		sendMessage(str);
	}

	private void sendMessage(String str) {
		try {
			// 首先创建一个DatagramSocket对象
			
			// 创建一个InetAddree
			InetAddress serverAddress = InetAddress.getByName(Settings.ipnum);
			byte data[] = str.getBytes();
			// 创建一个DatagramPacket对象，并指定对方地址，以及端口号
			DatagramPacket packet = new DatagramPacket(
					data, 
					data.length,
					serverAddress, 
					Settings.scoketnum);
			// 调用socket对象的send方法，发送数据
			socket.send(packet);
			//System.out.println("send");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onMiddleButtonDown(MotionEvent ev) {
		ly = ev.getY();

	}

	private void onMiddleButtonMove(MotionEvent ev) {
		// count++;

		float y = ev.getY();
		my = y - ly;
		ly = y;
		if (my > 3 || my < -3) { // 减少发送次数 滑轮移动慢点
			String str = "mousewheel" + ":" + my;
			sendMessage(str);
		}

	}

	/**
	 * 显示关于我们
	 */
	public void about() {
		new AlertDialog.Builder(MouseActivity.this)
				.setTitle("关于我们")
				.setMessage("欢迎使用 远程助手  \n作者：刘锐群")
				.setIcon(R.drawable.icon)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// finish();
					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				}).show();
	}

	public void keyboard() {
		/*
		 * // EditText et = (EditText) this.findViewById(R.id.editText); //
		 * 获得控制键盘的类的对象 InputMethodManager imm = (InputMethodManager)
		 * ControlActivity.this .getSystemService(INPUT_METHOD_SERVICE);
		 * 
		 * // 打开（自动控制的再次点击按钮就会消失的） imm.toggleSoftInput(0,
		 * InputMethodManager.HIDE_NOT_ALWAYS);
		 */
		Intent intent = new Intent(this, DOSActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
	public void hand(){
		Intent intent = new Intent(this, HandActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if(volumedownkey.equals("leftButton")){
				sendMessage("leftButton:click");
				
			} else if(volumedownkey.equals("rightButton"))
				sendMessage("rightButton:click");
			else
				sendMessage("keyboard:key,"+volumedownkey+",click");
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if(volumeupkey.equals("leftButton"))
				sendMessage("leftButton:click");
			else if(volumeupkey.equals("rightButton"))
				sendMessage("rightButton:click");
			else
				sendMessage("keyboard:key,"+volumeupkey+",click");
			return true;

		}else if( keyCode== KeyEvent.KEYCODE_HOME){
			return true;
		} else if( keyCode== KeyEvent.KEYCODE_BACK){
			return true;
		} 
		
		else {

			return super.onKeyDown(keyCode, event);

		}

	}
	
	private void doBack(){
		 Intent intent = new Intent(MouseActivity.this,ScreenActivity.class);
		 MouseActivity.this.startActivity(intent);
		 this.finish();
	}

}
