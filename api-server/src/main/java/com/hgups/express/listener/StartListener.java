package com.hgups.express.listener;

import cn.hutool.core.io.FileUtil;
import com.hgups.express.business.ShipPartnerFile;
import com.hgups.express.business.ShipServiceFile;
import com.hgups.express.util.LabelUtils;
import com.hgups.express.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("项目启动成功");
        try {
            LabelUtils.initTrackNo();
            ShipServiceFile.initSSFNoFile();
            ShipPartnerFile.initSpEventFile(PathUtils.resDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}