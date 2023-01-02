package kr.geneus.jskang.converter.json;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.naming.SizeLimitExceededException;
import kr.geneus.jskang.converter.common.Convert;

public class JsonConvert implements Convert {

	@Override
	public Map<String, Object> toMap(File file) throws IOException, SizeLimitExceededException {
		return null;
	}

	@Override
	public Map<String, Object> toMap(String json) {
		return null;
	}

	@Override
	public String toCsv(String xml) throws IOException {
		return null;
	}

	@Override
	public String toBeautify(String code, String encoding) {
		return null;
	}
}
