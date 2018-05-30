package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;

/**
 * Created by tantan on 2018/1/23.
 */

public class BindCreditcardMessage implements Serializable{


    /**
     * code : 200
     * message : 200
     * data : {"expired":"2212","credit_card":"6222530913519596","phoneNo":"18237119367","cvn2":"176","bank_logo":"http://p2bpklo99.bkt.clouddn.com/7.png","bankNameTrans":"交通银行","orderId":"201801251001704013734019557","txnTime":"20180125165321","created_time":"2018-01-25 16:53:21","credit_card_id":0}
     * error_code : 0
     * error_message :
     * time : 2018-01-25 16:53:21
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
         * expired : 2212
         * credit_card : 6222530913519596
         * phoneNo : 18237119367
         * cvn2 : 176
         * bank_logo : http://p2bpklo99.bkt.clouddn.com/7.png
         * bankNameTrans : 交通银行
         * orderId : 201801251001704013734019557
         * txnTime : 20180125165321
         * created_time : 2018-01-25 16:53:21
         * credit_card_id : 0
         */

        private String expired;
        private String credit_card;
        private String phoneNo;
        private String cvn2;
        private String bank_logo;
        private String bankNameTrans;
        private String orderId;
        private String txnTime;
        private String created_time;
        private String credit_card_id;

        public String getExpired() {
            return expired;
        }

        public void setExpired(String expired) {
            this.expired = expired;
        }

        public String getCredit_card() {
            return credit_card;
        }

        public void setCredit_card(String credit_card) {
            this.credit_card = credit_card;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getCvn2() {
            return cvn2;
        }

        public void setCvn2(String cvn2) {
            this.cvn2 = cvn2;
        }

        public String getBank_logo() {
            return bank_logo;
        }

        public void setBank_logo(String bank_logo) {
            this.bank_logo = bank_logo;
        }

        public String getBankNameTrans() {
            return bankNameTrans;
        }

        public void setBankNameTrans(String bankNameTrans) {
            this.bankNameTrans = bankNameTrans;
        }

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

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getCredit_card_id() {
            return credit_card_id;
        }

        public void setCredit_card_id(String credit_card_id) {
            this.credit_card_id = credit_card_id;
        }
    }

    @Override
    public String toString() {
        return "BindCreditcardMessage{" +
                "code=" + code +
                ", message=" + message +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
