/*
 * @(#)NumberTheory.java        4.4.2   2020-01-25
 *
 * You may use this software under the condition of "Simplified BSD License"
 *
 * Copyright 2010-2020 MARIUSZ GROMADA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <MARIUSZ GROMADA> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of MARIUSZ GROMADA.
 *
 * If you have any questions/bugs feel free to contact:
 *
 *     Mariusz Gromada
 *     mariuszgromada.org@gmail.com
 *     http://mathparser.org
 *     http://mathspace.pl
 *     http://janetsudoku.mariuszgromada.org
 *     http://github.com/mariuszgromada/MathParser.org-mXparser
 *     http://mariuszgromada.github.io/MathParser.org-mXparser
 *     http://mxparser.sourceforge.net
 *     http://bitbucket.org/mariuszgromada/mxparser
 *     http://mxparser.codeplex.com
 *     http://github.com/mariuszgromada/Janet-Sudoku
 *     http://janetsudoku.codeplex.com
 *     http://sourceforge.net/projects/janetsudoku
 *     http://bitbucket.org/mariuszgromada/janet-sudoku
 *     http://github.com/mariuszgromada/MathParser.org-mXparser
 *     http://scalarmath.org/
 *     https://play.google.com/store/apps/details?id=org.mathparser.scalar.lite
 *     https://play.google.com/store/apps/details?id=org.mathparser.scalar.pro
 *
 *                              Asked if he believes in one God, a mathematician answered:
 *                              "Yes, up to isomorphism."
 */
package org.mariuszgromada.math.mxparser.mathcollection;

import org.mariuszgromada.math.mxparser.mXparser;

/**
 * NumberTheory - summation / products etc...
 *
 * @author <b>Mariusz Gromada</b><br>
 *         <a href=
 *         "mailto:mariuszgromada.org@gmail.com">mariuszgromada.org@gmail.com</a><br>
 *         <a href="http://mathspace.pl" target="_blank">MathSpace.pl</a><br>
 *         <a href="http://mathparser.org" target="_blank">MathParser.org -
 *         mXparser project page</a><br>
 *         <a href="http://github.com/mariuszgromada/MathParser.org-mXparser"
 *         target="_blank">mXparser on GitHub</a><br>
 *         <a href="http://mxparser.sourceforge.net" target="_blank">mXparser on
 *         SourceForge</a><br>
 *         <a href="http://bitbucket.org/mariuszgromada/mxparser" target=
 *         "_blank">mXparser on Bitbucket</a><br>
 *         <a href="http://mxparser.codeplex.com" target="_blank">mXparser on
 *         CodePlex</a><br>
 *         <a href="http://janetsudoku.mariuszgromada.org" target="_blank">Janet
 *         Sudoku - project web page</a><br>
 *         <a href="http://github.com/mariuszgromada/Janet-Sudoku" target=
 *         "_blank">Janet Sudoku on GitHub</a><br>
 *         <a href="http://janetsudoku.codeplex.com" target="_blank">Janet
 *         Sudoku on CodePlex</a><br>
 *         <a href="http://sourceforge.net/projects/janetsudoku" target=
 *         "_blank">Janet Sudoku on SourceForge</a><br>
 *         <a href="http://bitbucket.org/mariuszgromada/janet-sudoku" target=
 *         "_blank">Janet Sudoku on BitBucket</a><br>
 *         <a href=
 *         "https://play.google.com/store/apps/details?id=org.mathparser.scalar.lite"
 *         target="_blank">Scalar Free</a><br>
 *         <a href=
 *         "https://play.google.com/store/apps/details?id=org.mathparser.scalar.pro"
 *         target="_blank">Scalar Pro</a><br>
 *         <a href="http://scalarmath.org/" target=
 *         "_blank">ScalarMath.org</a><br>
 *
 * @version 4.4.2
 */
public final class NumberTheory {

	public static final double max(double a, double b) {
		if (Double.isNaN(a) || Double.isNaN(b))
			return Double.NaN;
		return Math.max(a, b);
	}

	/**
	 * Prime test
	 *
	 * @param n
	 *            The number to be tested.
	 *
	 * @return true if number is prime, otherwise false
	 */
	public static final boolean primeTest(long n) {
		/*
		 * 2 is a prime :-)
		 */
		if (n == 2)
			return true;
		/*
		 * Even number is not a prime
		 */
		if (n % 2 == 0)
			return false;
		/*
		 * Everything <= 1 is not a prime
		 */
		if (n <= 1)
			return false;
		/*
		 * Will be searching for divisors till sqrt(n)
		 */
		long top = (long) Math.sqrt(n);
		/*
		 * Supporting variable indicating odd end of primes cache
		 */
		long primesCacheOddEnd = 3;
		/*
		 * If prime cache exist
		 */
		if (mXparser.primesCache != null)
			if (mXparser.primesCache.cacheStatus == PrimesCache.CACHING_FINISHED) {
				/*
				 * If prime cache is ready and number we are querying is in cache the cache
				 * answer will be returned
				 */
				if (n <= mXparser.primesCache.maxNumInCache)
					return mXparser.primesCache.isPrime[(int) n];
				else {
					/*
					 * If number is bigger than maximum stored in cache the we are querying each
					 * prime in cache and checking if it is a divisor of n
					 */
					long topCache = Math.min(top, mXparser.primesCache.maxNumInCache);
					long i;
					for (i = 3; i <= topCache; i += 2) {
						if (mXparser.primesCache.isPrime[(int) i] == true)
							if (n % i == 0)
								return false;
						if (mXparser.isCurrentCalculationCancelled())
							return false;
					}
					/*
					 * If no prime divisor of n in primes cache we are seting the odd end of prime
					 * cache
					 */
					primesCacheOddEnd = i;
				}
			}
		/*
		 * Finally we are checking any odd number that still left and is below sqrt(n)
		 * agains being divisor of n
		 */
		for (long i = primesCacheOddEnd; i <= top; i += 2) {
			if (n % i == 0)
				return false;
			if (mXparser.isCurrentCalculationCancelled())
				return false;
		}
		return true;
	}

