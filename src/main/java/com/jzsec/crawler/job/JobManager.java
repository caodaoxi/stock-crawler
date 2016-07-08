package com.jzsec.crawler.job;

import com.espertech.esper.client.EPRuntime;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by caodaoxi on 16-7-7.
 */
public class JobManager {
    private EPRuntime epRuntime = null;

    public JobManager(EPRuntime epRuntime) {
        this.epRuntime = epRuntime;
    }

    public void start() {
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = factory.getScheduler();
            //        CronTrigger trigger = new CronTrigger();
            scheduler.start();
            JobDetail job = newJob(CrawlerJob.class)
                    .withIdentity("job1", "group1")
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("job1", "group1")
//                    .withSchedule(cronSchedule("* */5 * ? * *"))
                    .startNow()
                    .build();
            job.getJobDataMap().put("epRuntime", this.epRuntime);
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
