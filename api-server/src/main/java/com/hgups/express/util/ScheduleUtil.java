package com.hgups.express.util;

/**
 * @author fanc
 * 2020/12/5-10:31
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务工具类
 *
 */
@Slf4j
public class ScheduleUtil {

    private static ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    private static Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();
    static{
        threadPoolTaskScheduler.initialize();
        log.info("初始化线程池...");
    }
    /**
     * 启动某定时任务，到时间点就运行一次
     * @param scheduleTask
     * @param startTime
     */
    public static void start(ScheduleTask scheduleTask, Date startTime){
        if (isExist(scheduleTask.getId())){
            log.info("启动定时任务"+ scheduleTask.getId()+"失败，任务已存在");
            return;
        }
        ScheduledFuture<?>scheduledFuture = threadPoolTaskScheduler.schedule(scheduleTask,startTime);
        scheduledFutureMap.put(scheduleTask.getId(),scheduledFuture);
        log.info("启动定时任务"+ scheduleTask.getId()+"，执行时间为"+ startTime);
    }

    /**
     * 启动某定时任务，以固定周期运行
     * @param scheduleTask
     * @param period
     */
    public static void start(ScheduleTask scheduleTask, Date date,long period){
        if (isExist(scheduleTask.getId())){
            log.info("启动定时任务"+ scheduleTask.getId()+"失败，任务已存在");
            return;
        }
        ScheduledFuture<?>scheduledFuture = threadPoolTaskScheduler.scheduleAtFixedRate(scheduleTask,date,period);
        scheduledFutureMap.put(scheduleTask.getId(),scheduledFuture);
        log.info("启动定时任务" + scheduleTask.getId() + "，执行周期为" + period + "毫秒");
    }

    /**
     * 取消某定时任务
     * @param scheduleTask*/
    public static void cancel(ScheduleTask scheduleTask){
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(scheduleTask.getId());
        if(scheduledFuture != null && !scheduledFuture.isCancelled()){
            scheduledFuture.cancel(false);
        }
        scheduledFutureMap.remove(scheduleTask.getId());
        log.info("取消定时任务"+ scheduleTask.getId());
    }
    /**
     * 修改定时任务执行时间
     * @param scheduleTask
     * @param startTime
     */
    public static void reset(ScheduleTask scheduleTask,Date startTime){
        //先取消定时任务
        String id = scheduleTask.getId();
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(id);
        if(scheduledFuture != null && !scheduledFuture.isCancelled()){
            scheduledFuture.cancel(false);
        }
        scheduledFutureMap.remove(id);
        //然后启动新的定时任务
        scheduledFuture = threadPoolTaskScheduler.schedule(scheduleTask,startTime);
        scheduledFutureMap.put(id,scheduledFuture);
        log.info("重置定时任务"+ id+"，执行时间为"+ startTime);
    }

    /**
     * 判断某个定时任务是否存在或已经取消
     * @param id
     */
    public static Boolean isExist(String id) {
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(id);
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            return true;
        }
        return false;
    }

    /**
     * 取消某定时任务根据名字
     * @param
     * */
    public static void cancelById(String id){
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(id);
        if(scheduledFuture != null && !scheduledFuture.isCancelled()){
            scheduledFuture.cancel(false);
        }
        scheduledFutureMap.remove(id);
        log.info("取消定时任务"+ id);
    }
}


