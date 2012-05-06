package edu.uri.ele.capstone.monitorfleet;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.util.Pair;
import edu.uri.ele.capstone.monitorfleet.util.Vehicle.VehicleType;

public class MFCMapActivity extends MapActivity {

	MapView _mv;
	
	class MapOverlay extends com.google.android.maps.Overlay {
		private final GeoPoint _p;
		private final VehicleType _type;
		
		public MapOverlay(GeoPoint p, VehicleType type){
			super();
			_p = p;
			_type = type;
		}
		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){
			super.draw(canvas, mapView, shadow);
			
			Point screenPt = new Point();
			mapView.getProjection().toPixels(_p, screenPt);
			
			// set the vehicle icon
			Bitmap bmp;
			switch(_type){
				case DRONE:
					bmp = BitmapFactory.decodeResource(getResources(), R.drawable.emo_im_cool);
					break;
				case FLAGSHIP:
					bmp = BitmapFactory.decodeResource(getResources(), R.drawable.emo_im_yelling);
					break;
				case TARGET:
					bmp = BitmapFactory.decodeResource(getResources(), R.drawable.btn_star_big_on);
					break;
				default:
					bmp = BitmapFactory.decodeResource(getResources(), R.drawable.indicator_input_error);
			}
			
			canvas.drawBitmap(bmp, screenPt.x, screenPt.y - bmp.getHeight(), null);
			
			return true;
		}
	}
	
	@Override
	protected void onCreate(Bundle icicle){	
		super.onCreate(icicle);
		setContentView(R.layout.map_activity);
		
		_mv = (MapView)findViewById(R.id.mapview);
		((MonitorFleetController)getParent()).attachMapActivity(this);
	}
	
	/*
	 * Center the map at point p
	 */
	public void setCentered(GeoPoint p){
		MapController mc = _mv.getController();
		
		_mv.getController().animateTo(p);
		
		mc.setZoom(25);
	}
	
	public void markPoint(GeoPoint p, VehicleType type){
		List<Overlay> overlays = _mv.getOverlays();
		overlays.clear();
		
		if(p == null) return;
		
		overlays.add(new MapOverlay(p, type));
		
		_mv.invalidate();
		
		setCentered(p);
	}
	
	/*
	 * Mark a list of vehicles on the map
	 */
	public void markPoints(List<Pair<GeoPoint, VehicleType>> points){
		List<Overlay> overlays = _mv.getOverlays();
		overlays.clear();
		
		if(points == null) return;
		
		Pair<GeoPoint, VehicleType> current = null;
		
		// add each of the map points
		for(int i = 0; i < points.size(); i++){
			current = points.get(i);
			overlays.add(new MapOverlay(current.first, current.second));	
		}
		
		if(current != null){
			setCentered(current.first);
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
