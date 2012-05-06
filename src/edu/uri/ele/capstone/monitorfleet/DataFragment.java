package edu.uri.ele.capstone.monitorfleet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uri.ele.capstone.monitorfleet.util.DataItem;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DataFragment extends ListFragment {
	
	private ArrayList<HashMap<String, String>> _listData = new ArrayList<HashMap<String, String>>();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
 
		// add a reference to myself in the main activity
        ((MonitorFleetController)getActivity()).attachDataFragment(this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return (View)inflater.inflate(R.layout.data, container, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onViewCreated(View view, Bundle savedInstanceState){
		
		// set up the data list
		final ListView lv = getListView();

		// define the appearance of each element
		setListAdapter(new SimpleAdapter(getActivity(), _listData, R.layout.list_item,
	  			 new String[] { "displayName", "value" },
	  			 new int[] { R.id.item_title, R.id.item_subtitle })
		);
		
		// define the appearance of the onClick popup details box
		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;		
				
				HashMap<String, String> item = (HashMap<String, String>)lv.getAdapter().getItem(info.position);
				
				menu.setHeaderTitle(item.get("displayName"));
				menu.add("Machine Name: " + item.get("machineName"));
				menu.add("Value: " + item.get("value"));
			}
		});
		
		// show the details box on item click
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				parent.showContextMenuForChild(view);
			}
		});
	}
	
	/*
	 * Enable or disable the data list
	 */
	public void toggleData(boolean enable){
		getListView().setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
		getActivity().findViewById(R.id.list_nodata).setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
	}
	
	public void updateListContent(List<DataItem> newData){
		// update the list content by clearing and replacing with new data
		
		_listData.clear();
		
		for(int i = 0; i < newData.size(); i++){
			DataItem d = (DataItem)newData.get(i);

			_listData.add(d.getStringHashMap());
		}

		// poke the list adapter and tell it we changed its data
		((BaseAdapter)getListAdapter()).notifyDataSetChanged();
	}
}
