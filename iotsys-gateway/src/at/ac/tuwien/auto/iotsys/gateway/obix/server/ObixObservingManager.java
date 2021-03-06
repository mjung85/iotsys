package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import static at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServer.DEFAULT_OBIX_URL_PROTOCOL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.obix.observer.ExternalObserver;
import at.ac.tuwien.auto.iotsys.obix.observer.ObjObserver;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ch.ethz.inf.vs.californium.layers.TransactionLayer;
import ch.ethz.inf.vs.californium.util.Properties;

/**
 * Modified CoAP Observing Manager for oBIX Resources.
 * @author Markus Jung
 *
 */
public class ObixObservingManager implements ExternalObserver{                   

	private static final Logger LOG = Logger.getLogger(ObixObservingManager.class
			.getName());

	private class ObservingRelationship {
		public String clientID;
		public String resourcePath;
		public GETRequest request;
		public int lastMID;

		public ObservingRelationship(GETRequest request) {

			request.setMID(-1);

			this.clientID = request.getPeerAddress().toString();
			this.resourcePath = new CoAPHelper(obixServer).getResourcePath(request);
			this.resourcePath = obixServer.getNormalizedPath(resourcePath);
			
			this.request = request;
			this.lastMID = -1;
			
		}
	}

	
	private static ObixObservingManager singleton = new ObixObservingManager();

	/**
	 * Maps a resource path string to the resource's observers stored by client
	 * address string.
	 */
	private Map<String, Map<String, ObservingRelationship>> observersByResource = new HashMap<String, Map<String, ObservingRelationship>>();

	/**
	 * Maps a peer address string to the clients relationships stored by
	 * resource path.
	 */
	private Map<String, Map<String, ObservingRelationship>> observersByClient = new HashMap<String, Map<String, ObservingRelationship>>();

	private int checkInterval = Properties.std
			.getInt("OBSERVING_REFRESH_INTERVAL");
	private Map<String, Integer> intervalByResource = new HashMap<String, Integer>();

	/**
	 * Default singleton constructor.
	 */
	private ObixObservingManager() {
		// register myself as external observer
		ObjObserver.setExternalObserver(this);
		Obj.setExternalObserver(this);
	}
	
	private ObixServer obixServer;
	
	

	public static ObixObservingManager getInstance() {
		return singleton;
	}

	public void setRefreshInterval(int interval) {
		this.checkInterval = interval;
	}

