package com.xinhe.kakaxianjin.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tantan on 2018/1/22.
 */

public class StateMessage implements Serializable{


    /**
     * code : 200
     * message : OK
     * data : {"id":"50","user_id":"54","id_card_status":"1","debit_card_status":"1","credit_card_status":"1","credit":{"38":1}}
     * error_code : 0
     * error_message :
     * time : 2018-01-25 19:07:55
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
         * id : 50
         * user_id : 54
         * id_card_status : 1
         * debit_card_status : 1
         * credit_card_status : 1
         * credit : {"38":1}
         */

        private String id;
        private String user_id;
        private String id_card_status;
        private String debit_card_status;
        private String credit_card_status;
        private CreditProduct credit;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getId_card_status() {
            return id_card_status;
        }

        public void setId_card_status(String id_card_status) {
            this.id_card_status = id_card_status;
        }

        public String getDebit_card_status() {
            return debit_card_status;
        }

        public void setDebit_card_status(String debit_card_status) {
            this.debit_card_status = debit_card_status;
        }

        public String getCredit_card_status() {
            return credit_card_status;
        }

        public void setCredit_card_status(String credit_card_status) {
            this.credit_card_status = credit_card_status;
        }

        public CreditProduct getCredit() {
            return credit;
        }

        public void setCredit(CreditProduct credit) {
            this.credit = credit;
        }

        public static class CreditProduct {
            /**
             * 38 : 1
             */

            @SerializedName("38")
            private int _$38;

            public int get_$38() {
                return _$38;
            }

            public void set_$38(int _$38) {
                this._$38 = _$38;
            }
        }
    }

    @Override
    public String toString() {
        return "StateMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
