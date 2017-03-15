package com.janita.mycat.one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Janita on 2017/3/14
 */
@SpringBootApplication
@ComponentScan(basePackages ="com.janita.mycat.one")
public class MyCatOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCatOneApplication.class,args);
    }
}
