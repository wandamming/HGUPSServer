package com.hgups.express.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fanc
 * 2020/12/5-10:32
 */
public class TaskTest {
    public static void main(String[] args) throws Exception {
        //任务1
       /* ScheduleTask helloTask1 = new MyTask("task1");//new一个具体的执行任务
        ScheduleUtil.start(helloTask1, new Date(System.currentTimeMillis() + 5000));//5秒后执行一次
        ScheduleUtil.reset(helloTask1, new Date(System.currentTimeMillis() + 10000));//修改时间，10秒后执行*/

       //启动任务的时间戳--启动时间加一天
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date(startTime));
        //设置启动时间点
        format = format + " " + "11:54:00";
        System.out.println(format);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=sdf1.parse(format);
        //任务2
        ScheduleTask scheduleTask = new MyTask("Timing_Task___________1");//new一个具体的执行任务
        ScheduleUtil.start(scheduleTask, date,2000);//每天执行一次
        Thread.sleep(4000);
        ScheduleUtil.cancel(scheduleTask);
    /*    System.out.println("===========取消定时任务===========");
        ScheduleUtil.cancel(scheduleTask);//取消定时任务
        Thread.sleep(5000);
        System.out.println("============================");
        ScheduleUtil.start(scheduleTask, date,2000);//每天执行一次*/
    }

    public void start111(){
        //启动任务的时间戳--启动时间加一天
        long startTime = System.currentTimeMillis() + 24*60*60*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date(startTime));
        //设置启动时间点
        format = format + " " + "11:08:00";
        System.out.println(format);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf1.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //任务2
        ScheduleTask scheduleTask = new MyTask("Timing_Task");//new一个具体的执行任务
        ScheduleUtil.start(scheduleTask, date,24*60*60*1000);//每天执行一次
        ScheduleUtil.cancel(scheduleTask);//取消定时任务
    }
}



