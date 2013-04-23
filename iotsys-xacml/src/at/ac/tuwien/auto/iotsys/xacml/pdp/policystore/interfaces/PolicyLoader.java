package at.ac.tuwien.auto.iotsys.xacml.pdp.policystore.interfaces;

/**
 * The PolicyLoader is used to retrieve policies from a source.
 * 
 * @author Thomas Hofer
 */
public interface PolicyLoader {

	/**
	 * Finds an XACML Policy according to the resourceId.
	 * 
	 * @param resourceId	a unique identifier used to find the policy
	 * @return XACML Policy as String
	 */
	public String findPolicy(String resourceId);
}
