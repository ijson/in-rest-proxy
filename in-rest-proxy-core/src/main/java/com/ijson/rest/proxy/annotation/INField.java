package com.ijson.rest.proxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cuiyongxu on 17/7/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface INField {
    /**
     * field name
     *
     * @return
     */
    String name() default "";

    /**
     * simple description
     *
     * @return
     */
    String desc() default "";

    /**
     * remark information
     *
     * @return
     */
    String remark() default "";
    /**
     * required
     *
     * @return
     */
    boolean required() default false;

    String requiredMessage() default "[ {0} ] is a required field";
}
