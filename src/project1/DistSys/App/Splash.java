package project1.DistSys.App;

import android.app.Activity;
import android.content.Intent;
//import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Splash extends Activity{

	MediaPlayer ourSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		//sound lines
		ourSong = MediaPlayer.create(Splash.this, R.raw.hero);
		ourSong.start();
		
		Thread timer = new Thread(){
			@Override
			public void run(){
				try{
					sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent OpenStartingActivity = new Intent("clientServer.ashwin.namespace.ANDCLISERACTIVITY");
					startActivity(OpenStartingActivity);
				}
			}
		};
		timer.start();	
	
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
		ourSong.release();
		finish();
	}
	
	

}
