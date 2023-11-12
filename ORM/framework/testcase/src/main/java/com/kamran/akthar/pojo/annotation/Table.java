package com.kamran.akthar.pojo.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table 
{
    String name() default "";
}