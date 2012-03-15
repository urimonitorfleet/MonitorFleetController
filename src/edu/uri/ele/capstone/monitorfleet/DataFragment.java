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
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DataFragment extends ListFragment {
	
	private ArrayList<HashMap<String, String>> _listData = new ArrayList<HashMap<String, String>>();
	
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

		setListAdapter(new SimpleAdapter(getActivity(), _listData, R.layout.list_item,
	  			 new String[] { "displayName", "value" },
	  			 new int[] { R.id.item_title, R.id.item_subtitle })
		);
		
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
	
	public void updateListContent(List<DataItem> newData, boolean clear){

		if (clear) _listData.clear();
		
		if(_listData.size() == 0){
			for(int i = 0; i < newData.size(); i++){
				DataItem d = (DataItem)newData.get(i);
	
				_listData.add(d.getStringHashMap());
			}
		}else{
			int j_st = 0;
			
			for(int i = 0; i < newData.size(); i++){
				if(j_st >= _listData.size()){
					_listData.add(((DataItem)newData.get(i)).getStringHashMap());
					continue;
				}
				
				for(int j = j_st; j < _listData.size(); j++){
					DataItem cur_di = (DataItem)newData.get(i);
					HashMap<String, String> cur_hm = _listData.get(j);
					
					int cmp = cur_di.getMachineName().compareTo(cur_hm.get("machineName"));
					
					if(cmp == 0){ // found -> update
						cur_hm.put("value", cur_di.getValue());
						j_st = j + 1;
						break;
					}else if(cmp > 0){  // missing -> insert
						_listData.add(j, cur_di.getStringHashMap());
						j_st = j + 1;
						break;
					}
				}
			}
		}
		
		((SimpleAdapter)getListAdapter()).notifyDataSetChanged();
	}
}
