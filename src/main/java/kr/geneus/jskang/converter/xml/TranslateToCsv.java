package kr.geneus.jskang.converter.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TranslateToCsv {

	private long line = 1;
	private final String DEPTH = ">";
	private final String SPLIT = ",";
	private final String ATTRIBUTE_SPLIT = "_";

	protected String getNodeList(Document doc, String rootName) {
		doc.getDocumentElement().normalize();
		NodeList nodes = doc.getElementsByTagName(rootName);

		List<String> columns = new ArrayList<>();
		String rows = travNode(rootName, nodes, columns).toString();

		String column = columns.toString();
		column = column.replaceAll(", ", ",")
				.replaceAll("\\[","")
				.replaceAll("\\]","");

		return column +"\n"+ rows;
	}

	private StringBuilder travNode(String rootName, NodeList nodes, List<String> columns) {
		StringBuilder row = new StringBuilder();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			// Generate attribute start.
			NamedNodeMap attributeNodes = n.getAttributes();
			if (attributeNodes != null) {
				for (int attributeIdx = 0; attributeIdx < attributeNodes.getLength(); attributeIdx++) {
					String nodeName = n.getNodeName();
					Node nodeAttribute = attributeNodes.item(attributeIdx);

					String key = nodeAttribute.getNodeName();
					String value = nodeAttribute.getNodeValue();

					if (columns.indexOf(rootName+DEPTH+nodeName+ATTRIBUTE_SPLIT+key) < 0) {
						columns.add(rootName+DEPTH+nodeName+ATTRIBUTE_SPLIT+key);
					}
					row.append(value+ ",");
				}
			}
			// Generate attribute end.

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				// Generate child node start.
				NodeList childNodes = n.getChildNodes();
				int nodeCnt = childNodes.getLength();

				if (columns.indexOf(rootName+DEPTH+n.getNodeName()) < 0 && nodeCnt <= 1) {
					columns.add(rootName+DEPTH+n.getNodeName());
				}

				if (nodeCnt <= 1) {
					row.append(n.getTextContent()+ SPLIT);
				}
				if (nodeCnt > 1) {
					row.append(travNode(rootName, childNodes, columns).toString());
					line++;
				}
				// Generate child node end.
			}
		}
		return row.delete(row.length()-1, row.length()).append("\n");
	}
}
