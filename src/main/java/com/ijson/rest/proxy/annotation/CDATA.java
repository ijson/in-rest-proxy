package com.ijson.rest.proxy.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * When interacting with the server interface, if the server wants an XML type of message,
 * and there are too many special characters in our message, we need to add a CDATA tag at
 * this time. The purpose of this annotation is that Of the addition of this annotation in
 * your field, the generated XML will automatically add <code>@CDATA</code> tag, of course, it needs and
 * <code>XmlUtil.toXml (Object obj)</code>
 * </p>
 * Created by cuiyongxu on 17/7/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface CDATA {
    boolean CDATA() default false;

    String desc() default "";
}
