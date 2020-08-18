package com.elikill58.negativity.api.yaml.util;

import java.nio.charset.CodingErrorAction;

import com.elikill58.negativity.api.yaml.error.YAMLException;
import com.elikill58.negativity.api.yaml.external.com.google.gdata.util.common.base.Escaper;
import com.elikill58.negativity.api.yaml.external.com.google.gdata.util.common.base.PercentEscaper;

import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;

public abstract class UriEncoder {
	private static final CharsetDecoder UTF8Decoder;
	// private static final String SAFE_CHARS = "-_.!~*'()@:$&,;=[]/";
	private static final Escaper escaper;

	public static String encode(final String uri) {
		return UriEncoder.escaper.escape(uri);
	}

	public static String decode(final ByteBuffer buff) throws CharacterCodingException {
		final CharBuffer chars = UriEncoder.UTF8Decoder.decode(buff);
		return chars.toString();
	}

	public static String decode(final String buff) {
		try {
			return URLDecoder.decode(buff, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new YAMLException(e);
		}
	}

	static {
		UTF8Decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT);
		escaper = new PercentEscaper("-_.!~*'()@:$&,;=[]/", false);
	}
}
