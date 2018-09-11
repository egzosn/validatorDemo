package com.egzosn.validator.error;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  消息状态码
 *
 *
 * @author egan
 *         email egzosn@gmail.com
 *         date 2014/10/24.16:08
 */
 @Target( {  FIELD ,PARAMETER})
 @Retention(RUNTIME)
public @interface MsgCode {

    int value() default -1;
}
