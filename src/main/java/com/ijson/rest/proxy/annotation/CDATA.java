package com.ijson.rest.proxy.annotation;

import java.lang.annotation.*;

/**
 * Created by cuiyongxu on 17/7/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface CDATA {
    boolean CDATA() default false;

    String desc() default "";
}
