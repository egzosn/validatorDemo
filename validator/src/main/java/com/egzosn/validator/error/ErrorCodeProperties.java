package com.egzosn.validator.error;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 错误码配置
 *
 * @author egan
 * @create 2016/10/11
 */

public class ErrorCodeProperties {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(ErrorCodeProperties.class);

    //系统名称
    private String name;
    //系统错误码前缀/系统序号
    private int seq;
    //成功编码
    private Map<Integer, String> success;
    //错误编码
    private ErrorCode errorCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Map<Integer, String> getSuccess() {
        return success;
    }

    public void setSuccess(Map<Integer, String> success) {
        this.success = success;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 拼合完整的错误码
     *
     * @param errorCode 错误码
     * @return
     */
    public Integer getCode(Integer errorCode) {
        String temp = String.valueOf(errorCode);
        //通用错误不需要加前缀
        if(!this.errorCode.getCommon().containsKey(errorCode)){
            temp = String.valueOf(seq) + temp;
        }
        return Integer.parseInt(temp);
    }

    /**
     * 获取错误码信息
     *
     * @param errorCode 错误码
     * @return 错误提示信息
     */
    public String getMessage(Integer errorCode, String... values) {
        String message = this.errorCode.getCommon().get(errorCode);
        if (StringUtils.isEmpty(message)) {
            message = this.errorCode.getCustom().get(errorCode);
        }
        if(StringUtils.isEmpty(message)){
            logger.error("无效的错误码:" + errorCode + "，请正确添加到error_code.yml 或者error_code.xml配置文件！");
        }
        if(values != null && values.length > 0){
            message = String.format(message, values);
        }
        return message;
    }

}
