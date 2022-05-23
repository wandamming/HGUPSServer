package com.hgups.express.constant;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {

    public static final int FAILED_CODE = 199;
    public static final int SUCCESS_CODE = 200;
    public static final int ACCESS_TOKEN_NOT_FOUND_CODE = 201;
    public static final int TOKEN_INVALID_CODE = 202;

    public static final Integer NOTICE_LOGISTICS=1;// 1：物流通知
    public static final Integer NOTICE_TASK=2;// 2：任务通知
    public static final Integer NOTICE_PRODUCT=3;// 3：产品通知
    public static final Integer NOTICE_SYSTEM=4;// 4：系统通知

    //语音播报
    public static final String VOICE_BROADCAST_ONE="https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-exist-girl.mp3";
    public static final String VOICE_BROADCAST_TWO="https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-intercept-girl.mp3";
    public static final String VOICE_BROADCAST_THREE="https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-entry-dismatch-girl.mp3";
    public static final String VOICE_BROADCAST_FOUR="https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-service-type-dismatch-girl.mp3";
    public static final String VOICE_BROADCAST_FIVE="https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-not-found-girl.mp3";
    public static final String VOICE_BROADCAST_SIX="https://www.onezerobeat.com/hgups/static/audio/ship-batch-success-girl.mp3";

    //预上线
    public static final int SHIP_FILE_NOT_MATCH_BILL_CODE = 300;
    public static final int SHIP_FILE_INVALID_STATE = 301;

    //SSF
    public static final int SSF_NOT_MATCH_BILL_CODE = 400;

    //入境口岸
    public static final int PORT_EXIST = 500;

    public static final Map<Integer, String> sCodeMsgMap = new HashMap();

    static {
        sCodeMsgMap.put(FAILED_CODE, " Fail ... ");
        sCodeMsgMap.put(SUCCESS_CODE, " Success... ");
        sCodeMsgMap.put(ACCESS_TOKEN_NOT_FOUND_CODE, "  not found access token... ");
        sCodeMsgMap.put(TOKEN_INVALID_CODE, " Invalid token... ");

        sCodeMsgMap.put(SHIP_FILE_NOT_MATCH_BILL_CODE, "预先上线失败：系统中未找到你的输入的运单号.");
        sCodeMsgMap.put(SHIP_FILE_INVALID_STATE, "预先上线失败：提交了无效的预先上状态");
        sCodeMsgMap.put(SSF_NOT_MATCH_BILL_CODE, "SSF失败：系统中未找到你的输入的运单号.");
        sCodeMsgMap.put(PORT_EXIST, "入境口岸已存在");
    }

    public static String getMsg(int code) {
        if (sCodeMsgMap.containsKey(code)) {
            return sCodeMsgMap.get(code);
        } else {
            return " Unknown ";
        }
    }


}
