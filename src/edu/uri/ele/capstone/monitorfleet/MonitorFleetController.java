package edu.uri.ele.capstone.monitorfleet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.uri.ele.capstone.monitorfleet.util.DataFeedParser;
import edu.uri.ele.capstone.monitorfleet.util.DataItem;
import edu.uri.ele.capstone.monitorfleet.util.Vehicle;
import edu.uri.ele.capstone.monitorfleet.util.Vehicle.VehicleType;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonitorFleetController extends FragmentActivity implements TabListener {
	
	private MFCMapActivity _map = null;
	private DataFragment _dataFragment = null;
	
	private static final String VehicleIpAddresses[] = { "Left", "Right", "Flagship" };
	private List<Vehicle> _vehicles;
	
	public MonitorFleetController(){
		super();
		
		_vehicles = new ArrayList<Vehicle>();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        setContentView(R.layout.main);
        
        ActionBar bar = getActionBar();
        
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);  
		
		findVehicles_start();
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
    		case R.id.menu_refresh:
    			findVehicles_start();
    			break;
    		case R.id.menu_test:
    			test();
    			break;
    		default:
    			return false;
    	}
    	
    	return true;
    }
    
    private void test(){
    	synchronized(_vehicles){
    		_vehicles.clear();
    	}
    	getActionBar().removeAllTabs();
		
		_dataFragment.toggleData(false);
    }

	private void findVehicles_start() {
		final Dialog dialog = ProgressDialog.show(this, "", "Discovering Vehicles, Please Wait...", true);

		final Handler h = new Handler(){
			public void handleMessage(Message msg){
				findVehicles_end();
				dialog.dismiss();
			}
		};
		
		Thread worker = new Thread(){
			public void run(){
				synchronized(_vehicles){
					_vehicles.clear();
					
					for(String _ip : VehicleIpAddresses){
						//String url = "http://" + _ip + "/data.xml";
						String url = "http://egr.uri.edu/~bkintz/files/capstone_test/" + _ip + ".xml";
						if(DataFeedParser.UrlExists(url)){
							_vehicles.add(new Vehicle(_ip, DataFeedParser.GetData(url)));
						}
					}
				}
				
				h.sendEmptyMessage(0);
			}
			
		};
		
		worker.start();

		getActionBar().removeAllTabs();
	}
	
	private void findVehicles_end(){
		synchronized(_vehicles){
			if (_vehicles.size() == 0) {
				_dataFragment.toggleData(false);
				
				String adMsg = "Nothing was found while scanning the following IPs:\n\n";
				for(String _ip : VehicleIpAddresses){
					adMsg += "\t" + _ip + "\n";
				}
				adMsg += "\nPlease review the network configuration!";
				
				AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
				adBuilder.setMessage(adMsg)
						 .setCancelable(false)
						 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();
			}else{
				_dataFragment.toggleData(true);
	
				List<Pair<GeoPoint, VehicleType>> vehiclePositions = new ArrayList<Pair<GeoPoint, VehicleType>>();
				ActionBar bar = getActionBar();
				
				for(Vehicle v : _vehicles){
					bar.addTab(bar.newTab().setText(v.getIpAddr() + " (" + v.getVehicleType() + ")")
								 	 	   .setTabListener(this)
								 	 	   .setTag(v));
					
					if(v.hasGps()){
						vehiclePositions.add(new Pair<GeoPoint, VehicleType>(v.getGps(), v.getVehicleType()));
					}
				}
				
				if(_map != null){
					_map.markPoints(vehiclePositions);
				}
			}
		}
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (_dataFragment == null) return;
		
		ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
		
		Vehicle current = (Vehicle)tab.getTag();
		List<DataItem> _data = current.getData();
		
		for(int i = 0; i < _data.size(); i++){
			DataItem d = (DataItem)_data.get(i);

			out.add(d.getStringHashMap());
		}

		_dataFragment.updateListContent(out);
		
		if(_map != null && current.hasGps()){
			_map.setCentered(current.getGps());
		}
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
	
	protected void attachMapActivity(MFCMapActivity map){
		_map = map;
	}
	protected void attachDataFragment(DataFragment dF){
		_dataFragment = dF;
	}
}