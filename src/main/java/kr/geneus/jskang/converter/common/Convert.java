package kr.geneus.jskang.converter.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.naming.SizeLimitExceededException;

public interface Convert {

	public Map<String, Object> toMap(File file) throws IOException, SizeLimitExceededException;
	public Map<String, Object> toMap(String code) throws IOException;
}
