/*
 * @(#)Miscellaneous.java        4.1.0    2017-05-28
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
package org.mariuszgromada.math.mxparser;

import java.util.Comparator;
import java.util.List;

import org.mariuszgromada.math.mxparser.parsertokens.KeyWord;
import org.mariuszgromada.math.mxparser.parsertokens.Token;

/*=================================================
*
* Package level classes and interfaces
*
*=================================================
*/
/**
 * Package level class for handling function parameters.
 */
class FunctionParameter {
	List<Token> tokens;
	String paramStr;
	int fromIndex;
	int toIndex;

	FunctionParameter(List<Token> tokens, String paramStr, int fromIndex, int toIndex) {
		this.tokens = tokens;
		this.paramStr = paramStr;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}
}

/**
 * Internal token class which is used with stack while evaluation of tokens
 * levels
 */
class TokenStackElement {
	boolean precedingFunction;
}

class SyntaxStackElement {
	String tokenStr;
	int tokenLevel;

}

/*
 * --------------------------------------------------------- Comparators for
 * sorting ---------------------------------------------------------
 */
/**
 * Comparator for key word list sorting by key word string. This king of sorting
 * is used while checking the syntax (duplicated key word error)
 */
class KwStrComparator implements Comparator<KeyWord> {
	/**
	 *
	 */
	public int compare(KeyWord kw1, KeyWord kw2) {
		String s1 = kw1.wordString;
		String s2 = kw2.wordString;
		return s1.compareTo(s2);
	}
}

/**
 * Comparator for key word list sorting by descending key word length . This
 * king of sorting is used while tokenizing (best match)
 */
class DescKwLenComparator implements Comparator<KeyWord> {
	/**
	 *
	 */
	public int compare(KeyWord kw1, KeyWord kw2) {
		int l1 = kw1.wordString.length();
		int l2 = kw2.wordString.length();
		return l2 - l1;
	}
}

/**
 * Comparator for key word list sorting by type of the key word
 *
 */
class KwTypeComparator implements Comparator<KeyWord> {
	/**
	 *
	 */
	public int compare(KeyWord kw1, KeyWord kw2) {
		int t1 = kw1.wordTypeId * 1000000 + kw1.wordId * 1000 + kw1.wordString.length();
		int t2 = kw2.wordTypeId * 1000000 + kw2.wordId * 1000 + kw2.wordString.length();
		return t1 - t2;
	}
}



/**
 * Data structure used internally for token to be modified list
 */
class TokenModification {
	String currentToken;
	String newToken;
	String newTokenDescription;
}