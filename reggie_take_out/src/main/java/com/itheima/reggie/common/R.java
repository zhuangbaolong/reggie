package com.itheima.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类
 */

@Data
public class R<T> {
    private Integer code; //编码 0失败，1成功
    private String msg;  //消息
    private T data; //数据

    private Map map = new HashMap<>(); //动态数据

    public static <T> R<T> success(T object){
        R<T> r = new R<T>();
        r.code = 1;
        r.data = object;
        return r;
    }

    public static R error(String msg){
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
