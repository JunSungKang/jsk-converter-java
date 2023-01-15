package kr.geneus.jskang.converter.xml;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.EmptyStackException;
import java.util.Map;
import javax.naming.SizeLimitExceededException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import kr.geneus.jskang.converter.common.Convert;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
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

	@Override
	public Map<String, Object> toMap(File file) throws IOException, SizeLimitExceededException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		long fileSize = randomAccessFile.length();
		if (fileSize >= Integer.MAX_VALUE) {
			randomAccessFile.close();
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

		StringReader stringReader = null;
		InputSource inputSource = null;
		Document document = null;
		try {
			stringReader = new StringReader(xml);
			inputSource = new InputSource(stringReader);
			document = this.builder.parse(inputSource);
		} catch (IOException e) {
			stringReader.close();
			throw new IOException(e.getMessage());
		} catch (SAXException e) {
			stringReader.close();
			throw new IOException(e.getMessage());
		} finally {
			stringReader.close();
		}

		String rootName = document.getFirstChild().getNodeName();
		return (Map) new TranslateToMap().getNodeList(document, rootName);
	}

	public String toCsv(File file) throws IOException, SizeLimitExceededException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		long fileSize = randomAccessFile.length();
		if (fileSize >= Integer.MAX_VALUE) {
			randomAccessFile.close();
			throw new SizeLimitExceededException("Files larger than 2 GB cannot be parsed.");
		}

		byte[] xml = new byte[(int) fileSize];
		randomAccessFile.read(xml);
		randomAccessFile.close();

		return this.toCsv(new String(xml));
	}

	/**
	 * Xml code conversion to csv code.
	 *
	 * @param xml input XML Code
	 * @return csv code.
	 * @throws IOException
	 */
	@Override
	public String toCsv(String xml) throws IOException {
		if (xml.isEmpty()) {
			throw new EmptyStackException();
		}

		StringReader stringReader = null;
		InputSource inputSource = null;
		Document document = null;
		try {
			stringReader = new StringReader(xml);
			inputSource = new InputSource(stringReader);
			document = this.builder.parse(inputSource);
		} catch (IOException e) {
			stringReader.close();
			throw new IOException(e.getMessage());
		} catch (SAXException e) {
			stringReader.close();
			throw new IOException(e.getMessage());
		} finally {
			stringReader.close();
		}

		String rootName = document.getFirstChild().getNodeName();
		// TODO: How can I process the CSV conversion at once?
		return null;
		//return getNodeList(document, rootName);
	}

	/**
	 * It helps you to beautifully output single-line XML code.
	 * Since no encoding was entered, it defaults to UTF-8.
	 *
	 * @param file input XML Code
	 * @return beautiful xml code
	 */
	public String toBeautify(File file) throws IOException, SizeLimitExceededException, TransformerException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		long fileSize = randomAccessFile.length();
		if (fileSize >= Integer.MAX_VALUE) {
			randomAccessFile.close();
			throw new SizeLimitExceededException("Files larger than 2 GB cannot be parsed.");
		}

		byte[] xml = new byte[(int) fileSize];
		randomAccessFile.read(xml);
		randomAccessFile.close();

		return this.toBeautify(new String(xml));
	}

	/**
	 * It helps you to beautifully output single-line XML code.
	 * Since no encoding was entered, it defaults to UTF-8.
	 *
	 * @param xml input XML Code
	 * @return beautiful xml code
	 * @throws TransformerException
	 */
	public String toBeautify(String xml) throws TransformerException {
		return this.toBeautify(xml, StandardCharsets.UTF_8.name());
	}

	/**
	 * It helps you to beautifully output single-line XML code.
	 * TODO: Why can't I go to the next line after "<?xml version="1.0" encoding="UTF-8"?>" ?
	 *
	 * @param xml      input XML Code
	 * @param encoding Encoding of values ​​to be returned after conversion
	 * @return beautiful xml code
	 * @throws TransformerException
	 */
	@Override
	public String toBeautify(String xml, String encoding) throws TransformerException {
		Source xmlInput = new StreamSource(new StringReader(xml));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", "4");

		// TODO: What role is this?
		//transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		//transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.transform(xmlInput, xmlOutput);

		return xmlOutput.getWriter().toString();
	}
}
