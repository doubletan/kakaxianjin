package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;

/**
 * Created by tantan on 2018/3/1.
 */

public class DaiHuanMessage implements Serializable{

    /**
     * code : 200
     * message : 200
     * data : {"url":"http://app.handbank.cn/hpayDFPaySupport/inside/index?realName=%E8%B0%AD%E8%B0%88&phone=7F180E30594B7D1A1240A3D0390A6F7B&idCode=584B01F9BE257F0D4B16C507606E8D0103D91EC2E2EF2F6E&channelCode=10001&merAgentCode=OLTEST&openid=2627b4f620&merchantCode=E7C3996D50E137F49E0660ABA4D20EBC&sign=0e1a61485ab44c4e49b731f12f4d9bbc"}
     * error_code : 0
     * error_message :
     * time : 2018-03-01 17:23:07
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
         * url : http://app.handbank.cn/hpayDFPaySupport/inside/index?realName=%E8%B0%AD%E8%B0%88&phone=7F180E30594B7D1A1240A3D0390A6F7B&idCode=584B01F9BE257F0D4B16C507606E8D0103D91EC2E2EF2F6E&channelCode=10001&merAgentCode=OLTEST&openid=2627b4f620&merchantCode=E7C3996D50E137F49E0660ABA4D20EBC&sign=0e1a61485ab44c4e49b731f12f4d9bbc
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Override
    public String toString() {
        return "DaiHuanMessage{" +
                "code=" + code +
                ", message=" + message +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
