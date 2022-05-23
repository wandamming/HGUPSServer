package com.hgups.express.controller.waybillmg;

import com.hgups.express.mapper.WayBillMapper;
import com.hgups.express.service.waybillmgi.WayBillVoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;


@Api(description = "自动更新运单轨迹")
@Slf4j
@RestController
@RequestMapping("/automaticWayBillTrajectory")
public class WaybillTrajectoryController implements ServletContextAware {

    @Resource
    private WayBillMapper wayBillMapper;
    private final int trajctoryNumber = 10;//更新运单轨迹一次请求的运单数量
    @Resource
    private WayBillVoService wayBillVoService;

    //自动更新运单追踪状态
    @Override
    public void setServletContext(ServletContext servletContext) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒

        Date time = calendar.getTime();         // 得出执行任务的时间,此处为5：00：00
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (time.before(new Date())) {
            time = this.addDay(time, 1);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    //开启线程自动更新运单轨迹
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> trackingNumbers = wayBillMapper.getTimingModifyTrackNumbers();
                            StringBuilder strUrl = new StringBuilder();
                            strUrl.append("https://secure.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=<TrackRequest USERID=\"707HGUPS0501\">");
                            List<String> strUrlList = new ArrayList<>();

                            int i = 0;
                            for (String str : trackingNumbers) {
                                i++;
                                strUrl.append("<TrackID ID=\"" + str + "\"></TrackID>");
                                if (i % trajctoryNumber == 0 || i == trackingNumbers.size()) {
                                    strUrl.append("</TrackRequest>");
                                    strUrlList.add(strUrl.toString());
                                    strUrl = new StringBuilder();
                                    strUrl.append("https://secure.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=<TrackRequest USERID=\"707HGUPS0501\">");
                                }
                            }
                            for (String str : strUrlList) {
                                i++;
                                try {
                                    String errorUrl = wayBillVoService.timingCreateBase(str);
                                    if ("error".equals(errorUrl)) {
                                        for (int j = 0; j < 10; j++) {
                                            wayBillVoService.timingCreateBase(str);
                                        }
                                    }
                                } catch (RejectedExecutionException e) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ite) {
                                        log.error("任务执行sleep错误：", ite);
                                    }
                                }
                            }
                        }
                    });
                    thread.start();
                    Thread.sleep(2 * 60 * 60 * 1000);
                    thread.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, time, 24 * 60 * 60 * 1000);// 这里设将延时每天固定执行
    }

    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}
