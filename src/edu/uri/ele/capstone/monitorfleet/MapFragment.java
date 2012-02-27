package edu.uri.ele.capstone.monitorfleet;

// Code from https://github.com/inazaruk/examples/tree/master/MapFragmentExample

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class MapFragment extends LocalActivityManagerFragment {

	private TabHost _tabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		_tabHost = (TabHost)view.findViewById(android.R.id.tabhost);
		_tabHost.setup(getLocalActivityManager());
		_tabHost.addTab(_tabHost.newTabSpec("map")
								.setIndicator("map")
								.setContent(new Intent(getActivity(), MFCMapActivity.class))
						);
		return view;
	}
}