	/**
	 * Prime test
	 *
	 * @param n
	 *            The number to be tested.
	 *
	 * @return true if number is prime, otherwise false
	 */
	public static final double primeTest(double n) {
		if (Double.isNaN(n))
			return Double.NaN;
		boolean isPrime = primeTest((long) n);
		if (isPrime == true)
			return 1;
		else
			return 0;
	}

	/**
	 * Digit index based on digit character for numeral systems with base between 1
	 * and 36.
	 *
	 * @param digitChar
	 *            Digit character (lower or upper case) representing digit in
	 *            numeral systems with base between 1 and 36. Digits: 0:0, 1:1, 2:2,
	 *            3:3, 4:4, 5:5, 6:6, 7:7, 8:8, 9:9, 10:A, 11:B, 12:C, 13:D, 14:E,
	 *            15:F, 16:G, 17:H, 18:I, 19:J, 20:K, 21:L, 22:M, 23:N, 24:O, 25:P,
	 *            26:Q, 27:R, 28:S, 29:T, 30:U, 31:V, 32:W, 33:X, 34:Y, 35:Z
	 * @return Returns digit index if digit char was recognized, otherwise returns
	 *         -1.
	 */
	public static final int digitIndex(char digitChar) {
		switch (digitChar) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'A':
			return 10;
		case 'B':
			return 11;
		case 'C':
			return 12;
		case 'D':
			return 13;
		case 'E':
			return 14;
		case 'F':
			return 15;
		case 'G':
			return 16;
		case 'H':
			return 17;
		case 'I':
			return 18;
		case 'J':
			return 19;
		case 'K':
			return 20;
		case 'L':
			return 21;
		case 'M':
			return 22;
		case 'N':
			return 23;
		case 'O':
			return 24;
		case 'P':
			return 25;
		case 'Q':
			return 26;
		case 'R':
			return 27;
		case 'S':
			return 28;
		case 'T':
			return 29;
		case 'U':
			return 30;
		case 'V':
			return 31;
		case 'W':
			return 32;
		case 'X':
			return 33;
		case 'Y':
			return 34;
		case 'Z':
			return 35;
		case 'a':
			return 10;
		case 'b':
			return 11;
		case 'c':
			return 12;
		case 'd':
			return 13;
		case 'e':
			return 14;
		case 'f':
			return 15;
		case 'g':
			return 16;
		case 'h':
			return 17;
		case 'i':
			return 18;
		case 'j':
			return 19;
		case 'k':
			return 20;
		case 'l':
			return 21;
		case 'm':
			return 22;
		case 'n':
			return 23;
		case 'o':
			return 24;
		case 'p':
			return 25;
		case 'q':
			return 26;
		case 'r':
			return 27;
		case 's':
			return 28;
		case 't':
			return 29;
		case 'u':
			return 30;
		case 'v':
			return 31;
		case 'w':
			return 32;
		case 'x':
			return 33;
		case 'y':
			return 34;
		case 'z':
			return 35;
		}
		return -1;
	}

	/**
	 * Other base (base between 1 and 36) number literal conversion to decimal
	 * number.
	 *
	 * @param numberLiteral
	 *            Number literal in given numeral system with base between 1 and 36.
	 *            Digits: 0:0, 1:1, 2:2, 3:3, 4:4, 5:5, 6:6, 7:7, 8:8, 9:9, 10:A,
	 *            11:B, 12:C, 13:D, 14:E, 15:F, 16:G, 17:H, 18:I, 19:J, 20:K, 21:L,
	 *            22:M, 23:N, 24:O, 25:P, 26:Q, 27:R, 28:S, 29:T, 30:U, 31:V, 32:W,
	 *            33:X, 34:Y, 35:Z
	 * @param numeralSystemBase
	 *            Numeral system base, between 1 and 36
	 * @return Decimal number after conversion. If conversion was not possible the
	 *         Double.NaN is returned.
	 */
	public static final double convOthBase2Decimal(String numberLiteral, int numeralSystemBase) {
		if (numberLiteral == null)
			return Double.NaN;
		numberLiteral = numberLiteral.trim();
		if (numberLiteral.length() == 0) {
			if (numeralSystemBase == 1)
				return 0;
			else
				return Double.NaN;
		}
		if (numeralSystemBase < 1)
			return Double.NaN;
		if (numeralSystemBase > 36)
			return Double.NaN;
		char signChar = numberLiteral.charAt(0);
		double sign = 1.0;
		if (signChar == '-') {
			sign = -1.0;
			numberLiteral = numberLiteral.substring(1);
		} else if (signChar == '+') {
			sign = 1.0;
			numberLiteral = numberLiteral.substring(1);
		}
		int length = numberLiteral.length();
		double decValue = 0;
		int digit;
		for (int i = 0; i < length; i++) {
			if (mXparser.isCurrentCalculationCancelled())
				return Double.NaN;
			digit = digitIndex(numberLiteral.charAt(i));
			if (numeralSystemBase > 1) {
				if ((digit >= 0) && (digit < numeralSystemBase))
					decValue = numeralSystemBase * decValue + digit;
				else
					return Double.NaN;
			} else {
				if (digit == 1)
					decValue = numeralSystemBase * decValue + digit;
				else
					return Double.NaN;
			}
		}
		return sign * decValue;
	}

}