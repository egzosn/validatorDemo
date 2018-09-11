package com.egzosn.validator.error;


import java.util.Map;

/**
 * 错误编码
 *
 * @author egan
 * @create 2016/11/12
 */

public class ErrorCode{
    public final static Integer SERVER_ERROR = 500;
    public final static Integer ILLEGAL_PARAMETER = 1000;

    //通用错误编码
    private Map<Integer, String> common;
    //自定义错误编码
    private Map<Integer, String> custom;

    public Map<Integer, String> getCommon() {
        return common;
    }

    public void setCommon(Map<Integer, String> common) {
        this.common = common;
    }

    public Map<Integer, String> getCustom() {
        return custom;
    }

    public void setCustom(Map<Integer, String> custom) {
        this.custom = custom;
    }
}
