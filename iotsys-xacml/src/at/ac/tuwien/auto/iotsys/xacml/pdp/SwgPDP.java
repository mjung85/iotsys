package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinderModule;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule;
import org.jboss.security.xacml.sunxacml.finder.ResourceFinderModule;
import org.jboss.security.xacml.sunxacml.finder.impl.CurrentEnvModule;
import org.jboss.security.xacml.sunxacml.finder.impl.SelectorModule;

import at.ac.tuwien.auto.iotsys.xacml.pdp.finder.PolicyStorePolicyFinderModule;
import at.ac.tuwien.auto.iotsys.xacml.pdp.policystore.interfaces.PolicyLoader;

/**
 * Instancing the PDP implementation and bootstrapping.
 */
public class SwgPDP extends PolicyDecisionPoint {

	/**
	 * Policy loader that access' the database.
	 */
	private PolicyLoader policyLoader;
	
	private static final Logger log = Logger
			.getLogger(SwgPDP.class);

	
	/**
	 * Default Constructor
	 * @param policyLoader
	 */
	public SwgPDP(PolicyLoader policyLoader) {
		this.policyLoader = policyLoader;
	}
	
	/**
	 * Creates list of AttributeFinderModule.
	 * 
	 * @return list of AttributeFinderModule
	 */
	protected List<AttributeFinderModule> createAttributeFinderModules() {
		List<AttributeFinderModule> modules = new ArrayList<AttributeFinderModule>();

		modules.add(new CurrentEnvModule());
		modules.add(new SelectorModule());

		return modules;
	}

	/**
	 * Creates a set of PolicyFinderModules.
	 * 
	 * @return
	 */
	protected Set<PolicyFinderModule> createPolicyFinderModules() {
		Set<PolicyFinderModule> modules = new HashSet<PolicyFinderModule>();

		log.info("Add PolicyStorePolicyFinderModule ....");
		modules.add(new PolicyStorePolicyFinderModule(this.policyLoader));

		return modules;
	}

	protected List<ResourceFinderModule> createResourceFinderModules() {
		List<ResourceFinderModule> modules = new ArrayList<ResourceFinderModule>();

		return modules;
	}
}