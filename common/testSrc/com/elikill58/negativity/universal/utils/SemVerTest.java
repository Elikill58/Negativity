package com.elikill58.negativity.universal.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SemVerTest {
	
	@Test
	public void testParseMajorMinorOnly() {
		assertEquals(new SemVer(0, 1, 0, null), SemVer.parse("0.1"));
		assertEquals(new SemVer(0, 2, 0, null), SemVer.parse("0.2"));
		assertEquals(new SemVer(0, 20, 0, null), SemVer.parse("0.20"));
		assertEquals(new SemVer(1, 0, 0, null), SemVer.parse("1.0"));
		assertEquals(new SemVer(1, 1, 0, null), SemVer.parse("1.1"));
		assertEquals(new SemVer(1, 10, 0, null), SemVer.parse("1.10"));
		assertEquals(new SemVer(2, 0, 0, null), SemVer.parse("2.0"));
	}
	
	@Test
	public void testParseWithoutSuffix() {
		assertEquals(new SemVer(1, 0, 0, null), SemVer.parse("1.0.0"));
		assertEquals(new SemVer(1, 1, 0, null), SemVer.parse("1.1.0"));
		assertEquals(new SemVer(1, 1, 1, null), SemVer.parse("1.1.1"));
		assertEquals(new SemVer(1, 0, 1, null), SemVer.parse("1.0.1"));
		assertEquals(new SemVer(2, 0, 0, null), SemVer.parse("2.0.0"));
		assertEquals(new SemVer(2, 1, 0, null), SemVer.parse("2.1.0"));
	}
	
	@Test
	public void testParseWithSuffix() {
		assertEquals(new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT), SemVer.parse("1.0-SNAPSHOT"));
		assertEquals(new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT), SemVer.parse("1.0.0-SNAPSHOT"));
		assertEquals(new SemVer(2, 1, 3, SemVer.Suffix.SNAPSHOT), SemVer.parse("2.1.3-SNAPSHOT"));
		assertEquals(new SemVer(1, 0, 0, SemVer.Suffix.ALPHA), SemVer.parse("1.0.0-ALPHA"));
		assertEquals(new SemVer(1, 0, 0, new SemVer.Suffix(-1, "INVALID")), SemVer.parse("1.0.0-INVALID"));
	}
	
	@Test
	public void testComparison() {
		assertEquals(0, new SemVer(1, 0, 0, null).compareTo(new SemVer(1, 0, 0, null)));
		assertEquals(-1, new SemVer(1, 0, 0, null).compareTo(new SemVer(2, 0, 0, null)));
		assertEquals(1, new SemVer(2, 0, 0, null).compareTo(new SemVer(1, 0, 0, null)));
		
		assertEquals(1, new SemVer(2, 0, 0, null).compareTo(new SemVer(1, 1, 0, null)));
		assertEquals(1, new SemVer(1, 2, 0, null).compareTo(new SemVer(1, 1, 0, null)));
		assertEquals(1, new SemVer(1, 2, 1, null).compareTo(new SemVer(1, 1, 0, null)));
		assertEquals(1, new SemVer(1, 0, 1, null).compareTo(new SemVer(1, 0, 0, null)));
		assertEquals(1, new SemVer(1, 0, 2, null).compareTo(new SemVer(1, 0, 1, null)));
		assertEquals(1, new SemVer(1, 1, 1, null).compareTo(new SemVer(1, 0, 2, null)));
		assertEquals(1, new SemVer(1, 1, 1, null).compareTo(new SemVer(1, 0, 2, null)));
		assertEquals(1, new SemVer(2, 0, 1, null).compareTo(new SemVer(1, 5, 2, null)));
		
		assertEquals(0, new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT).compareTo(new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT)));
		assertEquals(1, new SemVer(1, 0, 0, SemVer.Suffix.ALPHA).compareTo(new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT)));
		assertEquals(1, new SemVer(2, 0, 0, SemVer.Suffix.SNAPSHOT).compareTo(new SemVer(1, 0, 0, SemVer.Suffix.SNAPSHOT)));
		assertEquals(1, new SemVer(2, 0, 0, SemVer.Suffix.ALPHA).compareTo(new SemVer(1, 0, 0, SemVer.Suffix.ALPHA)));
		assertEquals(1, new SemVer(2, 1, 0, SemVer.Suffix.ALPHA).compareTo(new SemVer(2, 0, 0, SemVer.Suffix.ALPHA)));
		assertEquals(1, new SemVer(2, 1, 0, SemVer.Suffix.ALPHA).compareTo(new SemVer(2, 0, 0, SemVer.Suffix.SNAPSHOT)));
		assertEquals(1, new SemVer(2, 0, 0, null).compareTo(new SemVer(2, 0, 0, SemVer.Suffix.SNAPSHOT)));
		assertEquals(1, new SemVer(2, 0, 0, null).compareTo(new SemVer(2, 0, 0, SemVer.Suffix.ALPHA)));
	}
}
