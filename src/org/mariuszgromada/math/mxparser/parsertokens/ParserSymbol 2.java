/*
 * @(#)ParserSymbol.java        4.3.0    2019-01-18
 *
 * You may use this software under the condition of "Simplified BSD License"
 *
 * Copyright 2010-2018 MARIUSZ GROMADA. All rights reserved.
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
package org.mariuszgromada.math.mxparser.parsertokens;

import org.mariuszgromada.math.mxparser.mXparser;
/**
 * Parser symbols - mXparser tokens definition.
 *
 * @author         <b>Mariusz Gromada</b><br>
 *                 <a href="mailto:mariuszgromada.org@gmail.com">mariuszgromada.org@gmail.com</a><br>
 *                 <a href="http://mathspace.pl" target="_blank">MathSpace.pl</a><br>
 *                 <a href="http://mathparser.org" target="_blank">MathParser.org - mXparser project page</a><br>
 *                 <a href="http://github.com/mariuszgromada/MathParser.org-mXparser" target="_blank">mXparser on GitHub</a><br>
 *                 <a href="http://mxparser.sourceforge.net" target="_blank">mXparser on SourceForge</a><br>
 *                 <a href="http://bitbucket.org/mariuszgromada/mxparser" target="_blank">mXparser on Bitbucket</a><br>
 *                 <a href="http://mxparser.codeplex.com" target="_blank">mXparser on CodePlex</a><br>
 *                 <a href="http://janetsudoku.mariuszgromada.org" target="_blank">Janet Sudoku - project web page</a><br>
 *                 <a href="http://github.com/mariuszgromada/Janet-Sudoku" target="_blank">Janet Sudoku on GitHub</a><br>
 *                 <a href="http://janetsudoku.codeplex.com" target="_blank">Janet Sudoku on CodePlex</a><br>
 *                 <a href="http://sourceforge.net/projects/janetsudoku" target="_blank">Janet Sudoku on SourceForge</a><br>
 *                 <a href="http://bitbucket.org/mariuszgromada/janet-sudoku" target="_blank">Janet Sudoku on BitBucket</a><br>
 *                 <a href="https://play.google.com/store/apps/details?id=org.mathparser.scalar.lite" target="_blank">Scalar Free</a><br>
 *                 <a href="https://play.google.com/store/apps/details?id=org.mathparser.scalar.pro" target="_blank">Scalar Pro</a><br>
 *                 <a href="http://scalarmath.org/" target="_blank">ScalarMath.org</a><br>
 *
 * @version        4.2.0
 */
public final class ParserSymbol {
	/*
	 * ParserSymbol - reg exp patterns.
	 */
	private static final String DIGIT							= "[0-9]";
	
	private static final String INTEGER							= DIGIT + "(" + DIGIT + ")*";
	private static final String DEC_FRACT						= "(" + INTEGER + ")?" + "\\." + INTEGER;
	private static final String DEC_FRACT_OR_INT					= "(" + DEC_FRACT + "|" + INTEGER + ")";
	public static final String DECIMAL_REG_EXP					= "[+-]?" + DEC_FRACT_OR_INT + "([eE][+-]?" + INTEGER + ")?";
	
	
	public static final String FRACTION							= "(" + INTEGER + "\\_)?" + INTEGER + "\\_" + INTEGER;
	public static final String nameOnlyTokenRegExp				= "([a-zA-Z_])+([a-zA-Z0-9_])*";
	public static final String unitOnlyTokenRegExp				= "\\[" + nameOnlyTokenRegExp + "\\]";
	public static final String nameOnlyTokenOptBracketsRegExp	= "(" +  nameOnlyTokenRegExp + "|" + unitOnlyTokenRegExp + ")";
	/*
	 * ParserSymbol - token type id.
	 */
	public static final int TYPE_ID 						= 20;
	public static final String TYPE_DESC					= "Parser Symbol";
	/*
	 * ParserSymbol - tokens id.
	 */
	public static final int LEFT_PARENTHESES_ID 			= 1;
	public static final int RIGHT_PARENTHESES_ID			= 2;
	public static final int COMMA_ID						= 3;
	public static final int BLANK_ID						= 4;
	public static final int NUMBER_ID						= 1;
	public static final int NUMBER_TYPE_ID					= 0;

	/*
	 * ParserSymbol - tokens key words.
	 */
	public static final String LEFT_PARENTHESES_STR 		= "(";
	public static final String RIGHT_PARENTHESES_STR		= ")";
	public static final String COMMA_STR					= ",";
	public static final String SEMI_STR						= ";";
	public static final String BLANK_STR					= " ";
	public static final String NUMBER_STR					= "_num_";
	/*
	 * ParserSymbol - syntax.
	 */
	public static final String LEFT_PARENTHESES_SYN 		= "( ... )";
	public static final String RIGHT_PARENTHESES_SYN		= "( ... )";
	public static final String COMMA_SYN					= "(a1, ... ,an)";
	public static final String SEMI_SYN						= "(a1; ... ;an)";
	public static final String BLANK_SYN					= " ";
	public static final String NUMBER_SYN					= "Integer (since v.1.0): 1, -2; Decimal (since v.1.0): 0.2, -0.3, 1.2; Leading zero (since v.4.1): 001, -002.1; Scientific notation (since v.4.2): 1.2e-10, 1.2e+10, 2.3e10; No leading zero (since v.4.2): .2, -.212; Fractions (since v.4.2): 1_2, 2_1_3, 14_3; Other systems (since v.4.1): b1.111, b2.1001, b3.12021, b16.af12, h.af1, b.1001, o.0127";
	/*
	 * ParserSymbol - tokens description.
	 */
	public static final String LEFT_PARENTHESES_DESC 		= "Left parentheses";
	public static final String RIGHT_PARENTHESES_DESC		= "Right parentheses";
	public static final String COMMA_DESC					= "Comma (function parameters)";
	public static final String SEMI_DESC					= "Semicolon (function parameters)";
	public static final String BLANK_DESC					= "Blank (whitespace) character";
	
	public static final String NUMBER_REG_DESC				= "Regullar expression for decimal numbers";
	/*
	 * ParserSymbol - since.
	 */
	public static final String LEFT_PARENTHESES_SINCE 		= mXparser.NAMEv10;
	public static final String RIGHT_PARENTHESES_SINCE		= mXparser.NAMEv10;
	public static final String COMMA_SINCE					= mXparser.NAMEv10;
	
	public static final String BLANK_SINCE					= mXparser.NAMEv42;
	public static final String NUMBER_SINCE					= mXparser.NAMEv10;
}
