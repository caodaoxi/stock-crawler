package com.stock.engine;

import com.espertech.esper.client.UpdateListener;
import com.jzsec.crawler.job.CrawlerJob;
import com.jzsec.crawler.job.JobManager;
import com.jzsec.strategy.BuyListener;
import com.jzsec.strategy.EPLManager;
import com.jzsec.strategy.SaleListener;
import org.quartz.*;


/**
 * Created by caodaoxi on 16-7-6.
 */
public class StrategyEngine {

    public static void main(String[] args) throws SchedulerException {
        UpdateListener bugListener = new BuyListener();
        UpdateListener saleListener = new SaleListener();
        EPLManager eplManager = new EPLManager(bugListener, saleListener);
        JobManager jobManager = new JobManager(eplManager.getEPRuntime());
        jobManager.start();
    }
}
