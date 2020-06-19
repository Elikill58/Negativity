/*
 * @(#)Expression.java        4.4.0   2020-01-11
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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import org.mariuszgromada.math.mxparser.mathcollection.BinaryRelations;
import org.mariuszgromada.math.mxparser.mathcollection.BooleanAlgebra;
import org.mariuszgromada.math.mxparser.mathcollection.MathFunctions;
import org.mariuszgromada.math.mxparser.mathcollection.NumberTheory;
import org.mariuszgromada.math.mxparser.mathcollection.ProbabilityDistributions;
import org.mariuszgromada.math.mxparser.mathcollection.Units;
import org.mariuszgromada.math.mxparser.parsertokens.BinaryRelation;
import org.mariuszgromada.math.mxparser.parsertokens.BitwiseOperator;
import org.mariuszgromada.math.mxparser.parsertokens.BooleanOperator;
import org.mariuszgromada.math.mxparser.parsertokens.CalculusOperator;
import org.mariuszgromada.math.mxparser.parsertokens.KeyWord;
import org.mariuszgromada.math.mxparser.parsertokens.Operator;
import org.mariuszgromada.math.mxparser.parsertokens.ParserSymbol;
import org.mariuszgromada.math.mxparser.parsertokens.RandomVariable;
import org.mariuszgromada.math.mxparser.parsertokens.Token;
import org.mariuszgromada.math.mxparser.parsertokens.Unit;
import org.mariuszgromada.math.mxparser.syntaxchecker.SyntaxChecker;

/**
 * Expression - base class for real expressions definition.
 *
 * Examples:
 * <ul>
 * <li>'1+2'
 * <li>'sin(x)+1'
 * <li>'asin(3*x)^10-log(4,8)'
 * <li>in general 'f(x1,x2,...,xn)' where x1,...,xn are real arguments
 * </ul>
 * <p>
 * Class provides easy way to define multivariate arithmetic expression.
 *
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
 * @version 4.4.0
 *
 */
public class Expression {

	/**
	 * FOUND / NOT_FOUND used for matching purposes
	 */
	private static final int NOT_FOUND = mXparser.NOT_FOUND;
	private static final int FOUND = mXparser.FOUND;

	/**
	 * For verbose mode purposes
	 */
	private static final boolean WITH_EXP_STR = true;
	private static final boolean NO_EXP_STR = false;
	/**
	 * Status of the Expression syntax
	 */
	private static final boolean NO_SYNTAX_ERRORS = true;
	static final boolean SYNTAX_ERROR_OR_STATUS_UNKNOWN = false;
	/**
	 * Expression string (for example: "sin(x)+cos(y)")
	 */
	String expressionString;
	private String description;

	/**
	 * List of key words known by the parser
	 */
	private List<KeyWord> keyWordsList;
	/**
	 * List of expression tokens (words). Token class defines all needed attributes
	 * for recognizing the structure of arithmetic expression. This is the key
	 * result when initial parsing is finished (tokenizeExpressionString() -
	 * method). Token keeps information about: - token type (for example: function,
	 * operator, argument, number, etc...) - token identifier within given type
	 * (sin, cos, operaotr, etc...) - token value (if token is a number) - token
	 * level - key information regarding sequence (order) of further parsing
	 */
	private List<Token> initialTokens;
	/**
	 * the initialTokens list keeps unchanged information about found tokens.
	 *
	 * While parsing the tokensList is used. The tokensList is the same as
	 * initialTokens list at the beginning of the calculation process. Each math
	 * operation changes tokens list - it means that tokens are parameters when
	 * performing math operation and the result is also presented as token (usually
	 * as a number token) At the end of the calculation the tokensList should
	 * contain only one element - the result of all calculations.
	 */
	private List<Token> tokensList;
	/**
	 * List of related expressions, for example when user defined function is used
	 * in the expression or dependent argument was defined. Modification of function
	 * expression calls the method expression modified flag method to all related
	 * expressions.
	 *
	 * Related expression usually are used for - dependent arguments - recursive
	 * arguments - user functions
	 */
	List<Expression> relatedExpressionsList;
	/**
	 * Keeps computing time
	 */
	private double computingTime;
	/**
	 * if true then new tokenizing is required (the initialTokens list needs to be
	 * updated)
	 */
	private boolean expressionWasModified;
	/**
	 * If recursive mode is on the recursive calls are permitted. It means there
	 * will be no null pointer exceptions due to expression, and functions cloning.
	 */
	boolean recursiveMode;
	/**
	 * Verbose mode prints processing info calls System.out.print* methods
	 */
	private boolean verboseMode;
	/**
	 * Internal parameter for calculus expressions to avoid decrease in accuracy.
	 */
	boolean disableRounding;
	private static final boolean KEEP_ROUNDING_SETTINGS = false;
	/**
	 * Status of the expression syntax
	 *
	 * Please referet to the: - NO_SYNTAX_ERRORS - SYNTAX_ERROR_OR_STATUS_UNKNOWN
	 */
	private boolean syntaxStatus;
	/**
	 * Message after checking the syntax
	 */
	private String errorMessage;
	/**
	 * Flag used internally to mark started recursion call on the current object,
	 * necessary to avoid infinite loops while recursive syntax checking (i.e. f ->
	 * g and g -> f) or marking modified flags on the expressions related to this
	 * expression.
	 *
	 * @see setExpressionModifiedFlag()
	 * @see checkSyntax()
	 */
	private boolean recursionCallPending;
	/**
	 * Internal counter to avoid infinite loops while calculating expression defined
	 * in the way shown by below examples
	 *
	 * Argument x = new Argument("x = 2*y"); Argument y = new Argument("y = 2*x");
	 * x.addDefinitions(y); y.addDefinitions(x);
	 *
	 * Function f = new Function("f(x) = 2*g(x)"); Function g = new Function("g(x) =
	 * 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
	 */
	private int recursionCallsCounter;
	/**
	 * Internal indicator for tokenization process if true, then keywords such as
	 * constants functions etc.. will not be recognized during tokenization
	 */
	private boolean parserKeyWordsOnly;
	/**
	 * Indicator whether expression was automatically built for user defined
	 * functions purpose
	 *
	 * @see Function
	 */
	boolean UDFExpression = false;
	/**
	 * List of parameters provided by the user at run-time
	 *
	 * @see Function
	 */
	List<Double> UDFVariadicParamsAtRunTime;
	/**
	 * Internal indicator for calculation process Expression.Calculate() method It
	 * show whether to build again tokens list if clone - build again if not clone -
	 * build only at the beginning
	 *
	 * Indicator helps to solve the problem with above definitions
	 *
	 * Function f = new Function("f(x) = 2*g(x)"); Function g = new Function("g(x) =
	 * 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
	 */
	private boolean internalClone;
	/**
	 * mXparser options changeset used in checkSyntax() method
	 */
	private int optionsChangesetNumber = -1;

	/**
	 * Method return error message after calling checkSyntax() method or
	 * calculate().
	 *
	 * @return Error message as string.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Gets syntax status of the expression.
	 *
	 * @return true if there are no syntax errors, false when syntax error was found
	 *         or syntax status is unknown
	 */
	public boolean getSyntaxStatus() {
		return this.syntaxStatus;
	}

	/**
	 * Sets expression status to modified Calls setExpressionModifiedFlag() method
	 * to all related expressions.
	 */
	void setExpressionModifiedFlag() {
		if (recursionCallPending == false) {
			recursionCallPending = true;
			recursionCallsCounter = 0;
			internalClone = false;
			expressionWasModified = true;
			syntaxStatus = SYNTAX_ERROR_OR_STATUS_UNKNOWN;
			errorMessage = "Syntax status unknown.";
			for (Expression e : relatedExpressionsList)
				e.setExpressionModifiedFlag();
			recursionCallPending = false;
		}
	}

	/**
	 * Common variables while expression initializing
	 */
	private void expressionInternalVarsInit() {
		description = "";
		errorMessage = "";
		computingTime = 0;
		recursionCallPending = false;
		recursionCallsCounter = 0;
		internalClone = false;
		parserKeyWordsOnly = false;
		disableRounding = KEEP_ROUNDING_SETTINGS;
	}

	/**
	 * Common elements while expression initializing
	 */
	private void expressionInit() {
		/*
		 * New lists
		 */
		relatedExpressionsList = new ArrayList<Expression>();
		/*
		 * Empty description Silent mode No recursive mode
		 */
		setSilentMode();
		disableRecursiveMode();
		expressionInternalVarsInit();
	}

	/**
	 * Constructor - creates new expression from expression string.
	 *
	 * @param expressionString
	 *            definition of the expression
	 * @param elements
	 *            Optional elements list (variadic - comma separated) of types:
	 *            Argument, Constant, Function
	 *
	 * @see PrimitiveElement
	 *
	 */
	public Expression(String expressionString) {
		expressionInit();
		this.expressionString = new String(expressionString);
		setExpressionModifiedFlag();
	}

	/**
	 * Package level constructor - creates new expression from subexpression
	 * (sublist of the tokens list), arguments list, functions list and constants
	 * list (used by the internal calculus operations, etc...).
	 *
	 * @param expressionString
	 *            the expression string
	 * @param initialTokens
	 *            the tokens list (starting point - no tokenizing, no syntax
	 *            checking)
	 * @param argumentsList
	 *            the arguments list
	 * @param functionsList
	 *            the functions list
	 * @param constantsList
	 *            the constants list
	 */
	Expression(String expressionString, List<Token> initialTokens, boolean disableUlpRounding, boolean UDFExpression,
			List<Double> UDFVariadicParamsAtRunTime) {
		this.expressionString = expressionString;
		this.initialTokens = initialTokens;
		relatedExpressionsList = new ArrayList<Expression>();
		expressionWasModified = false;
		syntaxStatus = NO_SYNTAX_ERRORS;
		description = "_internal_";
		errorMessage = "";
		computingTime = 0;
		recursionCallPending = false;
		recursionCallsCounter = 0;
		internalClone = false;
		parserKeyWordsOnly = false;
		this.UDFExpression = UDFExpression;
		this.UDFVariadicParamsAtRunTime = UDFVariadicParamsAtRunTime;
		this.disableRounding = disableUlpRounding;
		setSilentMode();
		disableRecursiveMode();
	}

	/**
	 * Private constructor - expression cloning.
	 *
	 * @param expression
	 *            the base expression
	 */
	private Expression(Expression expression) {
		expressionString = new String(expression.expressionString);
		description = new String(expression.description);
		keyWordsList = expression.keyWordsList;
		relatedExpressionsList = expression.relatedExpressionsList;
		computingTime = 0;
		expressionWasModified = expression.expressionWasModified;
		recursiveMode = expression.recursiveMode;
		verboseMode = expression.verboseMode;
		syntaxStatus = expression.syntaxStatus;
		errorMessage = new String(expression.errorMessage);
		recursionCallPending = expression.recursionCallPending;
		recursionCallsCounter = expression.recursionCallsCounter;
		parserKeyWordsOnly = expression.parserKeyWordsOnly;
		disableRounding = expression.disableRounding;
		UDFExpression = expression.UDFExpression;
		UDFVariadicParamsAtRunTime = expression.UDFVariadicParamsAtRunTime;
		internalClone = true;
	}

