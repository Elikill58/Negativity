/*
 * @(#)ProbabilityDistributions.java        4.3.0   2018-12-12
 *
 * You may use this software under the condition of "Simplified BSD License"
 *
 * Copyright 2010-2019 MARIUSZ GROMADA. All rights reserved.
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
 * Some parts of the ProbabilityDistributions class were adopted from Math.NET Numerics project
 * Copyright (c) 2002-2015 Math.NET   http://numerics.mathdotnet.com/
 * http://numerics.mathdotnet.com/License.html
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

import java.util.Random;

import org.mariuszgromada.math.mxparser.mXparser;

/**
 * ProbabilityDistributions - random number generators, PDF - Probability
 * Distribution Functions, CDF - Cumulative Distribution Functions, QNT -
 * Quantile Functions (Inverse Cumulative Distribution Functions).
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
 * @version 4.3.0
 */
public final class ProbabilityDistributions {
	/**
	 * Random number generator
	 */
	public static Random randomGenerator = new Random();

	/**
	 * Random number from Uniform Continuous distribution over interval [0, 1).
	 *
	 * @param rnd
	 *            Random number generator.
	 * @return Random number.
	 */
	public static final double rndUniformContinuous(Random rnd) {
		return rnd.nextDouble();
	}

	/**
	 * Random number from Uniform Discrete distribution. over set interval (a, a+1,
	 * ..., b-1, b).
	 *
	 * @param a
	 *            Interval limit - left / lower.
	 * @param b
	 *            Interval limit - right / upper.
	 * @param rnd
	 *            Random number generator.
	 * @return Double.NaN if a or b is null, or b is lower than a - otherwise
	 *         returns random number.
	 */
	public static final double rndInteger(int a, int b, Random rnd) {
		if (Double.isNaN(a))
			return Double.NaN;
		if (Double.isNaN(b))
			return Double.NaN;
		if (b < a)
			return Double.NaN;
		if (a == b)
			return a;
		int n = (b - a) + 1;
		int r = a + rnd.nextInt(n);
		return r;
	}

	/**
	 * Random integer.
	 *
	 * @param rnd
	 *            Random number generator.
	 * @return Returns random number.
	 */
	public static final int rndInteger(Random rnd) {
		return rnd.nextInt();
	}

	/**
	 * Random number from normal distribution N(mean, stddev).
	 *
	 * @param mean
	 *            Mean value.
	 * @param stddev
	 *            Standard deviation.
	 * @param rnd
	 *            Random number generator.
	 * @return Double.NaN if mean or stddev or rnd is null or stddev is lower than 0
	 *         - otherwise random number.
	 */
	public static final double rndNormal(double mean, double stddev, Random rnd) {
		if (Double.isNaN(mean))
			return Double.NaN;
		if (Double.isNaN(stddev))
			return Double.NaN;
		if (rnd == null)
			return Double.NaN;
		if (stddev < 0)
			return Double.NaN;
		if (stddev == 0)
			return mean;
		double x, a, v1;
		double b, v2;
		double r, fac;
		boolean polarTransform;
		do {
			if (mXparser.isCurrentCalculationCancelled())
				return Double.NaN;
			a = rnd.nextDouble();
			b = rnd.nextDouble();
			v1 = 2.0 * a - 1.0;
			v2 = 2.0 * b - 1.0;
			r = (v1 * v1) + (v2 * v2);
			if (r >= 1.0 || r == 0.0) {
				x = 0.0;
				polarTransform = false;
			} else {
				fac = MathFunctions.sqrt(-2.0 * MathFunctions.ln(r) / r);
				x = v1 * fac;
				polarTransform = true;
			}
		} while (!polarTransform);
		return mean + (stddev * x);
	}

}
