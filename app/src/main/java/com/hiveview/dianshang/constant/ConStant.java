package com.hiveview.dianshang.constant;

import android.content.Context;

import com.hiveview.dianshang.utils.SPUtils;

/**
 * Created by carter on 4/10/17.
 */

public class ConStant {


    public static String Token ="";
    //7f0dfac38a2e3674bd7295e1ec184049_4055212
    //2f1e344843b733af97e641e9a517347c_4055212
    public static String userID = "";
    //RxView点击防止抖动时间间隔
    public static final long throttDuration=1;
    public static final long millisecondstime=400;


    public static final String obString_opt_natigation = "obString_opt_natigation";
    public static final String obString_opt_search_natigation = "obString_search_opt_natigation";
    public static final String obString_select_commodity_type = "obString_select_commodity_type";
    public static final String obString_modify_shopping_cart = "obString_modify_shopping_cart";
    public static final String obString_update_shopping_cart = "obString_update_shopping_cart";
    public static final String obString_search_recommend_back = "obString_search_recommend_back";
    public static final String obString_remove_collection_item= "obString_remove_collection_item";
    public static final String obString_main_load_finish= "obString_main_load_finish";
    public static final String obString_reset_nav_focus= "obString_reset_nav_focus";
    public static final String obString_item_load_icon_url= "obString_item_load_icon_url";
    //public static final String obString_opt_special_natigation = "obString_opt_special_natigation";

    public static final String obString_opt_cancel_acution_order = "obString_opt_cancel_acution_order";



    public static final int OP_SEARCH_MAIN = 1;
    public static final int OP_SEARCH_MAIN_RESULT = 2;
    public static final int OP_SHOW_ADDRESS_MANAGER = 3;
    public static final int OP_HOME = 4;
    public static final int OP_SHOW_COMMODITY = 5;
    public static final int OP_COLLECTION = 6;
    public static final int OP_ORDER = 7;
    public static final int OP_EDIT_ADDRESS = 8;
    public static final int OP_COMMODITY_CAGEGORY = 9;
    public static final int OP_AFTER_SALE_SERVICE = 10;
    public static final int OP_SHOPPING_CART = 11;
    public static final int OP_USER_CENTER = 12;
    public static final int OP_ACUTION = 13;

    public static final int OP_SPECIAL = 13; //专题
    public static final int OP_ALL = 14; //全部商品



    public static final String SEARCH_HOME = "search_home";
    public static final String SEARCH_RESULT = "search_result";
    public static final String SEARCH_EXIT = "search_exit";

    public static final int PAGESIZE = 16*6;
    public static final int ACUTION_PAGE_SIZE = 4*5;
    public static final int ACUTION_LOOP_TIME = 5;
    public static final int ACUTION_PROGRESS_TIME_INTERVER=100;


    //直接支付,购物车,购物车支付 共享UI
    public static final int DIRECT_PAYMENT = 1; //直接支付
    //public static final int SHOPPING_CART_PARMENT = 2; //购物车支付
    public static final int SHOPPING_CART = 3; //购物车
    public static final int ACUTION_PAYMENT = 4; //拍卖支付


    public static final String USERID= "userid";
    public static final String USER_TOKEN = "token";


    public static final int INVOICE_NULL = 0;
    public static final int INVOICE_PERSON = 1;
    public static final int INVOICE_COMPANY = 2;


    public static final String domyshop_key = "84a95c8a0c32907a43930a78d41f8a5e";
    //84a95c8a0c32907a43930a78d41f8a5e 正式
    //14432604858cb9aa02bc48c7e79443b7 测试
    public static final String product_type = "domyshop";
    public static final String partner_id = "p170703095873882"; //商户ID
    //p170703095873882　正式
    //p170601100113023  测试
    public static final String WEIXIN_PAMYMENT = "4";
    public static final String ZHIFUBAO_PAMYMNET = "3";
    public static final String PAYMENT_TIMEOUT = "7200";

    public static final String confirm_payment_key = "31871fa18f49742f95295ef7fe5d3550";

    public static final String domyshop_order_key = "7a773cb65e51632e23082cd9fe8e4219";


    /**
     * 记录埋点信息
     */
    public static final int TUIJIANWEI_TO_INFO = 1; //推荐位
    public static final int HOME_TO_INFO = 2;   //主页商品位
    public static final int SPECIAL_TO_INFO = 3;  //专题位
    public static final int CATEGORY_TO_INFO = 4;  //分类位
    public static final int COLLECTION_TO_INFO = 5; //收藏位
    public static final int SEARCH_RESULT_TO_INFO = 6; //搜索结果位
    public static final int SEARCH_RECOMMEND_TO_INFO = 7; //搜索推荐位
    public static final int ORDER_TO_INFO = 8; //订单位
    public static final int GOUWUCHE_TO_INFO=9;//购物车
    public static final int ALL_TO_INFO=10;//购物车
    public static final int PROMOTION_TO_INFO=11;//购物车
    public static final int ACUTION_TO_INFO=12;//拍卖


    public static final int SCALE_DURATION = 200;


    public static final int LIVE = 1; //直播流
    public static final int COMMODITY = 2;  //商品


    public static final String BAIDU_PACKAGENAME = "com.baidu.input_baidutv";
    public static final String BAIDU_INPUT ="com.baidu.input_baidutv/.ImeService";

    public static final String SWITCH_INPUT_MANAGER_PACKAGENAME="com.hiveview.dianshang.inputmanager";
    public static final String SWITCH_INPUT="com.hiveview.dianshang.inputmanager.InputManagerService";


    public static final int SEARCH_RESULT_NAME_MAX_LENGTH = 8;

