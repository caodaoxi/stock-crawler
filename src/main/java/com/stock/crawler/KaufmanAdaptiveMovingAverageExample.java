package com.stock.crawler;

import com.stock.crawler.config.Configuration;
import com.stock.crawler.utils.DBPoolContext;
import com.stock.crawler.utils.DBUtils;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by caodaoxi on 16-6-8.
 */
public class KaufmanAdaptiveMovingAverageExample {

    public static final int TOTAL_PERIODS = 100;

    /**
     * The number of periods to average together.
     */
    public static final int PERIODS_AVERAGE = 30;

    public static void main(String[] args) {
        double[] closePrice = new double[TOTAL_PERIODS];
        double[] out = new double[TOTAL_PERIODS];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        for (int i = 0; i < closePrice.length; i++) {
            closePrice[i] = (double) i;
        }
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
//            closePrice = DBUtils.getClosePrice("600030", 100030, con);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(con);
        }

        Core c = new Core();
        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        double macd[]   = new double[closePrice.length];
        double signal[] = new double[closePrice.length];
        double hist[]   = new double[closePrice.length];

        RetCode retCode = c.macd(0, closePrice.length-1, closePrice, 12, 26, 9, outBegIdx, outNbElement, macd, signal, hist);
        double ema12[] = new double[closePrice.length];
        int lookback = c.emaLookback(12);
        retCode = c.ema(0,closePrice.length-1,closePrice,12,outBegIdx,outNbElement,ema12);

        double ema26[] = new double[closePrice.length];
        lookback = c.emaLookback(26);
        retCode = c.ema(0,closePrice.length-1,closePrice,26,outBegIdx,outNbElement,ema26);

        double ema9[] = new double[closePrice.length];
        lookback = c.emaLookback(9);
        retCode = c.ema(0,closePrice.length-1,closePrice,9,outBegIdx,outNbElement,ema9);
        System.out.println(retCode);
////        RetCode retCode=c.movingAverage(0,closePrice.length-1,closePrice,PERIODS_AVERAGE, MAType.Kama,begin,length,out);
//
//
//        if (retCode == RetCode.Success) {
//            System.out.println("Output Begin:" + begin.value);
//            System.out.println("Output End:" + length.value);
//
//            for (int i = begin.value; i <= length.value; i++) {
//                StringBuilder line = new StringBuilder();
//                line.append("Period #");
//                line.append(i);
//                line.append(" close= ");
//                line.append(closePrice[i]);
//                line.append(" mov avg=");
//                line.append(out[i-begin.value]);
//                System.out.println(line.toString());
//            }
//        }
//        else {
//            System.out.println("Error");
//        }
    }

}
