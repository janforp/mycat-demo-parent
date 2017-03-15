package com.janita.mycat.one.util;

import java.util.UUID;

/**
 * Created by Janita on 2017/3/15
 */
public class MyUtils {

    public static String getRandomId(){
      UUID uuid = UUID.randomUUID();
      return uuid.toString();
    }
}
