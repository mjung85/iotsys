package an.xacml;

import java.util.Map;

/**
 * This interface represent an element that can be added additional namespace mappings. The additional namespace
 * mappings shall be used in Attribute Selector.
 */
public interface AdditionalNamespaceMappingEntry {
    public void setAdditionalNSMappings(Map<String, String> mappings);
    public Map<String, String> getAdditionalNSMappings();
}