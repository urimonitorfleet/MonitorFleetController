package edu.uri.ele.capstone.monitorfleet;

import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.uri.ele.capstone.monitorfleet.DataFragment.OnVehicleSelectionChangedListener;
import edu.uri.ele.capstone.monitorfleet.util.Vehicle;
import edu.uri.ele.capstone.monitorfleet.util.Vehicle.VehicleType;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonitorFleetController extends FragmentActivity implements OnVehicleSelectionChangedListener {
	private boolean _created = false;
	private MFCMapActivity _map = null;
	
	private static final String VehicleIpAddresses[] = { "131.128.53.250", "192.168.1.4" };
	private List<Vehicle> _vehicles;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ActionBar bar = getActionBar();
        
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);  
		
		_created = true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
    	switch(menu.getItemId()){
    		case R.id.menu_video:
    			//launchVideo();
    			break;
    		default:
    			return false;
    	}
    	
    	return true;
    }

	public void onVehicleSelectionChanged(GeoPoint p) {
		if (!_created || _map == null) return;
		
		_map.setCentered(p);
		_map.markPoint(p, VehicleType.DRONE);
	}
	
	private void findVehicles() {
		for(String _ip : VehicleIpAddresses){
			
		}
	}
	
	protected void attachMapActivity(MFCMapActivity map){
		_map = map;
	}
}