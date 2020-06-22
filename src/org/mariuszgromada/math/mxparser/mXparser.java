/*
 * @(#)mXparser.java        4.4.2   2020-01-25
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
package org.mariuszgromada.math.mxparser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.mariuszgromada.math.mxparser.mathcollection.PrimesCache;

/**
 * mXparser class provides usefull methods when parsing, calculating or
 * parameters transforming.
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
 *
 * @see RecursiveArgument
 * @see Expression
 * @see Function
 * @see Constant
 */
public final class mXparser {
	/**
	 * mXparser version
	 */
	private static final String VERSION = "4.4.2";

	/**
	 * Framework used to compile mXparser
	 */
	private static final String BUIT_FOR = "JDK 8";
	/**
	 * FOUND / NOT_FOUND used for matching purposes
	 */
	static final int NOT_FOUND = -1;
	static final int FOUND = 0;
	/**
	 * Console output string for below methods
	 *
	 * @see mXparser.#consolePrintln(Object)
	 * @see mXparser.#consolePrint(Object)
	 */
	private static volatile String CONSOLE_OUTPUT = "";
	private static volatile String CONSOLE_PREFIX = "[mXparser-v." + VERSION + " bin " + BUIT_FOR + "] ";
	private static volatile int CONSOLE_ROW_NUMBER = 1;
	/**
	 * Prime numbers cache
	 */
	public volatile static PrimesCache primesCache;
	
	/**
	 * Double floating-point precision arithmetic causes
	 *
	 * mXparser provides intelligent ULP rounding to avoid some type of this errors.
	 */
	static volatile boolean ulpRounding = false;
	/**
	 * Double floating-point precision arithmetic causes rounding problems, i.e. 0.1
	 * + 0.1 + 0.1 is different than 0.3
	 *
	 * mXparser provides intelligent canonical rounding to avoid majority of this
	 * errors.
	 *
	 */
	static volatile boolean canonicalRounding = true;
	/**
	 * Indicator marking whether to round final result to precise integer when
	 * result is very close to integer, solves problems like sin(pi) = 0
	 */
	static volatile boolean almostIntRounding = true;
	private static final int DEFAULT_MAX_RECURSION_CALLS = 200;
	/**
	 * Internal limit for counter to avoid infinite loops while calculating
	 * expression defined in the way shown by below examples
	 *
	 * Argument x = new Argument("x = 2*y"); Argument y = new Argument("y = 2*x");
	 * x.addDefinitions(y); y.addDefinitions(x);
	 *
	 * Function f = new Function("f(x) = 2*g(x)"); Function g = new Function("g(x) =
	 * 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
	 */
	static volatile int MAX_RECURSION_CALLS = DEFAULT_MAX_RECURSION_CALLS;
	/**
	 * List of built-in tokens to remove.
	 */
	static volatile List<String> tokensToRemove = new ArrayList<String>();
	/**
	 * List of built-in tokens to modify
	 */
	static volatile List<TokenModification> tokensToModify = new ArrayList<TokenModification>();
	/**
	 * Indicator whether mXparser operates in radians / degrees mode true - degrees
	 * mode false - radians mode
	 *
	 * Default false (radians mode)
	 */
	static volatile boolean degreesMode = false;
	/**
	 * Options changeset
	 */
	static volatile int optionsChangesetNumber = 0;
	/**
	 * Indicator whether to call cancel current calculation
	 */
	private static volatile boolean cancelCurrentCalculationFlag = false;

	

	

	

	

	/**
	 * Double floating-point precision arithmetic causes rounding problems, i.e. 0.1
	 * + 0.1 + 0.1 is slightly different than 0.3, additionally doubles are having a
	 * lot of advantages providing flexible number representation regardless of
	 * number size. mXparser is fully based on double numbers and that is why is
	 * providing intelligent canonical rounding to minimize misleading results. By
	 * default this option is enabled resulting in automatic rounding only in some
	 * cases. Using this mode 2.5 - 2.2 = 0.3
	 *
	 * @return True if Canonical rounding is enabled, otherwise false.
	 */
	public static final boolean checkIfCanonicalRounding() {
		return canonicalRounding;
	}

	

	


	

	

	

	

	

	

	

	/**
	 * Prints object.toString to the Console
	 *
	 * @param o
	 *            Object to print
	 */
	public static final void consolePrint(Object o) {
		synchronized (CONSOLE_OUTPUT) {
			if ((CONSOLE_ROW_NUMBER == 1) && (CONSOLE_OUTPUT.equals(""))) {
				System.out.print(CONSOLE_PREFIX);
				CONSOLE_OUTPUT = CONSOLE_PREFIX;
			}
			System.out.print(o);
			CONSOLE_OUTPUT = CONSOLE_OUTPUT + o;
		}
	}

	/**
	 * Function used to introduce some compatibility between JAVA and C# while
	 * regexp matching.
	 *
	 * @param str
	 *            String
	 * @param pattern
	 *            Pattern (regexp)
	 *
	 * @return True if pattern matches entirely, False otherwise
	 */
	public static final boolean regexMatch(String str, String pattern) {
		return Pattern.matches(pattern, str);
	}

	/**
	 * Check whether a flag to cancel current calculation process is set.
	 *
	 * {@link #cancelCurrentCalculation()}
	 * {@link #resetCancelCurrentCalculationFlag()}
	 *
	 * @return true in case cancel calculation flag is active, otherwise false
	 */
	public static final boolean isCurrentCalculationCancelled() {
		return cancelCurrentCalculationFlag;
	}

	/*
	 * mXparser version names
	 */
	public static final String NAMEv10 = "1.0";
	public static final String NAMEv24 = "2.4";
	public static final String NAMEv30 = "3.0";
	public static final String NAMEv40 = "4.0";
	public static final String NAMEv41 = "4.1";
	public static final String NAMEv42 = "4.2";
}
