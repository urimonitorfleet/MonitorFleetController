package edu.uri.ele.capstone.monitorfleet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ResizingVideoView extends VideoView {

	private int _forceW = 0;
	private int _forceH = 0;
	
	public ResizingVideoView(Context context) {
		super(context);
	}
	
	public ResizingVideoView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public ResizingVideoView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public void setDimensions(int w, int h){
		this._forceW = w;
		this._forceH = h;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		setMeasuredDimension(_forceW, _forceH);
	}
}
