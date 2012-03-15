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
		String gpsCoord[] = { null, null };
		
		for(DataItem i : this._data){
			if(i.getMachineName().equals("gps_lat")) {
				gpsCoord[0] = i.getValue();
				hasGps |= (gpsCoord[1] != null);
			}else if(i.getMachineName().equals("gps_long")){
				gpsCoord[1] = i.getValue();
				hasGps |= (gpsCoord[0] != null);
			}
			
			if (hasGps) break;
		}
		
		if(hasGps){
			this._hasGps = true;
			
			double lat = Double.parseDouble(gpsCoord[0]);
	        double lng = Double.parseDouble(gpsCoord[1]);
	        
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
