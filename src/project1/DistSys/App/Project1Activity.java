/*
 * Author :  Ashwin Ramesh Sidhalinganahalli
 * Title : Messenger App
 * Date : 5 FEB 2012
 * 
 */

package project1.DistSys.App;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class Project1Activity extends Activity {
	/** Called when the activity is first created. */

	Button submit,reset;
	TextView display;
	EditText editField;
	String mess;

	//redir add tcp:6200:8001
	public final int S_PORT_NUM= 8001;	//Server port number
	public final int C_PORT_NUM= 6200;	//Client port number

	static ServerSocket serverSocket = null;
	static Socket clientSocket = null; 
	static BufferedReader in = null;
	static PrintWriter out = null;


	String fromOthers=null,toOthers=null;
	int recClosed = 0 ;
	BufferedReader stdIn;
	Message msg1 = null;
	Message msg2 = null;
	Message msg3 = null;

	public Handler OutgoingMsg; 
	public Handler IncomingMsg;
	public Handler MyMsg; 
	TelephonyManager telMgr;
	String tm;
	int isServer = 0; // this is set only if the instance is a Server

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		submit = (Button)findViewById(R.id.bSend);
		reset = (Button)findViewById(R.id.bReset);
		display = (TextView)findViewById(R.id.tTextView);
		editField = (EditText)findViewById(R.id.tEditField);

		this.telMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE); 
		tm = this.telMgr.getLine1Number(); 
		Log.i("AAA"," device id "+tm);

		//If the emulator is running on 5554,then this instance is server
		if(tm.equals("15555215554")){
			isServer=1;
			Log.i("STATUS","This instance is server since port is 5554");
		}
		else{
			Log.i("STATUS","This instance is CLIENT since port is 5556");
		}

		new Thread() {
			@Override
			public void run(){
				try{
					if(isServer==1){
						serverSocket = new ServerSocket(S_PORT_NUM);
						clientSocket = serverSocket.accept();
					}else{
						clientSocket = new Socket("10.0.2.2",C_PORT_NUM);
					}
					out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					//This means that a server is already present, so be a client
					e.printStackTrace();
				}

				//send the ACK to the client
				toOthers = "Connection made...";
				while(out==null);
				out.println(toOthers);


				//This part keeps receiving from other systems
				while (true) {
					try {
						fromOthers=in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					msg1=Message.obtain();
					msg1.obj = fromOthers;
					IncomingMsg.sendMessage(msg1);					
				}
			}
		}.start();

		//Block which sends data once I press the send button
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				editField.setInputType(InputType.TYPE_CLASS_TEXT);
				String mess = editField.getText().toString();
				Log.i("hahaha","message from user" +mess);

				msg2=Message.obtain();
				msg2.obj=mess;
				OutgoingMsg.sendMessage(msg2);
				
				msg3=Message.obtain();
				msg3.obj=mess;
				MyMsg.sendMessage(msg3);
				
				
				editField.setText("");
			}
		});

		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				display.setText(" ");
				
				System.exit(0);
			}
		});

		IncomingMsg = new Handler(){
			@Override
			public void handleMessage(Message msg){
				display.setMovementMethod(new ScrollingMovementMethod());
				String str  = (String)msg.obj;
				if(str.contains("obj="))
					display.append("\nMessage received : " + str.split("obj=")[1].replace('}',' '));
				else
					display.append("\nMessage received : "+str.replace('}',' '));
			}
		};

		//Part which keeps sending from the system
		OutgoingMsg = new Handler(){	
			@Override
			public void handleMessage(Message msg){
				out.println(msg.toString());
			}
		};
		
		//Handler to output my messages onto the screen in UI
		MyMsg = new Handler(){
			@Override
			public void handleMessage(Message msg){
				display.setMovementMethod(new ScrollingMovementMethod());
				String str  = (String)msg.obj;
				if(str.contains("obj="))
					display.append("\nME : " + str.split("obj=")[1].replace('}',' '));
				else
					display.append("\nME : "+str.replace('}',' '));
			}
		};
		
	}
}