package com.kamran.akthar.pojo.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeginKey {
    String parent() default "";
    String column() default "";
}