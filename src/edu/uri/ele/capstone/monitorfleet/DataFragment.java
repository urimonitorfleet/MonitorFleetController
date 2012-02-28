package edu.uri.ele.capstone.monitorfleet;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DataFragment extends ListFragment {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
 
        ((MonitorFleetController)getActivity()).attachDataFragment(this);
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
	}
	
	public void toggleData(boolean enable){
		getListView().setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
		getActivity().findViewById(R.id.list_nodata).setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
	}
	
	public void updateListContent(ArrayList<HashMap<String, String>> newData){
		setListAdapter(new SimpleAdapter(getActivity(), newData, R.layout.list_item,
	  			 new String[] { "displayName", "value" },
	  			 new int[] { R.id.item_title, R.id.item_subtitle }));
	}
}
