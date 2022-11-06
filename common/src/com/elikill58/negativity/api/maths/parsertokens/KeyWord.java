/*
 * @(#)KeyWord.java        4.1.0    2017-06-04
 *
 * You may use this software under the condition of "Simplified BSD License"
 *
 * Copyright 2010-2017 MARIUSZ GROMADA. All rights reserved.
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

package com.elikill58.negativity.api.maths.parsertokens;

/**
 * Class representing key words knowon to the parser
 *
 * @author <b>Mariusz Gromada</b><br>On github: http://github.com/mariuszgromada/MathParser.org-mXparser
 *
 * @version        4.1.0
 */
public class KeyWord {
	static final int NO_DEFINITION = -1;
	public String wordString;
	public int	wordId;
	public int wordTypeId;
	public String description;
	public String syntax;
	public String since;
	public KeyWord() {
		wordString = "";
		wordId = NO_DEFINITION;
		wordTypeId = NO_DEFINITION;
		description = "";
		syntax = "";
		since = "";
	}
	/**
	 * Constructor - creates key words form wordString wordId
	 * wordTypId syntax since
	 *
	 * @param wordString   the word string (refers to below interfaces)
	 * @param wordId       the word identifier (refers to below interfaces)
	 * @param wordTypeId   the word type (refers to below interfaces)
	 * @param description  the word description
	 * @param syntax       the word syntax
	 * @param since        the word version since
	 */
	public KeyWord(String wordString, String description, int wordId, String syntax, String since, int wordTypeId) {
		this.wordString = wordString;
		this.wordId = wordId;
		this.wordTypeId = wordTypeId;
		this.description = description;
		this.syntax = syntax;
		this.since = since;
	}
}