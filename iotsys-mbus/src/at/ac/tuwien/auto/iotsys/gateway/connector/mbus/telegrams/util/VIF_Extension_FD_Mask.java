package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util;

public enum VIF_Extension_FD_Mask {
	// Currency Units
	CURRENCY_CREDIT(0x03),	//	E000 00nn	Credit of 10 nn-3 of the nominal local legal currency units
	CURRENCY_DEBIT(0x07),	//	E000 01nn	Debit of 10 nn-3 of the nominal local legal	currency units
	// Enhanced Identification
	ACCESS_NUMBER(0x08),	//	E000 1000 Access Number (transmission count)
	MEDIUM(0x09),			//	E000 1001 Medium (as in fixed header)
	MANUFACTURER(0x0A),		//	E000 1010 Manufacturer (as in fixed header)
	PARAMETER_SET_ID(0x0B),	//	E000 1011 Parameter set identification Enhanced Identification
	MODEL_VERSION(0x0C),	//	E000 1100 Model / Version
	HARDWARE_VERSION(0x0D),	//	E000 1101 Hardware version #
	FIRMWARE_VERSION(0x0E),	//	E000 1110 Firmware version #
	SOFTWARE_VERSION(0x0F),	//	E000 1111 Software version #
	// Implementation of all TC294 WG1 requirements (improved selection ..)
	CUSTOMER_LOCATION(0x10),//	E001 0000 Customer location
	CUSTOMER(0x11),			//	E001 0001 Customer
	ACCESS_CODE_USER(0x12),	//	E001 0010 Access Code User
	ACCESS_CODE_OPERATOR(0x13),			//	E001 0011 Access Code Operator 
	ACCESS_CODE_SYSTEM_OPERATOR(0x14),	//	E001 0100 Access Code System Operator 
	ACCESS_CODE_DEVELOPER(0x15),		//	E001 0101 Access Code Developer 
	PASSWORD(0x16),			//	E001 0110 Password
	ERROR_FLAGS(0x17),		//	E001 0111 Error flags (binary)
	ERROR_MASKS(0x18),		//	E001 1000 Error mask
	RESERVED(0x19),			//	E001 1001 Reserved
	DIGITAL_OUTPUT(0x1A),	//	E001 1010 Digital Output (binary)
	DIGITAL_INPUT(0x1B),	//	E001 1011 Digital Input (binary)
	BAUDRATE(0x1C),			//	E001 1100 Baudrate [Baud]
	RESPONSE_DELAY(0x1D),	//	E001 1101 response delay time [bittimes]
	RETRY(0x1E),			//	E001 1110 Retry
	RESERVED_2(0x1F),		//	E001 1111 Reserved
	//	Enhanced storage management	
	FIRST_STORAGE_NR(0x20),	//	E010 0000 First storage # for cyclic storage
	LAST_STORAGE_NR(0x21),	//	E010 0001 Last storage # for cyclic storage
	SIZE_OF_STORAGE_BLOCK(0x22),	//	E010 0010 Size of storage block
	RESERVED_3(0x23),				//	E010 0011 Reserved
	STORAGE_INTERVAL(0x27),			//	E010 01nn Storage interval [sec(s)..day(s)]
	STORAGE_INTERVAL_MONTH(0x28),	//	E010 1000 Storage interval month(s) 
	STORAGE_INTERVAL_YEARS(0x29),	//	E010 1001 Storage interval year(s)
	//	E010 1010 Reserved
	//	E010 1011 Reserved
	DURATION_SINCE_LAST_READOUT(0x2F),	//	E010 11nn Duration since last readout [sec(s)..day(s)] 
	//  Enhanced tarif management
	START_OF_TARIFF(0x30),			//	E011 0000 Start (date/time) of tariff
	DURATION_OF_TARIFF(0x3),		//	E011 00nn Duration of tariff (nn=01 ..11: min to days)
	PERIOD_OF_TARIFF(0x37),			//	E011 01nn Period of tariff [sec(s) to day(s)] 
	PERIOD_OF_TARIFF_MONTH(0x38),	//	E011 1000 Period of tariff months(s) 
	PERIOD_OF_TARIFF_YEARS(0x39),	//	E011 1001 Period of tariff year(s) 
	DIMENSIONLESS(0x3A),			//	E011 1010 dimensionless / no VIF
	//	E011 1011 Reserved
	//	E011 11xx Reserved
	//  electrical units
	VOLTS(0x4F),				//	E100 nnnn 10 nnnn-9 Volts 
	AMPERE(0x5F),				//	E101 nnnn 10 nnnn-12 A
	RESET_COUNTER(0x60),		//	E110 0000 Reset counter
	CUMULATION_COUNTER(0x61),	//	E110 0001 Cumulation counter
	CONTROL_SIGNAL(0x62),		//	E110 0010 Control signal
	DAY_OF_WEEK(0x63),			//	E110 0011 Day of week
	WEEK_NUMBER(0x64),			//	E110 0100 Week number
	TIME_POINT_OF_DAY_CHANGE(0x65),			//	E110 0101 Time point of day change
	STATE_OF_PARAMETER_ACTIVATION(0x66),	//	E110 0110 State of parameter activation
	SPECIAL_SUPPLIER_INFORMATION(0x67),		//	E110 0111 Special supplier information
	DURATION_SINCE_LAST_CUMULATION(0x6B),	//	E110 10pp Duration since last cumulation [hour(s)..years(s)]
	OPERATING_TIME_BATTERY(0x6F),			//	E110 11pp Operating time battery [hour(s)..years(s)]
	DATEAND_TIME_OF_BATTERY_CHANGE(0x70);	//	E111 0000 Date and time of battery change
	//	E111 0001 to E111 1111 Reserved

	private int value;
	
	private VIF_Extension_FD_Mask(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}	
}
