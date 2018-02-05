package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tantan on 2018/1/27.
 */

public class BankListMessage implements Serializable{

    /**
     * code : 200
     * message : OK
     * data : [{"bank_name":"工商银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/2.png"},{"bank_name":"农业银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/10.png"},{"bank_name":"中国银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/21.png"},{"bank_name":"中国邮政储蓄银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/22.png"},{"bank_name":"光大银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/18.png"},{"bank_name":"民生银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/8.png"},{"bank_name":"交通银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/7.png"},{"bank_name":"招商银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/16.png"},{"bank_name":"兴业银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/15.png"},{"bank_name":"浦发银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/11.png"},{"bank_name":"平安银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/20.png"},{"bank_name":"华夏银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/5.png"},{"bank_name":"中信银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/30.png"}]
     * error_code : 0
     * error_message :
     * time : 2018-01-27 16:06:25
     */

    private int code;
    private String message;
    private int error_code;
    private String error_message;
    private String time;
    private List<DataProduct> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<DataProduct> getData() {
        return data;
    }

    public void setData(List<DataProduct> data) {
        this.data = data;
    }

    public static class DataProduct implements Serializable{
        /**
         * bank_name : 工商银行
         * bank_logo : http://p2bpklo99.bkt.clouddn.com/2.png
         */

        private String bank_name;
        private String bank_logo;

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getBank_logo() {
            return bank_logo;
        }

        public void setBank_logo(String bank_logo) {
            this.bank_logo = bank_logo;
        }

        @Override
        public String toString() {
            return "DataProduct{" +
                    "bank_name='" + bank_name + '\'' +
                    ", bank_logo='" + bank_logo + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BankListMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                ", data=" + data +
                '}';
    }
}
