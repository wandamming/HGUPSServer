package com.hgups.express.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("rawtypes")
@Slf4j
public final class DomainCopyUtil {

    /**
     * 持有Dozer单例, 避免重复创建DozerMapper消耗资源.
     */
    private DomainCopyUtil() {
    }

    public static <T> T map(Object source, Class<T> destinationClass) {
        T t;
        try {
            t = destinationClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.info("source is  {}, destinationClass is {}", source, destinationClass);
            log.error(e.getMessage(), e);
            throw new RuntimeException("instance destination bean error.");
        }
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), destinationClass, false);
        beanCopier.copy(source, t, null);
        return t;
    }

    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();

        for (Object sourceObject : sourceList) {
            T destinationObject;
            try {
                destinationObject = destinationClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.info("sourceObject is  {}, destinationClass is {}", sourceObject, destinationClass);
                log.error(e.getMessage(), e);
                throw new RuntimeException("instance destination bean error.");
            }
            BeanCopier beanCopier = BeanCopier.create(sourceObject.getClass(), destinationClass, false);
            beanCopier.copy(sourceObject, destinationObject, null);
            destinationList.add(destinationObject);
        }

        return destinationList;
    }

    public static void copy(Object source, Object destinationObject) {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), destinationObject.getClass(), false);
        beanCopier.copy(source, destinationObject, null);
    }

}
