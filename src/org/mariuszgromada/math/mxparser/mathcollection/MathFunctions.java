/*
 * @(#)MathFunctions.java        4.4.2   2020-01-25
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.mariuszgromada.math.mxparser.mXparser;

/**
 * MathFunctions - the most popular math functions. Many of function implemented
 * by this class could be found in java Math package (in fact functions from
 * MathFunctions typically calls original functions from the Math package). The
 * reason why it was "re-implemented" is: if you decide to implement your own
 * function you do not need to change anything in the parser, jut modify
 * function implementation in this class.
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
public final class MathFunctions {
	/**
	 * Addition a + b applying canonical rounding if canonical rounding is enabled
	 *
	 * @param a
	 *            The a parameter
	 * @param b
	 *            The b parameter
	 * @return The result of addition
	 */
	public static final double plus(double a, double b) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(b))
			return Double.NaN;
		if (!mXparser.checkIfCanonicalRounding())
			return a + b;
		if (Double.isInfinite(a))
			return a + b;
		if (Double.isInfinite(b))
			return a + b;
		BigDecimal da = BigDecimal.valueOf(a);
		BigDecimal db = BigDecimal.valueOf(b);
		return da.add(db).doubleValue();
	}

	/**
	 * Subtraction a - b applying canonical rounding if canonical rounding is
	 * enabled
	 *
	 * @param a
	 *            The a parameter
	 * @param b
	 *            The b parameter
	 * @return The result of subtraction
	 */
	public static final double minus(double a, double b) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(b))
			return Double.NaN;
		if (!mXparser.checkIfCanonicalRounding())
			return a - b;
		if (Double.isInfinite(a))
			return a - b;
		if (Double.isInfinite(b))
			return a - b;
		BigDecimal da = BigDecimal.valueOf(a);
		BigDecimal db = BigDecimal.valueOf(b);
		return da.subtract(db).doubleValue();
	}

	/**
	 * Multiplication a * b applying canonical rounding if canonical rounding is
	 * enabled
	 *
	 * @param a
	 *            The a parameter
	 * @param b
	 *            The b parameter
	 * @return The result of multiplication
	 */
	public static final double multiply(double a, double b) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(b))
			return Double.NaN;
		if (!mXparser.checkIfCanonicalRounding())
			return a * b;
		if (Double.isInfinite(a))
			return a * b;
		if (Double.isInfinite(b))
			return a * b;
		BigDecimal da = BigDecimal.valueOf(a);
		BigDecimal db = BigDecimal.valueOf(b);
		return da.multiply(db).doubleValue();
	}

	/**
	 * Division a / b applying canonical rounding if canonical rounding is enabled
	 *
	 * @param a
	 *            The a parameter
	 * @param b
	 *            The b parameter
	 * @return The result of division
	 */
	public static final double div(double a, double b) {
		if (b == 0)
			return Double.NaN;
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(b))
			return Double.NaN;
		if (!mXparser.checkIfCanonicalRounding())
			return a / b;
		if (Double.isInfinite(a))
			return a / b;
		if (Double.isInfinite(b))
			return a / b;
		BigDecimal da = BigDecimal.valueOf(a);
		BigDecimal db = BigDecimal.valueOf(b);
		return da.divide(db, MathContext.DECIMAL128).doubleValue();
	}

	public static final double abs(double a) {
		if (Double.isNaN(a))
			return Double.NaN;
		return Math.abs(a);
	}

	/**
	 * Factorial
	 *
	 * @param n
	 *            the n function parameter
	 *
	 * @return Factorial if n &gt;=0, otherwise returns Double.NaN.
	 */
	public static final double factorial(int n) {
		double f = Double.NaN;
		if (n >= 0)
			if (n < 2)
				f = 1;
			else {
				f = 1;
				for (int i = 1; i <= n; i++) {
					f = f * i;
					if (mXparser.isCurrentCalculationCancelled())
						return Double.NaN;
				}
			}
		return f;
	}

	/**
	 * Factorial
	 *
	 * @param n
	 *            the n function parameter
	 *
	 * @return if n &lt;&gt; Double.NaN return factorial( (int)Math.round(n) ),
	 *         otherwise returns Double.NaN.
	 */
	public static final double factorial(double n) {
		if (Double.isNaN(n))
			return Double.NaN;
		return factorial((int) Math.round(n));
	}

	/**
	 * Applies the integer exponent to the base a
	 *
	 * @param a
	 *            The base
	 * @param n
	 *            The integer exponent
	 * @return Return a to the power of n, if canonical rounding is enable, the it
	 *         operates on big numbers
	 */
	private static final double powInt(double a, int n) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isInfinite(a))
			Math.pow(a, n);
		if (a == 0)
			return Math.pow(a, n);
		if (n == 0)
			return 1;
		if (n == 1)
			return a;
		if (mXparser.checkIfCanonicalRounding()) {
			BigDecimal da = BigDecimal.valueOf(a);
			if (n >= 0)
				return da.pow(n).doubleValue();
			else
				return BigDecimal.ONE.divide(da, MathContext.DECIMAL128).pow(-n).doubleValue();
		} else {
			return Math.pow(a, n);
		}
	}

	/**
	 * Power function a^b
	 *
	 * @param a
	 *            the a function parameter
	 * @param b
	 *            the b function parameter
	 *
	 * @return if a,b &lt;&gt; Double.NaN returns Math.pow(a, b), otherwise returns
	 *         Double.NaN.
	 */
	public static final double power(double a, double b) {
		if (Double.isNaN(a) || Double.isNaN(b))
			return Double.NaN;
		if (Double.isInfinite(a))
			Math.pow(a, b);
		if (Double.isInfinite(b))
			Math.pow(a, b);
		double babs = Math.abs(b);
		double bint = Math.round(babs);
		if (MathFunctions.abs(babs - bint) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON) {
			if (b >= 0)
				return powInt(a, (int) bint);
			else
				return powInt(a, -(int) bint);
		} else if (a >= 0)
			return Math.pow(a, b);
		else if (abs(b) >= 1)
			return Math.pow(a, b);
		else if (b == 0)
			return Math.pow(a, b);
		else {
			double ndob = 1.0 / abs(b);
			double nint = Math.round(ndob);
			if (MathFunctions.abs(ndob - nint) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON) {
				long n = (long) nint;
				if (n % 2 == 1)
					if (b > 0)
						return -Math.pow(abs(a), 1.0 / ndob);
					else
						return -Math.pow(abs(a), -1.0 / ndob);
				else
					return Double.NaN;
			} else
				return Double.NaN;
		}
	}

	/**
	 * Tetration, exponential power, power series
	 *
	 * @param a
	 *            base
	 * @param n
	 *            exponent
	 * @return Tetration result.
	 */
	public static final double tetration(double a, double n) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(n))
			return Double.NaN;
		if (n == Double.POSITIVE_INFINITY) {
			if (abs(a - MathConstants.EXP_MINUS_E) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON)
				return MathConstants.EXP_MINUS_1;
			if (abs(a - MathConstants.EXP_1_OVER_E) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON)
				return MathConstants.E;
			if (a > MathConstants.EXP_1_OVER_E)
				return Double.POSITIVE_INFINITY;
			if (a < MathConstants.EXP_MINUS_E)
				return Double.NaN;
		}
		if (n < -BinaryRelations.DEFAULT_COMPARISON_EPSILON)
			return Double.NaN;
		if (abs(n) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON) {
			if (abs(a) > BinaryRelations.DEFAULT_COMPARISON_EPSILON)
				return 1;
			else
				return Double.NaN;
		}
		n = floor(n);
		if (n == 0) {
			if (abs(a) > BinaryRelations.DEFAULT_COMPARISON_EPSILON)
				return 1;
			else
				return Double.NaN;
		}
		if (abs(a) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON)
			return 0;
		if (n == 1)
			return a;
		double r = a;
		for (double i = 2; i <= n; i++) {
			r = Math.pow(a, r);
			if (mXparser.isCurrentCalculationCancelled())
				return Double.NaN;
		}
		return r;
	}

	/**
	 * Modulo operator a % b
	 *
	 * @param a
	 *            the a function parameter
	 * @param b
	 *            the b function parameter
	 *
	 * @return if a,b &lt;&gt; Double.NaN returns a % b.
	 */
	public static final double mod(double a, double b) {
		if (Double.isNaN(a) || Double.isNaN(b))
			return Double.NaN;
		return a % b;
	}

	/**
	 * Natural logarithm
	 *
	 * @param a
	 *            the a function parameter
	 *
	 * @return if a &lt;&gt; Double.NaN returns Math.log(1/a), otherwise returns
	 *         Double.NaN.
	 */
	public static final double ln(double a) {
		if (Double.isNaN(a))
			return Double.NaN;
		return Math.log(a);
	}

	/**
	 * Exponential function.
	 *
	 * @param a
	 *            the a function parameter
	 *
	 * @return if a &lt;&gt; Double.NaN returns Math.exp(a), otherwise returns
	 *         Double.NaN.
	 */
	public static final double exp(double a) {
		if (Double.isNaN(a))
			return Double.NaN;
		return Math.exp(a);
	}

	/**
	 * Square root.
	 *
	 * @param a
	 *            the a function parameter
	 *
	 * @return if a &lt;&gt; Double.NaN returns Math.sqrt(a), otherwise returns
	 *         Double.NaN.
	 */
	public static final double sqrt(double a) {
		if (Double.isNaN(a))
			return Double.NaN;
		return Math.sqrt(a);
	}

	/**
	 * Floor function.
	 *
	 * @param a
	 *            the a function parameter
	 *
	 * @return if a &lt;&gt; Double.NaN returns Math.floor(a), otherwise returns
	 *         Double.NaN.
	 */
	public static final double floor(double a) {
		if (Double.isNaN(a))
			return Double.NaN;
		return Math.floor(a);
	}

	/**
	 * Double rounding
	 *
	 * @param value
	 *            double value to be rounded
	 * @param places
	 *            decimal places
	 * @return Rounded value
	 */
	public static final double round(double value, int places) {
		if (Double.isNaN(value))
			return Double.NaN;
		if (Double.isInfinite(value))
			return value;
		if (places < 0)
			return Double.NaN;
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * For very small number returns the position of first significant digit, ie 0.1
	 * = 1, 0.01 = 2
	 *
	 * @param value
	 *            Double value, small one.
	 * @return Number of digits, number of places.
	 */
	public static final int decimalDigitsBefore(double value) {
		if (value == 0)
			return -1;
		if (value <= 1e-322)
			return 322;
		else if (value <= 1e-321)
			return 321;
		else if (value <= 1e-320)
			return 320;
		else if (value <= 1e-319)
			return 319;
		else if (value <= 1e-318)
			return 318;
		else if (value <= 1e-317)
			return 317;
		else if (value <= 1e-316)
			return 316;
		else if (value <= 1e-315)
			return 315;
		else if (value <= 1e-314)
			return 314;
		else if (value <= 1e-313)
			return 313;
		else if (value <= 1e-312)
			return 312;
		else if (value <= 1e-311)
			return 311;
		else if (value <= 1e-310)
			return 310;
		else if (value <= 1e-309)
			return 309;
		else if (value <= 1e-308)
			return 308;
		else if (value <= 1e-307)
			return 307;
		else if (value <= 1e-306)
			return 306;
		else if (value <= 1e-305)
			return 305;
		else if (value <= 1e-304)
			return 304;
		else if (value <= 1e-303)
			return 303;
		else if (value <= 1e-302)
			return 302;
		else if (value <= 1e-301)
			return 301;
		else if (value <= 1e-300)
			return 300;
		else if (value <= 1e-299)
			return 299;
		else if (value <= 1e-298)
			return 298;
		else if (value <= 1e-297)
			return 297;
		else if (value <= 1e-296)
			return 296;
		else if (value <= 1e-295)
			return 295;
		else if (value <= 1e-294)
			return 294;
		else if (value <= 1e-293)
			return 293;
		else if (value <= 1e-292)
			return 292;
		else if (value <= 1e-291)
			return 291;
		else if (value <= 1e-290)
			return 290;
		else if (value <= 1e-289)
			return 289;
		else if (value <= 1e-288)
			return 288;
		else if (value <= 1e-287)
			return 287;
		else if (value <= 1e-286)
			return 286;
		else if (value <= 1e-285)
			return 285;
		else if (value <= 1e-284)
			return 284;
		else if (value <= 1e-283)
			return 283;
		else if (value <= 1e-282)
			return 282;
		else if (value <= 1e-281)
			return 281;
		else if (value <= 1e-280)
			return 280;
		else if (value <= 1e-279)
			return 279;
		else if (value <= 1e-278)
			return 278;
		else if (value <= 1e-277)
			return 277;
		else if (value <= 1e-276)
			return 276;
		else if (value <= 1e-275)
			return 275;
		else if (value <= 1e-274)
			return 274;
		else if (value <= 1e-273)
			return 273;
		else if (value <= 1e-272)
			return 272;
		else if (value <= 1e-271)
			return 271;
		else if (value <= 1e-270)
			return 270;
		else if (value <= 1e-269)
			return 269;
		else if (value <= 1e-268)
			return 268;
		else if (value <= 1e-267)
			return 267;
		else if (value <= 1e-266)
			return 266;
		else if (value <= 1e-265)
			return 265;
		else if (value <= 1e-264)
			return 264;
		else if (value <= 1e-263)
			return 263;
		else if (value <= 1e-262)
			return 262;
		else if (value <= 1e-261)
			return 261;
		else if (value <= 1e-260)
			return 260;
		else if (value <= 1e-259)
			return 259;
		else if (value <= 1e-258)
			return 258;
		else if (value <= 1e-257)
			return 257;
		else if (value <= 1e-256)
			return 256;
		else if (value <= 1e-255)
			return 255;
		else if (value <= 1e-254)
			return 254;
		else if (value <= 1e-253)
			return 253;
		else if (value <= 1e-252)
			return 252;
		else if (value <= 1e-251)
			return 251;
		else if (value <= 1e-250)
			return 250;
		else if (value <= 1e-249)
			return 249;
		else if (value <= 1e-248)
			return 248;
		else if (value <= 1e-247)
			return 247;
		else if (value <= 1e-246)
			return 246;
		else if (value <= 1e-245)
			return 245;
		else if (value <= 1e-244)
			return 244;
		else if (value <= 1e-243)
			return 243;
		else if (value <= 1e-242)
			return 242;
		else if (value <= 1e-241)
			return 241;
		else if (value <= 1e-240)
			return 240;
		else if (value <= 1e-239)
			return 239;
		else if (value <= 1e-238)
			return 238;
		else if (value <= 1e-237)
			return 237;
		else if (value <= 1e-236)
			return 236;
		else if (value <= 1e-235)
			return 235;
		else if (value <= 1e-234)
			return 234;
		else if (value <= 1e-233)
			return 233;
		else if (value <= 1e-232)
			return 232;
		else if (value <= 1e-231)
			return 231;
		else if (value <= 1e-230)
			return 230;
		else if (value <= 1e-229)
			return 229;
		else if (value <= 1e-228)
			return 228;
		else if (value <= 1e-227)
			return 227;
		else if (value <= 1e-226)
			return 226;
		else if (value <= 1e-225)
			return 225;
		else if (value <= 1e-224)
			return 224;
		else if (value <= 1e-223)
			return 223;
		else if (value <= 1e-222)
			return 222;
		else if (value <= 1e-221)
			return 221;
		else if (value <= 1e-220)
			return 220;
		else if (value <= 1e-219)
			return 219;
		else if (value <= 1e-218)
			return 218;
		else if (value <= 1e-217)
			return 217;
		else if (value <= 1e-216)
			return 216;
		else if (value <= 1e-215)
			return 215;
		else if (value <= 1e-214)
			return 214;
		else if (value <= 1e-213)
			return 213;
		else if (value <= 1e-212)
			return 212;
		else if (value <= 1e-211)
			return 211;
		else if (value <= 1e-210)
			return 210;
		else if (value <= 1e-209)
			return 209;
		else if (value <= 1e-208)
			return 208;
		else if (value <= 1e-207)
			return 207;
		else if (value <= 1e-206)
			return 206;
		else if (value <= 1e-205)
			return 205;
		else if (value <= 1e-204)
			return 204;
		else if (value <= 1e-203)
			return 203;
		else if (value <= 1e-202)
			return 202;
		else if (value <= 1e-201)
			return 201;
		else if (value <= 1e-200)
			return 200;
		else if (value <= 1e-199)
			return 199;
		else if (value <= 1e-198)
			return 198;
		else if (value <= 1e-197)
			return 197;
		else if (value <= 1e-196)
			return 196;
		else if (value <= 1e-195)
			return 195;
		else if (value <= 1e-194)
			return 194;
		else if (value <= 1e-193)
			return 193;
		else if (value <= 1e-192)
			return 192;
		else if (value <= 1e-191)
			return 191;
		else if (value <= 1e-190)
			return 190;
		else if (value <= 1e-189)
			return 189;
		else if (value <= 1e-188)
			return 188;
		else if (value <= 1e-187)
			return 187;
		else if (value <= 1e-186)
			return 186;
		else if (value <= 1e-185)
			return 185;
		else if (value <= 1e-184)
			return 184;
		else if (value <= 1e-183)
			return 183;
		else if (value <= 1e-182)
			return 182;
		else if (value <= 1e-181)
			return 181;
		else if (value <= 1e-180)
			return 180;
		else if (value <= 1e-179)
			return 179;
		else if (value <= 1e-178)
			return 178;
		else if (value <= 1e-177)
			return 177;
		else if (value <= 1e-176)
			return 176;
		else if (value <= 1e-175)
			return 175;
		else if (value <= 1e-174)
			return 174;
		else if (value <= 1e-173)
			return 173;
		else if (value <= 1e-172)
			return 172;
		else if (value <= 1e-171)
			return 171;
		else if (value <= 1e-170)
			return 170;
		else if (value <= 1e-169)
			return 169;
		else if (value <= 1e-168)
			return 168;
		else if (value <= 1e-167)
			return 167;
		else if (value <= 1e-166)
			return 166;
		else if (value <= 1e-165)
			return 165;
		else if (value <= 1e-164)
			return 164;
		else if (value <= 1e-163)
			return 163;
		else if (value <= 1e-162)
			return 162;
		else if (value <= 1e-161)
			return 161;
		else if (value <= 1e-160)
			return 160;
		else if (value <= 1e-159)
			return 159;
		else if (value <= 1e-158)
			return 158;
		else if (value <= 1e-157)
			return 157;
		else if (value <= 1e-156)
			return 156;
		else if (value <= 1e-155)
			return 155;
		else if (value <= 1e-154)
			return 154;
		else if (value <= 1e-153)
			return 153;
		else if (value <= 1e-152)
			return 152;
		else if (value <= 1e-151)
			return 151;
		else if (value <= 1e-150)
			return 150;
		else if (value <= 1e-149)
			return 149;
		else if (value <= 1e-148)
			return 148;
		else if (value <= 1e-147)
			return 147;
		else if (value <= 1e-146)
			return 146;
		else if (value <= 1e-145)
			return 145;
		else if (value <= 1e-144)
			return 144;
		else if (value <= 1e-143)
			return 143;
		else if (value <= 1e-142)
			return 142;
		else if (value <= 1e-141)
			return 141;
		else if (value <= 1e-140)
			return 140;
		else if (value <= 1e-139)
			return 139;
		else if (value <= 1e-138)
			return 138;
		else if (value <= 1e-137)
			return 137;
		else if (value <= 1e-136)
			return 136;
		else if (value <= 1e-135)
			return 135;
		else if (value <= 1e-134)
			return 134;
		else if (value <= 1e-133)
			return 133;
		else if (value <= 1e-132)
			return 132;
		else if (value <= 1e-131)
			return 131;
		else if (value <= 1e-130)
			return 130;
		else if (value <= 1e-129)
			return 129;
		else if (value <= 1e-128)
			return 128;
		else if (value <= 1e-127)
			return 127;
		else if (value <= 1e-126)
			return 126;
		else if (value <= 1e-125)
			return 125;
		else if (value <= 1e-124)
			return 124;
		else if (value <= 1e-123)
			return 123;
		else if (value <= 1e-122)
			return 122;
		else if (value <= 1e-121)
			return 121;
		else if (value <= 1e-120)
			return 120;
		else if (value <= 1e-119)
			return 119;
		else if (value <= 1e-118)
			return 118;
		else if (value <= 1e-117)
			return 117;
		else if (value <= 1e-116)
			return 116;
		else if (value <= 1e-115)
			return 115;
		else if (value <= 1e-114)
			return 114;
		else if (value <= 1e-113)
			return 113;
		else if (value <= 1e-112)
			return 112;
		else if (value <= 1e-111)
			return 111;
		else if (value <= 1e-110)
			return 110;
		else if (value <= 1e-109)
			return 109;
		else if (value <= 1e-108)
			return 108;
		else if (value <= 1e-107)
			return 107;
		else if (value <= 1e-106)
			return 106;
		else if (value <= 1e-105)
			return 105;
		else if (value <= 1e-104)
			return 104;
		else if (value <= 1e-103)
			return 103;
		else if (value <= 1e-102)
			return 102;
		else if (value <= 1e-101)
			return 101;
		else if (value <= 1e-100)
			return 100;
		else if (value <= 1e-99)
			return 99;
		else if (value <= 1e-98)
			return 98;
		else if (value <= 1e-97)
			return 97;
		else if (value <= 1e-96)
			return 96;
		else if (value <= 1e-95)
			return 95;
		else if (value <= 1e-94)
			return 94;
		else if (value <= 1e-93)
			return 93;
		else if (value <= 1e-92)
			return 92;
		else if (value <= 1e-91)
			return 91;
		else if (value <= 1e-90)
			return 90;
		else if (value <= 1e-89)
			return 89;
		else if (value <= 1e-88)
			return 88;
		else if (value <= 1e-87)
			return 87;
		else if (value <= 1e-86)
			return 86;
		else if (value <= 1e-85)
			return 85;
		else if (value <= 1e-84)
			return 84;
		else if (value <= 1e-83)
			return 83;
		else if (value <= 1e-82)
			return 82;
		else if (value <= 1e-81)
			return 81;
		else if (value <= 1e-80)
			return 80;
		else if (value <= 1e-79)
			return 79;
		else if (value <= 1e-78)
			return 78;
		else if (value <= 1e-77)
			return 77;
		else if (value <= 1e-76)
			return 76;
		else if (value <= 1e-75)
			return 75;
		else if (value <= 1e-74)
			return 74;
		else if (value <= 1e-73)
			return 73;
		else if (value <= 1e-72)
			return 72;
		else if (value <= 1e-71)
			return 71;
		else if (value <= 1e-70)
			return 70;
		else if (value <= 1e-69)
			return 69;
		else if (value <= 1e-68)
			return 68;
		else if (value <= 1e-67)
			return 67;
		else if (value <= 1e-66)
			return 66;
		else if (value <= 1e-65)
			return 65;
		else if (value <= 1e-64)
			return 64;
		else if (value <= 1e-63)
			return 63;
		else if (value <= 1e-62)
			return 62;
		else if (value <= 1e-61)
			return 61;
		else if (value <= 1e-60)
			return 60;
		else if (value <= 1e-59)
			return 59;
		else if (value <= 1e-58)
			return 58;
		else if (value <= 1e-57)
			return 57;
		else if (value <= 1e-56)
			return 56;
		else if (value <= 1e-55)
			return 55;
		else if (value <= 1e-54)
			return 54;
		else if (value <= 1e-53)
			return 53;
		else if (value <= 1e-52)
			return 52;
		else if (value <= 1e-51)
			return 51;
		else if (value <= 1e-50)
			return 50;
		else if (value <= 1e-49)
			return 49;
		else if (value <= 1e-48)
			return 48;
		else if (value <= 1e-47)
			return 47;
		else if (value <= 1e-46)
			return 46;
		else if (value <= 1e-45)
			return 45;
		else if (value <= 1e-44)
			return 44;
		else if (value <= 1e-43)
			return 43;
		else if (value <= 1e-42)
			return 42;
		else if (value <= 1e-41)
			return 41;
		else if (value <= 1e-40)
			return 40;
		else if (value <= 1e-39)
			return 39;
		else if (value <= 1e-38)
			return 38;
		else if (value <= 1e-37)
			return 37;
		else if (value <= 1e-36)
			return 36;
		else if (value <= 1e-35)
			return 35;
		else if (value <= 1e-34)
			return 34;
		else if (value <= 1e-33)
			return 33;
		else if (value <= 1e-32)
			return 32;
		else if (value <= 1e-31)
			return 31;
		else if (value <= 1e-30)
			return 30;
		else if (value <= 1e-29)
			return 29;
		else if (value <= 1e-28)
			return 28;
		else if (value <= 1e-27)
			return 27;
		else if (value <= 1e-26)
			return 26;
		else if (value <= 1e-25)
			return 25;
		else if (value <= 1e-24)
			return 24;
		else if (value <= 1e-23)
			return 23;
		else if (value <= 1e-22)
			return 22;
		else if (value <= 1e-21)
			return 21;
		else if (value <= 1e-20)
			return 20;
		else if (value <= 1e-19)
			return 19;
		else if (value <= 1e-18)
			return 18;
		else if (value <= 1e-17)
			return 17;
		else if (value <= 1e-16)
			return 16;
		else if (value <= 1e-15)
			return 15;
		else if (value <= 1e-14)
			return 14;
		else if (value <= 1e-13)
			return 13;
		else if (value <= 1e-12)
			return 12;
		else if (value <= 1e-11)
			return 11;
		else if (value <= 1e-10)
			return 10;
		else if (value <= 1e-9)
			return 9;
		else if (value <= 1e-8)
			return 8;
		else if (value <= 1e-7)
			return 7;
		else if (value <= 1e-6)
			return 6;
		else if (value <= 1e-5)
			return 5;
		else if (value <= 1e-4)
			return 4;
		else if (value <= 1e-3)
			return 3;
		else if (value <= 1e-2)
			return 2;
		else if (value <= 1e-1)
			return 1;
		else if (value <= 1e-0)
			return 0;
		else
			return -1;
	}

	/**
	 * Unit in the last place(ULP) for double
	 * 
	 * @param value
	 *            Double number
	 * @return ULP for a given double.
	 */
	public static final double ulp(double value) {
		return Math.ulp(value);
	}

	/**
	 * Unit in The Last Place - number of decimal digits before
	 * 
	 * @param value
	 *            Double number
	 * @return Positive number of digits N for ulp = 1e-{N+1}, if ulp is &gt; 1 then
	 *         -1 is returned. Returned proper value is always between -1 and +322.
	 *         If value is NaN then -2 is returned.
	 */
	public static final int ulpDecimalDigitsBefore(double value) {
		if (Double.isNaN(value))
			return -2;
		double u = ulp(value);
		return decimalDigitsBefore(u);
	}

	/**
	 * Check whether double value is almost integer.
	 * 
	 * @param x
	 *            Number
	 * @return True if double value is almost integer, otherwise false.
	 *         {@link BinaryRelations#DEFAULT_COMPARISON_EPSILON}
	 *
	 * @see BinaryRelations#DEFAULT_COMPARISON_EPSILON
	 */
	public static final boolean isInteger(double x) {
		if (Double.isNaN(x))
			return false;
		if (x == Double.POSITIVE_INFINITY)
			return false;
		if (x == Double.NEGATIVE_INFINITY)
			return false;
		if (x < 0)
			x = -x;
		double round = Math.round(x);
		if (Math.abs(x - round) < BinaryRelations.DEFAULT_COMPARISON_EPSILON)
			return true;
		else
			return false;
	}

	/**
	 * Check whether two double values are almost equal.
	 * 
	 * @param a
	 *            First number
	 * @param b
	 *            Second number
	 * @return True if double values are almost equal, otherwise false.
	 *         {@link BinaryRelations#DEFAULT_COMPARISON_EPSILON}
	 *
	 * @see BinaryRelations#DEFAULT_COMPARISON_EPSILON
	 */
	public static final boolean almostEqual(double a, double b) {
		if (Double.isNaN(a))
			return false;
		if (Double.isNaN(b))
			return false;
		if (a == b)
			return true;
		if (Math.abs(a - b) <= BinaryRelations.DEFAULT_COMPARISON_EPSILON)
			return true;
		return false;
	}
}
