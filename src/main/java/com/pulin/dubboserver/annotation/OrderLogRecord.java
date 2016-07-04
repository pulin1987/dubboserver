package com.pulin.dubboserver.annotation;

import java.lang.annotation.*;

/**
 * Created by pulin on 2016/7/1 0001.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrderLogRecord {
}
