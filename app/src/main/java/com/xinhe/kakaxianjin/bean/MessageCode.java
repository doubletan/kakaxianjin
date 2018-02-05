package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;

/**
 * Created by tantan on 2018/1/18.
 */

public class MessageCode implements Serializable{

    /**
     * code : 200
     * message : 0
     * data : {"isSucess":"1"}
     * error_code : 0
     * error_message :
     * time : 2018-01-18 16:10:45
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

    public static class DataProduct {
        /**
         * isSucess : 1
         */

        private String isSucess;

        public String getIsSucess() {
            return isSucess;
        }

        public void setIsSucess(String isSucess) {
            this.isSucess = isSucess;
        }
    }

    @Override
    public String toString() {
        return "MessageCode{" +
                "code=" + code +
                ", message=" + message +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
