package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tantan on 2018/1/22.
 */

public class CardList implements Serializable{


    /**
     * code : 200
     * message : OK
     * data : {"decredit_card":[{"decredit_card":" **** **** **** 3005","bankNameTrans":"浦发银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/11.png"}],"credit_card":[{"id":"51","bankNameTrans":"交通银行","bank_logo":"http://p2bpklo99.bkt.clouddn.com/7.png","bank_token":"6251640171392208","phoneNo":"18237119367","support":1}]}
     * error_code : 0
     * error_message :
     * time : 2018-01-29 15:21:59
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
        private List<DecreditCardProduct> decredit_card;
        private List<CreditCardProduct> credit_card;

        public List<DecreditCardProduct> getDecredit_card() {
            return decredit_card;
        }

        public void setDecredit_card(List<DecreditCardProduct> decredit_card) {
            this.decredit_card = decredit_card;
        }

        public List<CreditCardProduct> getCredit_card() {
            return credit_card;
        }

        public void setCredit_card(List<CreditCardProduct> credit_card) {
            this.credit_card = credit_card;
        }

        public static class DecreditCardProduct {
            /**
             * decredit_card :  **** **** **** 3005
             * bankNameTrans : 浦发银行
             * bank_logo : http://p2bpklo99.bkt.clouddn.com/11.png
             */

            private String decredit_card;
            private String bankNameTrans;
            private String bank_logo;

            public String getDecredit_card() {
                return decredit_card;
            }

            public void setDecredit_card(String decredit_card) {
                this.decredit_card = decredit_card;
            }

            public String getBankNameTrans() {
                return bankNameTrans;
            }

            public void setBankNameTrans(String bankNameTrans) {
                this.bankNameTrans = bankNameTrans;
            }

            public String getBank_logo() {
                return bank_logo;
            }

            public void setBank_logo(String bank_logo) {
                this.bank_logo = bank_logo;
            }
        }

        public static class CreditCardProduct {
            /**
             * id : 51
             * bankNameTrans : 交通银行
             * bank_logo : http://p2bpklo99.bkt.clouddn.com/7.png
             * credit_card : 6251640171392208
             * phoneNo : 18237119367
             * support : 1
             */

            private String id;
            private String bankNameTrans;
            private String bank_logo;
            private String credit_card;
            private String phoneNo;
            private int support;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getBankNameTrans() {
                return bankNameTrans;
            }

            public void setBankNameTrans(String bankNameTrans) {
                this.bankNameTrans = bankNameTrans;
            }

            public String getBank_logo() {
                return bank_logo;
            }

            public void setBank_logo(String bank_logo) {
                this.bank_logo = bank_logo;
            }

            public String getCredit_card() {
                return credit_card;
            }

            public void setCredit_card(String bank_token) {
                this.credit_card = bank_token;
            }

            public String getPhoneNo() {
                return phoneNo;
            }

            public void setPhoneNo(String phoneNo) {
                this.phoneNo = phoneNo;
            }

            public int getSupport() {
                return support;
            }

            public void setSupport(int support) {
                this.support = support;
            }
        }
    }

    @Override
    public String toString() {
        return "CardList{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

}