    /**
     * 发布调试采用
     */
    public static final int DEBUG_MODE = 1;
    public static final int RELEASE_MODE = 2;


    /**
     * 商品状态
     */
    public static final int OFF_LINE = 2;  //商品下架状态
    public static final int ON_LINE = 1;  //商品上架状态


    /**
     * 专题状态
     */
    public static final int SPECIAL_OFF_LINE = 0;  //商品下架状态
    public static final int SPECIAL_ON_LINE = 1;  //商品上架状态

    /**
     * 区分专题，商品
     */
    public static final int RECOMMENT_SPECIAL = 2;  //推荐专题
    public static final int RECOMMENT_COMMODITY = 1;  //推荐商品

    //购物车显示地址的最大长度
    public static final int SHOP_ADDRESS_SHOW_LENGTH=87;


    public static final int END_SIZE = 20;
    public static final int END_ACUTION_SIZE = 4;
    public static final String ACUTION_TOP_SN="acution_top_sn";
    public static final String ACUTION_TOP_PRICE="acution_top_price";


    public static final String ACTION_NETTRY_RECEIVER = "com.hiveview.dianshang.NETTY";

    //netty apk server name
    public static final String Netty_ServiceName= "com.hiveview.yunpush.server.service.OpenNettyService";

    public static final String YUNPUSH_PACKAGENAME ="com.hiveview.yunpush.server";
    public static final String Install_ServiceName="com.hiveview.dianshang.home.InstallService";
    public static final String YunPush_ServiceName="com.hiveview.dianshang.home.YunPushService";

    /**
     * netty user string
     */
    public static final int NETTY_OP_SUCESS = 0;
    public static final int NETTY_OP_FAILED = -1;
    public static final String PAY_SUCCESS_STR = "PAY_SUCCESS";
    public static final String ADD_RECEIVER_SUCCESS_STR = "ADD_RECEIVER_SUCCESS";
    public static final String ADD_RECEIVER_FAILED_STR = "ADD_RECEIVER_FAILED";
    public static final String UPDATE_RECEIVER_SUCCESS_STR = "UPDATE_RECEIVER_SUCCESS";
    public static final String UPDATE_RECEIVER_FAILED_STR = "UPDATE_RECEIVER_FAILED";


    /**
     * 手机端扫描后，通知客户端netty action定义
     */
    public static final int ACTION_TYPE_PAYMENT = 1; //支付
    public static final int ACTION_TYPE_ADD_ADDRESS = 2;//增加地址
    public static final int ACTION_TYPE_MODITY_ADDRESS = 3;//修改地址
    public static final int ACTION_TYPE_INVOICE = 4;  //发票内容编辑二维码
    public static final int ACTION_TYPE_SEARCH = 5; //搜索页面二维码
    public static final int ACTION_TYPE_CREATE_ACUTION_ORDER = 6; //拍卖未支付订单
    public static final int ACTION_TYPE_CANCEL_ACUTION_ORDER = 7; //拍卖超时取消未支付订单

    public static final String APP_VERSION = "v1.2";
    public static final String APP_VERSION1= "v1.1";


    public static String REMOTE_ACTION = "com.hiveview.cloudscreen.Action.HOME_CODE";


    public static String ACTION_ORDER = "com.hiveview.dianshang.action.order";
    public static String ACTION_FAVORITE = "com.hiveview.dianshang.action.favorite";
    public static String ACTION_USER_CENTER = "com.hiveview.dianshang.action.user.center";
    public static String ACTION_ACUTION = "com.hiveview.dianshang.action.acution";

    //促销商品的类型
    public static String FULL_CUT="FULL_CUT";//满减
    public static String FULL_GIFTS="FULL_GIFTS";//满赠
    public static String BUY_GIFTS="BUY_GIFTS";//买赠
    public static String LIMIT_BUY="LIMIT_BUY";//限购
    public static String COMMON="COMMON";//普通商品
    public static String INVALID="INVALID";//失效的商品


    //订单类型
    public static int ORDER_TYPE_ALL=0;//全部
    public static int ORDER_TYPE_COMMOM = 1;//商城订单
    public static int ORDER_TYPE_AUCTON = 2; //拍卖订单

    //订单状态
    public static int ORDER_STATUS_UNPAY=0;//待支付
    public static int ORDER_STATUS_UNDELIVERY=1; //待发货
    public static int ORDER_STATUS_UNTACK_DELIVERY=2;//待收货
    public static int ORDER_STATUS_TRANSACTION_SUCESS =3;//交易成功
    public static int ORDER_STATUS_PROCESSING = 4;//处理中（商户确认）
    public static int ORDER_STATUS_REFUNDING_SHANGHU =5;//退款中(商户确认)
    public static int ORDER_STATUS_REFUNDING_JIASHI = 6;//退款中(家视确认)
    public static int ORDER_STATUS_REFUNDED = 7;//已退款
    public static int ORDER_STATUS_EXCHANGE_GOODS = 8;//换货单
    public static int ORDER_STATUS_TRANSACTION_CLOSE = 9;//交易关闭
    public static int ORDER_STATUS_ALL=10;//所有状态


    /**
     * 对应服务器的参数
     */
    public static int INVOICE_TYPE_NULL = 1;
    public static int INVOICE_TYPE_PERSON = 2;
    public static int INVOICE_TYPE_COMPANY = 3;

    private static ConStant instance;
    public static ConStant getInstance(Context context) {
        if (null == instance) {
            instance = new ConStant(context);
        }
        return instance;
    }

    public ConStant(Context context){
        String user = (String) SPUtils.get(context,ConStant.USERID,"");
        String token = (String) SPUtils.get(context,ConStant.USER_TOKEN,"");
        ConStant.userID = user;
        ConStant.Token = token;
    }



}
