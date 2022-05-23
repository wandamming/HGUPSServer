package com.hgups.express.util;

import com.hgups.express.domain.User;
import com.hgups.express.exception.AuditException;
import com.hgups.express.exception.NoAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

@Slf4j
public final class ShiroUtil {

//    public static Long getLoginUserId() {
//        return 1L;
//    }

    public static Long getLoginUserId() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            log.error(" 还未登录");
            throw new NoAuthException();
        }
        User principal = (User) subject.getPrincipal();
        int state = principal.getState();
        if (state!=1) {
            log.error(" 未审核-----");
            throw new AuditException();
        }
//        if (!UserIdCache.userIdExist(principal.getId())) {
//            subject.logout();
//            return null;
//        }
        log.info("登录通过——————");
        return principal.getId();
    }

    public static User getLoginUser(){
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            log.error("还未登录");
            throw new NoAuthException();
        }
        return (User) subject.getPrincipal();
    }

}
