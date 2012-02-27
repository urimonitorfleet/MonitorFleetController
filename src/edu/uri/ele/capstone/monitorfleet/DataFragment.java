package edu.uri.ele.capstone.monitorfleet;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.maps.GeoPoint;

import edu.uri.ele.capstone.monitorfleet.util.DataFeedParser;
import edu.uri.ele.capstone.monitorfleet.util.DataItem;

import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.*;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DataFragment extends ListFragment implements TabListener {
	OnVehicleSelectionChangedListener vscListener;
	
	public interface OnVehicleSelectionChangedListener {
		public void onVehicleSelectionChanged(GeoPoint p);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			vscListener = (OnVehicleSelectionChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnVehicleSelectionChangedListener");
		}
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return (View)inflater.inflate(R.layout.data, container, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onViewCreated(View view, Bundle savedInstanceState){
		final ListView lv = getListView();

		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;		
				HashMap<String, String> item = (HashMap<String, String>)lv.getAdapter().getItem(info.position);
				menu.setHeaderTitle(item.get("displayName"));
				menu.add("Machine Name: " + item.get("machineName"));
				menu.add("Value: " + item.get("value"));
			}
			
		});
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				parent.showContextMenuForChild(view);
			}
		});

		ActionBar bar = getActivity().getActionBar();
		
		bar.addTab(bar.newTab().setText("Left").setTabListener(this));  
		bar.addTab(bar.newTab().setText("Flagship").setTabListener(this), true);
        bar.addTab(bar.newTab().setText("Right").setTabListener(this));
	}
	
	public void onTabSelected(Tab tab, FragmentTransaction ft) { 

		String vehicleName = tab.getText().toString();
		
		((TextView)getView().findViewById(R.id.vehicleName)).setText(vehicleName);
		
		ArrayList<DataItem> _data = DataFeedParser.GetData("http://egr.uri.edu/~bkintz/files/capstone_test/" + vehicleName + ".xml");
		ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
		
		boolean hasGPSCoord = false;
		String gpsCoord[] = { null, null };
		
		for(int i = 0; i < _data.size(); i++){
			DataItem current = (DataItem)_data.get(i);
			
			if(current.getMachineName().equals("gps_lat")) {
				gpsCoord[0] = current.getValue();
				hasGPSCoord |= (gpsCoord[1] != null);
			}else if(current.getMachineName().equals("gps_long")){
				gpsCoord[1] = current.getValue();
				hasGPSCoord |= (gpsCoord[0] != null);
			}
			
			out.add(current.getStringHashMap());
		}

		setListAdapter(new SimpleAdapter(getActivity(), out, R.layout.list_item,
							  			 new String[] { "displayName", "value" },
							  			 new int[] { R.id.item_title, R.id.item_subtitle }));
		
		if(hasGPSCoord){
			double lat = Double.parseDouble(gpsCoord[0]);
	        double lng = Double.parseDouble(gpsCoord[1]);
	        
	        GeoPoint p = new GeoPoint((int)(lat * 1E6),
	        						  (int)(lng * 1E6));
	        
	        vscListener.onVehicleSelectionChanged(p);
		}
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
}
