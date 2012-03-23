package edu.uri.ele.capstone.monitorfleet.util;

import java.util.List;

import com.google.android.maps.GeoPoint;

public class Vehicle {
	private final String _ipAddr;
	private final String _dataURL;
	
	private VehicleType _vType;
	
	private boolean _hasGps;
	private GeoPoint _gpsLoc;
	
	private List<DataItem> _data;
	
	public Vehicle(String ipAddress, String dataURL){
		_ipAddr = ipAddress;
		_dataURL = dataURL;

		update();
	}
	
	private void setType() {
		for(DataItem i : this._data){
			if(i.getMachineName().equals("op_mode")){
				String _s = i.getValue();
				if(_s.equals("Drone")){ 
					this._vType = VehicleType.DRONE;
				}else if(_s.equals("Flagship")){
					this._vType = VehicleType.FLAGSHIP;
				}else if(_s.equals("Target")){
					this._vType = VehicleType.TARGET;
				}
				
				return;
			}
		}
	}
	
	private void setGps(){
		boolean hasGps = false;
		String gpsQual, lat_s, lng_s;
		
		lat_s = lng_s = null;
		
		for(DataItem i : this._data){
			if(i.getMachineName().equals("gps_quality")) {
				gpsQual = i.getValue();
				hasGps = !gpsQual.equals("None");
				break;
			}else if(i.getMachineName().equals("gps_lat")){
				lat_s = i.getValue();
			}else if(i.getMachineName().equals("gps_long")){
				lng_s = i.getValue();
			}
		}
		
		if(hasGps && lat_s != null && lng_s != null){
			this._hasGps = true;
			
			double lat = Double.parseDouble(lat_s);
	        double lng = Double.parseDouble(lng_s);
	        
	        this._gpsLoc = new GeoPoint((int)(lat * 1E6), (int)(lng * 1E6));
		}else{
			this._hasGps = false;
			this._gpsLoc = null;
		}
	}
	
	public void update() { 
		_vType = VehicleType.NONE;
		_hasGps = false;

		_data = DataFeedParser.GetData(_dataURL);

		if(_data.isEmpty()) return;
		
		this.setType();
		this.setGps();
	}
	
	public String getIpAddr() { return this._ipAddr; }
	public VehicleType getVehicleType() { return this._vType; }
	public boolean hasGps() { return this._hasGps; }
	public GeoPoint getGps() { return this._gpsLoc; }
	public List<DataItem> getData() { return this._data; }
	
	public enum VehicleType { 
		DRONE,
		FLAGSHIP,
		TARGET,
		NONE
	} 
}
