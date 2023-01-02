package kr.geneus.jskang.converter.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.naming.SizeLimitExceededException;
import javax.xml.transform.TransformerException;

public interface Convert {

	public Map<String, Object> toMap(File file) throws IOException, SizeLimitExceededException;
	public Map<String, Object> toMap(String code) throws IOException;
	public String toCsv(String xml) throws IOException;
	public String toBeautify(String code, String encoding) throws TransformerException;
}
