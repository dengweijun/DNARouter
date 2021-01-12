package com.ben.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注入
 * 在跳转之后的activity中，需要接受的参数可使用此注解，注意：此参数不可用private修饰！！！
 * 相当于调用了：fieldName = getIntent().getStringExtra("fieldName");
 *
 * @author: BD
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Extra {
    String name() default "";
}
