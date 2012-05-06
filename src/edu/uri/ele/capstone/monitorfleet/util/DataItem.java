package edu.uri.ele.capstone.monitorfleet.util;

import java.util.HashMap;

/**
 * Container class for the information displayed on the tablet.
 * 
 * @author bkintz
 *
 */
public class DataItem implements Comparable<DataItem>{
	private String _machineName, _displayName, _value;
	
	public DataItem(String machineName, String displayName, String value){
		_machineName = machineName != null ? machineName : "machineName";
		_displayName = displayName != null ? displayName : "displayName";
		_value = value != null ? value : "value";
	}
	
	public String getMachineName() { return _machineName; }
	public String getDisplayName() { return _displayName; }
	public String getValue() { return _value; }
	
	/**
	 * Convert the DataItem to a HashMap.  Used to display the item as an Android list element
	 * 
	 * @return HashMap form of the DataItem
	 */
	public HashMap<String, String> getStringHashMap() { 
		HashMap<String, String> out = new HashMap<String, String>();
		
		out.put("machineName", _machineName);
		out.put("displayName",_displayName);
		out.put("value", _value);
		
		return out;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;
		
		return (obj instanceof DataItem) && _machineName.equals(((DataItem)obj)._machineName);
	}
	
	@Override
	public int hashCode(){
		return _machineName.hashCode();
	}

	@Override
	public int compareTo(DataItem another) {
		return this._machineName.compareTo(another._machineName);
	}
}
