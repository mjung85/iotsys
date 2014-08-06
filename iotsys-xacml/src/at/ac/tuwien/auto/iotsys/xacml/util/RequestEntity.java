package at.ac.tuwien.auto.iotsys.xacml.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RequestEntity extends AbstractNode {

	private List<Attribute> attributes = new ArrayList<Attribute>();

	private String tagName;
	
	public RequestEntity(String tagName) {
		setTagName(tagName);
	}
	
	public Document getDocument(Document doc) {
//		Document doc = getBuilder().newDocument();
		Element entity = doc.createElementNS(URN_XACML_CONTEXT, getTagName());
		doc.appendChild(entity);
		
		for (int i=0; i < attributes.size(); i++) {
			entity.appendChild(attributes.get(i).getDocument(doc));
		}
		
		return doc;
	}
	
	public Element getElement(Document doc) {
//		Document doc = getBuilder().newDocument();
		Element entity = doc.createElementNS(URN_XACML_CONTEXT, getTagName());
		
		Attr xmlns = doc.createAttribute("xmlns");
		xmlns.setValue(URN_XACML_CONTEXT);
		entity.setAttributeNode(xmlns);
		
		doc.appendChild(entity);
		
		for (int i=0; i < attributes.size(); i++) {
			entity.appendChild(attributes.get(i).getElement(doc));
		}
		
		return entity;
	}
	
	public void add(Attribute attr) {
		attributes.add(attr);
	}
		
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
