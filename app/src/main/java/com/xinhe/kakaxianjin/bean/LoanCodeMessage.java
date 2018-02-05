package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;

/**
 * Created by tantan on 2018/1/30.
 */

public class LoanCodeMessage implements Serializable{

    /**
     * code : 200
     * message : OK
     * data : {"orderId":"201801301001952465319541166","txnTime":"20180130145406","txnAmt":"50000"}
     * error_code : 0
     * error_message :
     * time : 2018-01-30 14:54:07
     */

    private int code;
    private String message;
    private DataProduct data;
    private int error_code;
    private String error_message;
    private String time;

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

    public DataProduct getData() {
        return data;
    }

    public void setData(DataProduct data) {
        this.data = data;
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

    public static class DataProduct implements Serializable{
        /**
         * orderId : 201801301001952465319541166
         * txnTime : 20180130145406
         * txnAmt : 50000
         */

        private String orderId;
        private String txnTime;
        private String txnAmt;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getTxnTime() {
            return txnTime;
        }

        public void setTxnTime(String txnTime) {
            this.txnTime = txnTime;
        }

        public String getTxnAmt() {
            return txnAmt;
        }

        public void setTxnAmt(String txnAmt) {
            this.txnAmt = txnAmt;
        }
    }

    @Override
    public String toString() {
        return "LoanCodeMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
