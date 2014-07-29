package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import obix.Ref;
import obix.List;
import obix.Int;
import obix.Enum;
import obix.Obj;
import obix.Str;
import obix.Uri;
import obix.Contract;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.*;

import org.apache.commons.configuration.XMLConfiguration;

public class KNXDeviceLoaderETSImplGenerated implements DeviceLoader {
	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		KNXConnector knxConnector = new KNXConnector("192.168.1.102", 3671,
				"auto");

		this.connect(knxConnector);
		this.initNetwork(knxConnector, objectBroker);

		ArrayList<Connector> connectors = new ArrayList<Connector>();
		connectors.add(knxConnector);

		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		// nothing to be done
	}

	@Override
	public void setConfiguration(XMLConfiguration devicesConfiguration) {
		// nothing to be done
	}

	private void connect(KNXConnector knxConnector) {
		try {
			knxConnector.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}

	private void initAddresses(HashMap<String, Integer> groupAddresses) {
		int currentAddress = -1;

		currentAddress = 2048;

		currentAddress = 2560;

		currentAddress = 2048;

		currentAddress = 2048;

		groupAddresses.put("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4",
				currentAddress);

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27",
				currentAddress);

		currentAddress = 2049;

		groupAddresses.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1",
				currentAddress);

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41",
				currentAddress);

		currentAddress = 2050;

		groupAddresses.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0",
				currentAddress);

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38",
				currentAddress);

		currentAddress = 2051;

		groupAddresses.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3",
				currentAddress);

		currentAddress = 2304;

		currentAddress = 2304;

		groupAddresses.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2",
				currentAddress);

		currentAddress = 2305;

		groupAddresses.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3",
				currentAddress);

		currentAddress = 2306;

		groupAddresses.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1",
				currentAddress);

		currentAddress = 2307;

		groupAddresses.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4",
				currentAddress);

		currentAddress = 2816;

		currentAddress = 2817;

		groupAddresses.put("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1",
				currentAddress);

		currentAddress = 2818;

		groupAddresses.put("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2",
				currentAddress);

		currentAddress = 2819;

		groupAddresses.put(
				"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50",
				currentAddress);

		currentAddress = 2820;

		groupAddresses.put(
				"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55",
				currentAddress);

		currentAddress = 2821;

		groupAddresses.put(
				"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60",
				currentAddress);

		currentAddress = 3072;

		currentAddress = 3072;

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10",
				currentAddress);

		currentAddress = 3073;

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18",
				currentAddress);

		currentAddress = 3074;

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27",
				currentAddress);

		currentAddress = 3075;

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38",
				currentAddress);

		currentAddress = 3076;

		groupAddresses.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41",
				currentAddress);

	}

	private void initTranslations(
			HashMap<String, ArrayList<ArrayList<String>>> translations) {
		ArrayList<ArrayList<String>> temp = null;
		ArrayList<String> singleTranslation = null;

		if (!translations
				.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1",
					temp);
		}
		temp = translations.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Dimmen");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Heller / Dunkler");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Dimming");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Brighter / Darker");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Dimmerizzazione");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Chiaro / Scuro");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Regulación");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Aclarar/ oscurecer");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3",
					temp);
		}
		temp = translations.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Status");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("8-bit Wert");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Status");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("8-bit Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Stato");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Valore 8-bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Estado");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Valor 8 bits");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0",
					temp);
		}
		temp = translations.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schalten, Status");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Ein / Aus");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Switch, Status");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("On / Off");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Commuta stato");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("On / Off");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Conectar, Status");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Encender / Apagar");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4",
					temp);
		}
		temp = translations.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schalten, Kanal A");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Ein / Aus");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Switch, Channel A");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("On / Off");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Commuta, canale A");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("On / Off");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2",
					temp);
		}
		temp = translations.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperatur, Kanal A");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("°C-Wert (EIS5)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperature, Channel A");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("°C-value (EIS5)");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3",
					temp);
		}
		temp = translations.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperatur, Kanal B");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("°C-Wert (EIS5)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperature, Channel B");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("°C-value (EIS5)");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1",
					temp);
		}
		temp = translations.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperatur, Kanal C");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("°C-Wert (EIS5)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperature, Channel C");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("°C-value (EIS5)");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4",
					temp);
		}
		temp = translations.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperatur, Kanal D");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("°C-Wert (EIS5)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperature, Channel D");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("°C-value (EIS5)");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put(
					"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50", temp);
		}
		temp = translations
				.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("Tærskel 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("display");
		singleTranslation.add("Værdisensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schwelle 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Wertgeber");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Threshold 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Umbral 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Transmisor de valores");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation.add("Seuil 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("Commande progressive");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Soglia 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Trasmettitore di valore");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("Drempel 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("display");
		singleTranslation.add("Meetsensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("Tröskel 1 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("Värdegivare");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put(
					"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55", temp);
		}
		temp = translations
				.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("Tærskel 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("display");
		singleTranslation.add("Værdisensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schwelle 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Wertgeber");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Threshold 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Umbral 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Transmisor de valores");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation.add("Seuil 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("Commande progressive");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Soglia 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Trasmettitore di valore");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("Drempel 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("display");
		singleTranslation.add("Meetsensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("Tröskel 2 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("Värdegivare");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put(
					"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60", temp);
		}
		temp = translations
				.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("Tærskel 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("display");
		singleTranslation.add("Værdisensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schwelle 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Wertgeber");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Threshold 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Umbral 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Transmisor de valores");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation.add("Seuil 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("Commande progressive");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Soglia 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Trasmettitore di valore");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("Drempel 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("display");
		singleTranslation.add("Meetsensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("Tröskel 3 CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("Värdegivare");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put(
					"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1", temp);
		}
		temp = translations
				.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2-værdi");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("display");
		singleTranslation.add("Fysisk værdi");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2 Wert");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Physikalischer Wert");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2 Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Physical Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Valor físico");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation.add("Teneur en CO2");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("Valeur physique");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2 valore");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Valore fisico");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2-waarde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("display");
		singleTranslation.add("Fysieke waarde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("CO2-värde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("Fysiskt värde");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put(
					"P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2", temp);
		}
		temp = translations
				.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("Rel. fugtværdi");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("display");
		singleTranslation.add("Fysisk værdi");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Rel. Feuchtewert");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Physikalischer Wert");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Rel. humidity value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Physical Value");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor de humedad relativa");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Valor físico");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation.add("Valeur humidité relative");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("Valeur physique");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Valore di umidità rel.");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("Valore fisico");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("Rel. vochtigheidswaarde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("display");
		singleTranslation.add("Fysieke waarde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("Rel. fuktvärde");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("Fysiskt värde");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10",
					temp);
		}
		temp = translations
				.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Wert links außen");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("8-bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Value of outer left");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Valore sinistra, esterno");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor, exterior izquierda");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("8 bits");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18",
					temp);
		}
		temp = translations
				.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Wert links Mitte");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("8-bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Value of centre left");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Valore sinistra, centro");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor, central izquierda");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("8 bits");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41",
					temp);
		}
		temp = translations
				.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Jalousie rechts außen");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Ab / Auf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Shutter outer right");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Down / Up");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Tapparella esterna destra");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("discesa / salita");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Persiana, exterior derecha");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("Bajar/Subir");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38",
					temp);
		}
		temp = translations
				.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Wert rechts außen");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("8-bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Value of outer right");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Valore destra, esterno");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor, exterior derecha");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("8 bits");
		temp.add(singleTranslation);

		if (!translations
				.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27",
					temp);
		}
		temp = translations
				.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Wert rechts Mitte");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("8-bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Value of centre right");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Valore destra, centro");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("display");
		singleTranslation.add("8 bit");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Valor, central derecha");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation.add("8 bits");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-1")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-1", temp);
		}
		temp = translations.get("P-0341-0_DI-1");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Jalousieschalter N 522/02");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   5221ab02_tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Shutter switch N 522/02");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   5221ab02_tpi_e.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation
				.add("Info Producto - ver fichero:   5221ab02_tpi_es.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Attuatore tapparelle N 522/02");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-2")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-2", temp);
		}
		temp = translations.get("P-0341-0_DI-2");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Universal-Dimmer N 527");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   5271ab01_tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Universal dimmer N 527");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   5271ab01_tpi_e.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation
				.add("Info Producto - ver fichero:   5271ab01_tpi_es.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("id_ID");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Dimmer universale N 527");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nb_NO");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-3")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-3", temp);
		}
		temp = translations.get("P-0341-0_DI-3");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Schaltaktor N 567/01, (8 Amp)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   5671ab01_tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Switching actuator N 567/01, (8 Amp)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   5671ab01_tpi_e.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Uscita binaria N 567/01, (8Amp)");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-11")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-11", temp);
		}
		temp = translations.get("P-0341-0_DI-11");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperatursensor N 258/02");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   1258ab02_tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("Temperature Sensor N 258/02");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   2581ab02_tpi_e.pdf");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-9")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-9", temp);
		}
		temp = translations.get("P-0341-0_DI-9");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("Taster 2-fach UP 211 DELTA studio (rote Linse)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   2112_b___tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation
				.add("Push button 2-fold UP 211 DELTA studio (red lens)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   2112_b___tpi_e.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation
				.add("Info Producto - ver fichero:   2112_b___tpi_es.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("id_ID");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Pulsante 2-c UP 211 DELTA studio (lente rossa)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nb_NO");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-7")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-7", temp);
		}
		temp = translations.get("P-0341-0_DI-7");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("da_DK");
		singleTranslation.add("displayName");
		singleTranslation.add("KNX CO2-, fugtigheds- og  temperatursensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation.add("KNX CO2-, Feuchte- und Temperatursensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation.add("KNX CO², Humidity and Temperature Sensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("displayName");
		singleTranslation.add("Sensor de temperatura, humedad y CO2 KNX");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("displayName");
		singleTranslation
				.add("Capteur de CO2, d´humidité et de temperature KNX");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation.add("Sensore KNX per CO2 umidità e temperatura");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nl_NL");
		singleTranslation.add("displayName");
		singleTranslation.add("KNX CO2-, vochtigheids- en temperatuursensor");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("displayName");
		singleTranslation.add("KNX CO2-, fukt- och temperatursensor");
		temp.add(singleTranslation);

		if (!translations.containsKey("P-0341-0_DI-10")) {
			temp = new ArrayList<ArrayList<String>>();
			translations.put("P-0341-0_DI-10", temp);
		}
		temp = translations.get("P-0341-0_DI-10");

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("displayName");
		singleTranslation
				.add("Taster 4-fach UP 245 DELTA profil (ohne Symbol)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("de_DE");
		singleTranslation.add("display");
		singleTranslation.add("Produktinfo - siehe Datei:   24_2ab_1_tpi.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("displayName");
		singleTranslation
				.add("Push button 4-f UP 245 DELTA profil (without sym)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("en_US");
		singleTranslation.add("display");
		singleTranslation.add("Productinfo - see file:   24_2ab_1_tpi_e.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("es_ES");
		singleTranslation.add("display");
		singleTranslation
				.add("Info Producto - ver fichero:   2452ab_1_tpi_es.pdf");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("fr_FR");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("id_ID");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("it_IT");
		singleTranslation.add("displayName");
		singleTranslation
				.add("Pulsante 4-c UP 245 DELTA profil (senza simboli)");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("nb_NO");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

		singleTranslation = new ArrayList<String>();
		singleTranslation.add("sv_SE");
		singleTranslation.add("display");
		singleTranslation.add("");
		temp.add(singleTranslation);

	}

	private void initNetwork(KNXConnector knxConnector,
			ObjectBroker objectBroker) {
		try {
			HashMap<String, Integer> groupAddresses = new HashMap<String, Integer>();
			initAddresses(groupAddresses);

			HashMap<String, ArrayList<ArrayList<String>>> translations = new HashMap<String, ArrayList<ArrayList<String>>>();
			initTranslations(translations);

			// create generic object		
			Obj _13950516787910 = new Obj();

			// init object
			_13950516787910.setName("P-0341");

			_13950516787910.setDisplayName("Office");

			_13950516787910.setIs(new Contract("bas:Network"));

			// add existing translations
			if (translations.containsKey("P-0341")) {
				for (ArrayList<String> entry : translations.get("P-0341")) {
					_13950516787910.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516787910.setHref(new Uri("/networks/office"));

			// add as hidden object to object broker

			objectBroker.addObj(_13950516787910, true);

			// create generic object		
			Enum _13950516787991 = new Enum();

			// init object
			_13950516787991.setName("standard");

			// add existing translations
			if (translations.containsKey("standard")) {
				for (ArrayList<String> entry : translations.get("standard")) {
					_13950516787991.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516787991.setHref(new Uri("standard"));

			// set value
			_13950516787991.set("knx");
			_13950516787991.setRange(new Uri("/enums/standard"));

			// add to parent (containment)
			_13950516787910.add(_13950516787991);

			// create generic object		
			Ref _13950516787992 = new Ref();

			// init object
			_13950516787992.setName("datapoints");

			_13950516787992.setIs(new Contract("bas:Datapoints"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_13950516787992.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516787992.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_13950516787910.add(_13950516787992);

			// create generic object		
			Ref _13950516788003 = new Ref();

			// init object
			_13950516788003.setName("entities");

			_13950516788003.setIs(new Contract("bas:Entities"));

			// add existing translations
			if (translations.containsKey("entities")) {
				for (ArrayList<String> entry : translations.get("entities")) {
					_13950516788003.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788003.setHref(new Uri("entities"));

			// add to parent (containment)
			_13950516787910.add(_13950516788003);

			// create generic object		
			Ref _13950516788004 = new Ref();

			// init object
			_13950516788004.setName("functional");

			_13950516788004.setIs(new Contract("bas:Functional"));

			// add existing translations
			if (translations.containsKey("functional")) {
				for (ArrayList<String> entry : translations.get("functional")) {
					_13950516788004.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788004.setHref(new Uri("functional"));

			// add to parent (containment)
			_13950516787910.add(_13950516788004);

			// create generic object		
			Ref _13950516788015 = new Ref();

			// init object
			_13950516788015.setName("topology");

			_13950516788015.setIs(new Contract("bas:Topology"));

			// add existing translations
			if (translations.containsKey("topology")) {
				for (ArrayList<String> entry : translations.get("topology")) {
					_13950516788015.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788015.setHref(new Uri("topology"));

			// add to parent (containment)
			_13950516787910.add(_13950516788015);

			// create generic object		
			Ref _13950516788016 = new Ref();

			// init object
			_13950516788016.setName("building");

			_13950516788016.setIs(new Contract("bas:Building"));

			// add existing translations
			if (translations.containsKey("building")) {
				for (ArrayList<String> entry : translations.get("building")) {
					_13950516788016.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788016.setHref(new Uri("building"));

			// add to parent (containment)
			_13950516787910.add(_13950516788016);

			// create generic object		
			Ref _13950516788017 = new Ref();

			// init object
			_13950516788017.setName("domains");

			_13950516788017.setIs(new Contract("bas:Domains"));

			// add existing translations
			if (translations.containsKey("domains")) {
				for (ArrayList<String> entry : translations.get("domains")) {
					_13950516788017.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788017.setHref(new Uri("domains"));

			// add to parent (containment)
			_13950516787910.add(_13950516788017);

			// create generic object		
			List _13950516788018 = new List();

			// init object
			_13950516788018.setName("datapoints");

			_13950516788018.setIs(new Contract("bas:Datapoints"));

			_13950516788018.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_13950516788018.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788018.setHref(new Uri("/networks/office/datapoints"));

			// add as hidden object to object broker

			_13950516788018.setHidden(true);
			objectBroker.addObj(_13950516788018, false);

			// create generic object		
			Ref _13950516788029 = new Ref();

			// init object
			_13950516788029
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1");

			_13950516788029.setDisplayName("Dimming");

			_13950516788029.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
					_13950516788029.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_13950516788029.setHref(new Uri("dimming/1"));

			// add to parent (containment)
			_13950516788018.add(_13950516788029);

			// create generic object		
			Ref _139505167880210 = new Ref();

			// init object
			_139505167880210
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3");

			_139505167880210.setDisplayName("Status");

			_139505167880210.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
					_139505167880210.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880210.setHref(new Uri("status/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880210);

			// create generic object		
			Ref _139505167880211 = new Ref();

			// init object
			_139505167880211
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0");

			_139505167880211.setDisplayName("Switch, Status");

			_139505167880211.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
					_139505167880211.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880211.setHref(new Uri("switch_status/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880211);

			// create generic object		
			Ref _139505167880312 = new Ref();

			// init object
			_139505167880312
					.setName("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4");

			_139505167880312.setDisplayName("Switch, Channel A");

			_139505167880312.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
					_139505167880312.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880312.setHref(new Uri("switch_channel_a/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880312);

			// create generic object		
			Ref _139505167880313 = new Ref();

			// init object
			_139505167880313
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2");

			_139505167880313.setDisplayName("Temperature, Channel A");

			_139505167880313.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
					_139505167880313.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880313.setHref(new Uri("temperature_channel_a/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880313);

			// create generic object		
			Ref _139505167880314 = new Ref();

			// init object
			_139505167880314
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3");

			_139505167880314.setDisplayName("Temperature, Channel B");

			_139505167880314.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
					_139505167880314.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880314.setHref(new Uri("temperature_channel_b/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880314);

			// create generic object		
			Ref _139505167880415 = new Ref();

			// init object
			_139505167880415
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1");

			_139505167880415.setDisplayName("Temperature, Channel C");

			_139505167880415.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
					_139505167880415.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880415.setHref(new Uri("temperature_channel_c/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880415);

			// create generic object		
			Ref _139505167880416 = new Ref();

			// init object
			_139505167880416
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4");

			_139505167880416.setDisplayName("Temperature, Channel D");

			_139505167880416.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
					_139505167880416.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880416.setHref(new Uri("temperature_channel_d/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880416);

			// create generic object		
			Ref _139505167880517 = new Ref();

			// init object
			_139505167880517
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50");

			_139505167880517.setDisplayName("Threshold 1 CO2");

			_139505167880517.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
					_139505167880517.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880517.setHref(new Uri("threshold_1_co2/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880517);

			// create generic object		
			Ref _139505167880518 = new Ref();

			// init object
			_139505167880518
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55");

			_139505167880518.setDisplayName("Threshold 2 CO2");

			_139505167880518.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
					_139505167880518.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880518.setHref(new Uri("threshold_2_co2/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880518);

			// create generic object		
			Ref _139505167880519 = new Ref();

			// init object
			_139505167880519
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60");

			_139505167880519.setDisplayName("Threshold 3 CO2");

			_139505167880519.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
					_139505167880519.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880519.setHref(new Uri("threshold_3_co2/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880519);

			// create generic object		
			Ref _139505167880620 = new Ref();

			// init object
			_139505167880620
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1");

			_139505167880620.setDisplayName("CO2 Value");

			_139505167880620.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
					_139505167880620.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880620.setHref(new Uri("co2_value/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880620);

			// create generic object		
			Ref _139505167880721 = new Ref();

			// init object
			_139505167880721
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2");

			_139505167880721.setDisplayName("Rel. humidity value");

			_139505167880721.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
					_139505167880721.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880721.setHref(new Uri("rel_humidity_value/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880721);

			// create generic object		
			Ref _139505167880822 = new Ref();

			// init object
			_139505167880822
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10");

			_139505167880822.setDisplayName("Switch outer left");

			_139505167880822.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
					_139505167880822.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880822.setHref(new Uri("switch_outer_left/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880822);

			// create generic object		
			Ref _139505167880823 = new Ref();

			// init object
			_139505167880823
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18");

			_139505167880823.setDisplayName("Switch centre left");

			_139505167880823.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
					_139505167880823.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880823.setHref(new Uri("switch_centre_left/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880823);

			// create generic object		
			Ref _139505167880824 = new Ref();

			// init object
			_139505167880824
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

			_139505167880824.setDisplayName("Dimming outer right");

			_139505167880824.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
					_139505167880824.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880824.setHref(new Uri("dimming_outer_right/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880824);

			// create generic object		
			Ref _139505167880825 = new Ref();

			// init object
			_139505167880825
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

			_139505167880825.setDisplayName("Dimming On / Off outer right");

			_139505167880825.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
					_139505167880825.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880825.setHref(new Uri("dimming_on_off_outer_right/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880825);

			// create generic object		
			Ref _139505167880926 = new Ref();

			// init object
			_139505167880926
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

			_139505167880926.setDisplayName("Switch centre right");

			_139505167880926.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
					_139505167880926.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880926.setHref(new Uri("switch_centre_right/1"));

			// add to parent (containment)
			_13950516788018.add(_139505167880926);

			// create generic object		
			List _139505167880927 = new List();

			// init object
			_139505167880927.setName("entities");

			_139505167880927.setIs(new Contract("bas:Entities"));

			_139505167880927.setOf(new Contract("obix:ref bas:Entity"));

			// add existing translations
			if (translations.containsKey("entities")) {
				for (ArrayList<String> entry : translations.get("entities")) {
					_139505167880927.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880927.setHref(new Uri("/networks/office/entities"));

			// add as hidden object to object broker

			_139505167880927.setHidden(true);
			objectBroker.addObj(_139505167880927, false);

			// create generic object		
			Ref _139505167880928 = new Ref();

			// init object
			_139505167880928.setName("P-0341-0_DI-1");

			_139505167880928.setDisplayName("Shutter switch N 522/02");

			_139505167880928.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-1")) {
					_139505167880928.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167880928.setHref(new Uri("shutter_switch_n_522_02/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167880928);

			// create generic object		
			Ref _139505167881029 = new Ref();

			// init object
			_139505167881029.setName("P-0341-0_DI-2");

			_139505167881029.setDisplayName("Universal dimmer N 527");

			_139505167881029.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2")) {
					_139505167881029.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881029.setHref(new Uri("universal_dimmer_n_527/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881029);

			// create generic object		
			Ref _139505167881030 = new Ref();

			// init object
			_139505167881030.setName("P-0341-0_DI-3");

			_139505167881030
					.setDisplayName("Switching actuator N 567/01, (8 Amp)");

			_139505167881030.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3")) {
					_139505167881030.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881030.setHref(new Uri(
					"switching_actuator_n_567_01_8_amp/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881030);

			// create generic object		
			Ref _139505167881031 = new Ref();

			// init object
			_139505167881031.setName("P-0341-0_DI-11");

			_139505167881031.setDisplayName("Temperature Sensor N 258/02");

			_139505167881031.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-11")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11")) {
					_139505167881031.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881031.setHref(new Uri("temperature_sensor_n_258_02/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881031);

			// create generic object		
			Ref _139505167881132 = new Ref();

			// init object
			_139505167881132.setName("P-0341-0_DI-9");

			_139505167881132
					.setDisplayName("Push button 2-fold UP 211 DELTA studio (red lens)");

			_139505167881132.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-9")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-9")) {
					_139505167881132.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881132.setHref(new Uri(
					"push_button_2_fold_up_211_delta_studio_red_lens/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881132);

			// create generic object		
			Ref _139505167881133 = new Ref();

			// init object
			_139505167881133.setName("P-0341-0_DI-7");

			_139505167881133
					.setDisplayName("KNX CO², Humidity and Temperature Sensor");

			_139505167881133.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-7")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7")) {
					_139505167881133.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881133.setHref(new Uri(
					"knx_co2_humidity_and_temperature_sensor/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881133);

			// create generic object		
			Ref _139505167881134 = new Ref();

			// init object
			_139505167881134.setName("P-0341-0_DI-10");

			_139505167881134
					.setDisplayName("Push button 4-f UP 245 DELTA profil (without sym)");

			_139505167881134.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10")) {
					_139505167881134.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881134.setHref(new Uri(
					"push_button_4_f_up_245_delta_profil_without_sym/1"));

			// add to parent (containment)
			_139505167880927.add(_139505167881134);

			// create generic object		
			Obj _139505167881235 = new Obj();

			// init object
			_139505167881235.setName("functional");

			_139505167881235.setIs(new Contract("bas:Functional"));

			// add existing translations
			if (translations.containsKey("functional")) {
				for (ArrayList<String> entry : translations.get("functional")) {
					_139505167881235.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881235.setHref(new Uri("/networks/office/functional"));

			// add as hidden object to object broker

			_139505167881235.setHidden(true);
			objectBroker.addObj(_139505167881235, false);

			// create generic object		
			List _139505167881236 = new List();

			// init object
			_139505167881236.setName("groups");

			_139505167881236.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_139505167881236.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881236.setHref(new Uri("groups"));

			// add to parent (containment)
			_139505167881235.add(_139505167881236);

			// create generic object		
			Obj _139505167881237 = new Obj();

			// init object
			_139505167881237.setName("P-0341-0_GR-1");

			_139505167881237.setDisplayName("All component");

			_139505167881237.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-1")) {
					_139505167881237.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881237.setHref(new Uri("all_component"));

			// add to parent (containment)
			_139505167881236.add(_139505167881237);

			// create generic object		
			Int _139505167881238 = new Int();

			// init object
			_139505167881238.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167881238.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881238.setHref(new Uri("address"));

			// set value
			_139505167881238.set(2048);

			// add to parent (containment)
			_139505167881237.add(_139505167881238);

			// create generic object		
			List _139505167881339 = new List();

			// init object
			_139505167881339.setName("groups");

			_139505167881339.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_139505167881339.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881339.setHref(new Uri("groups"));

			// add to parent (containment)
			_139505167881237.add(_139505167881339);

			// create generic object		
			Obj _139505167881340 = new Obj();

			// init object
			_139505167881340.setName("P-0341-0_GR-4");

			_139505167881340.setDisplayName("Sun Blind");

			_139505167881340.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-4")) {
					_139505167881340.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881340.setHref(new Uri("sun_blind"));

			// add to parent (containment)
			_139505167881339.add(_139505167881340);

			// create generic object		
			Int _139505167881341 = new Int();

			// init object
			_139505167881341.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167881341.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881341.setHref(new Uri("address"));

			// set value
			_139505167881341.set(2560);

			// add to parent (containment)
			_139505167881340.add(_139505167881341);

			// create generic object		
			Obj _139505167881442 = new Obj();

			// init object
			_139505167881442.setName("P-0341-0_GR-2");

			_139505167881442.setDisplayName("Light");

			_139505167881442.setDisplay("Contains groups for lighting");

			_139505167881442.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-2")) {
					_139505167881442.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881442.setHref(new Uri("light"));

			// add to parent (containment)
			_139505167881339.add(_139505167881442);

			// create generic object		
			Int _139505167881443 = new Int();

			// init object
			_139505167881443.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167881443.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881443.setHref(new Uri("address"));

			// set value
			_139505167881443.set(2048);

			// add to parent (containment)
			_139505167881442.add(_139505167881443);

			// create generic object		
			List _139505167881444 = new List();

			// init object
			_139505167881444.setName("groups");

			_139505167881444.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_139505167881444.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881444.setHref(new Uri("groups"));

			// add to parent (containment)
			_139505167881442.add(_139505167881444);

			// create generic object		
			Obj _139505167881445 = new Obj();

			// init object
			_139505167881445.setName("P-0341-0_GA-1");

			_139505167881445.setDisplayName("Light on/off");

			_139505167881445.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-1")) {
					_139505167881445.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881445.setHref(new Uri("light_on_off"));

			// add to parent (containment)
			_139505167881444.add(_139505167881445);

			// create generic object		
			Int _139505167881546 = new Int();

			// init object
			_139505167881546.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167881546.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881546.setHref(new Uri("address"));

			// set value
			_139505167881546.set(2048);

			// add to parent (containment)
			_139505167881445.add(_139505167881546);

			// create generic object		
			Ref _139505167881547 = new Ref();

			// init object
			_139505167881547.setName("function");

			_139505167881547.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167881547.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881547.setHref(new Uri(
					"/networks/office/datapoints/switch_channel_a/1"));

			// add to parent (containment)
			_139505167881445.add(_139505167881547);

			// create generic object		
			List _139505167881548 = new List();

			// init object
			_139505167881548.setName("instances");

			_139505167881548.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167881548.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881548.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167881445.add(_139505167881548);

			// create generic object		
			Obj _139505167881549 = new Obj();

			// init object
			_139505167881549
					.setName("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4");

			_139505167881549.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
					_139505167881549.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881549.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167881548.add(_139505167881549);

			// create generic object		
			Enum _139505167881650 = new Enum();

			// init object
			_139505167881650.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167881650.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881650.setHref(new Uri("connector"));

			// set value
			_139505167881650.set("send");
			_139505167881650.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167881549.add(_139505167881650);

			// create generic object		
			Ref _139505167881651 = new Ref();

			// init object
			_139505167881651.setName("reference");

			_139505167881651.setDisplayName("Switch, Channel A");

			_139505167881651.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167881651.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881651.setHref(new Uri(
					"/networks/office/datapoints/switch_channel_a/1"));

			// add to parent (containment)
			_139505167881549.add(_139505167881651);

			// create generic object		
			Obj _139505167881652 = new Obj();

			// init object
			_139505167881652
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

			_139505167881652.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
					_139505167881652.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881652.setHref(new Uri("2"));

			// add to parent (containment)
			_139505167881548.add(_139505167881652);

			// create generic object		
			Enum _139505167881653 = new Enum();

			// init object
			_139505167881653.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167881653.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881653.setHref(new Uri("connector"));

			// set value
			_139505167881653.set("send");
			_139505167881653.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167881652.add(_139505167881653);

			// create generic object		
			Ref _139505167881754 = new Ref();

			// init object
			_139505167881754.setName("reference");

			_139505167881754.setDisplayName("Switch centre right");

			_139505167881754.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167881754.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881754.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_right/1"));

			// add to parent (containment)
			_139505167881652.add(_139505167881754);

			// create generic object		
			Obj _139505167881755 = new Obj();

			// init object
			_139505167881755.setName("P-0341-0_GA-2");

			_139505167881755.setDisplayName("Light Dimming");

			_139505167881755.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-2")) {
					_139505167881755.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881755.setHref(new Uri("light_dimming"));

			// add to parent (containment)
			_139505167881444.add(_139505167881755);

			// create generic object		
			Int _139505167881756 = new Int();

			// init object
			_139505167881756.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167881756.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881756.setHref(new Uri("address"));

			// set value
			_139505167881756.set(2049);

			// add to parent (containment)
			_139505167881755.add(_139505167881756);

			// create generic object		
			Ref _139505167881757 = new Ref();

			// init object
			_139505167881757.setName("function");

			_139505167881757.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167881757.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881757.setHref(new Uri(
					"/networks/office/datapoints/dimming/1"));

			// add to parent (containment)
			_139505167881755.add(_139505167881757);

			// create generic object		
			List _139505167881858 = new List();

			// init object
			_139505167881858.setName("instances");

			_139505167881858.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167881858.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881858.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167881755.add(_139505167881858);

			// create generic object		
			Obj _139505167881859 = new Obj();

			// init object
			_139505167881859
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1");

			_139505167881859.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
					_139505167881859.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881859.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167881858.add(_139505167881859);

			// create generic object		
			Enum _139505167881860 = new Enum();

			// init object
			_139505167881860.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167881860.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881860.setHref(new Uri("connector"));

			// set value
			_139505167881860.set("send");
			_139505167881860.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167881859.add(_139505167881860);

			// create generic object		
			Ref _139505167881861 = new Ref();

			// init object
			_139505167881861.setName("reference");

			_139505167881861.setDisplayName("Dimming");

			_139505167881861.setIs(new Contract(
					"bas:Datapoint bas:DPST-3-7 bas:DPT-3 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167881861.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881861.setHref(new Uri(
					"/networks/office/datapoints/dimming/1"));

			// add to parent (containment)
			_139505167881859.add(_139505167881861);

			// create generic object		
			Obj _139505167881962 = new Obj();

			// init object
			_139505167881962
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

			_139505167881962.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
					_139505167881962.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881962.setHref(new Uri("2"));

			// add to parent (containment)
			_139505167881858.add(_139505167881962);

			// create generic object		
			Enum _139505167881963 = new Enum();

			// init object
			_139505167881963.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167881963.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881963.setHref(new Uri("connector"));

			// set value
			_139505167881963.set("send");
			_139505167881963.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167881962.add(_139505167881963);

			// create generic object		
			Ref _139505167881964 = new Ref();

			// init object
			_139505167881964.setName("reference");

			_139505167881964.setDisplayName("Dimming outer right");

			_139505167881964.setIs(new Contract(
					"bas:Datapoint bas:DPST-3-7 bas:DPT-3 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167881964.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881964.setHref(new Uri(
					"/networks/office/datapoints/dimming_outer_right/1"));

			// add to parent (containment)
			_139505167881962.add(_139505167881964);

			// create generic object		
			Obj _139505167881965 = new Obj();

			// init object
			_139505167881965.setName("P-0341-0_GA-13");

			_139505167881965.setDisplayName("Light on/off Dimmer");

			_139505167881965.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-13")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-13")) {
					_139505167881965.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167881965.setHref(new Uri("light_on_off_dimmer"));

			// add to parent (containment)
			_139505167881444.add(_139505167881965);

			// create generic object		
			Int _139505167882066 = new Int();

			// init object
			_139505167882066.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167882066.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882066.setHref(new Uri("address"));

			// set value
			_139505167882066.set(2050);

			// add to parent (containment)
			_139505167881965.add(_139505167882066);

			// create generic object		
			Ref _139505167882067 = new Ref();

			// init object
			_139505167882067.setName("function");

			_139505167882067.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167882067.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882067.setHref(new Uri(
					"/networks/office/datapoints/switch_status/1"));

			// add to parent (containment)
			_139505167881965.add(_139505167882067);

			// create generic object		
			List _139505167882068 = new List();

			// init object
			_139505167882068.setName("instances");

			_139505167882068.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167882068.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882068.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167881965.add(_139505167882068);

			// create generic object		
			Obj _139505167882069 = new Obj();

			// init object
			_139505167882069
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0");

			_139505167882069.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
					_139505167882069.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882069.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167882068.add(_139505167882069);

			// create generic object		
			Enum _139505167882170 = new Enum();

			// init object
			_139505167882170.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167882170.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882170.setHref(new Uri("connector"));

			// set value
			_139505167882170.set("send");
			_139505167882170.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167882069.add(_139505167882170);

			// create generic object		
			Ref _139505167882171 = new Ref();

			// init object
			_139505167882171.setName("reference");

			_139505167882171.setDisplayName("Switch, Status");

			_139505167882171.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167882171.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882171.setHref(new Uri(
					"/networks/office/datapoints/switch_status/1"));

			// add to parent (containment)
			_139505167882069.add(_139505167882171);

			// create generic object		
			Obj _139505167882172 = new Obj();

			// init object
			_139505167882172
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

			_139505167882172.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
					_139505167882172.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882172.setHref(new Uri("2"));

			// add to parent (containment)
			_139505167882068.add(_139505167882172);

			// create generic object		
			Enum _139505167882173 = new Enum();

			// init object
			_139505167882173.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167882173.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882173.setHref(new Uri("connector"));

			// set value
			_139505167882173.set("send");
			_139505167882173.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167882172.add(_139505167882173);

			// create generic object		
			Ref _139505167882274 = new Ref();

			// init object
			_139505167882274.setName("reference");

			_139505167882274.setDisplayName("Dimming On / Off outer right");

			_139505167882274.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167882274.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882274
					.setHref(new Uri(
							"/networks/office/datapoints/dimming_on_off_outer_right/1"));

			// add to parent (containment)
			_139505167882172.add(_139505167882274);

			// create generic object		
			Obj _139505167882275 = new Obj();

			// init object
			_139505167882275.setName("P-0341-0_GA-14");

			_139505167882275.setDisplayName("Light Status");

			_139505167882275.setDisplay("Status of dimming actuator");

			_139505167882275.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-14")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-14")) {
					_139505167882275.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882275.setHref(new Uri("light_status"));

			// add to parent (containment)
			_139505167881444.add(_139505167882275);

			// create generic object		
			Int _139505167882276 = new Int();

			// init object
			_139505167882276.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167882276.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882276.setHref(new Uri("address"));

			// set value
			_139505167882276.set(2051);

			// add to parent (containment)
			_139505167882275.add(_139505167882276);

			// create generic object		
			Ref _139505167882277 = new Ref();

			// init object
			_139505167882277.setName("function");

			_139505167882277.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167882277.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882277.setHref(new Uri(
					"/networks/office/datapoints/status/1"));

			// add to parent (containment)
			_139505167882275.add(_139505167882277);

			// create generic object		
			List _139505167882378 = new List();

			// init object
			_139505167882378.setName("instances");

			_139505167882378.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167882378.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882378.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167882275.add(_139505167882378);

			// create generic object		
			Obj _139505167882379 = new Obj();

			// init object
			_139505167882379
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3");

			_139505167882379.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
					_139505167882379.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882379.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167882378.add(_139505167882379);

			// create generic object		
			Enum _139505167882380 = new Enum();

			// init object
			_139505167882380.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167882380.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882380.setHref(new Uri("connector"));

			// set value
			_139505167882380.set("send");
			_139505167882380.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167882379.add(_139505167882380);

			// create generic object		
			Ref _139505167882381 = new Ref();

			// init object
			_139505167882381.setName("reference");

			_139505167882381.setDisplayName("Status");

			_139505167882381.setIs(new Contract(
					"bas:Datapoint bas:DPST-5-1 bas:DPT-5-A bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167882381.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882381.setHref(new Uri(
					"/networks/office/datapoints/status/1"));

			// add to parent (containment)
			_139505167882379.add(_139505167882381);

			// create generic object		
			Obj _139505167882382 = new Obj();

			// init object
			_139505167882382.setName("P-0341-0_GR-3");

			_139505167882382.setDisplayName("Temperature");

			_139505167882382
					.setDisplay("Contains groups for temperature reading	");

			_139505167882382.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-3")) {
					_139505167882382.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882382.setHref(new Uri("temperature"));

			// add to parent (containment)
			_139505167881339.add(_139505167882382);

			// create generic object		
			Int _139505167882483 = new Int();

			// init object
			_139505167882483.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167882483.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882483.setHref(new Uri("address"));

			// set value
			_139505167882483.set(2304);

			// add to parent (containment)
			_139505167882382.add(_139505167882483);

			// create generic object		
			List _139505167882484 = new List();

			// init object
			_139505167882484.setName("groups");

			_139505167882484.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_139505167882484.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882484.setHref(new Uri("groups"));

			// add to parent (containment)
			_139505167882382.add(_139505167882484);

			// create generic object		
			Obj _139505167882485 = new Obj();

			// init object
			_139505167882485.setName("P-0341-0_GA-3");

			_139505167882485.setDisplayName("Temperature 1");

			_139505167882485.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-3")) {
					_139505167882485.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882485.setHref(new Uri("temperature_1"));

			// add to parent (containment)
			_139505167882484.add(_139505167882485);

			// create generic object		
			Int _139505167882486 = new Int();

			// init object
			_139505167882486.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167882486.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882486.setHref(new Uri("address"));

			// set value
			_139505167882486.set(2304);

			// add to parent (containment)
			_139505167882485.add(_139505167882486);

			// create generic object		
			Ref _139505167882587 = new Ref();

			// init object
			_139505167882587.setName("function");

			_139505167882587.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167882587.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882587.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_a/1"));

			// add to parent (containment)
			_139505167882485.add(_139505167882587);

			// create generic object		
			List _139505167882588 = new List();

			// init object
			_139505167882588.setName("instances");

			_139505167882588.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167882588.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882588.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167882485.add(_139505167882588);

			// create generic object		
			Obj _139505167882589 = new Obj();

			// init object
			_139505167882589
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2");

			_139505167882589.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
					_139505167882589.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882589.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167882588.add(_139505167882589);

			// create generic object		
			Enum _139505167882690 = new Enum();

			// init object
			_139505167882690.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167882690.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882690.setHref(new Uri("connector"));

			// set value
			_139505167882690.set("send");
			_139505167882690.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167882589.add(_139505167882690);

			// create generic object		
			Ref _139505167882691 = new Ref();

			// init object
			_139505167882691.setName("reference");

			_139505167882691.setDisplayName("Temperature, Channel A");

			_139505167882691.setIs(new Contract(
					"bas:Datapoint bas:DPST-9-1 bas:DPT-9 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167882691.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882691.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_a/1"));

			// add to parent (containment)
			_139505167882589.add(_139505167882691);

			// create generic object		
			Obj _139505167882692 = new Obj();

			// init object
			_139505167882692.setName("P-0341-0_GA-4");

			_139505167882692.setDisplayName("Temperature 2");

			_139505167882692.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-4")) {
					_139505167882692.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882692.setHref(new Uri("temperature_2"));

			// add to parent (containment)
			_139505167882484.add(_139505167882692);

			// create generic object		
			Int _139505167882793 = new Int();

			// init object
			_139505167882793.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_139505167882793.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882793.setHref(new Uri("address"));

			// set value
			_139505167882793.set(2305);

			// add to parent (containment)
			_139505167882692.add(_139505167882793);

			// create generic object		
			Ref _139505167882794 = new Ref();

			// init object
			_139505167882794.setName("function");

			_139505167882794.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_139505167882794.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882794.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_b/1"));

			// add to parent (containment)
			_139505167882692.add(_139505167882794);

			// create generic object		
			List _139505167882795 = new List();

			// init object
			_139505167882795.setName("instances");

			_139505167882795.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_139505167882795.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882795.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167882692.add(_139505167882795);

			// create generic object		
			Obj _139505167882796 = new Obj();

			// init object
			_139505167882796
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3");

			_139505167882796.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
					_139505167882796.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882796.setHref(new Uri("1"));

			// add to parent (containment)
			_139505167882795.add(_139505167882796);

			// create generic object		
			Enum _139505167882897 = new Enum();

			// init object
			_139505167882897.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_139505167882897.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882897.setHref(new Uri("connector"));

			// set value
			_139505167882897.set("send");
			_139505167882897.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_139505167882796.add(_139505167882897);

			// create generic object		
			Ref _139505167882898 = new Ref();

			// init object
			_139505167882898.setName("reference");

			_139505167882898.setDisplayName("Temperature, Channel B");

			_139505167882898.setIs(new Contract(
					"bas:Datapoint bas:DPST-9-1 bas:DPT-9 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_139505167882898.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882898.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_b/1"));

			// add to parent (containment)
			_139505167882796.add(_139505167882898);

			// create generic object		
			Obj _139505167882899 = new Obj();

			// init object
			_139505167882899.setName("P-0341-0_GA-5");

			_139505167882899.setDisplayName("Temperature 3");

			_139505167882899.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-5")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-5")) {
					_139505167882899.addTranslation(entry.get(0), entry.get(1),
							entry.get(2));
				}
			}

			// set uri
			_139505167882899.setHref(new Uri("temperature_3"));

			// add to parent (containment)
			_139505167882484.add(_139505167882899);

			// create generic object		
			Int _1395051678829100 = new Int();

			// init object
			_1395051678829100.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678829100.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678829100.setHref(new Uri("address"));

			// set value
			_1395051678829100.set(2306);

			// add to parent (containment)
			_139505167882899.add(_1395051678829100);

			// create generic object		
			Ref _1395051678829101 = new Ref();

			// init object
			_1395051678829101.setName("function");

			_1395051678829101.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678829101.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678829101.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_c/1"));

			// add to parent (containment)
			_139505167882899.add(_1395051678829101);

			// create generic object		
			List _1395051678829102 = new List();

			// init object
			_1395051678829102.setName("instances");

			_1395051678829102.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678829102.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678829102.setHref(new Uri("instances"));

			// add to parent (containment)
			_139505167882899.add(_1395051678829102);

			// create generic object		
			Obj _1395051678829103 = new Obj();

			// init object
			_1395051678829103
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1");

			_1395051678829103.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
					_1395051678829103.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678829103.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678829102.add(_1395051678829103);

			// create generic object		
			Enum _1395051678829104 = new Enum();

			// init object
			_1395051678829104.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678829104.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678829104.setHref(new Uri("connector"));

			// set value
			_1395051678829104.set("send");
			_1395051678829104.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678829103.add(_1395051678829104);

			// create generic object		
			Ref _1395051678830105 = new Ref();

			// init object
			_1395051678830105.setName("reference");

			_1395051678830105.setDisplayName("Temperature, Channel C");

			_1395051678830105.setIs(new Contract(
					"bas:Datapoint bas:DPST-9-1 bas:DPT-9 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678830105.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678830105.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_c/1"));

			// add to parent (containment)
			_1395051678829103.add(_1395051678830105);

			// create generic object		
			Obj _1395051678830106 = new Obj();

			// init object
			_1395051678830106.setName("P-0341-0_GA-6");

			_1395051678830106.setDisplayName("Temperature 4");

			_1395051678830106.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-6")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-6")) {
					_1395051678830106.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678830106.setHref(new Uri("temperature_4"));

			// add to parent (containment)
			_139505167882484.add(_1395051678830106);

			// create generic object		
			Int _1395051678830107 = new Int();

			// init object
			_1395051678830107.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678830107.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678830107.setHref(new Uri("address"));

			// set value
			_1395051678830107.set(2307);

			// add to parent (containment)
			_1395051678830106.add(_1395051678830107);

			// create generic object		
			Ref _1395051678830108 = new Ref();

			// init object
			_1395051678830108.setName("function");

			_1395051678830108.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678830108.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678830108.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_d/1"));

			// add to parent (containment)
			_1395051678830106.add(_1395051678830108);

			// create generic object		
			List _1395051678831109 = new List();

			// init object
			_1395051678831109.setName("instances");

			_1395051678831109.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678831109.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678831109.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678830106.add(_1395051678831109);

			// create generic object		
			Obj _1395051678831110 = new Obj();

			// init object
			_1395051678831110
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4");

			_1395051678831110.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
					_1395051678831110.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678831110.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678831109.add(_1395051678831110);

			// create generic object		
			Enum _1395051678831111 = new Enum();

			// init object
			_1395051678831111.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678831111.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678831111.setHref(new Uri("connector"));

			// set value
			_1395051678831111.set("send");
			_1395051678831111.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678831110.add(_1395051678831111);

			// create generic object		
			Ref _1395051678831112 = new Ref();

			// init object
			_1395051678831112.setName("reference");

			_1395051678831112.setDisplayName("Temperature, Channel D");

			_1395051678831112.setIs(new Contract(
					"bas:Datapoint bas:DPST-9-1 bas:DPT-9 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678831112.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678831112.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_d/1"));

			// add to parent (containment)
			_1395051678831110.add(_1395051678831112);

			// create generic object		
			Obj _1395051678832113 = new Obj();

			// init object
			_1395051678832113.setName("P-0341-0_GR-5");

			_1395051678832113.setDisplayName("Other");

			_1395051678832113.setDisplay("Contains several groups");

			_1395051678832113.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-5")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-5")) {
					_1395051678832113.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678832113.setHref(new Uri("other"));

			// add to parent (containment)
			_139505167881339.add(_1395051678832113);

			// create generic object		
			Int _1395051678832114 = new Int();

			// init object
			_1395051678832114.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678832114.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678832114.setHref(new Uri("address"));

			// set value
			_1395051678832114.set(2816);

			// add to parent (containment)
			_1395051678832113.add(_1395051678832114);

			// create generic object		
			List _1395051678832115 = new List();

			// init object
			_1395051678832115.setName("groups");

			_1395051678832115.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_1395051678832115.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678832115.setHref(new Uri("groups"));

			// add to parent (containment)
			_1395051678832113.add(_1395051678832115);

			// create generic object		
			Obj _1395051678832116 = new Obj();

			// init object
			_1395051678832116.setName("P-0341-0_GA-8");

			_1395051678832116.setDisplayName("CO2");

			_1395051678832116.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-8")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-8")) {
					_1395051678832116.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678832116.setHref(new Uri("co2"));

			// add to parent (containment)
			_1395051678832115.add(_1395051678832116);

			// create generic object		
			Int _1395051678833117 = new Int();

			// init object
			_1395051678833117.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678833117.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678833117.setHref(new Uri("address"));

			// set value
			_1395051678833117.set(2817);

			// add to parent (containment)
			_1395051678832116.add(_1395051678833117);

			// create generic object		
			Ref _1395051678833118 = new Ref();

			// init object
			_1395051678833118.setName("function");

			_1395051678833118.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678833118.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678833118.setHref(new Uri(
					"/networks/office/datapoints/co2_value/1"));

			// add to parent (containment)
			_1395051678832116.add(_1395051678833118);

			// create generic object		
			List _1395051678833119 = new List();

			// init object
			_1395051678833119.setName("instances");

			_1395051678833119.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678833119.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678833119.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678832116.add(_1395051678833119);

			// create generic object		
			Obj _1395051678833120 = new Obj();

			// init object
			_1395051678833120
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1");

			_1395051678833120.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
					_1395051678833120.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678833120.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678833119.add(_1395051678833120);

			// create generic object		
			Enum _1395051678833121 = new Enum();

			// init object
			_1395051678833121.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678833121.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678833121.setHref(new Uri("connector"));

			// set value
			_1395051678833121.set("receive");
			_1395051678833121.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678833120.add(_1395051678833121);

			// create generic object		
			Ref _1395051678834122 = new Ref();

			// init object
			_1395051678834122.setName("reference");

			_1395051678834122.setDisplayName("CO2 Value");

			_1395051678834122.setIs(new Contract(
					"bas:Datapoint bas:DPST-9-8 bas:DPT-9 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678834122.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678834122.setHref(new Uri(
					"/networks/office/datapoints/co2_value/1"));

			// add to parent (containment)
			_1395051678833120.add(_1395051678834122);

			// create generic object		
			Obj _1395051678834123 = new Obj();

			// init object
			_1395051678834123.setName("P-0341-0_GA-9");

			_1395051678834123.setDisplayName("Relative Humidity");

			_1395051678834123.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-9")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-9")) {
					_1395051678834123.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678834123.setHref(new Uri("relative_humidity"));

			// add to parent (containment)
			_1395051678832115.add(_1395051678834123);

			// create generic object		
			Int _1395051678834124 = new Int();

			// init object
			_1395051678834124.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678834124.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678834124.setHref(new Uri("address"));

			// set value
			_1395051678834124.set(2818);

			// add to parent (containment)
			_1395051678834123.add(_1395051678834124);

			// create generic object		
			Ref _1395051678834125 = new Ref();

			// init object
			_1395051678834125.setName("function");

			_1395051678834125.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678834125.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678834125.setHref(new Uri(
					"/networks/office/datapoints/rel_humidity_value/1"));

			// add to parent (containment)
			_1395051678834123.add(_1395051678834125);

			// create generic object		
			List _1395051678835126 = new List();

			// init object
			_1395051678835126.setName("instances");

			_1395051678835126.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678835126.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678835126.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678834123.add(_1395051678835126);

			// create generic object		
			Obj _1395051678835127 = new Obj();

			// init object
			_1395051678835127
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2");

			_1395051678835127.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
					_1395051678835127.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678835127.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678835126.add(_1395051678835127);

			// create generic object		
			Enum _1395051678835128 = new Enum();

			// init object
			_1395051678835128.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678835128.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678835128.setHref(new Uri("connector"));

			// set value
			_1395051678835128.set("send");
			_1395051678835128.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678835127.add(_1395051678835128);

			// create generic object		
			Ref _1395051678835129 = new Ref();

			// init object
			_1395051678835129.setName("reference");

			_1395051678835129.setDisplayName("Rel. humidity value");

			_1395051678835129.setIs(new Contract(
					"bas:Datapoint bas:DPST-5-1 bas:DPT-5-A bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678835129.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678835129.setHref(new Uri(
					"/networks/office/datapoints/rel_humidity_value/1"));

			// add to parent (containment)
			_1395051678835127.add(_1395051678835129);

			// create generic object		
			Obj _1395051678835130 = new Obj();

			// init object
			_1395051678835130.setName("P-0341-0_GA-10");

			_1395051678835130.setDisplayName("CO2 Threshold 1");

			_1395051678835130.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-10")) {
					_1395051678835130.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678835130.setHref(new Uri("co2_threshold_1"));

			// add to parent (containment)
			_1395051678832115.add(_1395051678835130);

			// create generic object		
			Int _1395051678836131 = new Int();

			// init object
			_1395051678836131.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678836131.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678836131.setHref(new Uri("address"));

			// set value
			_1395051678836131.set(2819);

			// add to parent (containment)
			_1395051678835130.add(_1395051678836131);

			// create generic object		
			Ref _1395051678836132 = new Ref();

			// init object
			_1395051678836132.setName("function");

			_1395051678836132.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678836132.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678836132.setHref(new Uri(
					"/networks/office/datapoints/threshold_1_co2/1"));

			// add to parent (containment)
			_1395051678835130.add(_1395051678836132);

			// create generic object		
			List _1395051678836133 = new List();

			// init object
			_1395051678836133.setName("instances");

			_1395051678836133.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678836133.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678836133.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678835130.add(_1395051678836133);

			// create generic object		
			Obj _1395051678836134 = new Obj();

			// init object
			_1395051678836134
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50");

			_1395051678836134.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
					_1395051678836134.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678836134.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678836133.add(_1395051678836134);

			// create generic object		
			Enum _1395051678837135 = new Enum();

			// init object
			_1395051678837135.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678837135.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678837135.setHref(new Uri("connector"));

			// set value
			_1395051678837135.set("send");
			_1395051678837135.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678836134.add(_1395051678837135);

			// create generic object		
			Ref _1395051678837136 = new Ref();

			// init object
			_1395051678837136.setName("reference");

			_1395051678837136.setDisplayName("Threshold 1 CO2");

			_1395051678837136.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678837136.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678837136.setHref(new Uri(
					"/networks/office/datapoints/threshold_1_co2/1"));

			// add to parent (containment)
			_1395051678836134.add(_1395051678837136);

			// create generic object		
			Obj _1395051678837137 = new Obj();

			// init object
			_1395051678837137.setName("P-0341-0_GA-11");

			_1395051678837137.setDisplayName("CO2 Threshold 2");

			_1395051678837137.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-11")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-11")) {
					_1395051678837137.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678837137.setHref(new Uri("co2_threshold_2"));

			// add to parent (containment)
			_1395051678832115.add(_1395051678837137);

			// create generic object		
			Int _1395051678837138 = new Int();

			// init object
			_1395051678837138.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678837138.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678837138.setHref(new Uri("address"));

			// set value
			_1395051678837138.set(2820);

			// add to parent (containment)
			_1395051678837137.add(_1395051678837138);

			// create generic object		
			Ref _1395051678837139 = new Ref();

			// init object
			_1395051678837139.setName("function");

			_1395051678837139.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678837139.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678837139.setHref(new Uri(
					"/networks/office/datapoints/threshold_2_co2/1"));

			// add to parent (containment)
			_1395051678837137.add(_1395051678837139);

			// create generic object		
			List _1395051678838140 = new List();

			// init object
			_1395051678838140.setName("instances");

			_1395051678838140.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678838140.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678838140.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678837137.add(_1395051678838140);

			// create generic object		
			Obj _1395051678838141 = new Obj();

			// init object
			_1395051678838141
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55");

			_1395051678838141.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
					_1395051678838141.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678838141.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678838140.add(_1395051678838141);

			// create generic object		
			Enum _1395051678838142 = new Enum();

			// init object
			_1395051678838142.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678838142.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678838142.setHref(new Uri("connector"));

			// set value
			_1395051678838142.set("send");
			_1395051678838142.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678838141.add(_1395051678838142);

			// create generic object		
			Ref _1395051678838143 = new Ref();

			// init object
			_1395051678838143.setName("reference");

			_1395051678838143.setDisplayName("Threshold 2 CO2");

			_1395051678838143.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678838143.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678838143.setHref(new Uri(
					"/networks/office/datapoints/threshold_2_co2/1"));

			// add to parent (containment)
			_1395051678838141.add(_1395051678838143);

			// create generic object		
			Obj _1395051678839144 = new Obj();

			// init object
			_1395051678839144.setName("P-0341-0_GA-12");

			_1395051678839144.setDisplayName("CO2 Threshold 3");

			_1395051678839144.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-12")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-12")) {
					_1395051678839144.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678839144.setHref(new Uri("co2_threshold_3"));

			// add to parent (containment)
			_1395051678832115.add(_1395051678839144);

			// create generic object		
			Int _1395051678839145 = new Int();

			// init object
			_1395051678839145.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678839145.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678839145.setHref(new Uri("address"));

			// set value
			_1395051678839145.set(2821);

			// add to parent (containment)
			_1395051678839144.add(_1395051678839145);

			// create generic object		
			Ref _1395051678839146 = new Ref();

			// init object
			_1395051678839146.setName("function");

			_1395051678839146.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678839146.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678839146.setHref(new Uri(
					"/networks/office/datapoints/threshold_3_co2/1"));

			// add to parent (containment)
			_1395051678839144.add(_1395051678839146);

			// create generic object		
			List _1395051678839147 = new List();

			// init object
			_1395051678839147.setName("instances");

			_1395051678839147.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678839147.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678839147.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678839144.add(_1395051678839147);

			// create generic object		
			Obj _1395051678839148 = new Obj();

			// init object
			_1395051678839148
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60");

			_1395051678839148.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
					_1395051678839148.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678839148.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678839147.add(_1395051678839148);

			// create generic object		
			Enum _1395051678840149 = new Enum();

			// init object
			_1395051678840149.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678840149.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678840149.setHref(new Uri("connector"));

			// set value
			_1395051678840149.set("send");
			_1395051678840149.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678839148.add(_1395051678840149);

			// create generic object		
			Ref _1395051678840150 = new Ref();

			// init object
			_1395051678840150.setName("reference");

			_1395051678840150.setDisplayName("Threshold 3 CO2");

			_1395051678840150.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678840150.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678840150.setHref(new Uri(
					"/networks/office/datapoints/threshold_3_co2/1"));

			// add to parent (containment)
			_1395051678839148.add(_1395051678840150);

			// create generic object		
			Obj _1395051678840151 = new Obj();

			// init object
			_1395051678840151.setName("P-0341-0_GR-6");

			_1395051678840151.setDisplayName("Buttons");

			_1395051678840151.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GR-6")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GR-6")) {
					_1395051678840151.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678840151.setHref(new Uri("buttons"));

			// add to parent (containment)
			_139505167881339.add(_1395051678840151);

			// create generic object		
			Int _1395051678840152 = new Int();

			// init object
			_1395051678840152.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678840152.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678840152.setHref(new Uri("address"));

			// set value
			_1395051678840152.set(3072);

			// add to parent (containment)
			_1395051678840151.add(_1395051678840152);

			// create generic object		
			List _1395051678840153 = new List();

			// init object
			_1395051678840153.setName("groups");

			_1395051678840153.setOf(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("groups")) {
				for (ArrayList<String> entry : translations.get("groups")) {
					_1395051678840153.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678840153.setHref(new Uri("groups"));

			// add to parent (containment)
			_1395051678840151.add(_1395051678840153);

			// create generic object		
			Obj _1395051678841154 = new Obj();

			// init object
			_1395051678841154.setName("P-0341-0_GA-15");

			_1395051678841154.setDisplayName("Button 1");

			_1395051678841154.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-15")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-15")) {
					_1395051678841154.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678841154.setHref(new Uri("button_1"));

			// add to parent (containment)
			_1395051678840153.add(_1395051678841154);

			// create generic object		
			Int _1395051678841155 = new Int();

			// init object
			_1395051678841155.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678841155.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678841155.setHref(new Uri("address"));

			// set value
			_1395051678841155.set(3072);

			// add to parent (containment)
			_1395051678841154.add(_1395051678841155);

			// create generic object		
			Ref _1395051678841156 = new Ref();

			// init object
			_1395051678841156.setName("function");

			_1395051678841156.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678841156.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678841156.setHref(new Uri(
					"/networks/office/datapoints/switch_outer_left/1"));

			// add to parent (containment)
			_1395051678841154.add(_1395051678841156);

			// create generic object		
			List _1395051678841157 = new List();

			// init object
			_1395051678841157.setName("instances");

			_1395051678841157.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678841157.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678841157.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678841154.add(_1395051678841157);

			// create generic object		
			Obj _1395051678842158 = new Obj();

			// init object
			_1395051678842158
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10");

			_1395051678842158.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
					_1395051678842158.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678842158.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678841157.add(_1395051678842158);

			// create generic object		
			Enum _1395051678842159 = new Enum();

			// init object
			_1395051678842159.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678842159.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678842159.setHref(new Uri("connector"));

			// set value
			_1395051678842159.set("send");
			_1395051678842159.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678842158.add(_1395051678842159);

			// create generic object		
			Ref _1395051678842160 = new Ref();

			// init object
			_1395051678842160.setName("reference");

			_1395051678842160.setDisplayName("Switch outer left");

			_1395051678842160.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678842160.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678842160.setHref(new Uri(
					"/networks/office/datapoints/switch_outer_left/1"));

			// add to parent (containment)
			_1395051678842158.add(_1395051678842160);

			// create generic object		
			Obj _1395051678842161 = new Obj();

			// init object
			_1395051678842161.setName("P-0341-0_GA-16");

			_1395051678842161.setDisplayName("Button 2");

			_1395051678842161.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-16")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-16")) {
					_1395051678842161.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678842161.setHref(new Uri("button_2"));

			// add to parent (containment)
			_1395051678840153.add(_1395051678842161);

			// create generic object		
			Int _1395051678842162 = new Int();

			// init object
			_1395051678842162.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678842162.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678842162.setHref(new Uri("address"));

			// set value
			_1395051678842162.set(3073);

			// add to parent (containment)
			_1395051678842161.add(_1395051678842162);

			// create generic object		
			Ref _1395051678843163 = new Ref();

			// init object
			_1395051678843163.setName("function");

			_1395051678843163.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678843163.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678843163.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_left/1"));

			// add to parent (containment)
			_1395051678842161.add(_1395051678843163);

			// create generic object		
			List _1395051678843164 = new List();

			// init object
			_1395051678843164.setName("instances");

			_1395051678843164.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678843164.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678843164.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678842161.add(_1395051678843164);

			// create generic object		
			Obj _1395051678843165 = new Obj();

			// init object
			_1395051678843165
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18");

			_1395051678843165.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
					_1395051678843165.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678843165.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678843164.add(_1395051678843165);

			// create generic object		
			Enum _1395051678844166 = new Enum();

			// init object
			_1395051678844166.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678844166.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678844166.setHref(new Uri("connector"));

			// set value
			_1395051678844166.set("send");
			_1395051678844166.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678843165.add(_1395051678844166);

			// create generic object		
			Ref _1395051678844167 = new Ref();

			// init object
			_1395051678844167.setName("reference");

			_1395051678844167.setDisplayName("Switch centre left");

			_1395051678844167.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678844167.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678844167.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_left/1"));

			// add to parent (containment)
			_1395051678843165.add(_1395051678844167);

			// create generic object		
			Obj _1395051678844168 = new Obj();

			// init object
			_1395051678844168.setName("P-0341-0_GA-17");

			_1395051678844168.setDisplayName("Button 3");

			_1395051678844168.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-17")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-17")) {
					_1395051678844168.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678844168.setHref(new Uri("button_3"));

			// add to parent (containment)
			_1395051678840153.add(_1395051678844168);

			// create generic object		
			Int _1395051678845169 = new Int();

			// init object
			_1395051678845169.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678845169.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678845169.setHref(new Uri("address"));

			// set value
			_1395051678845169.set(3074);

			// add to parent (containment)
			_1395051678844168.add(_1395051678845169);

			// create generic object		
			Ref _1395051678845170 = new Ref();

			// init object
			_1395051678845170.setName("function");

			_1395051678845170.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678845170.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678845170.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_right/1"));

			// add to parent (containment)
			_1395051678844168.add(_1395051678845170);

			// create generic object		
			List _1395051678846171 = new List();

			// init object
			_1395051678846171.setName("instances");

			_1395051678846171.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678846171.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678846171.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678844168.add(_1395051678846171);

			// create generic object		
			Obj _1395051678846172 = new Obj();

			// init object
			_1395051678846172
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

			_1395051678846172.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
					_1395051678846172.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678846172.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678846171.add(_1395051678846172);

			// create generic object		
			Enum _1395051678846173 = new Enum();

			// init object
			_1395051678846173.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678846173.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678846173.setHref(new Uri("connector"));

			// set value
			_1395051678846173.set("receive");
			_1395051678846173.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678846172.add(_1395051678846173);

			// create generic object		
			Ref _1395051678847174 = new Ref();

			// init object
			_1395051678847174.setName("reference");

			_1395051678847174.setDisplayName("Switch centre right");

			_1395051678847174.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678847174.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678847174.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_right/1"));

			// add to parent (containment)
			_1395051678846172.add(_1395051678847174);

			// create generic object		
			Obj _1395051678847175 = new Obj();

			// init object
			_1395051678847175.setName("P-0341-0_GA-18");

			_1395051678847175.setDisplayName("Button 4");

			_1395051678847175.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-18")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-18")) {
					_1395051678847175.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678847175.setHref(new Uri("button_4"));

			// add to parent (containment)
			_1395051678840153.add(_1395051678847175);

			// create generic object		
			Int _1395051678847176 = new Int();

			// init object
			_1395051678847176.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678847176.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678847176.setHref(new Uri("address"));

			// set value
			_1395051678847176.set(3075);

			// add to parent (containment)
			_1395051678847175.add(_1395051678847176);

			// create generic object		
			Ref _1395051678848177 = new Ref();

			// init object
			_1395051678848177.setName("function");

			_1395051678848177.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678848177.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678848177
					.setHref(new Uri(
							"/networks/office/datapoints/dimming_on_off_outer_right/1"));

			// add to parent (containment)
			_1395051678847175.add(_1395051678848177);

			// create generic object		
			List _1395051678848178 = new List();

			// init object
			_1395051678848178.setName("instances");

			_1395051678848178.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678848178.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678848178.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678847175.add(_1395051678848178);

			// create generic object		
			Obj _1395051678848179 = new Obj();

			// init object
			_1395051678848179
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

			_1395051678848179.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
					_1395051678848179.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678848179.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678848178.add(_1395051678848179);

			// create generic object		
			Enum _1395051678848180 = new Enum();

			// init object
			_1395051678848180.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678848180.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678848180.setHref(new Uri("connector"));

			// set value
			_1395051678848180.set("receive");
			_1395051678848180.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678848179.add(_1395051678848180);

			// create generic object		
			Ref _1395051678849181 = new Ref();

			// init object
			_1395051678849181.setName("reference");

			_1395051678849181.setDisplayName("Dimming On / Off outer right");

			_1395051678849181.setIs(new Contract(
					"bas:Datapoint bas:DPST-1-1 bas:DPT-1 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678849181.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678849181
					.setHref(new Uri(
							"/networks/office/datapoints/dimming_on_off_outer_right/1"));

			// add to parent (containment)
			_1395051678848179.add(_1395051678849181);

			// create generic object		
			Obj _1395051678849182 = new Obj();

			// init object
			_1395051678849182.setName("P-0341-0_GA-19");

			_1395051678849182.setDisplayName("Dimmer");

			_1395051678849182.setIs(new Contract("bas:Group"));

			// add existing translations
			if (translations.containsKey("P-0341-0_GA-19")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_GA-19")) {
					_1395051678849182.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678849182.setHref(new Uri("dimmer"));

			// add to parent (containment)
			_1395051678840153.add(_1395051678849182);

			// create generic object		
			Int _1395051678849183 = new Int();

			// init object
			_1395051678849183.setName("address");

			// add existing translations
			if (translations.containsKey("address")) {
				for (ArrayList<String> entry : translations.get("address")) {
					_1395051678849183.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678849183.setHref(new Uri("address"));

			// set value
			_1395051678849183.set(3076);

			// add to parent (containment)
			_1395051678849182.add(_1395051678849183);

			// create generic object		
			Ref _1395051678849184 = new Ref();

			// init object
			_1395051678849184.setName("function");

			_1395051678849184.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("function")) {
				for (ArrayList<String> entry : translations.get("function")) {
					_1395051678849184.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678849184.setHref(new Uri(
					"/networks/office/datapoints/dimming_outer_right/1"));

			// add to parent (containment)
			_1395051678849182.add(_1395051678849184);

			// create generic object		
			List _1395051678860185 = new List();

			// init object
			_1395051678860185.setName("instances");

			_1395051678860185.setOf(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations.containsKey("instances")) {
				for (ArrayList<String> entry : translations.get("instances")) {
					_1395051678860185.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678860185.setHref(new Uri("instances"));

			// add to parent (containment)
			_1395051678849182.add(_1395051678860185);

			// create generic object		
			Obj _1395051678860186 = new Obj();

			// init object
			_1395051678860186
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

			_1395051678860186.setIs(new Contract("bas:InstanceGroup"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
					_1395051678860186.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678860186.setHref(new Uri("1"));

			// add to parent (containment)
			_1395051678860185.add(_1395051678860186);

			// create generic object		
			Enum _1395051678861187 = new Enum();

			// init object
			_1395051678861187.setName("connector");

			// add existing translations
			if (translations.containsKey("connector")) {
				for (ArrayList<String> entry : translations.get("connector")) {
					_1395051678861187.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678861187.setHref(new Uri("connector"));

			// set value
			_1395051678861187.set("receive");
			_1395051678861187.setRange(new Uri("/enums/connector"));

			// add to parent (containment)
			_1395051678860186.add(_1395051678861187);

			// create generic object		
			Ref _1395051678861188 = new Ref();

			// init object
			_1395051678861188.setName("reference");

			_1395051678861188.setDisplayName("Dimming outer right");

			_1395051678861188.setIs(new Contract(
					"bas:Datapoint bas:DPST-3-7 bas:DPT-3 bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("reference")) {
				for (ArrayList<String> entry : translations.get("reference")) {
					_1395051678861188.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678861188.setHref(new Uri(
					"/networks/office/datapoints/dimming_outer_right/1"));

			// add to parent (containment)
			_1395051678860186.add(_1395051678861188);

			// create generic object		
			Obj _1395051678861189 = new Obj();

			// init object
			_1395051678861189.setName("topology");

			_1395051678861189.setIs(new Contract("bas:Topology"));

			// add existing translations
			if (translations.containsKey("topology")) {
				for (ArrayList<String> entry : translations.get("topology")) {
					_1395051678861189.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678861189.setHref(new Uri("/networks/office/topology"));

			// add as hidden object to object broker

			_1395051678861189.setHidden(true);
			objectBroker.addObj(_1395051678861189, false);

			// create generic object		
			Obj _1395051678862190 = new Obj();

			// init object
			_1395051678862190.setName("building");

			_1395051678862190.setIs(new Contract("bas:Building"));

			// add existing translations
			if (translations.containsKey("building")) {
				for (ArrayList<String> entry : translations.get("building")) {
					_1395051678862190.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678862190.setHref(new Uri("/networks/office/building"));

			// add as hidden object to object broker

			_1395051678862190.setHidden(true);
			objectBroker.addObj(_1395051678862190, false);

			// create generic object		
			Obj _1395051678862191 = new Obj();

			// init object
			_1395051678862191.setName("domains");

			_1395051678862191.setIs(new Contract("bas:Domains"));

			// add existing translations
			if (translations.containsKey("domains")) {
				for (ArrayList<String> entry : translations.get("domains")) {
					_1395051678862191.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678862191.setHref(new Uri("/networks/office/domains"));

			// add as hidden object to object broker

			_1395051678862191.setHidden(true);
			objectBroker.addObj(_1395051678862191, false);

			// init datapoint		
			DataPointInit init__1395051678862192 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2"))
				init__1395051678862192
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")));

			init__1395051678862192.setDisplay("Physical Value");

			init__1395051678862192.setDisplayName("Rel. humidity value");

			init__1395051678862192
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2");

			init__1395051678862192.setReadable(true);
			init__1395051678862192.setWritable(false);

			// create datapoint
			DPST_5_1_ImplKnx _1395051678862192 = new DPST_5_1_ImplKnx(
					knxConnector, init__1395051678862192);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
					_1395051678862192.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678862192.setHref(new Uri(
					"/networks/office/datapoints/rel_humidity_value/1"));

			// add as hidden object to object broker

			_1395051678862192.setHidden(true);
			objectBroker.addObj(_1395051678862192, false);

			// create generic object		
			Obj _1395051678862193 = new Obj();

			// init object
			_1395051678862193.setName("P-0341-0_DI-2");

			_1395051678862193.setDisplayName("Universal dimmer N 527");

			_1395051678862193
					.setDisplay("Productinfo - see file:   5271ab01_tpi_e.pdf");

			_1395051678862193.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2")) {
					_1395051678862193.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678862193.setHref(new Uri(
					"/networks/office/entities/universal_dimmer_n_527/1"));

			// add as hidden object to object broker

			_1395051678862193.setHidden(true);
			objectBroker.addObj(_1395051678862193, false);

			// create generic object		
			Str _1395051678863194 = new Str();

			// init object
			_1395051678863194.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678863194.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678863194.setHref(new Uri("manufacturer"));

			// set value
			_1395051678863194.set("Siemens");

			// add to parent (containment)
			_1395051678862193.add(_1395051678863194);

			// create generic object		
			Str _1395051678863195 = new Str();

			// init object
			_1395051678863195.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678863195.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678863195.setHref(new Uri("orderNumber"));

			// set value
			_1395051678863195.set("5WG1 527-1AB01");

			// add to parent (containment)
			_1395051678862193.add(_1395051678863195);

			// create generic object		
			List _1395051678863196 = new List();

			// init object
			_1395051678863196.setName("datapoints");

			_1395051678863196.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_1395051678863196.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678863196.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_1395051678862193.add(_1395051678863196);

			// create generic object		
			Ref _1395051678863197 = new Ref();

			// init object
			_1395051678863197
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1");

			_1395051678863197.setDisplayName("Dimming");

			_1395051678863197.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
					_1395051678863197.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678863197.setHref(new Uri(
					"/networks/office/datapoints/dimming/1"));

			// add to parent (containment)
			_1395051678863196.add(_1395051678863197);

			// create generic object		
			Ref _1395051678864198 = new Ref();

			// init object
			_1395051678864198
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3");

			_1395051678864198.setDisplayName("Status");

			_1395051678864198.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
					_1395051678864198.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678864198.setHref(new Uri(
					"/networks/office/datapoints/status/1"));

			// add to parent (containment)
			_1395051678863196.add(_1395051678864198);

			// create generic object		
			Ref _1395051678864199 = new Ref();

			// init object
			_1395051678864199
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0");

			_1395051678864199.setDisplayName("Switch, Status");

			_1395051678864199.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
					_1395051678864199.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678864199.setHref(new Uri(
					"/networks/office/datapoints/switch_status/1"));

			// add to parent (containment)
			_1395051678863196.add(_1395051678864199);

			// create generic object		
			Obj _1395051678864200 = new Obj();

			// init object
			_1395051678864200.setName("P-0341-0_DI-1");

			_1395051678864200.setDisplayName("Shutter switch N 522/02");

			_1395051678864200
					.setDisplay("Productinfo - see file:   5221ab02_tpi_e.pdf");

			_1395051678864200.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-1")) {
					_1395051678864200.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678864200.setHref(new Uri(
					"/networks/office/entities/shutter_switch_n_522_02/1"));

			// add as hidden object to object broker

			_1395051678864200.setHidden(true);
			objectBroker.addObj(_1395051678864200, false);

			// create generic object		
			Str _1395051678864201 = new Str();

			// init object
			_1395051678864201.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678864201.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678864201.setHref(new Uri("manufacturer"));

			// set value
			_1395051678864201.set("Siemens");

			// add to parent (containment)
			_1395051678864200.add(_1395051678864201);

			// create generic object		
			Str _1395051678864202 = new Str();

			// init object
			_1395051678864202.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678864202.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678864202.setHref(new Uri("orderNumber"));

			// set value
			_1395051678864202.set("5WG1 522-1AB02");

			// add to parent (containment)
			_1395051678864200.add(_1395051678864202);

			// init datapoint		
			DataPointInit init__1395051678865203 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2"))
				init__1395051678865203
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")));

			init__1395051678865203.setDisplay("°C-value (EIS5)");

			init__1395051678865203.setDisplayName("Temperature, Channel A");

			init__1395051678865203
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2");

			init__1395051678865203.setReadable(true);
			init__1395051678865203.setWritable(false);

			// create datapoint
			DPST_9_1_ImplKnx _1395051678865203 = new DPST_9_1_ImplKnx(
					knxConnector, init__1395051678865203);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
					_1395051678865203.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678865203.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_a/1"));

			// add as hidden object to object broker

			_1395051678865203.setHidden(true);
			objectBroker.addObj(_1395051678865203, false);

			// init datapoint		
			DataPointInit init__1395051678865204 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38"))
				init__1395051678865204
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")));

			init__1395051678865204.setDisplay("On / Off");

			init__1395051678865204
					.setDisplayName("Dimming On / Off outer right");

			init__1395051678865204
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

			init__1395051678865204.setReadable(false);
			init__1395051678865204.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678865204 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678865204);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
					_1395051678865204.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678865204
					.setHref(new Uri(
							"/networks/office/datapoints/dimming_on_off_outer_right/1"));

			// add as hidden object to object broker

			_1395051678865204.setHidden(true);
			objectBroker.addObj(_1395051678865204, false);

			// create generic object		
			Obj _1395051678865205 = new Obj();

			// init object
			_1395051678865205.setName("P-0341-0_DI-10");

			_1395051678865205
					.setDisplayName("Push button 4-f UP 245 DELTA profil (without sym)");

			_1395051678865205
					.setDisplay("Productinfo - see file:   24_2ab_1_tpi_e.pdf");

			_1395051678865205.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10")) {
					_1395051678865205.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678865205
					.setHref(new Uri(
							"/networks/office/entities/push_button_4_f_up_245_delta_profil_without_sym/1"));

			// add as hidden object to object broker

			_1395051678865205.setHidden(true);
			objectBroker.addObj(_1395051678865205, false);

			// create generic object		
			Str _1395051678866206 = new Str();

			// init object
			_1395051678866206.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678866206.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678866206.setHref(new Uri("manufacturer"));

			// set value
			_1395051678866206.set("Siemens");

			// add to parent (containment)
			_1395051678865205.add(_1395051678866206);

			// create generic object		
			Str _1395051678866207 = new Str();

			// init object
			_1395051678866207.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678866207.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678866207.setHref(new Uri("orderNumber"));

			// set value
			_1395051678866207.set("5WG1 245-2AB_1");

			// add to parent (containment)
			_1395051678865205.add(_1395051678866207);

			// create generic object		
			List _1395051678866208 = new List();

			// init object
			_1395051678866208.setName("datapoints");

			_1395051678866208.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_1395051678866208.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678866208.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_1395051678865205.add(_1395051678866208);

			// create generic object		
			Ref _1395051678866209 = new Ref();

			// init object
			_1395051678866209
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10");

			_1395051678866209.setDisplayName("Switch outer left");

			_1395051678866209.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
					_1395051678866209.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678866209.setHref(new Uri(
					"/networks/office/datapoints/switch_outer_left/1"));

			// add to parent (containment)
			_1395051678866208.add(_1395051678866209);

			// create generic object		
			Ref _1395051678866210 = new Ref();

			// init object
			_1395051678866210
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18");

			_1395051678866210.setDisplayName("Switch centre left");

			_1395051678866210.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
					_1395051678866210.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678866210.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_left/1"));

			// add to parent (containment)
			_1395051678866208.add(_1395051678866210);

			// create generic object		
			Ref _1395051678867211 = new Ref();

			// init object
			_1395051678867211
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

			_1395051678867211.setDisplayName("Dimming outer right");

			_1395051678867211.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
					_1395051678867211.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678867211.setHref(new Uri(
					"/networks/office/datapoints/dimming_outer_right/1"));

			// add to parent (containment)
			_1395051678866208.add(_1395051678867211);

			// create generic object		
			Ref _1395051678867212 = new Ref();

			// init object
			_1395051678867212
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38");

			_1395051678867212.setDisplayName("Dimming On / Off outer right");

			_1395051678867212.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-6_R-38")) {
					_1395051678867212.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678867212
					.setHref(new Uri(
							"/networks/office/datapoints/dimming_on_off_outer_right/1"));

			// add to parent (containment)
			_1395051678866208.add(_1395051678867212);

			// create generic object		
			Ref _1395051678867213 = new Ref();

			// init object
			_1395051678867213
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

			_1395051678867213.setDisplayName("Switch centre right");

			_1395051678867213.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
					_1395051678867213.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678867213.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_right/1"));

			// add to parent (containment)
			_1395051678866208.add(_1395051678867213);

			// init datapoint		
			DataPointInit init__1395051678867214 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1"))
				init__1395051678867214
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")));

			init__1395051678867214.setDisplay("°C-value (EIS5)");

			init__1395051678867214.setDisplayName("Temperature, Channel C");

			init__1395051678867214
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1");

			init__1395051678867214.setReadable(true);
			init__1395051678867214.setWritable(false);

			// create datapoint
			DPST_9_1_ImplKnx _1395051678867214 = new DPST_9_1_ImplKnx(
					knxConnector, init__1395051678867214);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
					_1395051678867214.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678867214.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_c/1"));

			// add as hidden object to object broker

			_1395051678867214.setHidden(true);
			objectBroker.addObj(_1395051678867214, false);

			// create generic object		
			Obj _1395051678867215 = new Obj();

			// init object
			_1395051678867215.setName("P-0341-0_DI-3");

			_1395051678867215
					.setDisplayName("Switching actuator N 567/01, (8 Amp)");

			_1395051678867215
					.setDisplay("Productinfo - see file:   5671ab01_tpi_e.pdf");

			_1395051678867215.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3")) {
					_1395051678867215.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678867215
					.setHref(new Uri(
							"/networks/office/entities/switching_actuator_n_567_01_8_amp/1"));

			// add as hidden object to object broker

			_1395051678867215.setHidden(true);
			objectBroker.addObj(_1395051678867215, false);

			// create generic object		
			Str _1395051678868216 = new Str();

			// init object
			_1395051678868216.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678868216.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678868216.setHref(new Uri("manufacturer"));

			// set value
			_1395051678868216.set("Siemens");

			// add to parent (containment)
			_1395051678867215.add(_1395051678868216);

			// create generic object		
			Str _1395051678868217 = new Str();

			// init object
			_1395051678868217.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678868217.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678868217.setHref(new Uri("orderNumber"));

			// set value
			_1395051678868217.set("5WG1 567-1AB01");

			// add to parent (containment)
			_1395051678867215.add(_1395051678868217);

			// create generic object		
			List _1395051678868218 = new List();

			// init object
			_1395051678868218.setName("datapoints");

			_1395051678868218.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_1395051678868218.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678868218.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_1395051678867215.add(_1395051678868218);

			// create generic object		
			Ref _1395051678868219 = new Ref();

			// init object
			_1395051678868219
					.setName("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4");

			_1395051678868219.setDisplayName("Switch, Channel A");

			_1395051678868219.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
					_1395051678868219.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678868219.setHref(new Uri(
					"/networks/office/datapoints/switch_channel_a/1"));

			// add to parent (containment)
			_1395051678868218.add(_1395051678868219);

			// init datapoint		
			DataPointInit init__1395051678868220 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55"))
				init__1395051678868220
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")));

			init__1395051678868220.setDisplay("Switch");

			init__1395051678868220.setDisplayName("Threshold 2 CO2");

			init__1395051678868220
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55");

			init__1395051678868220.setReadable(true);
			init__1395051678868220.setWritable(false);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678868220 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678868220);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
					_1395051678868220.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678868220.setHref(new Uri(
					"/networks/office/datapoints/threshold_2_co2/1"));

			// add as hidden object to object broker

			_1395051678868220.setHidden(true);
			objectBroker.addObj(_1395051678868220, false);

			// init datapoint		
			DataPointInit init__1395051678869221 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4"))
				init__1395051678869221
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")));

			init__1395051678869221.setDisplay("°C-value (EIS5)");

			init__1395051678869221.setDisplayName("Temperature, Channel D");

			init__1395051678869221
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4");

			init__1395051678869221.setReadable(true);
			init__1395051678869221.setWritable(false);

			// create datapoint
			DPST_9_1_ImplKnx _1395051678869221 = new DPST_9_1_ImplKnx(
					knxConnector, init__1395051678869221);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
					_1395051678869221.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678869221.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_d/1"));

			// add as hidden object to object broker

			_1395051678869221.setHidden(true);
			objectBroker.addObj(_1395051678869221, false);

			// init datapoint		
			DataPointInit init__1395051678869222 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27"))
				init__1395051678869222
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")));

			init__1395051678869222.setDisplay("On / Off");

			init__1395051678869222.setDisplayName("Switch centre right");

			init__1395051678869222
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27");

			init__1395051678869222.setReadable(false);
			init__1395051678869222.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678869222 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678869222);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-4_R-27")) {
					_1395051678869222.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678869222.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_right/1"));

			// add as hidden object to object broker

			_1395051678869222.setHidden(true);
			objectBroker.addObj(_1395051678869222, false);

			// create generic object		
			Obj _1395051678869223 = new Obj();

			// init object
			_1395051678869223.setName("P-0341-0_DI-11");

			_1395051678869223.setDisplayName("Temperature Sensor N 258/02");

			_1395051678869223
					.setDisplay("Productinfo - see file:   2581ab02_tpi_e.pdf");

			_1395051678869223.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-11")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11")) {
					_1395051678869223.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678869223.setHref(new Uri(
					"/networks/office/entities/temperature_sensor_n_258_02/1"));

			// add as hidden object to object broker

			_1395051678869223.setHidden(true);
			objectBroker.addObj(_1395051678869223, false);

			// create generic object		
			Str _1395051678869224 = new Str();

			// init object
			_1395051678869224.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678869224.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678869224.setHref(new Uri("manufacturer"));

			// set value
			_1395051678869224.set("Siemens");

			// add to parent (containment)
			_1395051678869223.add(_1395051678869224);

			// create generic object		
			Str _1395051678870225 = new Str();

			// init object
			_1395051678870225.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678870225.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678870225.setHref(new Uri("orderNumber"));

			// set value
			_1395051678870225.set("5WG1 258-1AB02");

			// add to parent (containment)
			_1395051678869223.add(_1395051678870225);

			// create generic object		
			List _1395051678870226 = new List();

			// init object
			_1395051678870226.setName("datapoints");

			_1395051678870226.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_1395051678870226.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678870226.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_1395051678869223.add(_1395051678870226);

			// create generic object		
			Ref _1395051678870227 = new Ref();

			// init object
			_1395051678870227
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2");

			_1395051678870227.setDisplayName("Temperature, Channel A");

			_1395051678870227.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2")) {
					_1395051678870227.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678870227.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_a/1"));

			// add to parent (containment)
			_1395051678870226.add(_1395051678870227);

			// create generic object		
			Ref _1395051678870228 = new Ref();

			// init object
			_1395051678870228
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3");

			_1395051678870228.setDisplayName("Temperature, Channel B");

			_1395051678870228.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
					_1395051678870228.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678870228.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_b/1"));

			// add to parent (containment)
			_1395051678870226.add(_1395051678870228);

			// create generic object		
			Ref _1395051678870229 = new Ref();

			// init object
			_1395051678870229
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1");

			_1395051678870229.setDisplayName("Temperature, Channel C");

			_1395051678870229.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-2_R-1")) {
					_1395051678870229.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678870229.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_c/1"));

			// add to parent (containment)
			_1395051678870226.add(_1395051678870229);

			// create generic object		
			Ref _1395051678871230 = new Ref();

			// init object
			_1395051678871230
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4");

			_1395051678871230.setDisplayName("Temperature, Channel D");

			_1395051678871230.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-3_R-4")) {
					_1395051678871230.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678871230.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_d/1"));

			// add to parent (containment)
			_1395051678870226.add(_1395051678871230);

			// init datapoint		
			DataPointInit init__1395051678871231 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3"))
				init__1395051678871231
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")));

			init__1395051678871231.setDisplay("°C-value (EIS5)");

			init__1395051678871231.setDisplayName("Temperature, Channel B");

			init__1395051678871231
					.setName("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3");

			init__1395051678871231.setReadable(true);
			init__1395051678871231.setWritable(false);

			// create datapoint
			DPST_9_1_ImplKnx _1395051678871231 = new DPST_9_1_ImplKnx(
					knxConnector, init__1395051678871231);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-1_R-3")) {
					_1395051678871231.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678871231.setHref(new Uri(
					"/networks/office/datapoints/temperature_channel_b/1"));

			// add as hidden object to object broker

			_1395051678871231.setHidden(true);
			objectBroker.addObj(_1395051678871231, false);

			// init datapoint		
			DataPointInit init__1395051678871232 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60"))
				init__1395051678871232
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")));

			init__1395051678871232.setDisplay("Switch");

			init__1395051678871232.setDisplayName("Threshold 3 CO2");

			init__1395051678871232
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60");

			init__1395051678871232.setReadable(true);
			init__1395051678871232.setWritable(false);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678871232 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678871232);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
					_1395051678871232.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678871232.setHref(new Uri(
					"/networks/office/datapoints/threshold_3_co2/1"));

			// add as hidden object to object broker

			_1395051678871232.setHidden(true);
			objectBroker.addObj(_1395051678871232, false);

			// init datapoint		
			DataPointInit init__1395051678871233 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1"))
				init__1395051678871233
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")));

			init__1395051678871233.setDisplay("Physical Value");

			init__1395051678871233.setDisplayName("CO2 Value");

			init__1395051678871233
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1");

			init__1395051678871233.setReadable(true);
			init__1395051678871233.setWritable(false);

			// create datapoint
			DPST_9_8_ImplKnx _1395051678871233 = new DPST_9_8_ImplKnx(
					knxConnector, init__1395051678871233);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
					_1395051678871233.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678871233.setHref(new Uri(
					"/networks/office/datapoints/co2_value/1"));

			// add as hidden object to object broker

			_1395051678871233.setHidden(true);
			objectBroker.addObj(_1395051678871233, false);

			// init datapoint		
			DataPointInit init__1395051678872234 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3"))
				init__1395051678872234
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")));

			init__1395051678872234.setDisplay("8-bit Value");

			init__1395051678872234.setDisplayName("Status");

			init__1395051678872234
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3");

			init__1395051678872234.setReadable(true);
			init__1395051678872234.setWritable(false);

			// create datapoint
			DPST_5_1_ImplKnx _1395051678872234 = new DPST_5_1_ImplKnx(
					knxConnector, init__1395051678872234);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3")) {
					_1395051678872234.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678872234.setHref(new Uri(
					"/networks/office/datapoints/status/1"));

			// add as hidden object to object broker

			_1395051678872234.setHidden(true);
			objectBroker.addObj(_1395051678872234, false);

			// init datapoint		
			DataPointInit init__1395051678872235 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4"))
				init__1395051678872235
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")));

			init__1395051678872235.setDisplay("On / Off");

			init__1395051678872235.setDisplayName("Switch, Channel A");

			init__1395051678872235
					.setName("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4");

			init__1395051678872235.setReadable(false);
			init__1395051678872235.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678872235 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678872235);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4")) {
					_1395051678872235.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678872235.setHref(new Uri(
					"/networks/office/datapoints/switch_channel_a/1"));

			// add as hidden object to object broker

			_1395051678872235.setHidden(true);
			objectBroker.addObj(_1395051678872235, false);

			// init datapoint		
			DataPointInit init__1395051678872236 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18"))
				init__1395051678872236
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")));

			init__1395051678872236.setDisplay("On / Off");

			init__1395051678872236.setDisplayName("Switch centre left");

			init__1395051678872236
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18");

			init__1395051678872236.setReadable(false);
			init__1395051678872236.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678872236 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678872236);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-2_R-18")) {
					_1395051678872236.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678872236.setHref(new Uri(
					"/networks/office/datapoints/switch_centre_left/1"));

			// add as hidden object to object broker

			_1395051678872236.setHidden(true);
			objectBroker.addObj(_1395051678872236, false);

			// create generic object		
			Obj _1395051678872237 = new Obj();

			// init object
			_1395051678872237.setName("P-0341-0_DI-7");

			_1395051678872237
					.setDisplayName("KNX CO², Humidity and Temperature Sensor");

			_1395051678872237.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-7")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7")) {
					_1395051678872237.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678872237
					.setHref(new Uri(
							"/networks/office/entities/knx_co2_humidity_and_temperature_sensor/1"));

			// add as hidden object to object broker

			_1395051678872237.setHidden(true);
			objectBroker.addObj(_1395051678872237, false);

			// create generic object		
			Str _1395051678873238 = new Str();

			// init object
			_1395051678873238.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678873238.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678873238.setHref(new Uri("manufacturer"));

			// set value
			_1395051678873238.set("Schneider Electric Industries SAS");

			// add to parent (containment)
			_1395051678872237.add(_1395051678873238);

			// create generic object		
			Str _1395051678873239 = new Str();

			// init object
			_1395051678873239.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678873239.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678873239.setHref(new Uri("orderNumber"));

			// set value
			_1395051678873239.set("MTN6005-0001");

			// add to parent (containment)
			_1395051678872237.add(_1395051678873239);

			// create generic object		
			List _1395051678873240 = new List();

			// init object
			_1395051678873240.setName("datapoints");

			_1395051678873240.setOf(new Contract("obix:ref bas:Datapoint"));

			// add existing translations
			if (translations.containsKey("datapoints")) {
				for (ArrayList<String> entry : translations.get("datapoints")) {
					_1395051678873240.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678873240.setHref(new Uri("datapoints"));

			// add to parent (containment)
			_1395051678872237.add(_1395051678873240);

			// create generic object		
			Ref _1395051678873241 = new Ref();

			// init object
			_1395051678873241
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50");

			_1395051678873241.setDisplayName("Threshold 1 CO2");

			_1395051678873241.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
					_1395051678873241.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678873241.setHref(new Uri(
					"/networks/office/datapoints/threshold_1_co2/1"));

			// add to parent (containment)
			_1395051678873240.add(_1395051678873241);

			// create generic object		
			Ref _1395051678873242 = new Ref();

			// init object
			_1395051678873242
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55");

			_1395051678873242.setDisplayName("Threshold 2 CO2");

			_1395051678873242.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-5_R-55")) {
					_1395051678873242.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678873242.setHref(new Uri(
					"/networks/office/datapoints/threshold_2_co2/1"));

			// add to parent (containment)
			_1395051678873240.add(_1395051678873242);

			// create generic object		
			Ref _1395051678874243 = new Ref();

			// init object
			_1395051678874243
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60");

			_1395051678874243.setDisplayName("Threshold 3 CO2");

			_1395051678874243.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-7_R-60")) {
					_1395051678874243.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678874243.setHref(new Uri(
					"/networks/office/datapoints/threshold_3_co2/1"));

			// add to parent (containment)
			_1395051678873240.add(_1395051678874243);

			// create generic object		
			Ref _1395051678874244 = new Ref();

			// init object
			_1395051678874244
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1");

			_1395051678874244.setDisplayName("CO2 Value");

			_1395051678874244.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1")) {
					_1395051678874244.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678874244.setHref(new Uri(
					"/networks/office/datapoints/co2_value/1"));

			// add to parent (containment)
			_1395051678873240.add(_1395051678874244);

			// create generic object		
			Ref _1395051678875245 = new Ref();

			// init object
			_1395051678875245
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2");

			_1395051678875245.setDisplayName("Rel. humidity value");

			_1395051678875245.setIs(new Contract("bas:Datapoint"));

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-1_R-2")) {
					_1395051678875245.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678875245.setHref(new Uri(
					"/networks/office/datapoints/rel_humidity_value/1"));

			// add to parent (containment)
			_1395051678873240.add(_1395051678875245);

			// create generic object		
			Obj _1395051678875246 = new Obj();

			// init object
			_1395051678875246.setName("P-0341-0_DI-9");

			_1395051678875246
					.setDisplayName("Push button 2-fold UP 211 DELTA studio (red lens)");

			_1395051678875246
					.setDisplay("Productinfo - see file:   2112_b___tpi_e.pdf");

			_1395051678875246.setIs(new Contract("bas:Entity"));

			// add existing translations
			if (translations.containsKey("P-0341-0_DI-9")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-9")) {
					_1395051678875246.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678875246
					.setHref(new Uri(
							"/networks/office/entities/push_button_2_fold_up_211_delta_studio_red_lens/1"));

			// add as hidden object to object broker

			_1395051678875246.setHidden(true);
			objectBroker.addObj(_1395051678875246, false);

			// create generic object		
			Str _1395051678875247 = new Str();

			// init object
			_1395051678875247.setName("manufacturer");

			// add existing translations
			if (translations.containsKey("manufacturer")) {
				for (ArrayList<String> entry : translations.get("manufacturer")) {
					_1395051678875247.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678875247.setHref(new Uri("manufacturer"));

			// set value
			_1395051678875247.set("Siemens");

			// add to parent (containment)
			_1395051678875246.add(_1395051678875247);

			// create generic object		
			Str _1395051678875248 = new Str();

			// init object
			_1395051678875248.setName("orderNumber");

			// add existing translations
			if (translations.containsKey("orderNumber")) {
				for (ArrayList<String> entry : translations.get("orderNumber")) {
					_1395051678875248.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678875248.setHref(new Uri("orderNumber"));

			// set value
			_1395051678875248.set("5WG1 211-2AB_1");

			// add to parent (containment)
			_1395051678875246.add(_1395051678875248);

			// init datapoint		
			DataPointInit init__1395051678876249 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10"))
				init__1395051678876249
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")));

			init__1395051678876249.setDisplay("Off / On");

			init__1395051678876249.setDisplayName("Switch outer left");

			init__1395051678876249
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10");

			init__1395051678876249.setReadable(false);
			init__1395051678876249.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678876249 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678876249);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-0_R-10")) {
					_1395051678876249.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678876249.setHref(new Uri(
					"/networks/office/datapoints/switch_outer_left/1"));

			// add as hidden object to object broker

			_1395051678876249.setHidden(true);
			objectBroker.addObj(_1395051678876249, false);

			// init datapoint		
			DataPointInit init__1395051678876250 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50"))
				init__1395051678876250
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")));

			init__1395051678876250.setDisplay("Switch");

			init__1395051678876250.setDisplayName("Threshold 1 CO2");

			init__1395051678876250
					.setName("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50");

			init__1395051678876250.setReadable(true);
			init__1395051678876250.setWritable(false);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678876250 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678876250);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-3_R-50")) {
					_1395051678876250.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678876250.setHref(new Uri(
					"/networks/office/datapoints/threshold_1_co2/1"));

			// add as hidden object to object broker

			_1395051678876250.setHidden(true);
			objectBroker.addObj(_1395051678876250, false);

			// init datapoint		
			DataPointInit init__1395051678876251 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0"))
				init__1395051678876251
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")));

			init__1395051678876251.setDisplay("On / Off");

			init__1395051678876251.setDisplayName("Switch, Status");

			init__1395051678876251
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0");

			init__1395051678876251.setReadable(false);
			init__1395051678876251.setWritable(true);

			// create datapoint
			DPST_1_1_ImplKnx _1395051678876251 = new DPST_1_1_ImplKnx(
					knxConnector, init__1395051678876251);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-0_R-0")) {
					_1395051678876251.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678876251.setHref(new Uri(
					"/networks/office/datapoints/switch_status/1"));

			// add as hidden object to object broker

			_1395051678876251.setHidden(true);
			objectBroker.addObj(_1395051678876251, false);

			// init datapoint		
			DataPointInit init__1395051678877252 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41"))
				init__1395051678877252
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")));

			init__1395051678877252.setDisplay("Brighter / Darker");

			init__1395051678877252.setDisplayName("Dimming outer right");

			init__1395051678877252
					.setName("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41");

			init__1395051678877252.setReadable(false);
			init__1395051678877252.setWritable(true);

			// create datapoint
			DPST_3_7_ImplKnx _1395051678877252 = new DPST_3_7_ImplKnx(
					knxConnector, init__1395051678877252);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-10_M-0001_A-2413-01-B14F_O-7_R-41")) {
					_1395051678877252.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678877252.setHref(new Uri(
					"/networks/office/datapoints/dimming_outer_right/1"));

			// add as hidden object to object broker

			_1395051678877252.setHidden(true);
			objectBroker.addObj(_1395051678877252, false);

			// init datapoint		
			DataPointInit init__1395051678877253 = new DataPointInit();

			if (groupAddresses
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1"))
				init__1395051678877253
						.setGroupAddress(new GroupAddress(
								groupAddresses
										.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")));

			init__1395051678877253.setDisplay("Brighter / Darker");

			init__1395051678877253.setDisplayName("Dimming");

			init__1395051678877253
					.setName("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1");

			init__1395051678877253.setReadable(false);
			init__1395051678877253.setWritable(true);

			// create datapoint
			DPST_3_7_ImplKnx _1395051678877253 = new DPST_3_7_ImplKnx(
					knxConnector, init__1395051678877253);

			// add existing translations
			if (translations
					.containsKey("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
				for (ArrayList<String> entry : translations
						.get("P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1")) {
					_1395051678877253.addTranslation(entry.get(0),
							entry.get(1), entry.get(2));
				}
			}

			// set uri
			_1395051678877253.setHref(new Uri(
					"/networks/office/datapoints/dimming/1"));

			// add as hidden object to object broker

			_1395051678877253.setHidden(true);
			objectBroker.addObj(_1395051678877253, false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
