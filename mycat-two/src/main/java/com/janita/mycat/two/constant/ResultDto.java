package com.janita.mycat.two.constant;

import java.util.Objects;

/**
 * Created by Janita on 2017/3/15
 */
public class ResultDto {

    private Integer code;
    private String  msg;
    private Object data;

    public static ResultDto success(String msg, Object data){
        ResultDto dto = new ResultDto();
        dto.setCode(0);
        dto.setMsg(msg);
        dto.setData(data);
        return dto;
    }

    public static ResultDto error(String msg){
        ResultDto dto = new ResultDto();
        dto.setCode(1);
        dto.setMsg(msg);
        return dto;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
