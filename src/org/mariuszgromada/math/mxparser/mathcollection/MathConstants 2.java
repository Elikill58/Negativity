/*
 * @(#)MathConstants.java        4.3.4    2019-12-22
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

/**
 * MathConstants - class representing the most important math constants.
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
 * @version 4.3.4
 */
public final class MathConstants {
	/**
	 * Pi, Archimedes' constant or Ludolph's number
	 */
	public static final double PI = 3.14159265358979323846264338327950288;
	/**
	 * Pi/2
	 */
	static final double PIBY2 = PI / 2.0;
	/**
	 * Napier's constant, or Euler's number, base of Natural logarithm
	 */
	public static final double E = 2.71828182845904523536028747135266249;
	/**
	 * Catalan's constant
	 */
	public static final double CATALAN = 0.91596559417721901505460351493238411;
	/**
	 * Landau-Ramanujan constant
	 */
	public static final double LANDAU_RAMANUJAN = 0.76422365358922066299069873125009232;
	/**
	 * Viswanath's constant
	 */
	public static final double VISWANATH = 1.13198824;
	/**
	 * Legendre's constant
	 */
	public static final double LEGENDRE = 1.0;
	/**
	 * Omega constant
	 */
	static final double OMEGA = 0.56714329040978387299996866221035555;

	/**
	 * Square root of 2
	 */
	static final double SQRT2 = Math.sqrt(2.0);

	/**
	 * Natural logarithm of pi
	 */
	static final double LNPI = MathFunctions.ln(PI);
	/**
	 * Tetration left convergence limit
	 */
	static final double EXP_MINUS_E = Math.pow(E, -E);
	/**
	 * Tetration right convergence limit
	 */
	static final double EXP_1_OVER_E = Math.pow(E, 1.0 / E);
	/**
	 * 1 over e
	 */
	static final double EXP_MINUS_1 = 1.0 / Math.E;
	/**
	 * Natural logarithm of sqrt(2)
	 */
	static final double LN_SQRT2 = MathFunctions.ln(SQRT2);
	/**
	 * SQRT2BY2
	 */
	static final double SQRT2BY2 = SQRT2 / 2.0;
	/**
	 * SQRT3
	 */
	static final double SQRT3 = Math.sqrt(3.0);
	/**
	 * SQRT3BY2
	 */
	static final double SQRT3BY2 = SQRT3 / 2.0;
	/**
	 * D2BYSQRT3
	 */
	static final double D2BYSQRT3 = 2.0 / SQRT3;
	/**
	 * SQRT3BY3
	 */
	static final double SQRT3BY3 = SQRT3 / 3.0;
}