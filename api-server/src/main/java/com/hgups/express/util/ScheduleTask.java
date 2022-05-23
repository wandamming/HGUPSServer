package com.hgups.express.util;

/**
 * @author fanc
 * 2020/12/5-10:30
 */

/**
 * 定时任务抽象类(父类)
 * 具体执行方法请继承该类，然后重写run方法
 */
public abstract class ScheduleTask implements Runnable {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ScheduleTask(String id) {
        this.id = id;
    }

    @Override
    public abstract void run();


}


