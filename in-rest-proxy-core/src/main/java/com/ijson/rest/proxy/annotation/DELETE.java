package com.ijson.rest.proxy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cuiyongxu on 07/01/2017.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {
    String value() default "";

    String desc() default "";

    String contentType() default "";

    String codec() default "";
}
