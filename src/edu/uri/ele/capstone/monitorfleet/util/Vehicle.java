package edu.uri.ele.capstone.monitorfleet.util;

import java.util.List;

import com.google.android.maps.GeoPoint;

public class Vehicle {
	private String _ipAddr;
	private VehicleType _vType;
	private boolean _hasGps;
	private GeoPoint _gpsLoc;
	private List<DataItem> _data;
	
	public Vehicle(String ipAddress, VehicleType vehicleType){
		_ipAddr = ipAddress;
		_vType = vehicleType;
		_hasGps = false;
	}
	
	public Vehicle(String ipAddress, VehicleType vehicleType, List<DataItem> data){
		this(ipAddress, vehicleType);
		
		this._data = data;
		
		this.setType();  
		this.setGps();
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
	
	public void update(List<DataItem> newData) { 
		this._data = newData;
		
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
		TEST
	} 
}
