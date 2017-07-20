package com.ijson.rest.proxy.annotation;

import java.lang.annotation.*;

/**
 * Data structures are used in Resource to describe data in a parameter list.<br>
 * <code>@Body</code> can only be used with objects later, but not collections such as Map can be used<br>
 * Example:<br>
 * <code>@POST</code>(value = &quot;/pay/unifiedorder&quot;, desc = &quot;unifiedorder&quot;, contentType = &quot;application/json&quot;)<br>
 * Unifiedorder.Result unifiedorder(@Body Unifiedorder.Arg arg); <br>
 * Created by cuiyongxu on 05/01/2017.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
}
