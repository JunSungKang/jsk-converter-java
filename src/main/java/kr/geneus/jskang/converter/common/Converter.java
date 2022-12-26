package kr.geneus.jskang.converter.common;

import java.io.IOException;
import java.util.Map;

public interface Converter {

    public Map<String, Object> buildToMap() throws IOException, Exception;
}
