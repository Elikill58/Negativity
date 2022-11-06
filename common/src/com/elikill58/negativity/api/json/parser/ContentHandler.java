package com.elikill58.negativity.api.json.parser;

import java.io.IOException;

/**
 * A simplified and stoppable SAX-like content handler for stream processing of JSON text. 
 * 
 * @see com.elikill58.negativity.api.json.parser.JSONParser#parse(java.io.Reader, ContentHandler, boolean)
 * 
 * @author FangYidong
 */
public interface ContentHandler {
	/**
	 * Receive notification of the beginning of JSON processing.
	 * The parser will invoke this method only once.
     * 
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 */
	void startJSON() throws ParseException, IOException;
	
	/**
	 * Receive notification of the end of JSON processing.
	 * 
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 */
	void endJSON() throws ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * @see #endJSON
	 */
	boolean startObject() throws ParseException, IOException;
	
	/**
	 * Receive notification of the end of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * 
     * @see #startObject
	 */
	boolean endObject() throws ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON object entry.
	 * 
	 * @param key - Key of a JSON object entry. 
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * 
     * @see #endObjectEntry
	 */
	boolean startObjectEntry(String key) throws ParseException, IOException;
	
	/**
	 * Receive notification of the end of the value of previous object entry.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * 
     * @see #startObjectEntry
	 */
	boolean endObjectEntry() throws ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * 
     * @see #endArray
	 */
	boolean startArray() throws ParseException, IOException;
	
	/**
	 * Receive notification of the end of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * 
     * @see #startArray
	 */
	boolean endArray() throws ParseException, IOException;
	
	/**
	 * Receive notification of the JSON primitive values:
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean
	 * 	null
	 * 
	 * @param value - Instance of the following:
	 * 			java.lang.String,
	 * 			java.lang.Number,
	 * 			java.lang.Boolean
	 * 			null
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws IOException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 * @throws ParseException JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 */
	boolean primitive(Object value) throws ParseException, IOException;
		
}
