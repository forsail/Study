package com.steve;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yantinggeng on 2016/11/9.
 */

@Retention(RetentionPolicy.CLASS)
public @interface NameGenerate {

    String name();

}