	public void notifyObservers(String resourcePath) {
		Map<String, ObservingRelationship> resourceObservers = observersByResource
				.get(resourcePath);

		if (resourceObservers != null && resourceObservers.size() > 0) {

			LOG.info(String.format("Notifying observers: %d @ %s",
					resourceObservers.size(), resourcePath));

			int check = -1;

			// get/initialize
			if (!intervalByResource.containsKey(resourcePath)) {
				check = checkInterval;
			} else {
				check = intervalByResource.get(resourcePath) - 1;
			}
			// update
			if (check <= 0) {
				intervalByResource.put(resourcePath, checkInterval);
				LOG.info(String.format("Refreshing observing relationship: %s",
						resourcePath));
			} else {
				intervalByResource.put(resourcePath, check);
			}
			
			for (ObservingRelationship observer : resourceObservers.values()) {
				GETRequest request = observer.request;

				try {
					String obixResponse;
					Obj responseObj = obixServer.readObj(new URI(resourcePath), false);
					obixResponse = new CoAPHelper(obixServer).encodeObj(responseObj, request);
					obixResponse = obixResponse.replaceFirst(DEFAULT_OBIX_URL_PROTOCOL,
									CoAPServer.COAP_URL_PROTOCOL);
					
					new CoAPHelper(obixServer).encodeResponse(obixResponse.toString(), request);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				
				// check
				if (check <= 0) {
					request.setType(messageType.CON);
				} else {
					request.setType(messageType.NON);
				}
			
				prepareResponse(request);
				request.sendResponse();
			}
		}
	}

	private void prepareResponse(Request request) {

		// consecutive response require new MID that must be stored for RST
		// matching
		if (request.getResponse().getMID() == -1) {
			request.getResponse().setMID(TransactionLayer.nextMessageID());
		}

		// 16-bit second counter
		int secs = (int) ((System.currentTimeMillis() - request.startTime) / 1000) & 0xFFFF;
		request.getResponse().setOption(
				new Option(secs, OptionNumberRegistry.OBSERVE));

		// store MID for RST matching
		updateLastMID(request.getPeerAddress().toString(),
				request.getUriPath(), request.getResponse().getMID());
	}

	public synchronized void addObserver(GETRequest request) {
		ObservingRelationship toAdd = new ObservingRelationship(request);
		String resourcePath = toAdd.resourcePath;
		

		// get clients map for the given resource path
		Map<String, ObservingRelationship> resourceObservers = observersByResource
				.get(resourcePath);
		if (resourceObservers == null) {
			// lazy creation
			resourceObservers = new HashMap<String, ObservingRelationship>();
			observersByResource.put(resourcePath, resourceObservers);
		}
		
		// get resource map for given client address
		Map<String, ObservingRelationship> clientObservees = observersByClient
				.get(request.getPeerAddress().toString());
		if (clientObservees == null) {
			// lazy creation
			clientObservees = new HashMap<String, ObservingRelationship>();
			observersByClient.put(request.getPeerAddress().toString(),
					clientObservees);
		}

		// save relationship for notifications triggered by resource
		resourceObservers.put(request.getPeerAddress().toString(), toAdd);
		// save relationship for actions triggered by client
		clientObservees.put(resourcePath, toAdd);

		LOG.info(String.format("Established observing relationship: %s @ %s",
				request.getPeerAddress().toString(), resourcePath));

		// update response
		request.getResponse().setOption(
				new Option(0, OptionNumberRegistry.OBSERVE));

	}

	public synchronized void removeObserver(String clientID) {
		Map<String, ObservingRelationship> clientObservees = observersByClient
				.get(clientID);

		if (clientObservees != null) {

			for (Map<String, ObservingRelationship> entry : observersByResource
					.values()) {
				entry.remove(clientID);
			}
			observersByClient.remove(clientID);

			LOG.info(String.format(
					"Terminated all observing relationships for client: %s",
					clientID));

		}
	}

	/**
	 * Remove an observer by missing Observe option in GET.
	 * 
	 * @param clientID
	 *            the peer address as string
	 * @param resource
	 *            the resource to un-observe.
	 */
	public void removeObserver(String clientID, LocalResource resource) {

		Map<String, ObservingRelationship> resourceObservers = observersByResource
				.get(resource.getPath());
		Map<String, ObservingRelationship> clientObservees = observersByClient
				.get(clientID);

		if (resourceObservers != null && clientObservees != null) {
			if (resourceObservers.remove(clientID) != null
					&& clientObservees.remove(resource.getPath()) != null) {
				LOG.info(String.format(
						"Terminated observing relationship by GET: %s @ %s",
						clientID, resource.getPath()));
				return;
			}
		}

		// should not be called if not existent
		LOG.warning(String.format(
				"Cannot find observing relationship: %s @ %s", clientID,
				resource.getPath()));
	}

	/**
	 * Remove an observer by MID from RST.
	 * 
	 * @param clientID
	 *            the peer address as string
	 * @param mid
	 *            the MID from the RST
	 */
	public void removeObserver(String clientID, int mid) {
		ObservingRelationship toRemove = null;

		Map<String, ObservingRelationship> clientObservees = observersByClient
				.get(clientID);

		if (clientObservees != null) {
			for (ObservingRelationship entry : clientObservees.values()) {
				if (mid == entry.lastMID && clientID.equals(entry.clientID)) {
					// found it
					toRemove = entry;
					break;
				}
			}
		}

		if (toRemove != null) {
			Map<String, ObservingRelationship> resourceObservers = observersByResource
					.get(toRemove.resourcePath);

			// FIXME Inconsistent state check
			if (resourceObservers == null) {
				LOG.severe(String
						.format("FIXME: ObservingManager has clientObservee, but no resourceObservers (%s @ %s)",
								clientID, toRemove.resourcePath));
			}

			if (resourceObservers.remove(clientID) != null
					&& clientObservees.remove(toRemove.resourcePath) != null) {
				LOG.info(String.format(
						"Terminated observing relationship by RST: %s @ %s",
						clientID, toRemove.resourcePath));
				return;
			}
		}

		LOG.warning(String.format(
				"Cannot find observing relationship by MID: %s|%d", clientID,
				mid));
	}

	public boolean isObserved(String clientID, String resourcePath) {
		return observersByClient.containsKey(clientID)
				&& observersByClient.get(clientID).containsKey(
						resourcePath);
	}

	public void updateLastMID(String clientID, String path, int mid) {

		Map<String, ObservingRelationship> clientObservees = observersByClient
				.get(clientID);

		if (clientObservees != null) {
			ObservingRelationship toUpdate = clientObservees.get(path);
			if (toUpdate != null) {
				toUpdate.lastMID = mid;

				LOG.finer(String.format(
						"Updated last MID for observing relationship: %s @ %s",
						clientID, toUpdate.resourcePath));
				return;
			}
		}

		LOG.warning(String.format(
				"Cannot find observing relationship to update MID: %s @ %s",
				clientID, path));
	}

	public ObixServer getObixServer() {
		return obixServer;
	}

	public void setObixServer(ObixServer obixServer) {
		this.obixServer = obixServer;
	}

	@Override
	public void objectChanged(String fullContextPath) {
		this.notifyObservers(fullContextPath);
	}
}
