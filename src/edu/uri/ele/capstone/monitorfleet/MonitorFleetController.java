package edu.uri.ele.capstone.monitorfleet;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.uri.ele.capstone.monitorfleet.util.Utilities;
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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Main activity class for the app
 * 
 * @author bkintz
 *
 */
public class MonitorFleetController extends FragmentActivity implements TabListener {

	// static array of the IP addresses that the tablet should search for vehicle data
	private static final String VehicleIpAddresses[] = { "192.168.1.4" }; //"Left", "Right", "Flagship" };
	
	private MFCMapActivity _map = null;
	private DataFragment _dataFragment = null;

	private Vehicle _selected;
	
	// stuff to handle auto updating
	private Handler dataHandler = new Handler();
	private Runnable dataUpdateTask = new Runnable() {
		public void run(){
			synchronized(_selected){
				// don't do anything if the app hasn't been fully created yet
				if (_dataFragment == null || _map == null) return;
				
				ActionBar bar = getActionBar();
				
				List<Pair<GeoPoint, VehicleType>> pts = new ArrayList<Pair<GeoPoint, VehicleType>>();
				
				// update each vehicles data and set up to update the map
				for(int i = 0; i < bar.getTabCount(); i++){
					Vehicle cur = (Vehicle)bar.getTabAt(i).getTag();

					cur.update();
					
					// add the vehicle to the list of points to be shown on the map
					if(cur.hasGps()){
						pts.add(new Pair<GeoPoint, VehicleType>(cur.getGps(), cur.getVehicleType()));
					}
				}

				// update the list
				_dataFragment.updateListContent(_selected.getData());
				
				// update the map
				_map.markPoints(pts);
				
				// center the map on the currently selected vehicle
				if(_selected.hasGps()){
					_map.setCentered(_selected.getGps());
				}
				
				// set up to run again in a second
				dataHandler.postDelayed(this, 1000);
			}
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // loosen up the threading policy for the map fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        // show the app
        setContentView(R.layout.main);
        
        ActionBar bar = getActionBar();
        
        // enable action bar tabs and remove the icon/app name
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
    	// handle clicks/presses on the video and refresh buttons in the action bar
    	switch(menu.getItemId()){
    		case R.id.menu_video:
    			launchVideo();
    			break;
    		case R.id.menu_refresh:
    			new FindVehiclesTask().execute();
    			break;
    		default:
    			return false;
    	}
    	
    	return true;
    }
    
    // handle tab events
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		synchronized(_selected){
			
			// when we select a different vehicle, update _selected and update the list
			_selected = (Vehicle)tab.getTag();
			
			_dataFragment.updateListContent(_selected.getData());
		}
	}	
	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
	
	@Override
	protected void onResume(){
		super.onResume();
		
		// restart auto-updating
		new FindVehiclesTask().execute();
	}
	@Override
	protected void onPause(){
		// stop auto-updating
		dataHandler.removeCallbacks(dataUpdateTask);
		
		super.onPause();
	}
	
	protected void attachMapActivity(MFCMapActivity map) { _map = map; }
	protected void attachDataFragment(DataFragment dF)	 { _dataFragment = dF; }
	
	private void launchVideo(){
		// launch the video stream in the MjpegViewer app
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + _selected.getIpAddr() + ":8080?action=stream"));
		i.setClassName("com.dngames.mjpegviewer", "com.dngames.mjpegviewer.MJpegViewer");
		startActivity(i);

		/* Uncomment for VLC-powered RTSP MP4 streaming
		 
    	// Test streams at:  http://www.law.duke.edu/cspd/contest/finalists/
    	//String streamURL = "http://www.law.duke.edu/cspd/contest/finalists/viewentry.php?file=docandyou";
    	String streamURL = "rtsp://" + _selected.getIpAddr() + ":8554/main.sdp";
    	
		StreamDialog d = new StreamDialog(this);
    	d.show();
		d.play(streamURL);
		*/
    }
	
	/*
	 * Thread worker method to scan the IP list for active vehicles
	 */
	private class FindVehiclesTask extends AsyncTask<Void, Void, List<Vehicle>> {
		Dialog d;
		
		@Override
		protected void onPreExecute(){
			// prep:  show wait dialog, clear out any old vehicles
			d = ProgressDialog.show(MonitorFleetController.this, "", "Discovering Vehicles, Please Wait...", true);
			
			MonitorFleetController.this.getActionBar().removeAllTabs();
			
			_dataFragment.toggleData(false);
		}
		
		@Override
		protected List<Vehicle> doInBackground(Void... params) {
			List<Vehicle> out = new ArrayList<Vehicle>();
			
			// process:  for every ip in the list, check if a data file exists and add it if it does
			for(String ip : VehicleIpAddresses){
				String url = "http://" + ip + "/data.xml";
				//String url = "http://egr.uri.edu/~bkintz/files/capstone_test/" + ip + ".xml";
				if(Utilities.UrlExists(url)){
					out.add(new Vehicle(ip, url));
				}
			}
			
			return out;
		}
		
		@Override
		protected void onPostExecute(List<Vehicle> vehicles){
			// finish up:  if nothing found, say so in an alert box
			if (vehicles.size() == 0) {
				String adMsg = "Nothing was found while scanning the following IPs:\n\n";
				for(String ip : VehicleIpAddresses){
					adMsg += "\t" + ip + "\n";
				}
				adMsg += "\nPlease review the network configuration!";
				
				AlertDialog.Builder adBuilder = new AlertDialog.Builder(MonitorFleetController.this);
				adBuilder.setMessage(adMsg)
						 .setCancelable(false)
						 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();
			}else{
				// if we found vehicles, select and show the first one found
				_dataFragment.toggleData(true);
	
				_selected = vehicles.get(0);
				
				ActionBar bar = getActionBar();
				
				for(Vehicle v : vehicles){
					bar.addTab(bar.newTab().setText(v.getIpAddr() + " (" + v.getVehicleType() + ")")
								 	 	   .setTabListener(MonitorFleetController.this)
								 	 	   .setTag(v));
				}

				// start auto-updater
				dataHandler.post(dataUpdateTask);
			}
			
			d.dismiss();
		}
	}
}