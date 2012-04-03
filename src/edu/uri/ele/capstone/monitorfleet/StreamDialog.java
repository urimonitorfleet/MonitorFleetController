package edu.uri.ele.capstone.monitorfleet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class StreamDialog extends Dialog {

	private StreamDialog _self = null;
	private boolean isFull = false;
	
	private final int BASE_W = 640;
	private final int BASE_H = 360;
	private final int FULL_W = 1120;
	private final int FULL_H = 630;
	
	private Dialog _d;
	private ResizingVideoView _v;
	
	public StreamDialog(final Context context) {
		super(context, R.style.Theme_Dialog_Stream);
		
		_self = this;
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
			@Override
			public void onPrepared(MediaPlayer mp) {
				_d.dismiss();
			}
		});
		_v.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				AlertDialog.Builder b = new AlertDialog.Builder(_c);
				b.setTitle("Cannot play video");
				b.setMessage("Sorry, this video cannot be played.");
				b.setCancelable(false);
				b.setNeutralButton("OK", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						_d.cancel();
						_self.cancel();
					}	
				});
				b.create().show();		
				return true;
			}
		});
		_v.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getActionMasked() != MotionEvent.ACTION_DOWN) return false;
				
				int w = isFull ? BASE_W : FULL_W;
				int h = isFull ? BASE_H : FULL_H;

			    _v.setDimensions(w, h);
			    _v.getHolder().setFixedSize(w, h);
				
			    isFull = !isFull;
			    
				return true;
			}
		});
		
		_v.setVideoPath(url);
		_v.setZOrderOnTop(true);
		_v.requestFocus();
		
		_v.start();
	}
}
