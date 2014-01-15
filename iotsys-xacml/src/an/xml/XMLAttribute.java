package an.xml;

import static an.xml.XMLDataTypeRegistry.getJavaType;
import static an.xml.XMLDataTypeRegistry.getTypedValue;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.TypeInfo;

/**
 * Represent a XML attribute.
 */
public class XMLAttribute {
    private String name = null;
    private Class<?> valueType = null;
    private Object value = null;
    private String namespace = null;
    private XMLElement owner = null;

    /**
     * A copy constructor that we can clone an existing XMLAttribute object.
     * @param other The XMLAttribute object need to be cloned.
     */
    public XMLAttribute(XMLAttribute other) {
        this.name = other.name;
        this.valueType = other.valueType;
        this.value = other.value;
        this.namespace = other.namespace;
        // Then we need to call setOwnerElement from the outer element.
    }

    public XMLAttribute(String name, Object value) {
        this.name = name;
        this.valueType = value.getClass();
        this.value = value;
    }

    public XMLAttribute(String name, Object value, String namespace) {
        this(name, value);
        this.namespace = namespace;
    }

    /**
     * Construct a XMLAttribute from a W3C Attr object.
     * @param attr
     * @throws XMLGeneralException
     */
    public XMLAttribute(Attr attr) throws XMLGeneralException {
        this.name = attr.getName();
        this.namespace = attr.getNamespaceURI();

        TypeInfo schemaType = attr.getSchemaTypeInfo();

        // Get the XML type info, and use it to get corresponding java type.
        QName xmlType = new QName(schemaType.getTypeNamespace(), schemaType.getTypeName());
        try {
			valueType = getJavaType(xmlType);
		} catch (XMLDataTypeMappingException xmle) {
			throw new XMLGeneralException("Can not get corresponding Java type for \"" + xmlType + "\".", xmle);
		}

        // Convert the string value to Java typed value
        try {
            String strValue = attr.getValue();
            value = getTypedValue(valueType, strValue);
        }
        catch (XMLDataTypeMappingException e) {
            throw new XMLGeneralException(e.getMessage(), e);
        }
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getValueJavaType() {
        return valueType;
    }

    public String getNamespaceURI() {
        return namespace;
    }

    public void setOwnerElement(XMLElement owner) {
        this.owner = owner;
    }

    public XMLElement getOwnerElement() {
        return owner;
    }

    public String toString() {
        return (namespace == null ? name : namespace + ":" + name) + "=" +
                value.toString() + ":[" + valueType.getSimpleName() + "]";
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof XMLAttribute) {
            return toString().equals(o.toString());
        }
        return false;
    }
}