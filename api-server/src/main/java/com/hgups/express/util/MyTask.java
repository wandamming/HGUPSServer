package com.hgups.express.util;

/**
 * @author fanc
 * 2020/12/5-10:31
 */
public class MyTask extends ScheduleTask {
    public MyTask(String id) {
        super(id);
    }

    @Override
    public void run() {
        //todo Your operation
        for (int i = 0; i < 100; i++) {
            System.out.println("执行更新运单轨迹。。。。。。"+i+"。。。。。。");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

