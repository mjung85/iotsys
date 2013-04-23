package at.ac.tuwien.auto.iotsys.xacml.pdp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="DecisionType")
@XmlEnum
public enum DecisionType {
	
	@XmlEnumValue("Permit")
	PERMIT("Permit"),
	@XmlEnumValue("Deny")
	DENY("Deny"),
	@XmlEnumValue("Indeterminate")
	INDETERMINATE("Indeterminate"),
	@XmlEnumValue("NotApplicable")
	NOT_APPLICABLE("NotApplicable");
	
	private final String value;
	
	DecisionType(String v) {
		value = v;
	}
	
	public String value() {
		return value;
	}

	public static DecisionType fromValue(String v) {
		for (DecisionType c: DecisionType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		} throw new IllegalArgumentException(v);
	}
}
