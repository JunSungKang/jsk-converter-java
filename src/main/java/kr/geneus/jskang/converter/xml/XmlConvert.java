package kr.geneus.jskang.converter.xml;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.naming.SizeLimitExceededException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import kr.geneus.jskang.converter.common.Convert;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Slf4j
public class XmlConvert implements Convert {

	private DocumentBuilderFactory factory = null;
	private DocumentBuilder builder = null;

	public XmlConvert() throws ParserConfigurationException {
		this.factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
	}

	public Map<String, Object> toMap(File file) throws IOException, SizeLimitExceededException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		long fileSize = randomAccessFile.length();
		if (fileSize >= Integer.MAX_VALUE) {
			throw new SizeLimitExceededException("Files larger than 2 GB cannot be parsed.");
		}

		byte[] xml = new byte[(int) fileSize];
		randomAccessFile.read(xml);
		randomAccessFile.close();

		return this.toMap(new String(xml));
	}

	@Override
	public Map<String, Object> toMap(String xml) throws EmptyStackException, IOException {
		if (xml.isEmpty()) {
			throw new EmptyStackException();
		}

		Document document = null;
		try {
			StringReader stringReader = new StringReader(xml);
			InputSource inputSource = new InputSource(stringReader);
			document = this.builder.parse(inputSource);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		}

		String rootName = document.getFirstChild().getNodeName();
		return getNodeList(document, rootName);
	}

	private Map<String, Object> getNodeList(Document doc, String rootName) {
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName(rootName);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("attributes", new LinkedHashMap<>());
		map.put("value", new LinkedHashMap<>());

		map.put(rootName, null);
		log.debug("XML RootName: " + rootName);
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
			int attributeCnt = attributeNodes.getLength();
			if (attributes == null) {
				attributes = new LinkedHashMap<>();
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
			} else {
				((Map) attributes).put(n.getNodeName(), new LinkedHashMap<>());
			}
			// Generate attribute end.

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				// Generate child node start.
				NodeList childNodes = n.getChildNodes();
				int nodeCnt = childNodes.getLength();
				if (value == null) {
					value = new LinkedHashMap<>();
					log.debug("XML create linkedHashMap.");
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
		return (Map) data;
	}
}
