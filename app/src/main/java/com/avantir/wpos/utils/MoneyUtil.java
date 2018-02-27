package com.avantir.wpos.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

public class MoneyUtil {

    private static DecimalFormat doubleDF = new DecimalFormat("#0.00");

    public static String formatDouble2Str4Money(double d) {
        return doubleDF.format(d);
    }

    public static String kobo2Naira(long kobo) {
        //BigDecimal bigDecimal = new BigDecimal(kobo);
        //BigDecimal hundred = new BigDecimal(100);
        //MathContext mc = new MathContext(2, RoundingMode.HALF_EVEN);
        //return bigDecimal.divide(hundred, mc).toString();
        return formatDouble2Str4Money(kobo / 100.00);
    }

    public static Double koboTrans2Naira(Long kobo) {
        return Double.parseDouble(kobo2Naira(kobo));
    }

    public static String toCent(String dollar) {
        String cent = "";
        long cents = 0;

        if (TextUtils.isEmpty(dollar)) {
            cents = 0;
        } else {
            int index = dollar.indexOf(".");
            if (index >= 0) {
                int gap = dollar.length() - index - 1;
                if (gap == 0) {
                    cent = dollar + "00";
                } else if (gap == 1) {
                    cent = dollar.replace(".", "") + "0";
                } else if (gap == 2) {
                    cent = dollar.replace(".", "");
                } else {
                    cent = dollar.substring(0, index + 3).replace(".", "");
                }
            } else {
                cent = dollar + "00";
            }
            cents = NumberUtil.parseLong(cent);
        }

        return String.format(Locale.US, "%012d", cents);
    }
    /**
     * Convert the Naira as a unit (multiply 100)
     *
     * @param amount
     * @return
     */
    public static long naira2Kobo(double amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).longValue();
    }

}
