package com.automatedTradingApplication.scheduler;

import com.automatedTradingApplication.alpaca.AlpacaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskExecutor {

    Logger logger = LoggerFactory.getLogger(ScheduledTaskExecutor.class);

    @Autowired
    AlpacaClient alpacaClient;

    @Autowired
    ScheduledJobRepository scheduledJobRepository;

    public void executeJobs() throws Exception {
        List<ScheduledJob> allJobs = scheduledJobRepository.findAll();
        List<ScheduledJob> scheduledJobs = scheduledJobRepository.findByScheduledTimeLessThanEqual(LocalDateTime.now());
        logger.info("{} out of {} jobs scheduled to be executed now", scheduledJobs.size(), allJobs.size());
        for (ScheduledJob job: scheduledJobs){
            if (job.isBuy()){
                alpacaClient.partitionedBuy(job.getSymbol(), job.getQty(), true);
            }else if  (job.isSale()){
                alpacaClient.partitionedSale(job.getSymbol(), job.getQty(), true);
            }
        }
        scheduledJobRepository.deleteAll(scheduledJobs);
    }
}