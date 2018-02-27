package com.avantir.wpos.utils;

import android.text.TextUtils;

/**
 * Numerical processing tools function
 * @author baoxl
 *
 */
public class NumberUtil {
	private NumberUtil() {}

	/**
	 * String to long numeric value
	 * @param sLong Numeric string
	 * @return Numerical representation
	 */
	public static long parseLong(String sLong) {
		if (TextUtils.isEmpty(sLong)) {
			return 0;
		}

		long result = 0;
		try {
			result = Long.parseLong(sLong);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * String to integer numeric representation
	 * @param sInt Numeric string
	 * @return Numerical representation
	 */
	public static int parseInt(String sInt) {
		if (TextUtils.isEmpty(sInt)) {
			return 0;
		}

		int result = 0;
		try {
			result = Integer.parseInt(sInt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return result;
	}
}
