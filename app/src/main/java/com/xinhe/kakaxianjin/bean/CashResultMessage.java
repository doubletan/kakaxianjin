package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;

/**
 * Created by tantan on 2018/1/30.
 */

public class CashResultMessage implements Serializable{

    /**
     * code : 200
     * message : OK
     * data : {"id":"9","user_id":"58","order_id":"201801301001999271290899888","transAmt":"100.00","bankNoTrans":"6259584001436920","bank_logo":"http://p2bpklo99.bkt.clouddn.com/11.png","bankNameTrans":"浦发银行","decredit_card":"6217920606024318","decredit_bank_logo":"http://p2bpklo99.bkt.clouddn.com/11.png","txnTime":"20180130161207","txnAmtDF":"96.52","is_success_xf":"1","is_success_df":"1","is_query":"1","created_time":"2018-01-30 16:13:06","updated_time":"2018-01-30 16:16:07","decredit_bank":"浦发银行"}
     * error_code : 0
     * error_message :
     * time : 2018-01-30 17:15:15
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
         * id : 9
         * user_id : 58
         * order_id : 201801301001999271290899888
         * transAmt : 100.00
         * bankNoTrans : 6259584001436920
         * bank_logo : http://p2bpklo99.bkt.clouddn.com/11.png
         * bankNameTrans : 浦发银行
         * decredit_card : 6217920606024318
         * decredit_bank_logo : http://p2bpklo99.bkt.clouddn.com/11.png
         * txnTime : 20180130161207
         * txnAmtDF : 96.52
         * is_success_xf : 1
         * is_success_df : 1
         * is_query : 1
         * created_time : 2018-01-30 16:13:06
         * updated_time : 2018-01-30 16:16:07
         * decredit_bank : 浦发银行
         */

        private String id;
        private String user_id;
        private String order_id;
        private String transAmt;
        private String bankNoTrans;
        private String bank_logo;
        private String bankNameTrans;
        private String decredit_card;
        private String decredit_bank_logo;
        private String txnTime;
        private String txnAmtDF;
        private String is_success_xf;
        private String is_success_df;
        private String is_query;
        private String created_time;
        private String updated_time;
        private String decredit_bank;

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

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getTransAmt() {
            return transAmt;
        }

        public void setTransAmt(String transAmt) {
            this.transAmt = transAmt;
        }

        public String getBankNoTrans() {
            return bankNoTrans;
        }

        public void setBankNoTrans(String bankNoTrans) {
            this.bankNoTrans = bankNoTrans;
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

        public String getDecredit_card() {
            return decredit_card;
        }

        public void setDecredit_card(String decredit_card) {
            this.decredit_card = decredit_card;
        }

        public String getDecredit_bank_logo() {
            return decredit_bank_logo;
        }

        public void setDecredit_bank_logo(String decredit_bank_logo) {
            this.decredit_bank_logo = decredit_bank_logo;
        }

        public String getTxnTime() {
            return txnTime;
        }

        public void setTxnTime(String txnTime) {
            this.txnTime = txnTime;
        }

        public String getTxnAmtDF() {
            return txnAmtDF;
        }

        public void setTxnAmtDF(String txnAmtDF) {
            this.txnAmtDF = txnAmtDF;
        }

        public String getIs_success_xf() {
            return is_success_xf;
        }

        public void setIs_success_xf(String is_success_xf) {
            this.is_success_xf = is_success_xf;
        }

        public String getIs_success_df() {
            return is_success_df;
        }

        public void setIs_success_df(String is_success_df) {
            this.is_success_df = is_success_df;
        }

        public String getIs_query() {
            return is_query;
        }

        public void setIs_query(String is_query) {
            this.is_query = is_query;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getUpdated_time() {
            return updated_time;
        }

        public void setUpdated_time(String updated_time) {
            this.updated_time = updated_time;
        }

        public String getDecredit_bank() {
            return decredit_bank;
        }

        public void setDecredit_bank(String decredit_bank) {
            this.decredit_bank = decredit_bank;
        }
    }

    @Override
    public String toString() {
        return "CashResultMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
