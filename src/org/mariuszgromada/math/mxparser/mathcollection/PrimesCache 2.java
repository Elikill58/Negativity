/*
 * @(#)PrimesCache.java        4.4.2   2020-01-25
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
 * Class for generating prime numbers cache using
 * Eratosthenes Sieve.
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
 * @version        4.4.2
 */
public class PrimesCache {
	/**
	 * Default range of integer to store in cache
	 */
	private static final int DEFAULT_MAX_NUM_IN_CACHE = 10000000;
	/**
	 * Empty cache status
	 */
	private static final boolean CACHE_EMPTY = false;
	/**
	 * Cache ready to use
	 */
	static final boolean CACHING_FINISHED = true;
	/**
	 * Indicator if given number is a prime
	 */
	private static final int IS_PRIME = 1;
	/**
	 * Indicator if given number is not a prime
	 */
	private static final int IS_NOT_PRIME = 0;
	/**
	 * Indicator that the value is not stored
	 * in cache
	 */
	private static final int NOT_IN_CACHE = -1;
	/**
	 * Primes between 0 ... and ... maximumNumberInCache
	 * will be cached
	 */
	int maxNumInCache;
	/**
	 * Number of cached prime numbers
	 */
	int numberOfPrimes;
	/**
	 * Time in seconds showing
	 * how long did it take to finalize prime numbers
	 * caching.
	 */
	double computingTime;
	/**
	 * Caching process status
	 */
	boolean cacheStatus;
	/**
	 * Integers table to store number and indicate
	 * whether they are prime or not
	 */
	boolean[] isPrime;
	/**
	 * Internal flag marking that primes cache initialization was successful;
	 */
	boolean initSuccessful;
	/**
	 * Eratosthenes Sieve implementation
	 */
	private void EratosthenesSieve() {
		long startTime = System.currentTimeMillis();
		try {
			int size = maxNumInCache+1;
			if (size <= 0) {
				numberOfPrimes = 0;
				maxNumInCache = 0;
				initSuccessful = false;
				long endTime = System.currentTimeMillis();
				computingTime = (endTime - startTime)/1000.0;
				return;
			}
			isPrime = new boolean[size];
			numberOfPrimes = 0;
			/*
			 * Initially assume all integers are primes
			 */
			isPrime[0] = false;
			isPrime[1] = false;
			for (int i = 2; i <= maxNumInCache; i++) {
				isPrime[i] = true;
				if (mXparser.isCurrentCalculationCancelled()) return;
			}
			/*
			 * Sieve of Eratosthenes - marking non-primes
			 */
	        for (int i = 2; i*i <= maxNumInCache; i++) {
	        	if (mXparser.isCurrentCalculationCancelled()) return;
	            if (isPrime[i] == true)
	                for (int j = i; i*j <= maxNumInCache; j++) {
	                    isPrime[i*j] = false;
	                    if (mXparser.isCurrentCalculationCancelled()) return;
	                }
	        }
			initSuccessful = true;
		} catch (OutOfMemoryError e) {
			initSuccessful = false;
		} finally {
			long endTime = System.currentTimeMillis();
			computingTime = (endTime - startTime)/1000.0;
		}
	}
	/**
	 * Counting found primes
	 */
	private void countPrimes() {
		for (int i = 0; i <= maxNumInCache; i++) {
			if (isPrime[i] == true) numberOfPrimes++;
			if (mXparser.isCurrentCalculationCancelled()) return;
		}
	}
	/**
	 * Default constructor - setting prime cache for a default range if integers
	 */
	public PrimesCache() {
		initSuccessful = false;
		cacheStatus = CACHE_EMPTY;
		maxNumInCache = DEFAULT_MAX_NUM_IN_CACHE;
		EratosthenesSieve();
		if (initSuccessful) {
			countPrimes();
			cacheStatus = CACHING_FINISHED;
		} else {
			maxNumInCache = 0;
			numberOfPrimes = 0;
		}
	}
	
	/**
	 * Returns computing time of Eratosthenes Sieve
	 * @return Computing time in seconds
	 */
	public double getComputingTime() {
		return computingTime;
	}
	/**
	 * Returns cache status
	 * @return PrimesCache.CACHE_EMPTY or PrimesCache.CACHING_FINISHED;
	 */
	public boolean getCacheStatus() {
		return cacheStatus;
	}
	/**
	 * Returns number of found primes.
	 * @return Number of found primes.
	 */
	public int getNumberOfPrimes() {
		return numberOfPrimes;
	}
	/**
	 * Returns cache range.
	 * @return Maximum integera number in cache/
	 */
	public int getMaxNumInCache() {
		return maxNumInCache;
	}
	/**
	 * Check whether given number is prime
	 * @param n Given integer number.
	 * @return PrimesCache.IS_PRIME or PrimesCache.IS_NOT_PRIME or PrimesCache.NOT_IN_CACHE
	 */
	public int primeTest(int n) {
		if (n <= 1) return IS_NOT_PRIME;
		if ( (n <= maxNumInCache) && (cacheStatus = CACHING_FINISHED) )
			if ( isPrime[n] == true)
				return IS_PRIME;
			else
				return IS_NOT_PRIME;
		else
			return NOT_IN_CACHE;
	}
	/**
	 * Returns true in case when primes cache initialization was successful,
	 * otherwise returns false.
	 *
	 * @return Returns true in case when primes cache initialization was successful,
	 * otherwise returns false.
	 */
	public boolean isInitSuccessful() {
		return initSuccessful;
	}
	
}