	/**
	 * Sets (modifies expression) expression string.
	 *
	 * @param expressionString
	 *            the expression string
	 */
	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
		setExpressionModifiedFlag();
	}

	/**
	 * Returns expression string
	 *
	 * @return Expression string definition.
	 */
	public String getExpressionString() {
		return expressionString;
	}

	/**
	 * Sets expression description.
	 *
	 * @param description
	 *            the description string
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets expression description.
	 *
	 * @return String description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Enables verbose mode.
	 */
	public void setVerboseMode() {
		verboseMode = true;
	}

	/**
	 * Disables verbose mode (default silent mode).
	 */
	public void setSilentMode() {
		verboseMode = false;
	}

	/**
	 * Returns verbose mode status.
	 *
	 * @return true if verbose mode is on, otherwise returns false.
	 */
	public boolean getVerboseMode() {
		return verboseMode;
	}

	/**
	 * Disables recursive mode
	 */
	void disableRecursiveMode() {
		recursiveMode = false;
	}

	/**
	 * Gets recursive mode status
	 *
	 * @return true if recursive mode is enabled, otherwise returns false.
	 */
	public boolean getRecursiveMode() {
		return recursiveMode;
	}

	/**
	 * Gets computing time.
	 *
	 * @return computing time in seconds.
	 */
	public double getComputingTime() {
		return computingTime;
	}

	/*
	 * =================================================
	 *
	 * Common methods (supporting calculations)
	 *
	 * =================================================
	 */
	/**
	 * Sets given token to the number type / value. Method should be called only by
	 * the SetDecreaseRemove like methods
	 *
	 * @param pos
	 *            the position on which token should be updated to the given number
	 * @param number
	 *            the number
	 * @param ulpRound
	 *            If true, then if {@link mXparser#ulpRounding} = true intelligent
	 *            ULP rounding is applied.
	 */
	private void setToNumber(int pos, double number, boolean ulpRound) {
		Token token = tokensList.get(pos);
		if ((mXparser.ulpRounding) && (disableRounding == false)) {
			if (ulpRound) {
				if ((Double.isNaN(number)) || (Double.isInfinite(number)))
					token.tokenValue = number;
				else {
					int precision = MathFunctions.ulpDecimalDigitsBefore(number);
					if (precision >= 0)
						token.tokenValue = MathFunctions.round(number, precision);
					else
						token.tokenValue = number;
				}
			} else {
				token.tokenValue = number;
			}
		} else {
			token.tokenValue = number;
		}
		token.tokenTypeId = ParserSymbol.NUMBER_TYPE_ID;
		token.tokenId = ParserSymbol.NUMBER_ID;
		token.keyWord = ParserSymbol.NUMBER_STR;
	}

	private void setToNumber(int pos, double number) {
		setToNumber(pos, number, false);
	}

	/**
	 * SetDecreaseRemove for operators
	 *
	 * For detailed specification refer to the f1SetDecreaseRemove()
	 *
	 * @param pos
	 *            the position on which token should be updated to the given number
	 * @param result
	 *            the number
	 * @param ulpRound
	 *            If true, then if {@link mXparser#ulpRounding} = true intelligent
	 *            ULP rounding is applied.
	 */
	private void opSetDecreaseRemove(int pos, double result, boolean ulpRound) {
		setToNumber(pos, result, ulpRound);
		tokensList.remove(pos + 1);
		tokensList.remove(pos - 1);
	}

	private void opSetDecreaseRemove(int pos, double result) {
		opSetDecreaseRemove(pos, result, false);
	}

	/**
	 * If set remove method for the if function.
	 *
	 * @param pos
	 *            the position
	 * @param ifCondition
	 *            the result of if condition
	 * @param ulpRound
	 *            If true, then if {@link mXparser#ulpRounding} = true intelligent
	 *            ULP rounding is applied.
	 */
	private void ifSetRemove(int pos, double ifCondition, boolean ulpRound) {
		/*
		 * left parethesis position
		 */
		int lPos = pos + 1;
		int ifLevel = tokensList.get(lPos).tokenLevel;
		/*
		 * Evaluate 1 comma position on the same level
		 */
		int c1Pos = lPos + 1;
		while (!((tokensList.get(c1Pos).tokenTypeId == ParserSymbol.TYPE_ID)
				&& (tokensList.get(c1Pos).tokenId == ParserSymbol.COMMA_ID)
				&& (tokensList.get(c1Pos).tokenLevel == ifLevel)))
			c1Pos++;
		/*
		 * Evaluate 2 comma position on the same level
		 */
		int c2Pos = c1Pos + 1;
		while (!((tokensList.get(c2Pos).tokenTypeId == ParserSymbol.TYPE_ID)
				&& (tokensList.get(c2Pos).tokenId == ParserSymbol.COMMA_ID)
				&& (tokensList.get(c2Pos).tokenLevel == ifLevel)))
			c2Pos++;
		/*
		 * Evaluate right parenthesis position
		 */
		int rPos = c2Pos + 1;
		while (!((tokensList.get(rPos).tokenTypeId == ParserSymbol.TYPE_ID)
				&& (tokensList.get(rPos).tokenId == ParserSymbol.RIGHT_PARENTHESES_ID)
				&& (tokensList.get(rPos).tokenLevel == ifLevel)))
			rPos++;
		if (!Double.isNaN(ifCondition)) {
			if (ifCondition != 0) {
				setToNumber(c2Pos + 1, Double.NaN);
				tokensList.get(c2Pos + 1).tokenLevel = ifLevel;
				removeTokens(c2Pos + 2, rPos - 1);
			} else {
				setToNumber(c1Pos + 1, Double.NaN);
				tokensList.get(c1Pos + 1).tokenLevel = ifLevel;
				removeTokens(c1Pos + 2, c2Pos - 1);
			}
		} else {
			setToNumber(c1Pos + 1, Double.NaN);
			setToNumber(c2Pos + 1, Double.NaN);
			tokensList.get(c1Pos + 1).tokenLevel = ifLevel;
			tokensList.get(c2Pos + 1).tokenLevel = ifLevel;
			removeTokens(c2Pos + 2, rPos - 1);
			removeTokens(c1Pos + 2, c2Pos - 1);
		}
		setToNumber(lPos + 1, ifCondition, ulpRound);
		tokensList.get(lPos + 1).tokenLevel = ifLevel;
		removeTokens(lPos + 2, c1Pos - 1);
	}

	private void removeTokens(int from, int to) {
		if (from < to) {
			for (int p = to; p >= from; p--)
				tokensList.remove(p);
		} else if (from == to)
			tokensList.remove(from);
	}

	private void ifSetRemove(int pos, double ifCondition) {
		ifSetRemove(pos, ifCondition, false);
	}

	/**
	 * Creates string tokens list from the subexpression.
	 *
	 * @param startPos
	 *            start position (index)
	 * @param endPos
	 *            end position (index)
	 *
	 * @return tokens list representing requested subexpression.
	 */
	private List<Token> createInitialTokens(int startPos, int endPos, List<Token> tokensList) {
		List<Token> tokens = new ArrayList<Token>();
		Token t;
		for (int p = startPos; p <= endPos; p++) {
			t = tokensList.get(p).clone();
			tokens.add(t);
		}
		return tokens;
	}

	/**
	 * Returns list of the functions parameters.
	 *
	 * @param pos
	 *            the function position
	 * @param tokensList
	 *            the tokens list
	 *
	 * @return the list of function parameters
	 *
	 * @see FunctionParameter
	 */
	private List<FunctionParameter> getFunctionParameters(int pos, List<Token> tokensList) {
		List<FunctionParameter> functionParameters = new ArrayList<FunctionParameter>();
		int cPos = pos + 2;
		int tokenLevel = tokensList.get(pos + 1).tokenLevel;
		int pPos = cPos;
		boolean comma;
		boolean paren;
		boolean end = false;
		List<Token> paramTkones = new ArrayList<Token>();
		String paramStr = "";
		do {
			Token t = tokensList.get(cPos);
			comma = false;
			paren = false;
			if (t.tokenLevel == tokenLevel)
				if (t.tokenTypeId == ParserSymbol.TYPE_ID) {
					if (t.tokenId == ParserSymbol.RIGHT_PARENTHESES_ID)
						paren = true;
					else if (t.tokenId == ParserSymbol.COMMA_ID)
						comma = true;
				}
			if ((paren == true) || (comma == true)) {
				if (cPos > pos + 2) {
					functionParameters.add(new FunctionParameter(paramTkones, paramStr, pPos, cPos - 1));
					paramTkones = new ArrayList<Token>();
					paramStr = "";
					pPos = cPos + 1;
				}
			} else {
				paramTkones.add(t);
				paramStr = paramStr + t.tokenStr;
			}
			if (paren)
				end = true;
			else
				cPos++;
		} while (!end);
		return functionParameters;
	}

	/*
	 * =================================================
	 *
	 * Math implementations
	 *
	 * =================================================
	 */

	/**
	 * Constants handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void UNIT(int pos) {
		double unitValue = Double.NaN;
		switch (tokensList.get(pos).tokenId) {
		/* Ratio, Fraction */
		case Unit.PERC_ID:
			unitValue = Units.PERC;
			break;
		case Unit.PROMIL_ID:
			unitValue = Units.PROMIL;
			break;
		/* Metric prefixes */
		case Unit.YOTTA_ID:
			unitValue = Units.YOTTA;
			break;
		case Unit.ZETTA_ID:
			unitValue = Units.ZETTA;
			break;
		case Unit.EXA_ID:
			unitValue = Units.EXA;
			break;
		case Unit.PETA_ID:
			unitValue = Units.PETA;
			break;
		case Unit.TERA_ID:
			unitValue = Units.TERA;
			break;
		case Unit.GIGA_ID:
			unitValue = Units.GIGA;
			break;
		case Unit.MEGA_ID:
			unitValue = Units.MEGA;
			break;
		case Unit.KILO_ID:
			unitValue = Units.KILO;
			break;
		case Unit.HECTO_ID:
			unitValue = Units.HECTO;
			break;
		case Unit.DECA_ID:
			unitValue = Units.DECA;
			break;
		case Unit.DECI_ID:
			unitValue = Units.DECI;
			break;
		case Unit.CENTI_ID:
			unitValue = Units.CENTI;
			break;
		case Unit.MILLI_ID:
			unitValue = Units.MILLI;
			break;
		case Unit.MICRO_ID:
			unitValue = Units.MICRO;
			break;
		case Unit.NANO_ID:
			unitValue = Units.NANO;
			break;
		case Unit.PICO_ID:
			unitValue = Units.PICO;
			break;
		case Unit.FEMTO_ID:
			unitValue = Units.FEMTO;
			break;
		case Unit.ATTO_ID:
			unitValue = Units.ATTO;
			break;
		case Unit.ZEPTO_ID:
			unitValue = Units.ZEPTO;
			break;
		case Unit.YOCTO_ID:
			unitValue = Units.YOCTO;
			break;
		/* Units of length / distance */
		case Unit.METRE_ID:
			unitValue = Units.METRE;
			break;
		case Unit.KILOMETRE_ID:
			unitValue = Units.KILOMETRE;
			break;
		case Unit.CENTIMETRE_ID:
			unitValue = Units.CENTIMETRE;
			break;
		case Unit.MILLIMETRE_ID:
			unitValue = Units.MILLIMETRE;
			break;
		case Unit.INCH_ID:
			unitValue = Units.INCH;
			break;
		case Unit.YARD_ID:
			unitValue = Units.YARD;
			break;
		case Unit.FEET_ID:
			unitValue = Units.FEET;
			break;
		case Unit.MILE_ID:
			unitValue = Units.MILE;
			break;
		case Unit.NAUTICAL_MILE_ID:
			unitValue = Units.NAUTICAL_MILE;
			break;
		/* Units of area */
		case Unit.METRE2_ID:
			unitValue = Units.METRE2;
			break;
		case Unit.CENTIMETRE2_ID:
			unitValue = Units.CENTIMETRE2;
			break;
		case Unit.MILLIMETRE2_ID:
			unitValue = Units.MILLIMETRE2;
			break;
		case Unit.ARE_ID:
			unitValue = Units.ARE;
			break;
		case Unit.HECTARE_ID:
			unitValue = Units.HECTARE;
			break;
		case Unit.ACRE_ID:
			unitValue = Units.ACRE;
			break;
		case Unit.KILOMETRE2_ID:
			unitValue = Units.KILOMETRE2;
			break;
		/* Units of volume */
		case Unit.MILLIMETRE3_ID:
			unitValue = Units.MILLIMETRE3;
			break;
		case Unit.CENTIMETRE3_ID:
			unitValue = Units.CENTIMETRE3;
			break;
		case Unit.METRE3_ID:
			unitValue = Units.METRE3;
			break;
		case Unit.KILOMETRE3_ID:
			unitValue = Units.KILOMETRE3;
			break;
		case Unit.MILLILITRE_ID:
			unitValue = Units.MILLILITRE;
			break;
		case Unit.LITRE_ID:
			unitValue = Units.LITRE;
			break;
		case Unit.GALLON_ID:
			unitValue = Units.GALLON;
			break;
		case Unit.PINT_ID:
			unitValue = Units.PINT;
			break;
		/* Units of time */
		case Unit.SECOND_ID:
			unitValue = Units.SECOND;
			break;
		case Unit.MILLISECOND_ID:
			unitValue = Units.MILLISECOND;
			break;
		case Unit.MINUTE_ID:
			unitValue = Units.MINUTE;
			break;
		case Unit.HOUR_ID:
			unitValue = Units.HOUR;
			break;
		case Unit.DAY_ID:
			unitValue = Units.DAY;
			break;
		case Unit.WEEK_ID:
			unitValue = Units.WEEK;
			break;
		case Unit.JULIAN_YEAR_ID:
			unitValue = Units.JULIAN_YEAR;
			break;
		/* Units of mass */
		case Unit.KILOGRAM_ID:
			unitValue = Units.KILOGRAM;
			break;
		case Unit.GRAM_ID:
			unitValue = Units.GRAM;
			break;
		case Unit.MILLIGRAM_ID:
			unitValue = Units.MILLIGRAM;
			break;
		case Unit.DECAGRAM_ID:
			unitValue = Units.DECAGRAM;
			break;
		case Unit.TONNE_ID:
			unitValue = Units.TONNE;
			break;
		case Unit.OUNCE_ID:
			unitValue = Units.OUNCE;
			break;
		case Unit.POUND_ID:
			unitValue = Units.POUND;
			break;
		/* Units of information */
		case Unit.BIT_ID:
			unitValue = Units.BIT;
			break;
		case Unit.KILOBIT_ID:
			unitValue = Units.KILOBIT;
			break;
		case Unit.MEGABIT_ID:
			unitValue = Units.MEGABIT;
			break;
		case Unit.GIGABIT_ID:
			unitValue = Units.GIGABIT;
			break;
		case Unit.TERABIT_ID:
			unitValue = Units.TERABIT;
			break;
		case Unit.PETABIT_ID:
			unitValue = Units.PETABIT;
			break;
		case Unit.EXABIT_ID:
			unitValue = Units.EXABIT;
			break;
		case Unit.ZETTABIT_ID:
			unitValue = Units.ZETTABIT;
			break;
		case Unit.YOTTABIT_ID:
			unitValue = Units.YOTTABIT;
			break;
		case Unit.BYTE_ID:
			unitValue = Units.BYTE;
			break;
		case Unit.KILOBYTE_ID:
			unitValue = Units.KILOBYTE;
			break;
		case Unit.MEGABYTE_ID:
			unitValue = Units.MEGABYTE;
			break;
		case Unit.GIGABYTE_ID:
			unitValue = Units.GIGABYTE;
			break;
		case Unit.TERABYTE_ID:
			unitValue = Units.TERABYTE;
			break;
		case Unit.PETABYTE_ID:
			unitValue = Units.PETABYTE;
			break;
		case Unit.EXABYTE_ID:
			unitValue = Units.EXABYTE;
			break;
		case Unit.ZETTABYTE_ID:
			unitValue = Units.ZETTABYTE;
			break;
		case Unit.YOTTABYTE_ID:
			unitValue = Units.YOTTABYTE;
			break;
		/* Units of energy */
		case Unit.JOULE_ID:
			unitValue = Units.JOULE;
			break;
		case Unit.ELECTRONO_VOLT_ID:
			unitValue = Units.ELECTRONO_VOLT;
			break;
		case Unit.KILO_ELECTRONO_VOLT_ID:
			unitValue = Units.KILO_ELECTRONO_VOLT;
			break;
		case Unit.MEGA_ELECTRONO_VOLT_ID:
			unitValue = Units.MEGA_ELECTRONO_VOLT;
			break;
		case Unit.GIGA_ELECTRONO_VOLT_ID:
			unitValue = Units.GIGA_ELECTRONO_VOLT;
			break;
		case Unit.TERA_ELECTRONO_VOLT_ID:
			unitValue = Units.TERA_ELECTRONO_VOLT;
			break;
		/* Units of speed */
		case Unit.METRE_PER_SECOND_ID:
			unitValue = Units.METRE_PER_SECOND;
			break;
		case Unit.KILOMETRE_PER_HOUR_ID:
			unitValue = Units.KILOMETRE_PER_HOUR;
			break;
		case Unit.MILE_PER_HOUR_ID:
			unitValue = Units.MILE_PER_HOUR;
			break;
		case Unit.KNOT_ID:
			unitValue = Units.KNOT;
			break;
		/* Units of acceleration */
		case Unit.METRE_PER_SECOND2_ID:
			unitValue = Units.METRE_PER_SECOND2;
			break;
		case Unit.KILOMETRE_PER_HOUR2_ID:
			unitValue = Units.KILOMETRE_PER_HOUR2;
			break;
		case Unit.MILE_PER_HOUR2_ID:
			unitValue = Units.MILE_PER_HOUR2;
			break;
		/* Units of angle */
		case Unit.RADIAN_ARC_ID:
			unitValue = Units.RADIAN_ARC;
			break;
		case Unit.DEGREE_ARC_ID:
			unitValue = Units.DEGREE_ARC;
			break;
		case Unit.MINUTE_ARC_ID:
			unitValue = Units.MINUTE_ARC;
			break;
		case Unit.SECOND_ARC_ID:
			unitValue = Units.SECOND_ARC;
			break;
		}
		setToNumber(pos, unitValue);
	}

	/**
	 * Random Variables handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void RANDOM_VARIABLE(int pos) {
		double rndVar = Double.NaN;
		switch (tokensList.get(pos).tokenId) {
		case RandomVariable.UNIFORM_ID:
			rndVar = ProbabilityDistributions.rndUniformContinuous(ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT_ID:
			rndVar = ProbabilityDistributions.rndInteger(ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT1_ID:
			rndVar = ProbabilityDistributions.rndInteger(-10, 10, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT2_ID:
			rndVar = ProbabilityDistributions.rndInteger(-100, 100, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT3_ID:
			rndVar = ProbabilityDistributions.rndInteger(-1000, 1000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT4_ID:
			rndVar = ProbabilityDistributions.rndInteger(-10000, 10000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT5_ID:
			rndVar = ProbabilityDistributions.rndInteger(-100000, 100000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT6_ID:
			rndVar = ProbabilityDistributions.rndInteger(-1000000, 1000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT7_ID:
			rndVar = ProbabilityDistributions.rndInteger(-10000000, 10000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT8_ID:
			rndVar = ProbabilityDistributions.rndInteger(-100000000, 100000000,
					ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.INT9_ID:
			rndVar = ProbabilityDistributions.rndInteger(-1000000000, 1000000000,
					ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 2147483646, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_1_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 10, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_2_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 100, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_3_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 1000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_4_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 10000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_5_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 100000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_6_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 1000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_7_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 10000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_8_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 100000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT0_9_ID:
			rndVar = ProbabilityDistributions.rndInteger(0, 1000000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 2147483646, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_1_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 10, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_2_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 100, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_3_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 1000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_4_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 10000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_5_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 100000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_6_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 1000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_7_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 10000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_8_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 100000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NAT1_9_ID:
			rndVar = ProbabilityDistributions.rndInteger(1, 1000000000, ProbabilityDistributions.randomGenerator);
			break;
		case RandomVariable.NOR_ID:
			rndVar = ProbabilityDistributions.rndNormal(0.0, 1.0, ProbabilityDistributions.randomGenerator);
			break;
		}
		setToNumber(pos, rndVar);
	}

	/**
	 * Gets token value
	 * 
	 * @param tokenIndex
	 *            the token index
	 *
	 * @return the token value
	 */
	private double getTokenValue(int tokenIndex) {
		return tokensList.get(tokenIndex).tokenValue;
	}

	/**
	 * Tetration handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void TETRATION(int pos) {
		double a = getTokenValue(pos - 1);
		double n = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, MathFunctions.tetration(a, n), true);
	}

	/**
	 * Power handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void POWER(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, MathFunctions.power(a, b), true);
	}

	/**
	 * Modulo handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void MODULO(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, MathFunctions.mod(a, b));
	}

	/**
	 * Division handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void DIVIDE(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		if (disableRounding) {
			double result = Double.NaN;
			if (b != 0)
				result = a / b;
			opSetDecreaseRemove(pos, result, true);
		} else
			opSetDecreaseRemove(pos, MathFunctions.div(a, b), true);
	}

	/**
	 * Multiplication handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void MULTIPLY(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		if (disableRounding)
			opSetDecreaseRemove(pos, a * b, true);
		else
			opSetDecreaseRemove(pos, MathFunctions.multiply(a, b), true);
	}

	/**
	 * Addition handling.
	 *
	 * @param pos
	 *            the token position
	 */
	private void PLUS(int pos) {
		Token b = tokensList.get(pos + 1);
		if (pos > 0) {
			Token a = tokensList.get(pos - 1);
			if ((a.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) && (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID))
				if (disableRounding)
					opSetDecreaseRemove(pos, a.tokenValue + b.tokenValue, true);
				else
					opSetDecreaseRemove(pos, MathFunctions.plus(a.tokenValue, b.tokenValue), true);
			else if (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) {
				setToNumber(pos, b.tokenValue);
				tokensList.remove(pos + 1);
			}
		} else if (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) {
			setToNumber(pos, b.tokenValue);
			tokensList.remove(pos + 1);
		}
	}

	/**
	 * Subtraction handling
	 *
	 * @param pos
	 *            the token position
	 */
	private void MINUS(int pos) {
		Token b = tokensList.get(pos + 1);
		if (pos > 0) {
			Token a = tokensList.get(pos - 1);
			if ((a.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) && (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID))
				if (disableRounding)
					opSetDecreaseRemove(pos, a.tokenValue - b.tokenValue, true);
				else
					opSetDecreaseRemove(pos, MathFunctions.minus(a.tokenValue, b.tokenValue), true);
			else if (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) {
				setToNumber(pos, -b.tokenValue);
				tokensList.remove(pos + 1);
			}
		} else if (b.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) {
			setToNumber(pos, -b.tokenValue);
			tokensList.remove(pos + 1);
		}
	}

	/**
	 * Logical AND
	 *
	 * @param pos
	 *            the token position
	 */
	private void AND(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.and(a, b));
	}

	/**
	 * Logical OR
	 *
	 * @param pos
	 *            the token position
	 */
	private void OR(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.or(a, b));
	}

	/**
	 * Logical NAND
	 *
	 * @param pos
	 *            the token position
	 */
	private void NAND(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.nand(a, b));
	}

	/**
	 * Logical NOR
	 *
	 * @param pos
	 *            the token position
	 */
	private void NOR(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.nor(a, b));
	}

	/**
	 * Logical XOR
	 *
	 *
	 * @param pos
	 *            the token position
	 */
	private void XOR(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.xor(a, b));
	}

	/**
	 * Logical IMP
	 *
	 *
	 * @param pos
	 *            the token position
	 */
	private void IMP(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.imp(a, b));
	}

	/**
	 * Logical CIMP
	 *
	 * @param pos
	 *            the token position
	 */
	private void CIMP(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.cimp(a, b));
	}

	/**
	 * Logical NIMP
	 *
	 * @param pos
	 *            the token position
	 */
	private void NIMP(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.nimp(a, b));
	}

	/**
	 * Logical CNIMP
	 *
	 * @param pos
	 *            the token position
	 */
	private void CNIMP(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.cnimp(a, b));
	}

	/**
	 * Logical EQV
	 *
	 * @param pos
	 *            the token position
	 */
	private void EQV(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BooleanAlgebra.eqv(a, b));
	}

	/**
	 * Logical negation
	 *
	 * @param pos
	 *            the token position
	 */
	private void NEG(int pos) {
		double a = getTokenValue(pos + 1);
		setToNumber(pos, BooleanAlgebra.not(a));
		tokensList.remove(pos + 1);
	}

	/**
	 * Equality relation.
	 *
	 * @param pos
	 *            the token position
	 */
	private void EQ(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.eq(a, b));
	}

	/**
	 * Not equals.
	 *
	 * @param pos
	 *            the token position
	 */
	private void NEQ(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.neq(a, b));
	}

	/**
	 * Lower than.
	 *
	 * @param pos
	 *            the token position
	 */
	private void LT(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.lt(a, b));
	}

	/**
	 * Greater than.
	 *
	 * @param pos
	 *            the token position
	 */
	private void GT(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.gt(a, b));
	}

	/**
	 * Lower or equal.
	 *
	 * @param pos
	 *            the token position
	 */
	private void LEQ(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.leq(a, b));
	}

	/**
	 * Greater or equal
	 *
	 * @param pos
	 *            the token position
	 */
	private void GEQ(int pos) {
		double a = getTokenValue(pos - 1);
		double b = getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, BinaryRelations.geq(a, b));
	}

	/**
	 * Bitwise COMPL
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_COMPL(int pos) {
		long a = (long) getTokenValue(pos + 1);
		setToNumber(pos, ~a);
		tokensList.remove(pos + 1);
	}

	/**
	 * Bitwise AND
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_AND(int pos) {
		long a = (long) getTokenValue(pos - 1);
		long b = (long) getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, a & b);
	}

	/**
	 * Bitwise OR
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_OR(int pos) {
		long a = (long) getTokenValue(pos - 1);
		long b = (long) getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, a | b);
	}

	/**
	 * Bitwise XOR
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_XOR(int pos) {
		long a = (long) getTokenValue(pos - 1);
		long b = (long) getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, a ^ b);
	}

	/**
	 * Bitwise LEFT SHIFT
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_LEFT_SHIFT(int pos) {
		long a = (long) getTokenValue(pos - 1);
		int b = (int) getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, a << b);
	}

	/**
	 * Bitwise RIGHT SHIFT
	 *
	 * @param pos
	 *            the token position
	 */
	private void BITWISE_RIGHT_SHIFT(int pos) {
		long a = (long) getTokenValue(pos - 1);
		int b = (int) getTokenValue(pos + 1);
		opSetDecreaseRemove(pos, a >> b);
	}

	/**
	 * Factorilal function Sets tokens to number token
	 *
	 * @param pos
	 *            the token position
	 */
	private void FACT(int pos) {
		double a = getTokenValue(pos - 1);
		setToNumber(pos, MathFunctions.factorial(a));
		tokensList.remove(pos - 1);
	}

	/**
	 * Percentage Sets tokens to number token
	 *
	 * @param pos
	 *            the token position
	 */
	private void PERC(int pos) {
		double a = getTokenValue(pos - 1);
		setToNumber(pos, a * Units.PERC);
		tokensList.remove(pos - 1);
	}

	/**
	 * IF function
	 *
	 * @param pos
	 *            the token position
	 */
	private void IF_CONDITION(int pos) {
		/*
		 * Get condition string 1st parameter The goal is to avoid calculation of not
		 * needed part of IF function Example: If(1=1, 2, sin(3) ) - here sin(3) does
		 * not require to be calculated.
		 */
		List<FunctionParameter> ifParams = getFunctionParameters(pos, tokensList);
		FunctionParameter ifParam = ifParams.get(0);
		Expression ifExp = new Expression(ifParam.paramStr, ifParam.tokens, KEEP_ROUNDING_SETTINGS, UDFExpression,
				UDFVariadicParamsAtRunTime);
		if (verboseMode == true)
			ifExp.setVerboseMode();
		ifSetRemove(pos, ifExp.calculate());
	}

	/**
	 * IFF function
	 *
	 * @param pos
	 *            the token position
	 */
	private void IFF(int pos) {
		/*
		 * Get condition string 1st parameter
		 */
		List<FunctionParameter> iffParams = getFunctionParameters(pos, tokensList);
		FunctionParameter iffParam = iffParams.get(0);
		int parametersNumber = iffParams.size();
		int trueParamNumber;
		int paramNumber;
		paramNumber = 1;
		Expression iffExp;
		double iffValue = 0;
		boolean iffCon = true;
		do {
			iffExp = new Expression(iffParam.paramStr, iffParam.tokens, KEEP_ROUNDING_SETTINGS, UDFExpression,
					UDFVariadicParamsAtRunTime);
			if (verboseMode == true)
				iffExp.setVerboseMode();
			iffCon = true;
			iffValue = iffExp.calculate();
			if ((iffValue == 0) || (Double.isNaN(iffValue))) {
				paramNumber += 2;
				iffCon = false;
				if (paramNumber < parametersNumber)
					iffParam = iffParams.get(paramNumber - 1);
			}
		} while ((!iffCon) && (paramNumber < parametersNumber));
		int from;
		int to;
		int p;
		if (iffCon) {
			trueParamNumber = paramNumber + 1;
			from = pos + 1;
			to = iffParams.get(parametersNumber - 1).toIndex + 1;
			tokensList.get(from).tokenLevel--;
			tokensList.get(to).tokenLevel--;
			if (trueParamNumber < parametersNumber) {
				to = iffParams.get(parametersNumber - 1).toIndex;
				from = iffParams.get(trueParamNumber).fromIndex - 1;
				for (p = to; p >= from; p--)
					tokensList.remove(p);
			}
			from = iffParams.get(trueParamNumber - 1).fromIndex;
			to = iffParams.get(trueParamNumber - 1).toIndex;
			for (p = from; p <= to; p++)
				tokensList.get(p).tokenLevel--;
			to = from - 1;
			from = pos;
			for (p = to; p >= from; p--)
				if (p != pos + 1)
					tokensList.remove(p);
		} else {
			to = iffParams.get(parametersNumber - 1).toIndex + 1;
			from = pos + 1;
			for (p = to; p >= from; p--)
				tokensList.remove(p);
			setToNumber(pos, Double.NaN);
			tokensList.get(pos).tokenLevel--;
		}
	}

	/**
	 * Parser symbols Removes comma
	 *
	 * @param pos
	 *            token index (position)
	 */
	private void COMMA(int pos) {
		tokensList.remove(pos);
	}

	/**
	 * Parser symbols Removes parenthesis
	 *
	 * @param lPos
	 *            left token index (position)
	 * @param rPos
	 *            roght token index (position)
	 */
	private void PARENTHESES(int lPos, int rPos) {
		for (int p = lPos; p <= rPos; p++)
			tokensList.get(p).tokenLevel--;
		tokensList.remove(rPos);
		tokensList.remove(lPos);
	}

	/**
	 * Checks syntax of the expression string.
	 *
	 * @return true if syntax is ok
	 */
	public boolean checkSyntax() {
		boolean syntax = checkSyntax("[" + expressionString + "] ", false);
		return syntax;
	}

	/**
	 * Checking the syntax (recursively).
	 *
	 * @param level
	 *            string representing the recurssion level.
	 * @return true if syntax was correct, otherwise returns false.
	 */
	private boolean checkSyntax(String level, boolean functionWithBodyExt) {
		if ((expressionWasModified == false) && (syntaxStatus == NO_SYNTAX_ERRORS)
				&& (optionsChangesetNumber == mXparser.optionsChangesetNumber)) {
			errorMessage = level + "already checked - no errors!\n";
			recursionCallPending = false;
			return NO_SYNTAX_ERRORS;
		}
		optionsChangesetNumber = mXparser.optionsChangesetNumber;
		if (functionWithBodyExt) {
			syntaxStatus = NO_SYNTAX_ERRORS;
			recursionCallPending = false;
			expressionWasModified = false;
			errorMessage = errorMessage + level + "function with extended body - assuming no errors.\n";
			return NO_SYNTAX_ERRORS;
		}
		recursionCallPending = true;
		errorMessage = level + "checking ...\n";
		boolean syntax = NO_SYNTAX_ERRORS;
		if (expressionString.length() == 0) {
			syntax = SYNTAX_ERROR_OR_STATUS_UNKNOWN;
			errorMessage = errorMessage + level + "Empty expression string\n";
			syntaxStatus = syntax;
			recursionCallPending = false;
			return syntax;
		}
		SyntaxChecker syn = new SyntaxChecker(new ByteArrayInputStream(expressionString.getBytes()));
		try {
			syn.checkSyntax();
			/*
			 * IF there are no lex error
			 */
			tokenizeExpressionString();
			/*
			 * Duplicated tokens?
			 */
			String kw1;
			String kw2;
			java.util.Collections.sort(keyWordsList, new KwStrComparator());
			for (int kwId = 1; kwId < keyWordsList.size(); kwId++) {
				kw1 = keyWordsList.get(kwId - 1).wordString;
				kw2 = keyWordsList.get(kwId).wordString;
				if (kw1.equals(kw2)) {
					syntax = SYNTAX_ERROR_OR_STATUS_UNKNOWN;
					errorMessage = errorMessage + level + "(" + kw1 + ") Duplicated <KEYWORD>.\n";
				}
			}
			int tokensNumber = initialTokens.size();
			Stack<SyntaxStackElement> syntaxStack = new Stack<SyntaxStackElement>();
			for (int tokenIndex = 0; tokenIndex < tokensNumber; tokenIndex++) {
				Token t = initialTokens.get(tokenIndex);
				String tokenStr = "(" + t.tokenStr + ", " + tokenIndex + ") ";
				/*
				 * Check syntax for "NOT RECOGNIZED" token
				 */
				if (t.tokenTypeId == Token.NOT_MATCHED) {
					boolean calculusToken = false;
					for (SyntaxStackElement e : syntaxStack)
						if (e.tokenStr.equals(t.tokenStr))
							calculusToken = true;
					if (!calculusToken) {
						syntax = SYNTAX_ERROR_OR_STATUS_UNKNOWN;
						errorMessage = errorMessage + level + tokenStr + "invalid <TOKEN>.\n";
					}
				}
				if ((t.tokenTypeId == ParserSymbol.TYPE_ID) && (t.tokenId == ParserSymbol.RIGHT_PARENTHESES_ID)) {
					if (syntaxStack.size() > 0)
						if (t.tokenLevel == syntaxStack.lastElement().tokenLevel)
							syntaxStack.pop();
				}
			}
		} catch (Exception e) {
			syntax = SYNTAX_ERROR_OR_STATUS_UNKNOWN;
			errorMessage = errorMessage + level + "lexical error \n\n" + e.getMessage() + "\n";
		}
		if (syntax == NO_SYNTAX_ERRORS) {
			errorMessage = errorMessage + level + "no errors.\n";
			expressionWasModified = false;
		} else {
			errorMessage = errorMessage + level + "errors were found.\n";
			expressionWasModified = true;
		}
		syntaxStatus = syntax;
		recursionCallPending = false;
		return syntax;
	}

	/**
	 * Calculates the expression value
	 *
	 * @return The expression value if syntax was ok, otherwise returns Double.NaN.
	 */
	public double calculate() {
		computingTime = 0;
		long startTime = System.currentTimeMillis();
		/*
		 * check expression syntax and evaluate expression string tokens
		 *
		 */
		if ((expressionWasModified == true) || (syntaxStatus != NO_SYNTAX_ERRORS))
			syntaxStatus = checkSyntax();
		if (syntaxStatus == SYNTAX_ERROR_OR_STATUS_UNKNOWN) {
			errorMessage = errorMessage + "Problem with expression syntax\n";
			if (verboseMode == true)
				printSystemInfo("syntaxStatus == SYNTAX_ERROR_OR_STATUS_UNKNOWN, returning Double.NaN\n", NO_EXP_STR);
			/*
			 * Recursive counter to avoid infinite loops in expressions created in they way
			 * shown in below examples
			 *
			 * Argument x = new Argument("x = 2*y"); Argument y = new Argument("y = 2*x");
			 * x.addDefinitions(y); y.addDefinitions(x);
			 *
			 * Function f = new Function("f(x) = 2*g(x)"); Function g = new
			 * Function("g(x) = 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
			 *
			 */
			recursionCallsCounter = 0;
			return Double.NaN;
		}
		/*
		 * Building initial tokens only if this is first recursion call or we have
		 * expression clone, helps to solve problem with definitions similar to the
		 * below example
		 *
		 *
		 * Function f = new Function("f(x) = 2*g(x)"); Function g = new
		 * Function("g(x) = 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
		 */
		if ((recursionCallsCounter == 0) || (internalClone))
			copyInitialTokens();
		/*
		 * if nothing to calculate return Double.NaN
		 */
		if (tokensList.size() == 0) {
			errorMessage = errorMessage + "Empty expression\n";
			if (verboseMode == true)
				printSystemInfo("tokensList.size() == 0, returning Double.NaN\n", NO_EXP_STR);
			recursionCallsCounter = 0;
			return Double.NaN;
		}
		/*
		 * Incrementing recursive counter to avoid infinite loops in expressions created
		 * in they way shown in below examples
		 *
		 * Argument x = new Argument("x = 2*y"); Argument y = new Argument("y = 2*x");
		 * x.addDefinitions(y); y.addDefinitions(x);
		 *
		 * Function f = new Function("f(x) = 2*g(x)"); Function g = new
		 * Function("g(x) = 2*f(x)"); f.addDefinitions(g); g.addDefinitions(f);
		 *
		 */
		if (recursionCallsCounter >= mXparser.MAX_RECURSION_CALLS) {
			errorMessage = errorMessage + "recursionCallsCounter >= MAX_RECURSION_CALLS\n";
			if (verboseMode == true) {
				printSystemInfo("recursionCallsCounter >= mXparser.MAX_RECURSION_CALLS, returning Double.NaN\n",
						NO_EXP_STR);
				printSystemInfo("recursionCallsCounter = " + recursionCallsCounter + "\n", NO_EXP_STR);
				printSystemInfo("mXparser.MAX_RECURSION_CALLS = " + mXparser.MAX_RECURSION_CALLS + "\n", NO_EXP_STR);
			}
			recursionCallsCounter = 0;
			this.errorMessage = errorMessage + "\n" + "[" + description + "][" + expressionString + "] "
					+ "Maximum recursion calls reached.\n";
			return Double.NaN;
		}
		recursionCallsCounter++;
		/*
		 * position for particular tokens types
		 */
		int calculusPos;
		int ifPos;
		int iffPos;
		int depArgPos;
		int plusPos;
		int minusPos;
		int multiplyPos;
		int dividePos;
		int powerPos;
		int tetrationPos;
		int powerNum;
		int factPos;
		int modPos;
		int percPos;
		int negPos;
		int andGroupPos;
		int orGroupPos;
		int implGroupPos;
		int bolPos;
		int eqPos;
		int neqPos;
		int ltPos;
		int gtPos;
		int leqPos;
		int geqPos;
		int commaPos;
		int lParPos;
		int rParPos;
		int bitwisePos;
		int bitwiseComplPos;
		Token token;
		Token tokenL;
		Token tokenR;
		int tokensNumber;
		int maxPartLevel;
		int lPos;
		int rPos;
		int tokenIndex;
		int pos;
		int p;
		List<Integer> commas = null;
		int emptyLoopCounter = 0;

		/* While exist token which needs to bee evaluated */
		if (verboseMode == true)
			printSystemInfo("Starting calculation loop\n", WITH_EXP_STR);
		do {
			if (mXparser.isCurrentCalculationCancelled()) {
				errorMessage = errorMessage + "\n" + "Cancel request - finishing";
				return Double.NaN;
			}
			tokensNumber = tokensList.size();
			maxPartLevel = -1;
			lPos = -1;
			rPos = -1;
			/*
			 * initializing tokens types positions
			 */
			calculusPos = -1;
			ifPos = -1;
			iffPos = -1;
			depArgPos = -1;
			plusPos = -1;
			minusPos = -1;
			multiplyPos = -1;
			dividePos = -1;
			powerPos = -1;
			tetrationPos = -1;
			factPos = -1;
			modPos = -1;
			percPos = -1;
			powerNum = 0;
			negPos = -1;
			andGroupPos = -1;
			orGroupPos = -1;
			implGroupPos = -1;
			bolPos = -1;
			eqPos = -1;
			neqPos = -1;
			ltPos = -1;
			gtPos = -1;
			leqPos = -1;
			geqPos = -1;
			commaPos = -1;
			lParPos = -1;
			rParPos = -1;
			bitwisePos = -1;
			bitwiseComplPos = -1;
			/* calculus or if or iff operations ... */
			p = -1;
			do {
				p++;
				token = tokensList.get(p);
				if (token.tokenTypeId == CalculusOperator.TYPE_ID)
					calculusPos = p;
			} while ((p < tokensNumber - 1) && (calculusPos < 0) && (ifPos < 0) && (iffPos < 0));
			if ((calculusPos < 0) && (ifPos < 0) && (iffPos < 0)) {
				/* Find start index of the tokens with the highest level */
				for (tokenIndex = 0; tokenIndex < tokensNumber; tokenIndex++) {
					token = tokensList.get(tokenIndex);
					if (token.tokenLevel > maxPartLevel) {
						maxPartLevel = tokensList.get(tokenIndex).tokenLevel;
						lPos = tokenIndex;
					}
					if (token.tokenTypeId == Unit.TYPE_ID)
						UNIT(tokenIndex);
					else if (token.tokenTypeId == RandomVariable.TYPE_ID)
						RANDOM_VARIABLE(tokenIndex);
				}
				if (lPos < 0) {
					errorMessage = errorMessage + "\n" + "Internal error / strange token level - finishing";
					return Double.NaN;
				}
				/*
				 * If dependent argument was found then dependent arguments in the tokensList
				 * need to replaced one after another in separate loops as tokensList might
				 * change in some other call done in possible recursive call.
				 *
				 * Argument x = new Argument("x = 2*y"); Argument y = new Argument("y = 2*x");
				 * x.addDefinitions(y); y.addDefinitions(x);
				 */
				if (depArgPos >= 0) {
				} else {
					tokenIndex = lPos;
					/* Find end index of the tokens with the highest level */
					while ((tokenIndex < tokensNumber) && (maxPartLevel == tokensList.get(tokenIndex).tokenLevel))
						tokenIndex++;
					rPos = tokenIndex - 1;
					if (verboseMode == true) {
						printSystemInfo("Parsing (" + lPos + ", " + rPos + ") ", WITH_EXP_STR);
						showParsing(lPos, rPos);
					}
					/*
					 * if no calculus operations were found check for other tokens
					 */
					boolean leftIsNumber;
					boolean rigthIsNumber;
					for (pos = lPos; pos <= rPos; pos++) {
						leftIsNumber = false;
						rigthIsNumber = false;
						token = tokensList.get(pos);
						if (pos - 1 >= 0) {
							tokenL = tokensList.get(pos - 1);
							if (tokenL.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID)
								leftIsNumber = true;
						}
						if (pos + 1 < tokensNumber) {
							tokenR = tokensList.get(pos + 1);
							if (tokenR.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID)
								rigthIsNumber = true;
						}
						if (token.tokenTypeId == Operator.TYPE_ID) {
							if ((token.tokenId == Operator.POWER_ID) && (leftIsNumber && rigthIsNumber)) {
								powerPos = pos;
								powerNum++;
							} else if ((token.tokenId == Operator.TETRATION_ID) && (leftIsNumber && rigthIsNumber)) {
								tetrationPos = pos;
							} else if ((token.tokenId == Operator.FACT_ID) && (factPos < 0) && (leftIsNumber)) {
								factPos = pos;
							} else if ((token.tokenId == Operator.PERC_ID) && (percPos < 0) && (leftIsNumber)) {
								percPos = pos;
							} else if ((token.tokenId == Operator.MOD_ID) && (modPos < 0)
									&& (leftIsNumber && rigthIsNumber)) {
								modPos = pos;
							} else if ((token.tokenId == Operator.PLUS_ID) && (plusPos < 0) && (rigthIsNumber))
								plusPos = pos;
							else if ((token.tokenId == Operator.MINUS_ID) && (minusPos < 0) && (rigthIsNumber))
								minusPos = pos;
							else if ((token.tokenId == Operator.MULTIPLY_ID) && (multiplyPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								multiplyPos = pos;
							else if ((token.tokenId == Operator.DIVIDE_ID) && (dividePos < 0)
									&& (leftIsNumber && rigthIsNumber))
								dividePos = pos;
						} else if (token.tokenTypeId == BooleanOperator.TYPE_ID) {
							if ((token.tokenId == BooleanOperator.NEG_ID) && (negPos < 0) && (rigthIsNumber))
								negPos = pos;
							else if (leftIsNumber && rigthIsNumber) {
								if ((token.tokenId == BooleanOperator.AND_ID
										|| token.tokenId == BooleanOperator.NAND_ID) && (andGroupPos < 0))
									andGroupPos = pos;
								else if ((token.tokenId == BooleanOperator.OR_ID
										|| token.tokenId == BooleanOperator.NOR_ID
										|| token.tokenId == BooleanOperator.XOR_ID) && (orGroupPos < 0))
									orGroupPos = pos;
								else if ((token.tokenId == BooleanOperator.IMP_ID
										|| token.tokenId == BooleanOperator.CIMP_ID
										|| token.tokenId == BooleanOperator.NIMP_ID
										|| token.tokenId == BooleanOperator.CNIMP_ID
										|| token.tokenId == BooleanOperator.EQV_ID) && (implGroupPos < 0))
									implGroupPos = pos;
								else if (bolPos < 0)
									bolPos = pos;
							}
						} else if (token.tokenTypeId == BinaryRelation.TYPE_ID) {
							if ((token.tokenId == BinaryRelation.EQ_ID) && (eqPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								eqPos = pos;
							else if ((token.tokenId == BinaryRelation.NEQ_ID) && (neqPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								neqPos = pos;
							else if ((token.tokenId == BinaryRelation.LT_ID) && (ltPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								ltPos = pos;
							else if ((token.tokenId == BinaryRelation.GT_ID) && (gtPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								gtPos = pos;
							else if ((token.tokenId == BinaryRelation.LEQ_ID) && (leqPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								leqPos = pos;
							else if ((token.tokenId == BinaryRelation.GEQ_ID) && (geqPos < 0)
									&& (leftIsNumber && rigthIsNumber))
								geqPos = pos;
						} else if (token.tokenTypeId == BitwiseOperator.TYPE_ID) {
							if ((token.tokenId == BitwiseOperator.COMPL_ID) && (bitwiseComplPos < 0) && (rigthIsNumber))
								bitwiseComplPos = pos;
							else if ((bitwisePos < 0) && (leftIsNumber && rigthIsNumber))
								bitwisePos = pos;
						} else if (token.tokenTypeId == ParserSymbol.TYPE_ID) {
							if ((token.tokenId == ParserSymbol.COMMA_ID)) {
								if (commaPos < 0)
									commas = new ArrayList<Integer>();
								commas.add(pos);
								commaPos = pos;
							} else if ((token.tokenId == ParserSymbol.LEFT_PARENTHESES_ID) && (lParPos < 0))
								lParPos = pos;
							else if ((token.tokenId == ParserSymbol.RIGHT_PARENTHESES_ID) && (rParPos < 0))
								rParPos = pos;
						}
					}
					/*
					 * powering should be done using backwards sequence
					 */
					if (powerNum > 1) {
						powerPos = -1;
						p = rPos + 1;
						do {
							p--;
							token = tokensList.get(p);
							if ((token.tokenTypeId == Operator.TYPE_ID) && (token.tokenId == Operator.POWER_ID))
								powerPos = p;
						} while ((p > lPos) && (powerPos == -1));
					}
				}
			}
			/* calculus operations */
			if (ifPos >= 0) {
				IF_CONDITION(ifPos);
			} else if (iffPos >= 0) {
				IFF(iffPos);
			} else
			/* ... powering ... */
			if (tetrationPos >= 0) {
				TETRATION(tetrationPos);
			} else if (powerPos >= 0) {
				POWER(powerPos);
			} else if (factPos >= 0) {
				FACT(factPos);
			} else if (percPos >= 0) {
				PERC(percPos);
			} else if (modPos >= 0) {
				MODULO(modPos);
			} else if (negPos >= 0) {
				NEG(negPos);
			} else if (bitwiseComplPos >= 0) {
				BITWISE_COMPL(bitwiseComplPos);
			} else
			/* ... arithmetical operators ... */
			if ((multiplyPos >= 0) || (dividePos >= 0)) {
				if ((multiplyPos >= 0) && (dividePos >= 0))
					if (multiplyPos <= dividePos)
						MULTIPLY(multiplyPos);
					else
						DIVIDE(dividePos);
				else if (multiplyPos >= 0)
					MULTIPLY(multiplyPos);
				else
					DIVIDE(dividePos);
			} else if ((minusPos >= 0) || (plusPos >= 0)) {
				if ((minusPos >= 0) && (plusPos >= 0))
					if (minusPos <= plusPos)
						MINUS(minusPos);
					else
						PLUS(plusPos);
				else if (minusPos >= 0)
					MINUS(minusPos);
				else
					PLUS(plusPos);
			} else if (neqPos >= 0) {
				NEQ(neqPos);
			} else
			/* ... binary relations ... */
			if (eqPos >= 0) {
				EQ(eqPos);
			} else if (ltPos >= 0) {
				LT(ltPos);
			} else if (gtPos >= 0) {
				GT(gtPos);
			} else if (leqPos >= 0) {
				LEQ(leqPos);
			} else if (geqPos >= 0) {
				GEQ(geqPos);
			} else if (commaPos >= 0) {
				for (int i = commas.size() - 1; i >= 0; i--)
					COMMA(commas.get(i));
			} else
			/* ... logical operators ... */
			if (andGroupPos >= 0)
				bolCalc(andGroupPos);
			else if (orGroupPos >= 0)
				bolCalc(orGroupPos);
			else if (implGroupPos >= 0)
				bolCalc(implGroupPos);
			else if (bolPos >= 0)
				bolCalc(bolPos);
			else
			/* ... bitwise operators ... */
			if (bitwisePos >= 0)
				bitwiseCalc(bitwisePos);
			else if ((lParPos >= 0) && (rParPos > lParPos)) {
				PARENTHESES(lParPos, rParPos);
			} else if (tokensList.size() > 1) {
				this.errorMessage = errorMessage + "\n" + "[" + description + "][" + expressionString + "] "
						+ "Fatal error - not know what to do with tokens while calculate().\n";
			}
			if (verboseMode == true) {
				showParsing(0, tokensList.size() - 1);
				printSystemInfo(" done\n", NO_EXP_STR);
			}

			if (tokensList.size() == tokensNumber)
				emptyLoopCounter++;
			else
				emptyLoopCounter = 0;

			if (emptyLoopCounter > 10) {
				errorMessage = errorMessage + "\n"
						+ "Internal error, do not know what to do with the token, probably mXparser bug, please report - finishing";
				return Double.NaN;
			}
		} while (tokensList.size() > 1);
		if (verboseMode == true) {
			printSystemInfo("Calculated value: " + tokensList.get(0).tokenValue + "\n", WITH_EXP_STR);
			printSystemInfo("Exiting\n", WITH_EXP_STR);
			printSystemInfo("\n", NO_EXP_STR);
		}
		long endTime = System.currentTimeMillis();
		computingTime = (endTime - startTime) / 1000.0;
		recursionCallsCounter = 0;
		double result = tokensList.get(0).tokenValue;
		if (mXparser.almostIntRounding) {
			double resultint = Math.round(result);
			if (Math.abs(result - resultint) <= BinaryRelations.getEpsilon())
				result = resultint;
		}
		return result;
	}

	/**
	 * Calculates boolean operators
	 * 
	 * @param pos
	 */
	private void bolCalc(int pos) {
		switch (tokensList.get(pos).tokenId) {
		case BooleanOperator.AND_ID:
			AND(pos);
			break;
		case BooleanOperator.CIMP_ID:
			CIMP(pos);
			break;
		case BooleanOperator.CNIMP_ID:
			CNIMP(pos);
			break;
		case BooleanOperator.EQV_ID:
			EQV(pos);
			break;
		case BooleanOperator.IMP_ID:
			IMP(pos);
			break;
		case BooleanOperator.NAND_ID:
			NAND(pos);
			break;
		case BooleanOperator.NIMP_ID:
			NIMP(pos);
			break;
		case BooleanOperator.NOR_ID:
			NOR(pos);
			break;
		case BooleanOperator.OR_ID:
			OR(pos);
			break;
		case BooleanOperator.XOR_ID:
			XOR(pos);
			break;
		}
	}

	/**
	 * Calculates Bitwise operators
	 * 
	 * @param pos
	 */
	private void bitwiseCalc(int pos) {
		switch (tokensList.get(pos).tokenId) {
		case BitwiseOperator.AND_ID:
			BITWISE_AND(pos);
			break;
		case BitwiseOperator.OR_ID:
			BITWISE_OR(pos);
			break;
		case BitwiseOperator.XOR_ID:
			BITWISE_XOR(pos);
			break;
		case BitwiseOperator.LEFT_SHIFT_ID:
			BITWISE_LEFT_SHIFT(pos);
			break;
		case BitwiseOperator.RIGHT_SHIFT_ID:
			BITWISE_RIGHT_SHIFT(pos);
			break;
		}
	}

	/*
	 * =================================================
	 *
	 * Parser methods
	 *
	 * =================================================
	 */

	/**
	 * Creates parser key words list
	 */
	private void addParserKeyWords() {
		/*
		 * Operators key words
		 */
		addKeyWord(Operator.PLUS_STR, Operator.PLUS_DESC, Operator.PLUS_ID, Operator.PLUS_SYN, Operator.PLUS_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.MINUS_STR, Operator.MINUS_DESC, Operator.MINUS_ID, Operator.MINUS_SYN, Operator.MINUS_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.MULTIPLY_STR, Operator.MULTIPLY_DESC, Operator.MULTIPLY_ID, Operator.MULTIPLY_SYN,
				Operator.MULTIPLY_SINCE, Operator.TYPE_ID);
		addKeyWord(Operator.DIVIDE_STR, Operator.DIVIDE_DESC, Operator.DIVIDE_ID, Operator.DIVIDE_SYN,
				Operator.DIVIDE_SINCE, Operator.TYPE_ID);
		addKeyWord(Operator.POWER_STR, Operator.POWER_DESC, Operator.POWER_ID, Operator.POWER_SYN, Operator.POWER_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.FACT_STR, Operator.FACT_DESC, Operator.FACT_ID, Operator.FACT_SYN, Operator.FACT_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.MOD_STR, Operator.MOD_DESC, Operator.MOD_ID, Operator.MOD_SYN, Operator.MOD_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.PERC_STR, Operator.PERC_DESC, Operator.PERC_ID, Operator.PERC_SYN, Operator.PERC_SINCE,
				Operator.TYPE_ID);
		addKeyWord(Operator.TETRATION_STR, Operator.TETRATION_DESC, Operator.TETRATION_ID, Operator.TETRATION_SYN,
				Operator.TETRATION_SINCE, Operator.TYPE_ID);
		/*
		 * Boolean operators key words
		 */
		addKeyWord(BooleanOperator.NEG_STR, BooleanOperator.NEG_DESC, BooleanOperator.NEG_ID, BooleanOperator.NEG_SYN,
				BooleanOperator.NEG_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.AND_STR, BooleanOperator.AND_DESC, BooleanOperator.AND_ID, BooleanOperator.AND_SYN,
				BooleanOperator.AND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.AND1_STR, BooleanOperator.AND_DESC, BooleanOperator.AND_ID, BooleanOperator.AND1_SYN,
				BooleanOperator.AND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.AND2_STR, BooleanOperator.AND_DESC, BooleanOperator.AND_ID, BooleanOperator.AND2_SYN,
				BooleanOperator.AND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NAND_STR, BooleanOperator.NAND_DESC, BooleanOperator.NAND_ID,
				BooleanOperator.NAND_SYN, BooleanOperator.NAND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NAND1_STR, BooleanOperator.NAND_DESC, BooleanOperator.NAND_ID,
				BooleanOperator.NAND1_SYN, BooleanOperator.NAND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NAND2_STR, BooleanOperator.NAND_DESC, BooleanOperator.NAND_ID,
				BooleanOperator.NAND2_SYN, BooleanOperator.NAND_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.OR_STR, BooleanOperator.OR_DESC, BooleanOperator.OR_ID, BooleanOperator.OR_SYN,
				BooleanOperator.OR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.OR1_STR, BooleanOperator.OR_DESC, BooleanOperator.OR_ID, BooleanOperator.OR1_SYN,
				BooleanOperator.OR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.OR2_STR, BooleanOperator.OR_DESC, BooleanOperator.OR_ID, BooleanOperator.OR2_SYN,
				BooleanOperator.OR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NOR_STR, BooleanOperator.NOR_DESC, BooleanOperator.NOR_ID, BooleanOperator.NOR_SYN,
				BooleanOperator.NOR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NOR1_STR, BooleanOperator.NOR_DESC, BooleanOperator.NOR_ID, BooleanOperator.NOR1_SYN,
				BooleanOperator.NOR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NOR2_STR, BooleanOperator.NOR_DESC, BooleanOperator.NOR_ID, BooleanOperator.NOR2_SYN,
				BooleanOperator.NOR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.XOR_STR, BooleanOperator.XOR_DESC, BooleanOperator.XOR_ID, BooleanOperator.XOR_SYN,
				BooleanOperator.XOR_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.IMP_STR, BooleanOperator.IMP_DESC, BooleanOperator.IMP_ID, BooleanOperator.IMP_SYN,
				BooleanOperator.IMP_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.NIMP_STR, BooleanOperator.NIMP_DESC, BooleanOperator.NIMP_ID,
				BooleanOperator.NIMP_SYN, BooleanOperator.NIMP_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.CIMP_STR, BooleanOperator.CIMP_DESC, BooleanOperator.CIMP_ID,
				BooleanOperator.CIMP_SYN, BooleanOperator.CIMP_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.CNIMP_STR, BooleanOperator.CNIMP_DESC, BooleanOperator.CNIMP_ID,
				BooleanOperator.CNIMP_SYN, BooleanOperator.CNIMP_SINCE, BooleanOperator.TYPE_ID);
		addKeyWord(BooleanOperator.EQV_STR, BooleanOperator.EQV_DESC, BooleanOperator.EQV_ID, BooleanOperator.EQV_SYN,
				BooleanOperator.EQV_SINCE, BooleanOperator.TYPE_ID);
		/*
		 * Binary relations key words
		 */
		addKeyWord(BinaryRelation.EQ_STR, BinaryRelation.EQ_DESC, BinaryRelation.EQ_ID, BinaryRelation.EQ_SYN,
				BinaryRelation.EQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.EQ1_STR, BinaryRelation.EQ_DESC, BinaryRelation.EQ_ID, BinaryRelation.EQ1_SYN,
				BinaryRelation.EQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.NEQ_STR, BinaryRelation.NEQ_DESC, BinaryRelation.NEQ_ID, BinaryRelation.NEQ_SYN,
				BinaryRelation.NEQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.NEQ1_STR, BinaryRelation.NEQ_DESC, BinaryRelation.NEQ_ID, BinaryRelation.NEQ1_SYN,
				BinaryRelation.NEQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.NEQ2_STR, BinaryRelation.NEQ_DESC, BinaryRelation.NEQ_ID, BinaryRelation.NEQ2_SYN,
				BinaryRelation.NEQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.LT_STR, BinaryRelation.LT_DESC, BinaryRelation.LT_ID, BinaryRelation.LT_SYN,
				BinaryRelation.LT_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.GT_STR, BinaryRelation.GT_DESC, BinaryRelation.GT_ID, BinaryRelation.GT_SYN,
				BinaryRelation.GT_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.LEQ_STR, BinaryRelation.LEQ_DESC, BinaryRelation.LEQ_ID, BinaryRelation.LEQ_SYN,
				BinaryRelation.LEQ_SINCE, BinaryRelation.TYPE_ID);
		addKeyWord(BinaryRelation.GEQ_STR, BinaryRelation.GEQ_DESC, BinaryRelation.GEQ_ID, BinaryRelation.GEQ_SYN,
				BinaryRelation.GEQ_SINCE, BinaryRelation.TYPE_ID);
		if (parserKeyWordsOnly == false) {
			/*
			 * Calculus key words
			 */
			addKeyWord(CalculusOperator.SUM_STR, CalculusOperator.SUM_DESC, CalculusOperator.SUM_ID,
					CalculusOperator.SUM_SYN, CalculusOperator.SUM_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.PROD_STR, CalculusOperator.PROD_DESC, CalculusOperator.PROD_ID,
					CalculusOperator.PROD_SYN, CalculusOperator.PROD_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.INT_STR, CalculusOperator.INT_DESC, CalculusOperator.INT_ID,
					CalculusOperator.INT_SYN, CalculusOperator.INT_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.DER_STR, CalculusOperator.DER_DESC, CalculusOperator.DER_ID,
					CalculusOperator.DER_SYN, CalculusOperator.DER_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.DER_LEFT_STR, CalculusOperator.DER_LEFT_DESC, CalculusOperator.DER_LEFT_ID,
					CalculusOperator.DER_LEFT_SYN, CalculusOperator.DER_LEFT_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.DER_RIGHT_STR, CalculusOperator.DER_RIGHT_DESC, CalculusOperator.DER_RIGHT_ID,
					CalculusOperator.DER_RIGHT_SYN, CalculusOperator.DER_RIGHT_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.DERN_STR, CalculusOperator.DERN_DESC, CalculusOperator.DERN_ID,
					CalculusOperator.DERN_SYN, CalculusOperator.DERN_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.FORW_DIFF_STR, CalculusOperator.FORW_DIFF_DESC, CalculusOperator.FORW_DIFF_ID,
					CalculusOperator.FORW_DIFF_SYN, CalculusOperator.FORW_DIFF_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.BACKW_DIFF_STR, CalculusOperator.BACKW_DIFF_DESC,
					CalculusOperator.BACKW_DIFF_ID, CalculusOperator.BACKW_DIFF_SYN, CalculusOperator.BACKW_DIFF_SINCE,
					CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.AVG_STR, CalculusOperator.AVG_DESC, CalculusOperator.AVG_ID,
					CalculusOperator.AVG_SYN, CalculusOperator.AVG_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.VAR_STR, CalculusOperator.VAR_DESC, CalculusOperator.VAR_ID,
					CalculusOperator.VAR_SYN, CalculusOperator.VAR_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.STD_STR, CalculusOperator.STD_DESC, CalculusOperator.STD_ID,
					CalculusOperator.STD_SYN, CalculusOperator.STD_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.MIN_STR, CalculusOperator.MIN_DESC, CalculusOperator.MIN_ID,
					CalculusOperator.MIN_SYN, CalculusOperator.MIN_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.MAX_STR, CalculusOperator.MAX_DESC, CalculusOperator.MAX_ID,
					CalculusOperator.MAX_SYN, CalculusOperator.MAX_SINCE, CalculusOperator.TYPE_ID);
			addKeyWord(CalculusOperator.SOLVE_STR, CalculusOperator.SOLVE_DESC, CalculusOperator.SOLVE_ID,
					CalculusOperator.SOLVE_SYN, CalculusOperator.SOLVE_SINCE, CalculusOperator.TYPE_ID);

			/*
			 * Random variables
			 */
			addKeyWord(RandomVariable.UNIFORM_STR, RandomVariable.UNIFORM_DESC, RandomVariable.UNIFORM_ID,
					RandomVariable.UNIFORM_SYN, RandomVariable.UNIFORM_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT_STR, RandomVariable.INT_DESC, RandomVariable.INT_ID, RandomVariable.INT_SYN,
					RandomVariable.INT_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT1_STR, RandomVariable.INT1_DESC, RandomVariable.INT1_ID,
					RandomVariable.INT1_SYN, RandomVariable.INT1_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT2_STR, RandomVariable.INT2_DESC, RandomVariable.INT2_ID,
					RandomVariable.INT2_SYN, RandomVariable.INT2_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT3_STR, RandomVariable.INT3_DESC, RandomVariable.INT3_ID,
					RandomVariable.INT3_SYN, RandomVariable.INT3_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT4_STR, RandomVariable.INT4_DESC, RandomVariable.INT4_ID,
					RandomVariable.INT4_SYN, RandomVariable.INT4_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT5_STR, RandomVariable.INT5_DESC, RandomVariable.INT5_ID,
					RandomVariable.INT5_SYN, RandomVariable.INT5_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT6_STR, RandomVariable.INT6_DESC, RandomVariable.INT6_ID,
					RandomVariable.INT6_SYN, RandomVariable.INT6_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT7_STR, RandomVariable.INT7_DESC, RandomVariable.INT7_ID,
					RandomVariable.INT7_SYN, RandomVariable.INT7_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT8_STR, RandomVariable.INT8_DESC, RandomVariable.INT8_ID,
					RandomVariable.INT8_SYN, RandomVariable.INT8_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.INT9_STR, RandomVariable.INT9_DESC, RandomVariable.INT9_ID,
					RandomVariable.INT9_SYN, RandomVariable.INT9_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_STR, RandomVariable.NAT0_DESC, RandomVariable.NAT0_ID,
					RandomVariable.NAT0_SYN, RandomVariable.NAT0_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_1_STR, RandomVariable.NAT0_1_DESC, RandomVariable.NAT0_1_ID,
					RandomVariable.NAT0_1_SYN, RandomVariable.NAT0_1_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_2_STR, RandomVariable.NAT0_2_DESC, RandomVariable.NAT0_2_ID,
					RandomVariable.NAT0_2_SYN, RandomVariable.NAT0_2_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_3_STR, RandomVariable.NAT0_3_DESC, RandomVariable.NAT0_3_ID,
					RandomVariable.NAT0_3_SYN, RandomVariable.NAT0_3_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_4_STR, RandomVariable.NAT0_4_DESC, RandomVariable.NAT0_4_ID,
					RandomVariable.NAT0_4_SYN, RandomVariable.NAT0_4_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_5_STR, RandomVariable.NAT0_5_DESC, RandomVariable.NAT0_5_ID,
					RandomVariable.NAT0_5_SYN, RandomVariable.NAT0_5_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_6_STR, RandomVariable.NAT0_6_DESC, RandomVariable.NAT0_6_ID,
					RandomVariable.NAT0_6_SYN, RandomVariable.NAT0_6_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_7_STR, RandomVariable.NAT0_7_DESC, RandomVariable.NAT0_7_ID,
					RandomVariable.NAT0_7_SYN, RandomVariable.NAT0_7_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_8_STR, RandomVariable.NAT0_8_DESC, RandomVariable.NAT0_8_ID,
					RandomVariable.NAT0_8_SYN, RandomVariable.NAT0_8_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT0_9_STR, RandomVariable.NAT0_9_DESC, RandomVariable.NAT0_9_ID,
					RandomVariable.NAT0_9_SYN, RandomVariable.NAT0_9_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_STR, RandomVariable.NAT1_DESC, RandomVariable.NAT1_ID,
					RandomVariable.NAT1_SYN, RandomVariable.NAT1_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_1_STR, RandomVariable.NAT1_1_DESC, RandomVariable.NAT1_1_ID,
					RandomVariable.NAT1_1_SYN, RandomVariable.NAT1_1_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_2_STR, RandomVariable.NAT1_2_DESC, RandomVariable.NAT1_2_ID,
					RandomVariable.NAT1_2_SYN, RandomVariable.NAT1_2_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_3_STR, RandomVariable.NAT1_3_DESC, RandomVariable.NAT1_3_ID,
					RandomVariable.NAT1_3_SYN, RandomVariable.NAT1_3_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_4_STR, RandomVariable.NAT1_4_DESC, RandomVariable.NAT1_4_ID,
					RandomVariable.NAT1_4_SYN, RandomVariable.NAT1_4_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_5_STR, RandomVariable.NAT1_5_DESC, RandomVariable.NAT1_5_ID,
					RandomVariable.NAT1_5_SYN, RandomVariable.NAT1_5_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_6_STR, RandomVariable.NAT1_6_DESC, RandomVariable.NAT1_6_ID,
					RandomVariable.NAT1_6_SYN, RandomVariable.NAT1_6_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_7_STR, RandomVariable.NAT1_7_DESC, RandomVariable.NAT1_7_ID,
					RandomVariable.NAT1_7_SYN, RandomVariable.NAT1_7_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_8_STR, RandomVariable.NAT1_8_DESC, RandomVariable.NAT1_8_ID,
					RandomVariable.NAT1_8_SYN, RandomVariable.NAT1_8_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NAT1_9_STR, RandomVariable.NAT1_9_DESC, RandomVariable.NAT1_9_ID,
					RandomVariable.NAT1_9_SYN, RandomVariable.NAT1_9_SINCE, RandomVariable.TYPE_ID);
			addKeyWord(RandomVariable.NOR_STR, RandomVariable.NOR_DESC, RandomVariable.NOR_ID, RandomVariable.NOR_SYN,
					RandomVariable.NOR_SINCE, RandomVariable.TYPE_ID);
			/*
			 * BiteWise Operators
			 */
			addKeyWord(BitwiseOperator.COMPL_STR, BitwiseOperator.COMPL_DESC, BitwiseOperator.COMPL_ID,
					BitwiseOperator.COMPL_SYN, BitwiseOperator.COMPL_SINCE, BitwiseOperator.TYPE_ID);
			addKeyWord(BitwiseOperator.AND_STR, BitwiseOperator.AND_DESC, BitwiseOperator.AND_ID,
					BitwiseOperator.AND_SYN, BitwiseOperator.AND_SINCE, BitwiseOperator.TYPE_ID);
			addKeyWord(BitwiseOperator.XOR_STR, BitwiseOperator.XOR_DESC, BitwiseOperator.XOR_ID,
					BitwiseOperator.XOR_SYN, BitwiseOperator.XOR_SINCE, BitwiseOperator.TYPE_ID);
			addKeyWord(BitwiseOperator.OR_STR, BitwiseOperator.OR_DESC, BitwiseOperator.OR_ID, BitwiseOperator.OR_SYN,
					BitwiseOperator.OR_SINCE, BitwiseOperator.TYPE_ID);
			addKeyWord(BitwiseOperator.LEFT_SHIFT_STR, BitwiseOperator.LEFT_SHIFT_DESC, BitwiseOperator.LEFT_SHIFT_ID,
					BitwiseOperator.LEFT_SHIFT_SYN, BitwiseOperator.LEFT_SHIFT_SINCE, BitwiseOperator.TYPE_ID);
			addKeyWord(BitwiseOperator.RIGHT_SHIFT_STR, BitwiseOperator.RIGHT_SHIFT_DESC,
					BitwiseOperator.RIGHT_SHIFT_ID, BitwiseOperator.RIGHT_SHIFT_SYN, BitwiseOperator.RIGHT_SHIFT_SINCE,
					BitwiseOperator.TYPE_ID);
			/*
			 * Units
			 */
			addKeyWord(Unit.PERC_STR, Unit.PERC_DESC, Unit.PERC_ID, Unit.PERC_SYN, Unit.PERC_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.PROMIL_STR, Unit.PROMIL_DESC, Unit.PROMIL_ID, Unit.PROMIL_SYN, Unit.PROMIL_SINCE,
					Unit.TYPE_ID);
			/* Metric prefixes */
			addKeyWord(Unit.YOTTA_STR, Unit.YOTTA_DESC, Unit.YOTTA_ID, Unit.YOTTA_SYN, Unit.YOTTA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.YOTTA_SEPT_STR, Unit.YOTTA_DESC, Unit.YOTTA_ID, Unit.YOTTA_SEPT_SYN, Unit.YOTTA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.ZETTA_STR, Unit.ZETTA_DESC, Unit.ZETTA_ID, Unit.ZETTA_SYN, Unit.ZETTA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.ZETTA_SEXT_STR, Unit.ZETTA_DESC, Unit.ZETTA_ID, Unit.ZETTA_SEXT_SYN, Unit.ZETTA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.EXA_STR, Unit.EXA_DESC, Unit.EXA_ID, Unit.EXA_SYN, Unit.EXA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.EXA_QUINT_STR, Unit.EXA_DESC, Unit.EXA_ID, Unit.EXA_QUINT_SYN, Unit.EXA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.PETA_STR, Unit.PETA_DESC, Unit.PETA_ID, Unit.PETA_SYN, Unit.PETA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.PETA_QUAD_STR, Unit.PETA_DESC, Unit.PETA_ID, Unit.PETA_QUAD_SYN, Unit.PETA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.TERA_STR, Unit.TERA_DESC, Unit.TERA_ID, Unit.TERA_SYN, Unit.TERA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.TERA_TRIL_STR, Unit.TERA_DESC, Unit.TERA_ID, Unit.TERA_TRIL_SYN, Unit.TERA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.GIGA_STR, Unit.GIGA_DESC, Unit.GIGA_ID, Unit.GIGA_SYN, Unit.GIGA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.GIGA_BIL_STR, Unit.GIGA_DESC, Unit.GIGA_ID, Unit.GIGA_BIL_SYN, Unit.GIGA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.MEGA_STR, Unit.MEGA_DESC, Unit.MEGA_ID, Unit.MEGA_SYN, Unit.MEGA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MEGA_MIL_STR, Unit.MEGA_DESC, Unit.MEGA_ID, Unit.MEGA_MIL_SYN, Unit.MEGA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.KILO_STR, Unit.KILO_DESC, Unit.KILO_ID, Unit.KILO_SYN, Unit.KILO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILO_TH_STR, Unit.KILO_DESC, Unit.KILO_ID, Unit.KILO_TH_SYN, Unit.KILO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.HECTO_STR, Unit.HECTO_DESC, Unit.HECTO_ID, Unit.HECTO_SYN, Unit.HECTO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.HECTO_HUND_STR, Unit.HECTO_DESC, Unit.HECTO_ID, Unit.HECTO_HUND_SYN, Unit.HECTO_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.DECA_STR, Unit.DECA_DESC, Unit.DECA_ID, Unit.DECA_SYN, Unit.DECA_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.DECA_TEN_STR, Unit.DECA_DESC, Unit.DECA_ID, Unit.DECA_TEN_SYN, Unit.DECA_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.DECI_STR, Unit.DECI_DESC, Unit.DECI_ID, Unit.DECI_SYN, Unit.DECI_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.CENTI_STR, Unit.CENTI_DESC, Unit.CENTI_ID, Unit.CENTI_SYN, Unit.CENTI_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILLI_STR, Unit.MILLI_DESC, Unit.MILLI_ID, Unit.MILLI_SYN, Unit.MILLI_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MICRO_STR, Unit.MICRO_DESC, Unit.MICRO_ID, Unit.MICRO_SYN, Unit.MICRO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.NANO_STR, Unit.NANO_DESC, Unit.NANO_ID, Unit.NANO_SYN, Unit.NANO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.PICO_STR, Unit.PICO_DESC, Unit.PICO_ID, Unit.PICO_SYN, Unit.PICO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.FEMTO_STR, Unit.FEMTO_DESC, Unit.FEMTO_ID, Unit.FEMTO_SYN, Unit.FEMTO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.ATTO_STR, Unit.ATTO_DESC, Unit.ATTO_ID, Unit.ATTO_SYN, Unit.ATTO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.ZEPTO_STR, Unit.ZEPTO_DESC, Unit.ZEPTO_ID, Unit.ZEPTO_SYN, Unit.ZEPTO_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.YOCTO_STR, Unit.YOCTO_DESC, Unit.YOCTO_ID, Unit.YOCTO_SYN, Unit.YOCTO_SINCE, Unit.TYPE_ID);
			/* Units of length / distance */
			addKeyWord(Unit.METRE_STR, Unit.METRE_DESC, Unit.METRE_ID, Unit.METRE_SYN, Unit.METRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOMETRE_STR, Unit.KILOMETRE_DESC, Unit.KILOMETRE_ID, Unit.KILOMETRE_SYN,
					Unit.KILOMETRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.CENTIMETRE_STR, Unit.CENTIMETRE_DESC, Unit.CENTIMETRE_ID, Unit.CENTIMETRE_SYN,
					Unit.CENTIMETRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILLIMETRE_STR, Unit.MILLIMETRE_DESC, Unit.MILLIMETRE_ID, Unit.MILLIMETRE_SYN,
					Unit.MILLIMETRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.INCH_STR, Unit.INCH_DESC, Unit.INCH_ID, Unit.INCH_SYN, Unit.INCH_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.YARD_STR, Unit.YARD_DESC, Unit.YARD_ID, Unit.YARD_SYN, Unit.YARD_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.FEET_STR, Unit.FEET_DESC, Unit.FEET_ID, Unit.FEET_SYN, Unit.FEET_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILE_STR, Unit.MILE_DESC, Unit.MILE_ID, Unit.MILE_SYN, Unit.MILE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.NAUTICAL_MILE_STR, Unit.NAUTICAL_MILE_DESC, Unit.NAUTICAL_MILE_ID, Unit.NAUTICAL_MILE_SYN,
					Unit.NAUTICAL_MILE_SINCE, Unit.TYPE_ID);
			/* Units of area */
			addKeyWord(Unit.METRE2_STR, Unit.METRE2_DESC, Unit.METRE2_ID, Unit.METRE2_SYN, Unit.METRE2_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.CENTIMETRE2_STR, Unit.CENTIMETRE2_DESC, Unit.CENTIMETRE2_ID, Unit.CENTIMETRE2_SYN,
					Unit.CENTIMETRE2_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILLIMETRE2_STR, Unit.MILLIMETRE2_DESC, Unit.MILLIMETRE2_ID, Unit.MILLIMETRE2_SYN,
					Unit.MILLIMETRE2_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.ARE_STR, Unit.ARE_DESC, Unit.ARE_ID, Unit.ARE_SYN, Unit.ARE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.HECTARE_STR, Unit.HECTARE_DESC, Unit.HECTARE_ID, Unit.HECTARE_SYN, Unit.HECTARE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.ACRE_STR, Unit.ACRE_DESC, Unit.ACRE_ID, Unit.ACRE_SYN, Unit.ACRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOMETRE2_STR, Unit.KILOMETRE2_DESC, Unit.KILOMETRE2_ID, Unit.KILOMETRE2_SYN,
					Unit.KILOMETRE2_SINCE, Unit.TYPE_ID);
			/* Units of volume */
			addKeyWord(Unit.MILLIMETRE3_STR, Unit.MILLIMETRE3_DESC, Unit.MILLIMETRE3_ID, Unit.MILLIMETRE3_SYN,
					Unit.MILLIMETRE3_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.CENTIMETRE3_STR, Unit.CENTIMETRE3_DESC, Unit.CENTIMETRE3_ID, Unit.CENTIMETRE3_SYN,
					Unit.CENTIMETRE3_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.METRE3_STR, Unit.METRE3_DESC, Unit.METRE3_ID, Unit.METRE3_SYN, Unit.METRE3_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.KILOMETRE3_STR, Unit.KILOMETRE3_DESC, Unit.KILOMETRE3_ID, Unit.KILOMETRE3_SYN,
					Unit.KILOMETRE3_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILLILITRE_STR, Unit.MILLILITRE_DESC, Unit.MILLILITRE_ID, Unit.MILLILITRE_SYN,
					Unit.MILLILITRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.LITRE_STR, Unit.LITRE_DESC, Unit.LITRE_ID, Unit.LITRE_SYN, Unit.LITRE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.GALLON_STR, Unit.GALLON_DESC, Unit.GALLON_ID, Unit.GALLON_SYN, Unit.GALLON_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.PINT_STR, Unit.PINT_DESC, Unit.PINT_ID, Unit.PINT_SYN, Unit.PINT_SINCE, Unit.TYPE_ID);
			/* Units of time */
			addKeyWord(Unit.SECOND_STR, Unit.SECOND_DESC, Unit.SECOND_ID, Unit.SECOND_SYN, Unit.SECOND_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.MILLISECOND_STR, Unit.MILLISECOND_DESC, Unit.MILLISECOND_ID, Unit.MILLISECOND_SYN,
					Unit.MILLISECOND_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MINUTE_STR, Unit.MINUTE_DESC, Unit.MINUTE_ID, Unit.MINUTE_SYN, Unit.MINUTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.HOUR_STR, Unit.HOUR_DESC, Unit.HOUR_ID, Unit.HOUR_SYN, Unit.HOUR_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.DAY_STR, Unit.DAY_DESC, Unit.DAY_ID, Unit.DAY_SYN, Unit.DAY_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.WEEK_STR, Unit.WEEK_DESC, Unit.WEEK_ID, Unit.WEEK_SYN, Unit.WEEK_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.JULIAN_YEAR_STR, Unit.JULIAN_YEAR_DESC, Unit.JULIAN_YEAR_ID, Unit.JULIAN_YEAR_SYN,
					Unit.JULIAN_YEAR_SINCE, Unit.TYPE_ID);
			/* Units of mass */
			addKeyWord(Unit.KILOGRAM_STR, Unit.KILOGRAM_DESC, Unit.KILOGRAM_ID, Unit.KILOGRAM_SYN, Unit.KILOGRAM_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.GRAM_STR, Unit.GRAM_DESC, Unit.GRAM_ID, Unit.GRAM_SYN, Unit.GRAM_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILLIGRAM_STR, Unit.MILLIGRAM_DESC, Unit.MILLIGRAM_ID, Unit.MILLIGRAM_SYN,
					Unit.MILLIGRAM_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.DECAGRAM_STR, Unit.DECAGRAM_DESC, Unit.DECAGRAM_ID, Unit.DECAGRAM_SYN, Unit.DECAGRAM_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.TONNE_STR, Unit.TONNE_DESC, Unit.TONNE_ID, Unit.TONNE_SYN, Unit.TONNE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.OUNCE_STR, Unit.OUNCE_DESC, Unit.OUNCE_ID, Unit.OUNCE_SYN, Unit.OUNCE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.POUND_STR, Unit.POUND_DESC, Unit.POUND_ID, Unit.POUND_SYN, Unit.POUND_SINCE, Unit.TYPE_ID);
			/* Units of information */
			addKeyWord(Unit.BIT_STR, Unit.BIT_DESC, Unit.BIT_ID, Unit.BIT_SYN, Unit.BIT_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOBIT_STR, Unit.KILOBIT_DESC, Unit.KILOBIT_ID, Unit.KILOBIT_SYN, Unit.KILOBIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.MEGABIT_STR, Unit.MEGABIT_DESC, Unit.MEGABIT_ID, Unit.MEGABIT_SYN, Unit.MEGABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.GIGABIT_STR, Unit.GIGABIT_DESC, Unit.GIGABIT_ID, Unit.GIGABIT_SYN, Unit.GIGABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.TERABIT_STR, Unit.TERABIT_DESC, Unit.TERABIT_ID, Unit.TERABIT_SYN, Unit.TERABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.PETABIT_STR, Unit.PETABIT_DESC, Unit.PETABIT_ID, Unit.PETABIT_SYN, Unit.PETABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.EXABIT_STR, Unit.EXABIT_DESC, Unit.EXABIT_ID, Unit.EXABIT_SYN, Unit.EXABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.ZETTABIT_STR, Unit.ZETTABIT_DESC, Unit.ZETTABIT_ID, Unit.ZETTABIT_SYN, Unit.ZETTABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.YOTTABIT_STR, Unit.YOTTABIT_DESC, Unit.YOTTABIT_ID, Unit.YOTTABIT_SYN, Unit.YOTTABIT_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.BYTE_STR, Unit.BYTE_DESC, Unit.BYTE_ID, Unit.BYTE_SYN, Unit.BYTE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOBYTE_STR, Unit.KILOBYTE_DESC, Unit.KILOBYTE_ID, Unit.KILOBYTE_SYN, Unit.KILOBYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.MEGABYTE_STR, Unit.MEGABYTE_DESC, Unit.MEGABYTE_ID, Unit.MEGABYTE_SYN, Unit.MEGABYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.GIGABYTE_STR, Unit.GIGABYTE_DESC, Unit.GIGABYTE_ID, Unit.GIGABYTE_SYN, Unit.GIGABYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.TERABYTE_STR, Unit.TERABYTE_DESC, Unit.TERABYTE_ID, Unit.TERABYTE_SYN, Unit.TERABYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.PETABYTE_STR, Unit.PETABYTE_DESC, Unit.PETABYTE_ID, Unit.PETABYTE_SYN, Unit.PETABYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.EXABYTE_STR, Unit.EXABYTE_DESC, Unit.EXABYTE_ID, Unit.EXABYTE_SYN, Unit.EXABYTE_SINCE,
					Unit.TYPE_ID);
			addKeyWord(Unit.ZETTABYTE_STR, Unit.ZETTABYTE_DESC, Unit.ZETTABYTE_ID, Unit.ZETTABYTE_SYN,
					Unit.ZETTABYTE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.YOTTABYTE_STR, Unit.YOTTABYTE_DESC, Unit.YOTTABYTE_ID, Unit.YOTTABYTE_SYN,
					Unit.YOTTABYTE_SINCE, Unit.TYPE_ID);
			/* Units of energy */
			addKeyWord(Unit.JOULE_STR, Unit.JOULE_DESC, Unit.JOULE_ID, Unit.JOULE_SYN, Unit.JOULE_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.ELECTRONO_VOLT_STR, Unit.ELECTRONO_VOLT_DESC, Unit.ELECTRONO_VOLT_ID,
					Unit.ELECTRONO_VOLT_SYN, Unit.ELECTRONO_VOLT_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILO_ELECTRONO_VOLT_STR, Unit.KILO_ELECTRONO_VOLT_DESC, Unit.KILO_ELECTRONO_VOLT_ID,
					Unit.KILO_ELECTRONO_VOLT_SYN, Unit.KILO_ELECTRONO_VOLT_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MEGA_ELECTRONO_VOLT_STR, Unit.MEGA_ELECTRONO_VOLT_DESC, Unit.MEGA_ELECTRONO_VOLT_ID,
					Unit.MEGA_ELECTRONO_VOLT_SYN, Unit.MEGA_ELECTRONO_VOLT_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.GIGA_ELECTRONO_VOLT_STR, Unit.GIGA_ELECTRONO_VOLT_DESC, Unit.GIGA_ELECTRONO_VOLT_ID,
					Unit.GIGA_ELECTRONO_VOLT_SYN, Unit.GIGA_ELECTRONO_VOLT_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.TERA_ELECTRONO_VOLT_STR, Unit.TERA_ELECTRONO_VOLT_DESC, Unit.TERA_ELECTRONO_VOLT_ID,
					Unit.TERA_ELECTRONO_VOLT_SYN, Unit.TERA_ELECTRONO_VOLT_SINCE, Unit.TYPE_ID);
			/* Units of speed */
			addKeyWord(Unit.METRE_PER_SECOND_STR, Unit.METRE_PER_SECOND_DESC, Unit.METRE_PER_SECOND_ID,
					Unit.METRE_PER_SECOND_SYN, Unit.METRE_PER_SECOND_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOMETRE_PER_HOUR_STR, Unit.KILOMETRE_PER_HOUR_DESC, Unit.KILOMETRE_PER_HOUR_ID,
					Unit.KILOMETRE_PER_HOUR_SYN, Unit.KILOMETRE_PER_HOUR_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILE_PER_HOUR_STR, Unit.MILE_PER_HOUR_DESC, Unit.MILE_PER_HOUR_ID, Unit.MILE_PER_HOUR_SYN,
					Unit.MILE_PER_HOUR_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KNOT_STR, Unit.KNOT_DESC, Unit.KNOT_ID, Unit.KNOT_SYN, Unit.KNOT_SINCE, Unit.TYPE_ID);
			/* Units of acceleration */
			addKeyWord(Unit.METRE_PER_SECOND2_STR, Unit.METRE_PER_SECOND2_DESC, Unit.METRE_PER_SECOND2_ID,
					Unit.METRE_PER_SECOND2_SYN, Unit.METRE_PER_SECOND2_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.KILOMETRE_PER_HOUR2_STR, Unit.KILOMETRE_PER_HOUR2_DESC, Unit.KILOMETRE_PER_HOUR2_ID,
					Unit.KILOMETRE_PER_HOUR2_SYN, Unit.KILOMETRE_PER_HOUR2_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MILE_PER_HOUR2_STR, Unit.MILE_PER_HOUR2_DESC, Unit.MILE_PER_HOUR2_ID,
					Unit.MILE_PER_HOUR2_SYN, Unit.MILE_PER_HOUR2_SINCE, Unit.TYPE_ID);
			/* Units of angle */
			addKeyWord(Unit.RADIAN_ARC_STR, Unit.RADIAN_ARC_DESC, Unit.RADIAN_ARC_ID, Unit.RADIAN_ARC_SYN,
					Unit.RADIAN_ARC_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.DEGREE_ARC_STR, Unit.DEGREE_ARC_DESC, Unit.DEGREE_ARC_ID, Unit.DEGREE_ARC_SYN,
					Unit.DEGREE_ARC_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.MINUTE_ARC_STR, Unit.MINUTE_ARC_DESC, Unit.MINUTE_ARC_ID, Unit.MINUTE_ARC_SYN,
					Unit.MINUTE_ARC_SINCE, Unit.TYPE_ID);
			addKeyWord(Unit.SECOND_ARC_STR, Unit.SECOND_ARC_DESC, Unit.SECOND_ARC_ID, Unit.SECOND_ARC_SYN,
					Unit.SECOND_ARC_SINCE, Unit.TYPE_ID);
		}
		/*
		 * Other parser symbols key words
		 */
		addKeyWord(ParserSymbol.LEFT_PARENTHESES_STR, ParserSymbol.LEFT_PARENTHESES_DESC,
				ParserSymbol.LEFT_PARENTHESES_ID, ParserSymbol.LEFT_PARENTHESES_SYN,
				ParserSymbol.LEFT_PARENTHESES_SINCE, ParserSymbol.TYPE_ID);
		addKeyWord(ParserSymbol.RIGHT_PARENTHESES_STR, ParserSymbol.RIGHT_PARENTHESES_DESC,
				ParserSymbol.RIGHT_PARENTHESES_ID, ParserSymbol.RIGHT_PARENTHESES_SYN,
				ParserSymbol.RIGHT_PARENTHESES_SINCE, ParserSymbol.TYPE_ID);
		addKeyWord(ParserSymbol.COMMA_STR, ParserSymbol.COMMA_DESC, ParserSymbol.COMMA_ID, ParserSymbol.COMMA_SYN,
				ParserSymbol.COMMA_SINCE, ParserSymbol.TYPE_ID);
		addKeyWord(ParserSymbol.SEMI_STR, ParserSymbol.SEMI_DESC, ParserSymbol.COMMA_ID, ParserSymbol.SEMI_SYN,
				ParserSymbol.COMMA_SINCE, ParserSymbol.TYPE_ID);
		addKeyWord(ParserSymbol.DECIMAL_REG_EXP, ParserSymbol.NUMBER_REG_DESC, ParserSymbol.NUMBER_ID,
				ParserSymbol.NUMBER_SYN, ParserSymbol.NUMBER_SINCE, ParserSymbol.NUMBER_TYPE_ID);
		addKeyWord(ParserSymbol.BLANK_STR, ParserSymbol.BLANK_DESC, ParserSymbol.BLANK_ID, ParserSymbol.BLANK_SYN,
				ParserSymbol.BLANK_SINCE, ParserSymbol.TYPE_ID);
	}

	/**
	 * Adds key word to the keyWords list
	 *
	 * @param wordString
	 * @param wordDescription
	 * @param wordId
	 * @param wordTypeId
	 */
	private void addKeyWord(String wordString, String wordDescription, int wordId, String wordSyntax, String wordSince,
			int wordTypeId) {
		if ((mXparser.tokensToRemove.size() > 0) || (mXparser.tokensToModify.size() > 0))
			if ((wordTypeId == CalculusOperator.TYPE_ID) || (wordTypeId == RandomVariable.TYPE_ID)
					|| (wordTypeId == Unit.TYPE_ID)) {
				if (mXparser.tokensToRemove.size() > 0)
					if (mXparser.tokensToRemove.contains(wordString))
						return;
				if (mXparser.tokensToModify.size() > 0) {
					for (TokenModification tm : mXparser.tokensToModify)
						if (tm.currentToken.equals(wordString)) {
							wordString = tm.newToken;
							if (tm.newTokenDescription != null)
								wordDescription = tm.newTokenDescription;
							wordSyntax = wordSyntax.replace(tm.currentToken, tm.newToken);
						}
				}
			}
		keyWordsList.add(new KeyWord(wordString, wordDescription, wordId, wordSyntax, wordSince, wordTypeId));
	}

	/**
	 * Checks whether unknown token represents number literal provided in different
	 * numeral base system, where base is between 1 and 36.
	 *
	 * @param token
	 *            The token not know to the parser
	 */
	private void checkOtherNumberBases(Token token) {
		int dotPos = 0;
		int tokenStrLength = token.tokenStr.length();
		/* find dot position */
		if (tokenStrLength >= 2) {
			if (token.tokenStr.charAt(1) == '.')
				dotPos = 1;
		}
		if ((dotPos == 0) && (tokenStrLength >= 3)) {
			if (token.tokenStr.charAt(2) == '.')
				dotPos = 2;
		}
		if ((dotPos == 0) && (tokenStrLength >= 4)) {
			if (token.tokenStr.charAt(3) == '.')
				dotPos = 3;
		}
		if (dotPos == 0)
			return;
		/* check if there is base indicator */
		String baseInd = token.tokenStr.substring(0, dotPos).toLowerCase();
		String numberLiteral = "";
		if (tokenStrLength > dotPos + 1)
			numberLiteral = token.tokenStr.substring(dotPos + 1);
		int numeralSystemBase = 0;
		/* evaluate numeral system base */
		if (baseInd.equals("b"))
			numeralSystemBase = 2;
		else if (baseInd.equals("o"))
			numeralSystemBase = 8;
		else if (baseInd.equals("h"))
			numeralSystemBase = 16;
		else if (baseInd.equals("b1"))
			numeralSystemBase = 1;
		else if (baseInd.equals("b2"))
			numeralSystemBase = 2;
		else if (baseInd.equals("b3"))
			numeralSystemBase = 3;
		else if (baseInd.equals("b4"))
			numeralSystemBase = 4;
		else if (baseInd.equals("b5"))
			numeralSystemBase = 5;
		else if (baseInd.equals("b6"))
			numeralSystemBase = 6;
		else if (baseInd.equals("b7"))
			numeralSystemBase = 7;
		else if (baseInd.equals("b8"))
			numeralSystemBase = 8;
		else if (baseInd.equals("b9"))
			numeralSystemBase = 9;
		else if (baseInd.equals("b10"))
			numeralSystemBase = 10;
		else if (baseInd.equals("b11"))
			numeralSystemBase = 11;
		else if (baseInd.equals("b12"))
			numeralSystemBase = 12;
		else if (baseInd.equals("b13"))
			numeralSystemBase = 13;
		else if (baseInd.equals("b14"))
			numeralSystemBase = 14;
		else if (baseInd.equals("b15"))
			numeralSystemBase = 15;
		else if (baseInd.equals("b16"))
			numeralSystemBase = 16;
		else if (baseInd.equals("b17"))
			numeralSystemBase = 17;
		else if (baseInd.equals("b18"))
			numeralSystemBase = 18;
		else if (baseInd.equals("b19"))
			numeralSystemBase = 19;
		else if (baseInd.equals("b20"))
			numeralSystemBase = 20;
		else if (baseInd.equals("b21"))
			numeralSystemBase = 21;
		else if (baseInd.equals("b22"))
			numeralSystemBase = 22;
		else if (baseInd.equals("b23"))
			numeralSystemBase = 23;
		else if (baseInd.equals("b24"))
			numeralSystemBase = 24;
		else if (baseInd.equals("b25"))
			numeralSystemBase = 25;
		else if (baseInd.equals("b26"))
			numeralSystemBase = 26;
		else if (baseInd.equals("b27"))
			numeralSystemBase = 27;
		else if (baseInd.equals("b28"))
			numeralSystemBase = 28;
		else if (baseInd.equals("b29"))
			numeralSystemBase = 29;
		else if (baseInd.equals("b30"))
			numeralSystemBase = 30;
		else if (baseInd.equals("b31"))
			numeralSystemBase = 31;
		else if (baseInd.equals("b32"))
			numeralSystemBase = 32;
		else if (baseInd.equals("b33"))
			numeralSystemBase = 33;
		else if (baseInd.equals("b34"))
			numeralSystemBase = 34;
		else if (baseInd.equals("b35"))
			numeralSystemBase = 35;
		else if (baseInd.equals("b36"))
			numeralSystemBase = 36;
		/* if base was found, perform conversion */
		if ((numeralSystemBase > 0) && (numeralSystemBase <= 36)) {
			token.tokenTypeId = ParserSymbol.NUMBER_TYPE_ID;
			token.tokenId = ParserSymbol.NUMBER_ID;
			token.tokenValue = NumberTheory.convOthBase2Decimal(numberLiteral, numeralSystemBase);
		}
	}

	/**
	 * Checks whether unknown token represents fraction provided as fraction or
	 * mixed fraction
	 *
	 * @param token
	 *            The token not know to the parser
	 */
	private void checkFraction(Token token) {
		int tokenStrLength = token.tokenStr.length();
		if (tokenStrLength < 3)
			return;
		if (!mXparser.regexMatch(token.tokenStr, ParserSymbol.FRACTION))
			return;
		int underscore1stPos = token.tokenStr.indexOf('_');
		int underscore2ndPos = token.tokenStr.indexOf('_', underscore1stPos + 1);
		boolean mixedFraction = false;
		if (underscore2ndPos > 0)
			mixedFraction = true;
		double fractionValue;
		if (mixedFraction) {
			String wholeStr = token.tokenStr.substring(0, underscore1stPos);
			String numeratorStr = token.tokenStr.substring(underscore1stPos + 1, underscore2ndPos);
			String denominatorStr = token.tokenStr.substring(underscore2ndPos + 1);
			double whole = Double.parseDouble(wholeStr);
			double numerator = Double.parseDouble(numeratorStr);
			double denominator = Double.parseDouble(denominatorStr);
			if (denominator == 0)
				fractionValue = Double.NaN;
			else {
				fractionValue = whole + numerator / denominator;
			}
		} else {
			String numeratorStr = token.tokenStr.substring(0, underscore1stPos);
			String denominatorStr = token.tokenStr.substring(underscore1stPos + 1);
			double numerator = Double.parseDouble(numeratorStr);
			double denominator = Double.parseDouble(denominatorStr);
			if (denominator == 0)
				fractionValue = Double.NaN;
			else {
				fractionValue = numerator / denominator;
			}
		}
		token.tokenTypeId = ParserSymbol.NUMBER_TYPE_ID;
		token.tokenId = ParserSymbol.NUMBER_ID;
		token.tokenValue = fractionValue;
	}

	/**
	 * Adds expression token Method is called by the tokenExpressionString() while
	 * parsing string expression
	 *
	 * @param tokenStr
	 *            the token string
	 * @param keyWord
	 *            the key word
	 */
	private void addToken(String tokenStr, KeyWord keyWord) {
		Token token = new Token();
		initialTokens.add(token);
		token.tokenStr = tokenStr;
		token.keyWord = keyWord.wordString;
		token.tokenTypeId = keyWord.wordTypeId;
		token.tokenId = keyWord.wordId;
		if (token.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID) {
			token.tokenValue = Double.valueOf(token.tokenStr);
			token.keyWord = ParserSymbol.NUMBER_STR;
		} else if (token.tokenTypeId == Token.NOT_MATCHED) {
			checkOtherNumberBases(token);
			if (token.tokenTypeId == Token.NOT_MATCHED)
				checkFraction(token);
		}
	}

	private boolean isNotSpecialChar(char c) {
		if (c == '+')
			return false;
		if (c == '-')
			return false;
		if (c == '+')
			return false;
		if (c == '*')
			return false;
		if (c == '/')
			return false;
		if (c == '^')
			return false;
		if (c == ',')
			return false;
		if (c == ';')
			return false;
		if (c == '(')
			return false;
		if (c == ')')
			return false;
		if (c == '|')
			return false;
		if (c == '&')
			return false;
		if (c == '=')
			return false;
		if (c == '>')
			return false;
		if (c == '<')
			return false;
		if (c == '~')
			return false;
		if (c == '\\')
			return false;
		if (c == '#')
			return false;
		if (c == '@')
			return false;
		return true;
	}

	/**
	 * Tokenizing expression string
	 */
	private void tokenizeExpressionString() {
		/*
		 * Add parser and argument key words
		 */
		keyWordsList = new ArrayList<KeyWord>();
		addParserKeyWords();
		java.util.Collections.sort(keyWordsList, new DescKwLenComparator());
		/*
		 * Evaluate position after sorting for the following keywords types number plus
		 * operator minus operator
		 *
		 * Above mentioned information is required when distinguishing between numbers
		 * (regexp) and operators
		 *
		 * For example
		 *
		 * 1-2 : two numbers and one operator, but -2 is also a valid number (-2)+3 :
		 * two number and one operator
		 */
		int numberKwId = -1;
		int plusKwId = -1;
		int minusKwId = -1;
		for (int kwId = 0; kwId < keyWordsList.size(); kwId++) {
			if (keyWordsList.get(kwId).wordTypeId == ParserSymbol.NUMBER_TYPE_ID)
				numberKwId = kwId;
			if (keyWordsList.get(kwId).wordTypeId == Operator.TYPE_ID) {
				if (keyWordsList.get(kwId).wordId == Operator.PLUS_ID)
					plusKwId = kwId;
				if (keyWordsList.get(kwId).wordId == Operator.MINUS_ID)
					minusKwId = kwId;
			}
		}
		initialTokens = new ArrayList<Token>();
		int expLen = expressionString.length();
		if (expLen == 0)
			return;
		/*
		 * Clearing expression string from spaces
		 */
		String newExpressionString = "";
		char c;
		char clag1 = 'a';
		int blankCnt = 0;
		int newExpLen = 0;
		for (int i = 0; i < expLen; i++) {
			c = expressionString.charAt(i);
			if ((c == ' ') || (c == '\n') || (c == '\r') || (c == '\t') || (c == '\f')) {
				blankCnt++;
			} else if (blankCnt > 0) {
				if (newExpLen > 0) {
					if (isNotSpecialChar(clag1))
						newExpressionString = newExpressionString + " ";
				}
				blankCnt = 0;
			}
			if (blankCnt == 0) {
				newExpressionString = newExpressionString + c;
				clag1 = c;
				newExpLen++;
			}
		}
		/*
		 * words list and tokens list
		 */
		if (newExpressionString.length() == 0)
			return;
		int lastPos = 0; /* position of the key word previously added */
		int pos = 0; /* current position */
		String tokenStr = "";
		int matchStatusPrev = NOT_FOUND; /* unknown key word (previous) */
		int matchStatus = NOT_FOUND; /* unknown key word (current) */
		KeyWord kw = null;
		String sub = "";
		String kwStr = "";
		char precedingChar;
		char followingChar;
		char firstChar;
		/*
		 * Check all available positions in the expression tokens list
		 */
		do {
			/*
			 * 1st step
			 *
			 * compare with the regExp for real numbers find the longest word which could be
			 * matched with the given regExp
			 */
			int numEnd = -1;
			/*
			 * Number has to start with digit or dot
			 */
			firstChar = newExpressionString.charAt(pos);
			if ((firstChar == '+') || (firstChar == '-') || (firstChar == '.') || (firstChar == '0')
					|| (firstChar == '1') || (firstChar == '2') || (firstChar == '3') || (firstChar == '4')
					|| (firstChar == '5') || (firstChar == '6') || (firstChar == '7') || (firstChar == '8')
					|| (firstChar == '9')) {
				for (int i = pos; i < newExpressionString.length(); i++) {
					/*
					 * Escaping if encountering char that can not be included in number
					 */
					if (i > pos) {
						c = newExpressionString.charAt(i);
						if ((c != '+') && (c != '-') && (c != '0') && (c != '1') && (c != '2') && (c != '3')
								&& (c != '4') && (c != '5') && (c != '6') && (c != '7') && (c != '8') && (c != '9')
								&& (c != '.') && (c != 'e') && (c != 'E'))
							break;
					}
					/*
					 * Checking if substring represents number
					 */
					String str = newExpressionString.substring(pos, i + 1);
					if (mXparser.regexMatch(str, ParserSymbol.DECIMAL_REG_EXP))
						numEnd = i;
				}
			}
			/*
			 * If number was found
			 */
			if (numEnd >= 0)
				if (pos > 0) {
					precedingChar = newExpressionString.charAt(pos - 1);
					if ((precedingChar != ' ') && (precedingChar != ',') && (precedingChar != ';')
							&& (precedingChar != '|') && (precedingChar != '&') && (precedingChar != '+')
							&& (precedingChar != '-') && (precedingChar != '*') && (precedingChar != '\\')
							&& (precedingChar != '/') && (precedingChar != '(') && (precedingChar != ')')
							&& (precedingChar != '=') && (precedingChar != '>') && (precedingChar != '<')
							&& (precedingChar != '~') && (precedingChar != '^') && (precedingChar != '#')
							&& (precedingChar != '%') && (precedingChar != '@') && (precedingChar != '!'))
						numEnd = -1;
				}
			if (numEnd >= 0)
				if (numEnd < newExpressionString.length() - 1) {
					followingChar = newExpressionString.charAt(numEnd + 1);
					if ((followingChar != ' ') && (followingChar != ',') && (followingChar != ';')
							&& (followingChar != '|') && (followingChar != '&') && (followingChar != '+')
							&& (followingChar != '-') && (followingChar != '*') && (followingChar != '\\')
							&& (followingChar != '/') && (followingChar != '(') && (followingChar != ')')
							&& (followingChar != '=') && (followingChar != '>') && (followingChar != '<')
							&& (followingChar != '~') && (followingChar != '^') && (followingChar != '#')
							&& (followingChar != '%') && (followingChar != '@') && (followingChar != '!'))
						numEnd = -1;
				}
			if (numEnd >= 0) {
				/*
				 * If preceding word was unknown
				 *
				 * For example: 'abc-2'
				 *
				 * number starts with '-', preceding word 'abc' is not known by the parser
				 */
				if ((matchStatusPrev == NOT_FOUND) && (pos > 0)) {
					/*
					 * add preceding word to the list of tokens as unknown key word word
					 */
					tokenStr = newExpressionString.substring(lastPos, pos);
					addToken(tokenStr, new KeyWord());
				}
				/*
				 * Check leading operators ('-' or '+')
				 *
				 * For example: '2-1' : 1(num) -(op) 2(num) = 1(num) -1+2 : -1(num) +(op) 2(num)
				 * = 1(num)
				 */
				firstChar = newExpressionString.charAt(pos);
				boolean leadingOp = true;
				if ((firstChar == '-') || (firstChar == '+')) {
					if (initialTokens.size() > 0) {
						Token lastToken = initialTokens.get(initialTokens.size() - 1);
						if (((lastToken.tokenTypeId == Operator.TYPE_ID) && (lastToken.tokenId != Operator.FACT_ID)
								&& (lastToken.tokenId != Operator.PERC_ID))
								|| (lastToken.tokenTypeId == BinaryRelation.TYPE_ID)
								|| (lastToken.tokenTypeId == BooleanOperator.TYPE_ID)
								|| (lastToken.tokenTypeId == BitwiseOperator.TYPE_ID)
								|| ((lastToken.tokenTypeId == ParserSymbol.TYPE_ID)
										&& (lastToken.tokenId == ParserSymbol.LEFT_PARENTHESES_ID)))
							leadingOp = false;
						else
							leadingOp = true;
					} else
						leadingOp = false;
				} else
					leadingOp = false;
				/*
				 * If leading operator was found
				 */
				if (leadingOp == true) {
					/*
					 * Add leading operator to the tokens list
					 */
					if (firstChar == '-')
						addToken("-", keyWordsList.get(minusKwId));
					if (firstChar == '+')
						addToken("+", keyWordsList.get(plusKwId));
					pos++;
				}
				/*
				 * Add found number to the tokens list
				 */
				tokenStr = newExpressionString.substring(pos, numEnd + 1);
				addToken(tokenStr, keyWordsList.get(numberKwId));
				/*
				 * change current position (just after the number ends)
				 */
				pos = numEnd + 1;
				lastPos = pos;
				/*
				 * Mark match status indicators
				 */
				matchStatus = FOUND;
				matchStatusPrev = FOUND;
			} else {
				/*
				 * If there is no number which starts with current position Check for known key
				 * words
				 */
				int kwId = -1;
				matchStatus = NOT_FOUND;
				do {
					kwId++;
					kw = keyWordsList.get(kwId);
					kwStr = kw.wordString;
					if (pos + kwStr.length() <= newExpressionString.length()) {
						sub = newExpressionString.substring(pos, pos + kwStr.length());
						if (sub.equals(kwStr))
							matchStatus = FOUND;
						/*
						 * If key word is known by the parser
						 */
						if (matchStatus == FOUND) {
							/*
							 * If key word is in the form of identifier then check preceding and following
							 * characters
							 */
							if ((kw.wordTypeId == RandomVariable.TYPE_ID) || (kw.wordTypeId == Unit.TYPE_ID)
									|| (kw.wordTypeId == CalculusOperator.TYPE_ID)) {
								/*
								 * Checking preceding character
								 */
								if (pos > 0) {
									precedingChar = newExpressionString.charAt(pos - 1);
									if ((precedingChar != ' ') && (precedingChar != ',') && (precedingChar != ';')
											&& (precedingChar != '|') && (precedingChar != '&')
											&& (precedingChar != '+') && (precedingChar != '-')
											&& (precedingChar != '*') && (precedingChar != '\\')
											&& (precedingChar != '/') && (precedingChar != '(')
											&& (precedingChar != ')') && (precedingChar != '=')
											&& (precedingChar != '>') && (precedingChar != '<')
											&& (precedingChar != '~') && (precedingChar != '^')
											&& (precedingChar != '#') && (precedingChar != '%')
											&& (precedingChar != '@') && (precedingChar != '!'))
										matchStatus = NOT_FOUND;
								}
								/*
								 * Checking following character
								 */
								if ((matchStatus == FOUND) && (pos + kwStr.length() < newExpressionString.length())) {
									followingChar = newExpressionString.charAt(pos + kwStr.length());
									if ((followingChar != ' ') && (followingChar != ',') && (followingChar != ';')
											&& (followingChar != '|') && (followingChar != '&')
											&& (followingChar != '+') && (followingChar != '-')
											&& (followingChar != '*') && (followingChar != '\\')
											&& (followingChar != '/') && (followingChar != '(')
											&& (followingChar != ')') && (followingChar != '=')
											&& (followingChar != '>') && (followingChar != '<')
											&& (followingChar != '~') && (followingChar != '^')
											&& (followingChar != '#') && (followingChar != '%')
											&& (followingChar != '@') && (followingChar != '!'))
										matchStatus = NOT_FOUND;
								}
							}
						}
					}
				} while ((kwId < keyWordsList.size() - 1) && (matchStatus == NOT_FOUND));
				/*
				 * If key word known by the parser was found
				 */
				if (matchStatus == FOUND) {
					/*
					 * if preceding word was not known by the parser
					 */
					if ((matchStatusPrev == NOT_FOUND) && (pos > 0)) {
						/*
						 * Add preceding word to the tokens list as unknown key word
						 */
						tokenStr = newExpressionString.substring(lastPos, pos);
						addToken(tokenStr, new KeyWord());
					}
					matchStatusPrev = FOUND;
					/*
					 * Add current (known by the parser) key word to the tokens list
					 */
					tokenStr = newExpressionString.substring(pos, pos + kwStr.length());
					if (!((kw.wordTypeId == ParserSymbol.TYPE_ID) && (kw.wordId == ParserSymbol.BLANK_ID)))
						addToken(tokenStr, kw);
					/*
					 * Remember position where last added word ends + 1
					 */
					lastPos = pos + kwStr.length();
					/*
					 * Change current position;
					 */
					pos = pos + kwStr.length();
				} else {
					/*
					 * Update preceding word indicator
					 */
					matchStatusPrev = NOT_FOUND;
					/*
					 * Increment position if possible
					 */
					if (pos < newExpressionString.length())
						pos++;
				}
			}
			/*
			 * while there is still something to analyse
			 */
		} while (pos < newExpressionString.length());
		/*
		 * If key word was not known by the parser and end with the string end it needs
		 * to be added to the tokens list as unknown key word
		 */
		if (matchStatus == NOT_FOUND) {
			tokenStr = newExpressionString.substring(lastPos, pos);
			addToken(tokenStr, new KeyWord());
		}
		/*
		 * Evaluate tokens levels
		 *
		 * token level identifies the sequance of parsing
		 */
		evaluateTokensLevels();
	}

	/**
	 * Evaluates tokens levels
	 */
	private void evaluateTokensLevels() {
		int tokenLevel = 0;
		Stack<TokenStackElement> tokenStack = new Stack<TokenStackElement>();
		boolean precedingFunction = false;
		if (initialTokens.size() > 0)
			for (int tokenIndex = 0; tokenIndex < initialTokens.size(); tokenIndex++) {
				Token token = initialTokens.get(tokenIndex);
				if ((token.tokenTypeId == CalculusOperator.TYPE_ID)) {
					tokenLevel++;
					precedingFunction = true;
				} else if ((token.tokenTypeId == ParserSymbol.TYPE_ID)
						&& (token.tokenId == ParserSymbol.LEFT_PARENTHESES_ID)) {
					tokenLevel++;
					TokenStackElement stackEl = new TokenStackElement();
					stackEl.precedingFunction = precedingFunction;
					tokenStack.push(stackEl);
					precedingFunction = false;
				} else
					precedingFunction = false;
				token.tokenLevel = tokenLevel;
				if ((token.tokenTypeId == ParserSymbol.TYPE_ID)
						&& (token.tokenId == ParserSymbol.RIGHT_PARENTHESES_ID)) {
					tokenLevel--;
					if (!tokenStack.isEmpty()) {
						TokenStackElement stackEl = tokenStack.pop();
						if (stackEl.precedingFunction == true)
							tokenLevel--;
					}
				}
			}
	}

	/**
	 * copy initial tokens lito to tokens list
	 */
	private void copyInitialTokens() {
		tokensList = new ArrayList<Token>();
		for (Token token : initialTokens) {
			tokensList.add(token.clone());
		}
	}

	private final String FUNCTION = "function";
	private final String ARGUMENT = "argument";
	private final String UNITCONST = "unit/const";
	private final String ERROR = "error";

	/**
	 * Tokenizes expression string and returns tokens list, including: string, type,
	 * level.
	 *
	 * @return Copy of initial tokens.
	 *
	 * @see Token
	 * @see mXparser#consolePrintTokens(List)
	 */
	public List<Token> getCopyOfInitialTokens() {
		List<Token> tokensListCopy = new ArrayList<Token>();
		if (expressionString.length() == 0)
			return tokensListCopy;
		tokenizeExpressionString();
		if (initialTokens.size() == 0)
			return tokensListCopy;
		Token token;
		for (int i = 0; i < initialTokens.size(); i++) {
			token = initialTokens.get(i);
			if (token.tokenTypeId == Token.NOT_MATCHED) {
				if (mXparser.regexMatch(token.tokenStr, ParserSymbol.unitOnlyTokenRegExp)) {
					token.looksLike = UNITCONST;
				} else if (mXparser.regexMatch(token.tokenStr, ParserSymbol.nameOnlyTokenRegExp)) {
					token.looksLike = ARGUMENT;
					if (i < initialTokens.size() - 1) {
						Token tokenNext = initialTokens.get(i + 1);
						if ((tokenNext.tokenTypeId == ParserSymbol.TYPE_ID)
								&& (tokenNext.tokenId == ParserSymbol.LEFT_PARENTHESES_ID))
							token.looksLike = FUNCTION;
					}
				} else {
					token.looksLike = ERROR;
				}
			}
			tokensListCopy.add(token.clone());
		}
		return tokensListCopy;
	}

	/**
	 * Returns missing user defined arguments names, i.e. sin(x) + cos(y) where x
	 * and y are not defined function will return x and y.
	 *
	 * @return Array of missing user defined arguments names - distinct strings.
	 */
	public String[] getMissingUserDefinedArguments() {
		List<Token> tokens = getCopyOfInitialTokens();
		List<String> missingArguments = new ArrayList<String>();
		for (Token t : tokens)
			if (t.looksLike.equals(ARGUMENT))
				if (!missingArguments.contains(t.tokenStr))
					missingArguments.add(t.tokenStr);
		int n = missingArguments.size();
		String[] missArgs = new String[n];
		for (int i = 0; i < n; i++)
			missArgs[i] = missingArguments.get(i);
		return missArgs;
	}

	/**
	 * Returns missing user defined units names, i.e. 2*[w] + [q] where [w] and [q]
	 * are not defined function will return [w] and [q].
	 *
	 * @return Array of missing user defined units names - distinct strings.
	 */
	public String[] getMissingUserDefinedUnits() {
		List<Token> tokens = getCopyOfInitialTokens();
		List<String> missingUnits = new ArrayList<String>();
		for (Token t : tokens)
			if (t.looksLike.equals(UNITCONST))
				if (!missingUnits.contains(t.tokenStr))
					missingUnits.add(t.tokenStr);
		int n = missingUnits.size();
		String[] missUnits = new String[n];
		for (int i = 0; i < n; i++)
			missUnits[i] = missingUnits.get(i);
		return missUnits;
	}

	/**
	 * Returns missing user defined functions names, i.e. sin(x) + fun(x,y) where
	 * fun is not defined function will return fun.
	 *
	 * @return Array of missing user defined functions names - distinct strings.
	 */
	public String[] getMissingUserDefinedFunctions() {
		List<Token> tokens = getCopyOfInitialTokens();
		List<String> missingFunctions = new ArrayList<String>();
		for (Token t : tokens)
			if (t.looksLike.equals(FUNCTION))
				if (!missingFunctions.contains(t.tokenStr))
					missingFunctions.add(t.tokenStr);
		int n = missingFunctions.size();
		String[] missFun = new String[n];
		for (int i = 0; i < n; i++)
			missFun[i] = missingFunctions.get(i);
		return missFun;
	}

	/*
	 * Text adjusting.
	 */
	private static final String getLeftSpaces(String maxStr, String str) {
		String spc = "";
		for (int i = 0; i < maxStr.length() - str.length(); i++)
			spc = spc + " ";
		return spc + str;
	}

	/*
	 * Text adjusting.
	 */
	private static final String getRightSpaces(String maxStr, String str) {
		String spc = "";
		for (int i = 0; i < maxStr.length() - str.length(); i++)
			spc = " " + spc;
		return str + spc;
	}

	/**
	 * Shows parsing (verbose mode purposes).
	 *
	 */
	private void showParsing(int lPos, int rPos) {
		mXparser.consolePrint(" ---> ");
		for (int i = lPos; i <= rPos; i++) {
			Token token = tokensList.get(i);
			if (token.tokenTypeId == ParserSymbol.NUMBER_TYPE_ID)
				mXparser.consolePrint(token.tokenValue + " ");
			else
				mXparser.consolePrint(token.tokenStr + " ");
		}
		mXparser.consolePrint(" ... ");
	}

	/**
	 * Gets help content.
	 *
	 * @return The help content.
	 */
	public String getHelp() {
		return getHelp("");
	}

	/**
	 * Searching help content.
	 *
	 * @param word
	 *            searching key word
	 *
	 * @return The help content.
	 */
	public String getHelp(String word) {
		keyWordsList = new ArrayList<KeyWord>();
		String helpStr = "Help content: \n\n";
		addParserKeyWords();
		helpStr = helpStr + getLeftSpaces("12345", "#") + "  " + getRightSpaces("01234567890123456789", "key word")
				+ getRightSpaces("                        ", "type")
				+ getRightSpaces("0123456789012345678901234567890123456789012345", "syntax")
				+ getRightSpaces("012345", "since") + "description" + "\n";
		helpStr = helpStr + getLeftSpaces("12345", "-") + "  " + getRightSpaces("01234567890123456789", "--------")
				+ getRightSpaces("                        ", "----")
				+ getRightSpaces("0123456789012345678901234567890123456789012345", "------")
				+ getRightSpaces("012345", "-----") + "-----------" + "\n";

		java.util.Collections.sort(keyWordsList, new KwTypeComparator());
		int keyWordsNumber = keyWordsList.size();
		String type, kw;
		String line;
		for (int keyWordIndex = 0; keyWordIndex < keyWordsNumber; keyWordIndex++) {
			KeyWord keyWord = keyWordsList.get(keyWordIndex);
			type = "";
			kw = keyWord.wordString;
			switch (keyWord.wordTypeId) {
			case ParserSymbol.TYPE_ID:
				type = ParserSymbol.TYPE_DESC;
				break;
			case ParserSymbol.NUMBER_TYPE_ID:
				type = "number";
				kw = "_number_";
				break;
			case Operator.TYPE_ID:
				type = Operator.TYPE_DESC;
				break;
			case BooleanOperator.TYPE_ID:
				type = BooleanOperator.TYPE_DESC;
				break;
			case BinaryRelation.TYPE_ID:
				type = BinaryRelation.TYPE_DESC;
				break;
			case CalculusOperator.TYPE_ID:
				type = CalculusOperator.TYPE_DESC;
				break;
			case RandomVariable.TYPE_ID:
				type = RandomVariable.TYPE_DESC;
				break;
			case Unit.TYPE_ID:
				type = Unit.TYPE_DESC;
				break;
			case BitwiseOperator.TYPE_ID:
				type = BitwiseOperator.TYPE_DESC;
				break;
			}
			line = getLeftSpaces("12345", Integer.toString(keyWordIndex + 1)) + ". "
					+ getRightSpaces("01234567890123456789", kw)
					+ getRightSpaces("                        ", "<" + type + ">")
					+ getRightSpaces("0123456789012345678901234567890123456789012345", keyWord.syntax)
					+ getRightSpaces("012345", keyWord.since) + keyWord.description + "\n";
			if ((line.toLowerCase().indexOf(word.toLowerCase()) >= 0)) {
				helpStr = helpStr + line;
			}
		}
		return helpStr;
	}

	/**
	 * Returns list of key words known to the parser
	 *
	 * @return List of keywords known to the parser.
	 *
	 * @see KeyWord
	 * @see KeyWord#wordTypeId
	 * @see Expression#getHelp()
	 */
	public List<KeyWord> getKeyWords() {
		return getKeyWords("");
	}

	/**
	 * Returns list of key words known to the parser
	 *
	 * @param query
	 *            Give any string to filter list of key words against this string.
	 *            User more precise syntax: str=tokenString, desc=tokenDescription,
	 *            syn=TokenSyntax, sin=tokenSince, wid=wordId, tid=wordTypeId to
	 *            narrow the result.
	 *
	 * @return List of keywords known to the parser filter against query string.
	 *
	 * @see KeyWord
	 * @see KeyWord#wordTypeId
	 * @see Expression#getHelp(String)
	 */
	public List<KeyWord> getKeyWords(String query) {
		keyWordsList = new ArrayList<KeyWord>();
		List<KeyWord> kwyWordsToReturn = new ArrayList<KeyWord>();
		addParserKeyWords();
		java.util.Collections.sort(keyWordsList, new KwTypeComparator());
		String line;
		for (KeyWord kw : keyWordsList) {
			line = "str=" + kw.wordString + " " + "desc=" + kw.description + " " + "syn=" + kw.syntax + " " + "sin="
					+ kw.since + " " + "wid=" + kw.wordId + " " + "tid=" + kw.wordTypeId;
			if ((line.toLowerCase().indexOf(query.toLowerCase()) >= 0))
				kwyWordsToReturn.add(kw);
		}
		return kwyWordsToReturn;
	}

	/**
	 *
	 * @param info
	 * @param withExpressionString
	 */
	private void printSystemInfo(String info, boolean withExpressionString) {
		if (withExpressionString)
			mXparser.consolePrint(
					/* "[" + this + "]" + */ "[" + description + "]" + "[" + expressionString + "] " + info);
		else
			mXparser.consolePrint(/* "[" + this + "]" + */ info);
	}

	/**
	 * Expression cloning.
	 */
	@Override
	protected Expression clone() {
		Expression newExp = new Expression(this);
		if ((initialTokens != null) && (initialTokens.size() > 0))
			newExp.initialTokens = createInitialTokens(0, initialTokens.size() - 1, initialTokens);
		return newExp;
	}
}