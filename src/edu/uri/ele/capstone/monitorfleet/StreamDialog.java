package edu.uri.ele.capstone.monitorfleet;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class StreamDialog extends Dialog {

	private boolean isSmall = true;
	
	private final int BASE_W = 640;
	private final int BASE_H = 360;
	private final int SMALL_W = 1120;
	private final int SMALL_H = 630;
	
	private Dialog _d;
	private ResizingVideoView _v;
	
	public StreamDialog(final Context context) {
		super(context, R.style.Theme_Dialog_Stream);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.stream);
		
		_v = (ResizingVideoView)findViewById(R.id.stream_vidView);
		
		_v.setDimensions(640, 360);
	}

	public void play(String url){
		final Context _c = this.getContext();
		_d = new Dialog(_c, R.style.Theme_Dialog_Spinner);
		_d.setContentView(R.layout.spinner);
		_d.show();

		_v.setOnPreparedListener(new OnPreparedListener(){
			public void onPrepared(MediaPlayer mp) {
				_d.dismiss();
			}
		});

		_v.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getActionMasked() != MotionEvent.ACTION_DOWN) return false;
				
				int w = isSmall ? BASE_W : SMALL_W;
				int h = isSmall ? BASE_H : SMALL_H;

			    _v.setDimensions(w, h);
			    _v.getHolder().setFixedSize(w, h);
				
			    isSmall = !isSmall;
			    
				return true;
			}

			
		});
		
		_v.setVideoURI(Uri.parse(url));
		_v.setZOrderOnTop(true);
		_v.requestFocus();
		
		_v.start();
	}
}
