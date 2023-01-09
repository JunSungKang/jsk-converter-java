package kr.geneus.jskang.converter.xml;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TranslateToMap {

	protected Map getNodeList(Document doc, String rootName) {
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName(rootName);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("attributes", new LinkedHashMap<>());
		map.put("value", new LinkedHashMap<>());
		map.put(rootName, null);

		return travNode(
			((Map) map.get("attributes")).get(rootName),
			((Map) map.get("value")).get(rootName),
			nList
		);
	}

	private Map travNode(Object attributes, Object value, NodeList nodes) {

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			// Generate attribute start.
			NamedNodeMap attributeNodes = n.getAttributes();
			int attributeCnt = 0;
			if (attributeNodes != null) {
				attributeCnt = attributeNodes.getLength();
				if (attributes == null) {
					attributes = new LinkedHashMap<>();
				}
			}

			if (attributeCnt >= 1) {
				for (int idx = 0; idx < attributeCnt; idx++) {
					Node attributeNode = attributeNodes.item(idx);
					if (((Map) attributes).get(n.getNodeName()) == null) {
						List<String> attribute = new LinkedList<>();
						attribute.add(attributeNode.getNodeValue());

						Map<String, List> tAttributes = new LinkedHashMap<>();
						tAttributes.put(attributeNode.getNodeName(), attribute);
						((Map) attributes).put(n.getNodeName(), tAttributes);
					} else {
						Map a = (Map) ((Map) attributes).get(n.getNodeName());
						if (a.get(attributeNode.getNodeName()) == null) {
							List<String> attribute = new LinkedList<>();
							attribute.add(attributeNode.getNodeValue());
							a.put(attributeNode.getNodeName(), attribute);
						} else {
							((List) a.get(attributeNode.getNodeName())).add(attributeNode.getNodeValue());
						}
					}
				}
			} else if (attributeNodes != null) {
				((Map) attributes).put(n.getNodeName(), new LinkedHashMap<>());
			}
			// Generate attribute end.

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				// Generate child node start.
				NodeList childNodes = n.getChildNodes();
				int nodeCnt = childNodes.getLength();
				if (value == null) {
					value = new LinkedHashMap<>();
				}

				if (nodeCnt <= 1) {
					((Map) value).put(n.getNodeName(), n.getTextContent());
				}
				if (nodeCnt > 1) {
					if (((Map) value).get(n.getNodeName()) instanceof String) {
						((Map) value).put(n.getNodeName(), new LinkedHashMap<>());
					}
					Map<String, Object> node = travNode(((Map) attributes).get(n.getNodeName()), ((Map) value).get(n.getNodeName()), childNodes);

					Map tValue = (Map) node.get("value");
					((Map) value).put(n.getNodeName(), tValue);
				}
				// Generate child node end.
			}
		}

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("attributes", attributes);
		data.put("value", value);
		return data;
	}
}
