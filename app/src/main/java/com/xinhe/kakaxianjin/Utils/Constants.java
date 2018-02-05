package com.xinhe.kakaxianjin.Utils;

/**
 * Created by tantan on 2017/7/11.
 */

public class Constants {
    //注册协议
    public static String registerAgreement="https://www.shoujiweidai.com/credit_card/#/protocol";
    //取现协议
    public static String loanAgreement="https://www.shoujiweidai.com/credit_card/#/confirmation";
    //常见问题
    public static String problems="https://www.shoujiweidai.com/credit_card/#/problem";
    //贷款
    public static String loan="https://www.shoujijiekuan.com/xieExtend12/index.html";
    //进度
    public static String progress="https://www.shoujiweidai.com/credit_card/#/schedule";
    //办卡
    public static String card="https://www.shoujiweidai.com/credit_card/#/apply_list";
    //攻略
    public static String gonglue="https://www.shoujiweidai.com/credit_card/#/guide";



    //地址
    public static String commonURL = "https://api.anwenqianbao.com/v0/";
    //注册发送验证码
    public static String sendCode = "sms/getcode";
    //忘记密码发送验证码
    public static String sendForgetCode = "sms/getPwdCode";
    //忘记密码重置
    public static String forgetCode = "person/testCode";
    //修改密码
    public static String setPassword = "person/setPassword";
    //注册
    public static String register = "quick/login";
    //登录
    public static String login = "login/login";
    //获取银行卡列表
    public static String getBankCard = "bank/getBankList";
    //获取信用卡银行列表
    public static String getCbank = "bank/getCbank";
    //获取储蓄卡银行列表
    public static String getDbank = "bank/getDbank";
    //获取信息状态
    public static String getStatus = "hengxin/getStatus";
    //获取信用卡绑定状态
    public static String cardStatus = "bank/cardStatus";
    //绑定身份证
    public static String distinguish = "IdCard/Distinguish";
    //识别银行
    public static String fetchBank = "hengxin/fetchBank";
    //绑定储蓄卡
    public static String smsmange = "hengxin/smsmange";
    //绑定信用卡前
    public static String frontTran = "hengxin/frontTran";
    //获取个人信息
    public static String getIdCard = "IdCard/getIdCard";
    //绑定信用卡
    public static String frontTransTokens = "hengxin/frontTransTokens";
    //取现记录
    public static String queryList = "hengxin/queryList";
    //删除信用卡
    public static String delBank = "hengxin/delBank";
    //无积分发送验证码
    public static String SmsConsume = "hengxin/SmsConsume";
    //无积分套现
    public static String open = "hengxin/open";
    //有积分套现
    public static String openJifen = "hengxin/openJifen";
    //交易结果
    public static String queryComplete = "hengxin/queryComplete";
    //取现中未绑定银联的跳银联
    public static String frontTransTokens1 = "bank/frontTransTokens";




    //fileprovider
    public static String fileprovider = "com.xinhe.kakaxianjin.fileprovider";





    //地址
    public static String URL = "http://www.shoujijiekuan.com/Service/WS4Simple.asmx";
    //地址1
    public static String URL1 = "http://www.shoujijiekuan.com/Service/WS4New2018.asmx";
    //命名空间
    public static String nameSpace = "http://chachaxy.com/";
    //图片地址
    public static String piURL = "http://www.shoujijiekuan.com";
    //渠道
    public static String qudao = "android";
    //注册渠道
    public static String channel = "2";
    //注册渠道1
    public static String channel1 = "tengxun";
    //App名称
    public static String appName = "现金贷";
    //公司名字
    public static String companyName = "北京信和浙大教育科技有限公司";

    // 版本信息地址
    public static String versionUrl = "http://www.shoujijiekuan.com/v/version.xml";



    // 权限请求码

    public static int PERMISSION_CAMERA = 101;
    public static int PERMISSION_READ_PHONE_STATE = 102;
    public static int PERMISSION_WRITE_EXTERNAL_STORAGE = 103;
    public static int PERMISSION_CALL_PHONE = 104;


    // 拍照请求码
    public static int REQUEST_CODE_TAKE_PICETURE = 105;
    // 相册请求码
    public static int REQUEST_CODE_PICK_PHOTO = 106;
    // 拍照权限请求码
    public static int REQUEST_CODE_CAMERA_PERMISSION = 107;
    // 储存权限请求码
    public static int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 108;



    //绑定信用卡页面到银联h5的请求码
    public static int CREDITCARDACTIVITY_TO_WEBVIEW = 120;
    //个人信息页面到修改密码的请求码
    public static int PERSONALINFORMATIONACTIVITY_TO_MODIFYPASSWORD = 121;
    //信用卡页面到银行列表的请求码
    public static int CREDITCARDACTIVITY_TO_BANKLIST = 122;
    //取现页面到银联h5的请求码
    public static int CHOOSECREDITCARDACTIVITY_TO_WEBVIEW = 123;
    //取现页面到取现结果的请求码
    public static int CHOOSECREDITCARDACTIVITY_TO_CASHRESULT = 124;


    //登录成功的广播
    public static String INTENT_EXTRA_LOGIN_SUCESS = "com.xinhe.intent.action.INTENT_EXTRA_LOGIN_SUCESS";
    //退出时的广播
    public static String INTENT_EXTRA_EXIT = "com.xinhe.intent.action.INTENT_EXTRA_EXIT";
    //信用卡和储蓄卡绑定成功的广播
    public static String INTENT_EXTRA_CARD_LIST_CHANGE = "com.xinhe.intent.action.INTENT_EXTRA_CARD_LIST_CHANGE";
}
