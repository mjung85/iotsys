package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import at.ac.tuwien.auto.calimero.GroupAddress;

public class DataPointInit {
	
	private GroupAddress groupAddress;
	
	private String name;
	
	private String displayName;
	
	private String display;
	
	private boolean writable;
	
	private boolean readFlag;

	public GroupAddress getGroupAddress() {
		return groupAddress;
	}

	public void setGroupAddress(GroupAddress groupAddress) {
		this.groupAddress = groupAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	public boolean isReadFlag() {
		return readFlag;
	}

	public void setReadFlag(boolean readFlag) {
		this.readFlag = readFlag;
	}
	
	public DataPointInit(){
		
	}

	public DataPointInit(GroupAddress groupAddress, String name,
			String displayName, String display, boolean writable, boolean readFlag) {
		super();
		this.groupAddress = groupAddress;
		this.name = name;
		this.displayName = displayName;
		this.display = display;
		this.writable = writable;
		this.readFlag = readFlag;
	}		
}
