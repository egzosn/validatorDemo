package com.egzosn.validator.validator.plug;

import com.egzosn.validator.exception.CommonException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * 约束
 *  Created by ZaoSheng on 2018/06/13.
 */
public enum Restriction {

    /**
     * 等于
     */
    EQ{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 1);
            if (expected[0].equals(repositoryData)){
                return true;
            }
            return false;
        }
    },

    /**
     * 非等于
     */
    NE{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            return !EQ.match(repositoryData, expected);
        }
    },

    /**
     * 大于等于
     */
    GE{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 1);

            if (!(repositoryData instanceof Number)){
                return true;
            }
            Number start = validateNumber(expected[0]);

            double  data =((Number)repositoryData).doubleValue();
            if (start.doubleValue() >=  data ){
                return true;
            }
            return false;
        }
    },

    /**
     * 大于
     */
    GT{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 1);

            if (!(repositoryData instanceof Number)){
                return true;
            }
            Number start = validateNumber(expected[0]);

            double  data =((Number)repositoryData).doubleValue();
            if (start.doubleValue() >  data ){
                return true;
            }
            return false;
        }
    },

    /**
     * 小于等于
     */
    LE{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 1);

            if (!(repositoryData instanceof Number)){
                return true;
            }
            Number start = validateNumber(expected[0]);

            double  data =((Number)repositoryData).doubleValue();
            if (start.doubleValue() <=  data ){
                return true;
            }
            return false;
        }
    },

    /**
     * 小于
     */
    LT{
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 1);

            if (!(repositoryData instanceof Number)){
                return true;
            }
            Number start = validateNumber(expected[0]);

            double  data =((Number)repositoryData).doubleValue();
            if (start.doubleValue() <  data ){
                return true;
            }
            return false;
        }
    },
    /**
     * 两个值之间
     */
    BW {
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            validateExpected(expected, 2);

            if (!(repositoryData instanceof Number)){
                 return true;
            }
            Number start = validateNumber(expected[0]);
            Number end = validateNumber(expected[1]);
            if (!(repositoryData instanceof Number)){
                return true;

            }
            double  data =((Number)repositoryData).doubleValue();
            if (start.doubleValue() <=  data && end.doubleValue() > data){
                return true;
            }
            return false;
        }
    },

    /**
     * 包含
     */
    IN {
        @Override
        public boolean match(Object repositoryData, String[] expected) {
            if (null == expected || expected.length == 0){
                throw new CommonException(1000, "约束对应的通配符的长度不能为0");
            }
            if (null == repositoryData){
                return true;
            }
            for (String e : expected){
                if (e.equals(repositoryData.toString())){
                    return true;
                }
            }
            return false;
        }
    },

    /**
     * 非包含
     */
    NIN{
        @Override
        public boolean match(Object repositoryData, String[] expected) {

            return !IN.match(repositoryData, expected);
        }
    },
    /**
     * 结果为空
     */
    NULL {
        @Override
        public boolean match(Object repositoryData, String[] expected) {

            return null == repositoryData;
        }
    },
    /**
     * 结果不为空
     */
    NOT_NULL {
        @Override
        public boolean match(Object repositoryData, String[] expected) {

            return null != repositoryData;
        }
    };

    public Number validateNumber(String expected){
        if (!NumberUtils.isNumber(expected)){
            throw new CommonException(1000, "约束对应的通配符必须是数字" );
        }
         if (NumberUtils.isDigits(expected)){
            return NumberUtils.createDouble(expected);
         }
        return NumberUtils.createLong(expected);
    }

    public void validateExpected(String[] expected, int len){
        if (null == expected || expected.length != len || StringUtils.isEmpty(expected[0])){
            throw new CommonException(1000, "约束对应的通配符的长度必须为" + len);
        }
    }

    /**
     *
     * @param repositoryData 仓储返回来的数据
     * @param expected 期望值
     * @return 是否预期，预期true
     */
    public abstract boolean match(Object repositoryData, String[] expected);

}