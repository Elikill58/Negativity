package com.elikill58.negativity.api.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Beans that support customized output of JSON text to a writer shall implement this interface.  
 * 
 * @author FangYidong
 */
public interface JSONStreamAware {
	/**
	 * write JSON string to out.
	 * 
	 * @param out the writer of json
	 * 
	 * @throws IOException if something gone wrong
	 */
	void writeJSONString(Writer out) throws IOException;
}
