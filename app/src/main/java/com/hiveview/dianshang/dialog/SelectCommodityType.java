package com.hiveview.dianshang.dialog;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemTypeSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetail;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetailData;
import com.hiveview.dianshang.entity.commodity.type.CommodityTypeData;
import com.hiveview.dianshang.entity.commodity.type.PromotionActiivty;
import com.hiveview.dianshang.entity.commodity.type.SkuList;
import com.hiveview.dianshang.entity.commodity.type.SpecItemList;
import com.hiveview.dianshang.entity.commodity.type.SpecList;
import com.hiveview.dianshang.entity.shoppingcart.DirectShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoData;
import com.hiveview.dianshang.shoppingcart.ShoppingCartGrid;
import com.hiveview.dianshang.shoppingcart.ShoppingCartList;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.showcommodity.PromotionCommodity;
import com.hiveview.dianshang.showcommodity.PromotionInformation;
import com.hiveview.dianshang.utils.ActivityManagerApplication;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.view.CircleImageView;
import com.hiveview.dianshang.view.TypeFaceTextView;
import com.hiveview.dianshang.view.TypeFocusRelativeLayout;
import com.hiveview.dianshang.view.TypeGallery;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * 商品展示主页，根据不同的分类选择，展示不同的商品列表信息
 * Created by carter on 4/17/17.
 */
public class SelectCommodityType extends BDialog implements OnItemTypeSelectedListener{


    private String tag = "SelectCommodityType";

    private LayoutInflater mFactory = null;
    private Context context;

    @BindView(R.id.layout_contain)
    LinearLayout layout_contain;

    @BindView(R.id.layout_select_commodity_number)
    RelativeLayout layout_select_commodity_number;

    @BindView(R.id.homebuy_tips)
    TextView homebuy_tips;

    @BindView(R.id.commodity_number)
    TextView commodity_number;

    @BindView(R.id.title_commodity_number)
    TextView title_commodity_number;

    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    @BindView(R.id.arrow_right)
    ImageView arrow_right;


    @BindView(R.id.layout_select_commodity_contain)
    LinearLayout layout_select_commodity_contain;

    @BindView(R.id.commodity_item_information)
    LinearLayout commodity_item_information;

    @BindView(R.id.layout_shopping)
    LinearLayout layout_shopping;



    private List<LinearLayout> layout_types = new ArrayList<>();
    private List<ImageView> item_focus = new ArrayList<>();
    private List<TypeGallery> item_gallery = new ArrayList<>();


    /**
     * 颜色规格适配器
     */
    private ColorAdapter colorAdapter;


    private ArrayList<Integer> select_id = new ArrayList<>();

    private int actionType = 1;
    public final static int SELECT_COMMODITY = 1;
    public final static int MODIFY_COMMODITY = 2;

    //商品详情数据
    private CommodityDetail commodityDetail;


    private int goods_number = 1; //当前选择的商品数量
    private int goods_max_number = 0; //最大库存
    private int canbuy_number=0;  //限购数量
    /**
     * 商品sn
     */
    private String goods_sn ="";
    /**
     * 商品类型sn
     */
    private String goods_Sku_sn = "";

    //是否参与限购
    private boolean isHomeBuy=false;

    /**
     * 购物车item sn
     */
    private String cartSn;

    /**
     * 选择的商品类型名称
     */
    private String specItemValues ="";

    /**
     * 选择商品的item sn
     */
    private String specItemSns;

    private int skuID = -1;

    /**
     * 加入购物车焦点
     */
    private RelativeLayout layout_shopping_cart;

    //加入购物车文字
    private TextView add_to_shopping_cart_text;
    //加入购物车图标
    private ImageView add_to_shopping_cart_icon;
    /**
     * 确认并结算焦点
     */
    private RelativeLayout layout_jiesuan;
    //直接购买文字
    private TextView direct_buy_text;
    //直接购买图标
    private ImageView direct_buy_icon;

    /**
     * 确认修改焦点
     */
    private RelativeLayout layout_modify;
    //修改商品文字
    private TextView commodity_modify_text;
    //修改商品图标
    private ImageView commodity_modify_icon;


    @BindView(R.id.commodity_icon)
    SimpleDraweeView commodity_icon;

    /**
     * 商品名称
     */
    @BindView(R.id.commodity_name)
    TextView commodity_name;

    /**
     * 商品标题
     */
    @BindView(R.id.commodity_title)
    TextView commodity_title;

    /**
     * 商品收藏状态
     */
    @BindView(R.id.commodity_collecton_status)
    ImageView commodity_collecton_status;

    /**
     * 商品促销价格
     */
    @BindView(R.id.commodity_new_price)
    TextView commodity_new_price;

    /**
     * 商品原价
     */
    @BindView(R.id.commodity_old_price)
    TextView commodity_old_price;


    /**
     * 商品支持的信誉
     */
    @BindView(R.id.commodity_support)
    TextView commodity_support;

    /**
     * 商品的物流价格
     */
    @BindView(R.id.commodity_express_price)
    TextView commodity_express_price;

    /**
     * 整屏的滚动view
     */
    @BindView(R.id.scrollview)
    ScrollView scrollview;

    /**
     * 促销策略展示
     */
    @BindView(R.id.layout_sale_promotion)
    LinearLayout layout_sale_promotion;

    /**
     * 促销详情
     */
    @BindView(R.id.promotion_info_show)
    Button promotion_info_show;

    List<SkuList> skuLists = new ArrayList<>();
    List<SpecList> specLists = new ArrayList<>();
    List<SpecList> validSpecLists = new ArrayList<>();
    private boolean uiLoadFinished = false;


    private ShoppingCartRecord shoppingCartRecord;


    /**
     * 埋点统计需要
     */
    private final int ADD_SHOPPING_CART = 1;
    private final int JIESUAN = 2;

    /**
     * 删除商品确认对话框
     */
    ConfirmDialog confirmDialog;

    /**
     * 服务器交互标志位
     */
    private boolean op_status = false;


    private AddShoppingCartTipDialog addShoppingCartTipDialog;

    /**
     * 直接购买存放的数据
     */
    private DirectShoppingCartData directShoppingCartData;

    private String oldSelectSpecItemSn = "";



    public SelectCommodityType(Context context,int type,int actionType) {
        super(context, type);
        this.context = context;
        this.actionType = actionType;

        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.select_commodity_type, null);
        setContentView(mView);


        ButterKnife.bind(this);
        setOnDismissListener(this);

        initView();



    }



    private void initView(){

        commodity_collecton_status.setVisibility(View.INVISIBLE);
        layout_select_commodity_number.setOnKeyListener(onNumberKeyListener);
        layout_select_commodity_number.setOnFocusChangeListener(onTextFocus);

        layout_select_commodity_contain.removeAllViews();
        View view =null;
        if(actionType==SELECT_COMMODITY){
            view = mFactory.inflate(R.layout.layout_select_commodity_button, null);
            layout_jiesuan = (RelativeLayout) view.findViewById(R.id.layout_jiesuan);
            layout_shopping_cart = (RelativeLayout)view.findViewById(R.id.layout_shopping_cart);
            direct_buy_text = (TextView)view.findViewById(R.id.direct_buy_text);
            direct_buy_icon = (ImageView)view.findViewById(R.id.direct_buy_icon);
            add_to_shopping_cart_text = (TextView)view.findViewById(R.id.add_to_shopping_cart_text);
            add_to_shopping_cart_icon = (ImageView)view.findViewById(R.id.add_to_shopping_cart_icon);

            layout_jiesuan.setOnClickListener(onClickListener);
            layout_shopping_cart.setOnClickListener(onClickListener);

            layout_jiesuan.setOnKeyListener(onButtonKeyListener);
            layout_shopping_cart.setOnKeyListener(onButtonKeyListener);
            layout_jiesuan.setOnFocusChangeListener(onTextFocus);
            layout_shopping_cart.setOnFocusChangeListener(onTextFocus);

            layout_select_commodity_number.setNextFocusDownId(R.id.layout_jiesuan);
        }else if(actionType==MODIFY_COMMODITY){
            view = mFactory.inflate(R.layout.layout_modity_commodity_button, null);
            layout_modify = (RelativeLayout)view.findViewById(R.id.layout_modify);
            commodity_modify_text = (TextView)view.findViewById(R.id.commodity_modify_text);
            commodity_modify_icon = (ImageView)view.findViewById(R.id.commodity_modify_icon);

            layout_modify.setOnClickListener(onClickListener);
            layout_modify.setOnKeyListener(onButtonKeyListener);
            layout_modify.setOnFocusChangeListener(onTextFocus);

        }
        layout_select_commodity_contain.addView(view);
    }

    /**
     * 选择商品类型对话框
     * @param sn
     */
    public void showUI(String sn){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        show();

        this.goods_sn = sn;
        getCommodityDetail();


        layout_select_commodity_number.setVisibility(View.INVISIBLE);
        layout_select_commodity_contain.setVisibility(View.INVISIBLE);

    }


    /**
     * 修改商品类型对话框
     * @param shoppingCartRecord
     */
    public void showFullScreenUI(ShoppingCartRecord shoppingCartRecord){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        show();

        this.shoppingCartRecord = shoppingCartRecord;
        this.goods_sn = shoppingCartRecord.getGoodsSn();
        this.cartSn = shoppingCartRecord.getCartSn();

        //获取商品图标等详细信息
        getCommodityDetail();


        layout_select_commodity_number.setVisibility(View.INVISIBLE);
        layout_select_commodity_contain.setVisibility(View.INVISIBLE);

    }



    /**
     * 类型view onkey事件
     */
    View.OnKeyListener onNumberKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Utils.print(tag,"key2");
                if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(layout_select_commodity_number.isFocused()){
                        Utils.print(tag,">>>>"+keyCode);
                        if(!iSelectTypeOk())
                            return true;
                        goods_number++;
                        if(goods_number>goods_max_number){
                            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
                            goods_number--;
                            return true;
                        }

                        if(isHomeBuy && goods_number>canbuy_number){
                            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_homebuy));
                            goods_number--;
                            return true;
                        }

                        commodity_number.setText(goods_number+"");
                        return true;
                    }
                }else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    if(layout_select_commodity_number.isFocused()){
                        Utils.print(tag,">>>>"+keyCode);
                        if(!iSelectTypeOk())
                            return true;
                        goods_number--;
                        if(goods_number<=0){
                            goods_number++;
                            return true;
                        }

                        commodity_number.setText(goods_number+"");
                        return true;
                    }
                }
            }
            return false;
        }
    };


    View.OnFocusChangeListener onTextFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.layout_select_commodity_number:
                    if(hasFocus){
                        arrow_left.setBackgroundResource(R.drawable.reduce_commodity_white);
                        arrow_right.setBackgroundResource(R.drawable.add_commodity_white);
                        for (int i = 0; i < item_focus.size(); i++) {
                            item_focus.get(i).setBackgroundResource(R.drawable.select_commodity_item_type_no_focus);
                        }
                        title_commodity_number.setTextColor(context.getResources().getColor(R.color.white));
                        commodity_number.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        arrow_left.setBackgroundResource(R.drawable.reduce_commodity);
                        arrow_right.setBackgroundResource(R.drawable.add_commodity);
                        title_commodity_number.setTextColor(context.getResources().getColor(R.color.black));
                        commodity_number.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    break;
                case R.id.layout_modify:
                    if(hasFocus){
                        scrollButtom();
                        commodity_modify_text.setTextColor(context.getResources().getColor(R.color.white));
                        commodity_modify_icon.setBackgroundResource(R.drawable.select_commodity_modity_white);
                    }else{
                        commodity_modify_text.setTextColor(context.getResources().getColor(R.color.black));
                        commodity_modify_icon.setBackgroundResource(R.drawable.select_commodity_modity);
                    }
                    break;
                case R.id.layout_jiesuan:
                    Utils.print(tag,">>>>"+hasFocus);
                    if(hasFocus){
                        direct_buy_text.setTextColor(context.getResources().getColor(R.color.white));
                        direct_buy_icon.setBackgroundResource(R.drawable.select_commodity_accounts_white);
                        //scrollButtom();
                    }else{
                        direct_buy_text.setTextColor(context.getResources().getColor(R.color.black));
                        direct_buy_icon.setBackgroundResource(R.drawable.select_commodity_accounts);
                    }
                    break;
                case R.id.layout_shopping_cart:
                    if(hasFocus){
                        //scrollButtom();
                        add_to_shopping_cart_text.setTextColor(context.getResources().getColor(R.color.white));
                        add_to_shopping_cart_icon.setBackgroundResource(R.drawable.select_commodity_type_shopping_cart_icon_white);
                    }else{
                        add_to_shopping_cart_text.setTextColor(context.getResources().getColor(R.color.black));
                        add_to_shopping_cart_icon.setBackgroundResource(R.drawable.select_commodity_type_shopping_cart_icon);
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.print(tag,"dialog back");
        RxBus.get().post(ConStant.obString_select_commodity_type,"back");
    }


    /**
     * 按钮操作
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mProgressDialog!=null && mProgressDialog.isShowing()){
                return;
            }
            switch (v.getId()){
                case R.id.layout_jiesuan:
                    jiesuanOperate();
                    break;
                case R.id.layout_modify:
                    Utils.print(tag,"modify shopping cart");
                    if(!iSelectTypeOk())
                        return;
                    updateShoppingCartCommodity();
                    break;
                case R.id.layout_shopping_cart:
                    Utils.print(tag,"add shopping cart");
                    if(!iSelectTypeOk())
                        return;
                    addShoppingCartCommodity();
                    break;
            }
        }
    };


    View.OnKeyListener onButtonKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction()==KeyEvent.ACTION_DOWN){
                if(v.getId()==R.id.layout_jiesuan && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    return true;
                }else if(v.getId()==R.id.layout_shopping_cart && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    return  true;
                }else if(v.getId()==R.id.layout_modify && (keyCode==KeyEvent.KEYCODE_DPAD_LEFT || keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)){
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * 结算操作(直接支付，不经过购物车)
     */
    private void jiesuanOperate(){
        Utils.print(tag,"jiesuan");
        if(commodityDetail==null)
            return;

        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;

        if(!iSelectTypeOk())
            return;

        if(goods_max_number==0){
            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
            return;
        }


        if(goods_number>goods_max_number){
            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
            return;
        }

        Utils.print(tag,"sku="+skuID+",goods_Sku_sn="+goods_Sku_sn);

        if(goods_Sku_sn.equals("")){
            ToastUtils.showToast(context,context.getResources().getString(R.string.sku_offline));
            return;
        }

        if(skuID>=0){
            sendOnclickStatistics(JIESUAN);
            Utils.print(tag,"skuID="+skuID);
            directShoppingCartData.setSkuInfo(skuLists.get(skuID));
            directShoppingCartData.setGoodskuSn(goods_Sku_sn);
            directShoppingCartData.setQuantity(goods_number);
            directShoppingCartData.setGoodName(commodityDetail.getName());

            Utils.print(tag,"id..."+directShoppingCartData.getReceiveId());
            if(directShoppingCartData.getReceiveId()!=-1){
                getShoppingItemInfoData();  //直接购买先获取价格数据
            }else{
                startDirectCommitOrder();
            }
        }
    }


    private void startDirectCommitOrder(){
        ShoppingCartList.launch((CommodityInfomation)context,ConStant.DIRECT_PAYMENT,directShoppingCartData);
        dismiss();
    }


    /**
     * 显示类型选择UI
     * @param commodityTypeData
     */
    private void handCommodityTypeLayout(CommodityTypeData commodityTypeData){

        Utils.print(tag,"handCommodityTypeLayout");
        skuLists = commodityTypeData.getData().getSkuList();
        if(skuLists==null || skuLists.size()==0){
            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
            RxBus.get().post(ConStant.obString_select_commodity_type,"back");
            dismiss();
            return;
        }

        //数据与UI对应排序,颜色选择放到最底层
        specLists.addAll(commodityTypeData.getData().getSpecList());
        for(int i=0;i<specLists.size();i++) {
            //init valid data
            //初始化仅对第一行数据进行查找，其余数据不动
            if(i==0){
                //print old
                for (int j = 0; j < specLists.get(0).getSpecItemList().size(); j++) {
                    Utils.print(tag,"old type name="+specLists.get(0).getSpecItemList().get(j).getName());
                }

                SpecList validSpecList = new SpecList();
                validSpecList.setName(specLists.get(i).getName());
                validSpecList.setType(specLists.get(i).getType());
                validSpecList.setSpecSn(specLists.get(i).getSpecSn());
                List<SpecItemList> specItemLists = new ArrayList<>();
                validSpecList.setSpecItemList(specItemLists);


                //循环第一行当中的多个类型
                for(SpecItemList list:specLists.get(i).getSpecItemList()){
                    String itemSN = list.getSpecItemSn();
                    if(specLists.size()>1){
                        itemSN = itemSN+",";
                    }

                    Utils.print(tag,"compare sn="+itemSN);
                    //查找sku是否存在
                    for (int j = 0; j < skuLists.size(); j++) {
                        Utils.print(tag,"compare sku sn="+skuLists.get(j).getSpecItemSns());
                        if(specLists.size()>1){
                            if(skuLists.get(j).getSpecItemSns().contains(itemSN)){
                                validSpecList.getSpecItemList().add(list);
                                break;
                            }
                        }else{
                            if(skuLists.get(j).getSpecItemSns().equals(itemSN)){
                                validSpecList.getSpecItemList().add(list);
                                break;
                            }
                        }
                    }
                }

                //print result
                for (int j = 0; j < validSpecList.getSpecItemList().size(); j++) {
                    Utils.print(tag,"valid type name="+validSpecList.getSpecItemList().get(j).getName());
                }
                validSpecLists.add(validSpecList);
            }else{
                validSpecLists.add(specLists.get(i));
            }
        }



        int typesize = validSpecLists.size();
        layout_contain.removeAllViews();
        int p=0;
        for(int i=0;i<typesize;i++){
            if(validSpecLists.get(i).getType().equals("common")){

                Utils.print(tag,">>"+i+",type="+validSpecLists.get(i).getType());
                View view = mFactory.inflate(R.layout.commodity_type_layout, null);
                ImageView focus = (ImageView) view.findViewById(R.id.focus);
                TypeGallery gallery = (TypeGallery)view.findViewById(R.id.gallery_type);

                //初始化添加
                select_id.add(-1);
                item_focus.add(focus);
                item_gallery.add(gallery);
                gallery.setTag(p+"");

                //初始化适配
                TextAdapter textAdapter = new TextAdapter(validSpecLists.get(i).getSpecItemList(),p);
                gallery.setAdapter(textAdapter);
                textAdapter.notifyDataSetChanged();

                //默认选中行
                if(actionType==SELECT_COMMODITY){
                    defaultSelection(i);
                }

                p++;
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lParams.setMargins(0,5,0,0);
                layout_contain.addView(view,lParams);
            }else if(validSpecLists.get(i).getType().equals("color")){

                Utils.print(tag,">>"+i+",type="+validSpecLists.get(i).getType());
                View view = mFactory.inflate(R.layout.commodity_type_layout, null);
                ImageView focus = (ImageView) view.findViewById(R.id.focus);
                TypeGallery colorgalley = (TypeGallery)view.findViewById(R.id.gallery_type);


                select_id.add(-1);
                item_focus.add(focus);
                item_gallery.add(colorgalley);
                colorgalley.setTag(p+"");

                colorAdapter = new ColorAdapter(validSpecLists.get(i).getSpecItemList(),p);
                colorgalley.setAdapter(colorAdapter);
                colorAdapter.notifyDataSetChanged();

                //默认选中行
                if(actionType==SELECT_COMMODITY){
                    defaultSelection(i);
                }

                p++;
                layout_contain.addView(view);
            }
        }

        item_gallery.get(0).requestFocus();
    }


    public class TextAdapter extends BaseAdapter {
        private List<SpecItemList> lists;
        private LayoutInflater mInflater;

        /**
         * 标注当前是那个gallery
         */
        private int item;

        public class ViewHolder {
            Button name;

            TypeFocusRelativeLayout commodity_item_type;
        }

        public TextAdapter(List<SpecItemList> lists,int item) {
            this.lists = lists;
            this.item = item;
            mInflater = LayoutInflater.from(context);
        }

        public int getCount()  {
            return lists.size();
        }

        public Object getItem(int position)  {
            return position;
        }

        public long getItemId(int position)  {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.select_commodity_item_textview, null);
                holder = new ViewHolder();
                holder.name = (Button) convertView.findViewById(R.id.item_type_textview);
                holder.commodity_item_type = (TypeFocusRelativeLayout) convertView.findViewById(R.id.commodity_item_type);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final int pos = position;

            //Utils.print(tag,"pos>>>>"+lists.get(position).getName());
            holder.name.setText(lists.get(position).getName());
            holder.commodity_item_type.setOnItemTypeSelectedListener(SelectCommodityType.this);
            holder.commodity_item_type.setData(TypeFocusRelativeLayout.GENERAL_TYPE,item,position);


            holder.commodity_item_type.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                   Utils.print(tag,"onKey.....");
                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                        if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT && pos==0){
                            return true;
                        }else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT && pos==lists.size()-1){
                            return true;
                        }
                    }

                    return false;
                }
            });

            return convertView;
        }
    }



    public class ColorAdapter extends BaseAdapter {
        private List<SpecItemList> list;
        private LayoutInflater mInflater;

        /**
         * 标注当前是那个gallery
         */
        private int item;


        public class ViewHolder {
            CircleImageView color;
            Button name;

            TypeFocusRelativeLayout layout_color_item;

        }

        public ColorAdapter(List<SpecItemList> list,int item) {
            this.list = list;
            this.item = item;
            mInflater = LayoutInflater.from(context);
        }

        public int getCount()  {
            return list.size();
        }

        public Object getItem(int position)  {
            return position;
        }

        public long getItemId(int position)  {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.select_commodity_color_item_textview, null);
                holder = new ViewHolder();
                holder.name = (Button) convertView.findViewById(R.id.item_type_textview);
                holder.color = (CircleImageView) convertView.findViewById(R.id.item_color);
                holder.layout_color_item = (TypeFocusRelativeLayout)convertView.findViewById(R.id.layout_color_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final int pos = position;

            holder.name.setText(list.get(position).getName());
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(list.get(position).getColor()));
            holder.color.setImageDrawable(colorDrawable);

            holder.layout_color_item.setOnItemTypeSelectedListener(SelectCommodityType.this);
            holder.layout_color_item.setData(TypeFocusRelativeLayout.COLOR_TYPE,item,position);


            holder.layout_color_item.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(pos==0 || pos==list.size()-1){
                        return true;
                    }
                    return false;
                }
            });

            return convertView;
        }
    }


    /**
     * 获取商品详细信息
     */
    public void getCommodityDetail(){
        Utils.print(tag,"getCommodityDetail");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            dismiss();
            return;
        }

        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("goodsSn",goods_sn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetCommodityDetailData(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityDetailData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"111error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityDetailData commodityDetailData) {
                        Utils.print(tag,"status=="+commodityDetailData.getErrorMessage());
                        stopProgressDialog();
                        if(commodityDetailData.getReturnValue()==-1){
                            ToastUtils.showToast(context,commodityDetailData.getErrorMessage());
                            return;
                        }

                        List<String> imageurls = commodityDetailData.getData().getDetailUrl();
                        /*for(int i=0;i<imageurls.size();i++){
                            Utils.print(tag,"name="+imageurls.get(i));
                        }*/


                        initCommodityInfoData(commodityDetailData.getData());

                        //获取默认用户地址
                        getUserDefaultAddress();


                        //获取商品所有类型
                        getCommoditySkuInfo();
                    }
                });
        addSubscription(s);
    }


    /**
     * 初始化显示商品价格等信息
     * @param commodityDetail
     */
    private void initCommodityInfoData(CommodityDetail commodityDetail) {

        this.commodityDetail = commodityDetail;
        //Utils.print(tag,"icon path="+commodityDetail.getProductImages());
        FrescoHelper.setImage(commodity_icon, Uri.parse(commodityDetail.getProductImages()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.commodityinfomation_300),context.getResources().getDimensionPixelSize(R.dimen.commodityinfomation_300)));
        commodity_name.setText(commodityDetail.getName());
        commodity_title.setText(commodityDetail.getProductTitle());
        String price = Utils.getPrice(commodityDetail.getPrice());
        commodity_new_price.setText("¥"+price);
        commodity_old_price.setText("¥"+Utils.getPrice(commodityDetail.getMarketPrice()));

        if(commodityDetail.isSupportFlag()){
            commodity_support.setVisibility(View.VISIBLE);
        }else{
            commodity_support.setVisibility(View.INVISIBLE);
        }
        commodity_support.setText(commodityDetail.getSupport().get(0));
		/**
		*RMB换￥
		*/
        commodity_express_price.setText(context.getResources().getString(R.string.express_title) + "¥"+ Utils.getPrice(commodityDetail.getDeliveryPrice()));


        //设置商品价格横线
        commodity_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

    }



    /**
     * 获取商品类型信息
     */
    public void getCommoditySkuInfo(){
        Utils.print(tag,"getCommoditySkuInfo");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn",goods_sn);
            json.put("userid",ConStant.getInstance(context).userID);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetCommoditySkuInfoData(input,ConStant.getInstance(context).Token) //
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityTypeData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        dismiss();
                        Utils.print(tag,"getCommoditySkuInfo error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityTypeData commodityTypeData) {
                        Utils.print(tag,"getCommoditySkuInfo status=="+commodityTypeData.getErrorMessage()+",value="+commodityTypeData.getReturnValue());
                        if(commodityTypeData.getReturnValue()==-1){
                            ToastUtils.showToast(context,commodityTypeData.getErrorMessage());
                            layout_select_commodity_number.setVisibility(View.VISIBLE);
                            layout_select_commodity_contain.setVisibility(View.VISIBLE);
                            dismiss();
                            return;
                        }

                        try{

                            handCommodityTypeLayout(commodityTypeData);


                            uiLoadFinished = true;
                            //处理修改商品默认选择
                            if(actionType==MODIFY_COMMODITY){
                                handDefaultModityType();
                            }
                            handleLimitBuyTips(true);
                            layout_select_commodity_number.setVisibility(View.VISIBLE);
                            layout_select_commodity_contain.setVisibility(View.VISIBLE);


                            updateCommoditySelect();


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        addSubscription(s);

    }





    /**
     * 获取已选择种类商品的库存数量
     */
    private void updateCommoditySelect(){
        Utils.print(tag,"updateCommoditySelect");
        boolean isUpdate =true;
        Utils.print(tag,"size>>"+select_id.size());

        for(int i=0;i<select_id.size();i++){
            if(select_id.get(i)==null || select_id.get(i)==-1){
                isUpdate = false;
                break;
            }
        }

        /*for(int i=0;i<select_id.size();i++){
            Utils.print(tag,"id=="+select_id.get(i));
        }*/

        if(!isUpdate || !uiLoadFinished)
            return;

        for(int i=0;i<select_id.size();i++){
            Utils.print(tag,"id=="+select_id.get(i));
        }


        ArrayList<String> specSN = new ArrayList<>();
        for(int i=0;i<validSpecLists.size();i++){
            Utils.print(tag,"i=="+i+",selectid="+select_id.get(i));
            Utils.print(tag,"itemsize="+validSpecLists.get(i).getSpecItemList().size());
            specSN.add(validSpecLists.get(i).getSpecItemList().get(select_id.get(i)).getSpecItemSn());
        }


        Utils.print(tag,"111select sn="+specSN.toString());

        for(int i=0;i<skuLists.size();i++){
            if(checkSpecSnEqual(specSN,skuLists.get(i).getSpecItemSns())){
                goods_max_number = skuLists.get(i).getAvailableStock();

                String new_price= Utils.getPrice(skuLists.get(i).getDiscountPrice());
                commodity_old_price.setText("¥"+Utils.getPrice(skuLists.get(i).getMarketPrice()));
                commodity_new_price.setText("¥"+new_price);

                //Utils.print(tag,"==="+skuLists.get(i).getImgUrl());
                FrescoHelper.setImage(commodity_icon, Uri.parse(skuLists.get(i).getImgUrl()),new ResizeOptions(300,300));
                goods_Sku_sn = skuLists.get(i).getGoodskuSn();
                specItemValues = skuLists.get(i).getSpecItemValues();
                specItemSns = skuLists.get(i).getSpecItemSns();
                skuID = i;

                //处理限购情况下,最大库存的更新,
                //must befor handle goods_max_number
                handleLimitBuyTips(true);
                isHomeBuy = false;
                if(skuLists.get(i).isPromotion() && checkIsLimitBuy(skuLists.get(i).getPromotionActList()) &&  getHomeBuyNumber(skuLists.get(i).getPromotionActList())!=-1){
                    isHomeBuy = true;
                    updateAvailableStock(skuLists.get(i).getPromotionActList());
                    if(goods_number>canbuy_number){
                        goods_number = canbuy_number;
                        commodity_number.setText(goods_number+"");
                    }
                }




                if(goods_number>goods_max_number){
                    goods_number = goods_max_number;
                    commodity_number.setText(goods_number+"");
                }

                if(goods_number==0 && goods_max_number!=0){
                    goods_number = 1;
                    commodity_number.setText(goods_number+"");
                }

                //处理促销信息的展示
                Utils.print(tag,"ispromotion="+skuLists.get(i).isPromotion());
                if(skuLists.get(i).isPromotion()){
                    Utils.print(tag,"111");
                    handlePromotionView(skuLists.get(i).getPromotionActList());
                    handPromotionButton();
                }else{
                    LinearLayout.LayoutParams layoutParams  = (LinearLayout.LayoutParams) commodity_item_information.getLayoutParams();
                    layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.select_commodity_item_information_height_230);
                    commodity_item_information.setLayoutParams(layoutParams);
                    promotion_info_show.setVisibility(View.INVISIBLE);
                    layout_sale_promotion.removeAllViews();
                }
                break;
            }
        }
        Utils.print(tag,"max number="+goods_max_number+",goods sku sn="+goods_Sku_sn);


        //检测当前规格是否已经下架
        /*if(!checkSkuExist(specSN.toString())){
            skuID=-1;
            goods_Sku_sn="";
            ToastUtils.showToast(context,context.getResources().getString(R.string.sku_offline));
        }*/



    }


    /**
     * 检测是否有限购促销类型
     * @param list
     * @return
     */
    private boolean checkIsLimitBuy(List<PromotionActiivty> list){
        boolean limit = false;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getSaleType().equals(ConStant.LIMIT_BUY)){
                limit = true;
                break;
            }
        }
        return limit;
    }


    /**
     * 更新最大库存
     * @param list
     */
    private void updateAvailableStock(List<PromotionActiivty> list){
        Utils.print(tag,"updateAvailableStock");
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getSaleType().equals(ConStant.LIMIT_BUY)){
                int buynumber = list.get(i).getCanBuyNum();
                canbuy_number = buynumber;
                if(buynumber<=0){
                    handleLimitBuyTips(false);
                }
                break;
            }
        }
    }

    /**
     * 获取限购规格的数量
     * @param list
     * @return
     */
    private int getHomeBuyNumber(List<PromotionActiivty> list){
        Utils.print(tag,"getHomeBuyNumber");
        int number = Integer.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getSaleType().equals(ConStant.LIMIT_BUY)){
                int buynumber = list.get(i).getCanBuyNum();
                number = buynumber;
                break;
            }
        }
        return number;
    }



    private boolean checkSkuExist(String specSN){
        boolean exist = false;
        Utils.print(tag,"specSN1==="+specSN);
        specSN = specSN.replace("[","").replace("]","").replace(" ","");
        Utils.print(tag,"specSN2==="+specSN);
        for (int i = 0; i < skuLists.size(); i++) {
            Utils.print(tag,"----"+skuLists.get(i).getSpecItemSns());
            if(specSN.equals(skuLists.get(i).getSpecItemSns())){
                Utils.print(tag,"exist");
                exist = true;
            }
        }
        return exist;
    }


    /**
     * 判断获取正确类型的商品sn
     * @param list
     * @param sn
     * @return
     */
    private boolean checkSpecSnEqual(ArrayList<String> list,String sn){
        boolean eq = true;
        //Utils.print(tag,"size="+list.size()+",sn="+sn);
        for(int i=0;i<list.size();i++){
            if(!sn.contains(list.get(i))){
                eq = false;
                break;
            }
        }
        return eq;
    }




    /**
     * 添加购物车商品
     */
    public void addShoppingCartCommodity(){
        Utils.print(tag,"addShoppingCartCommodity");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        if(goods_Sku_sn.equals("")){
            ToastUtils.showToast(context,context.getResources().getString(R.string.sku_offline));
            return;
        }

        if(goods_max_number==0){
            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
            return;
        }

        if(goods_number>goods_max_number){
            ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_sku_out_of_stock));
            return;
        }

        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;

        //埋点统计
        sendOnclickStatistics(ADD_SHOPPING_CART);

        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("goodsSn",goods_sn);
            json.put("goodsSkuSn",goods_Sku_sn);
            json.put("quantity",goods_number);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpAddShoppingcartData(ConStant.APP_VERSION,input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"addShoppingCartCommodity error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"addShoppingCartCommodity status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status=false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }
                        //发送广播，更新商品总价和数量的数据
                        Intent mIntent = new Intent("com.dianshang.damai.shopping");
                        mIntent.putExtra("tag","add");
                        //发送广播
                        context.sendBroadcast(mIntent);
                        showAddShoppingCartTips();
                        //一旦加入购物车成功，购物车数据需要刷新
                        ShoppingCartGrid.isRefresh=true;
                    }
                });
        addSubscription(s);
    }





    /**
     * 更新购物车商品
     */
    public void updateShoppingCartCommodity(){
        Utils.print(tag,"updateShoppingCartCommodity");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;

        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("cartItemId",shoppingCartRecord.getCartItemId());
            json.put("goodsSkuSn",goods_Sku_sn);
            json.put("cartSn",cartSn);
            json.put("goodsSn",goods_sn);
            json.put("quantity",goods_number);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpUpdateShoppingcartData(ConStant.APP_VERSION,input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"updateShoppingCartCommodity error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"updateShoppingCartCommodity status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status=false;

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }

                        Utils.print(tag,"2222");
                        Log.i("====sss====","specItemSns=="+specItemSns);
                        RxBus.get().post(ConStant.obString_update_shopping_cart,specItemSns);
                        RxBus.get().post(ConStant.obString_select_commodity_type,"back");
                        dismiss();
                    }
                });
        addSubscription(s);
    }


    /**
     * 检测是否所有类型都选择了,如果没有提示不可继续后面的操作
     * @return
     */
    private boolean iSelectTypeOk(){
        for(int i=0;i<select_id.size();i++){
            if(select_id.get(i)==-1){
                ToastUtils.showToast(context,context.getResources().getString(R.string.please_select_commodity_type));
                return false;
            }
        }
        return true;
    }




    /**
     * 购物车编辑商品类型时,默认之前的选择状态
     */
    private void handDefaultModityType(){
        Utils.print(tag,"handDefaultModityType");
        String[] specSnsList = shoppingCartRecord.getSpecSns().split(",");
        Utils.print(tag,"specValue="+shoppingCartRecord.getSpecValue());
        for(int i=0;i<specSnsList.length;i++){
            Utils.print(tag,"specSns="+specSnsList[i]);
            for(int x=0;x<validSpecLists.size();x++){
                List<SpecItemList> itemLists = validSpecLists.get(x).getSpecItemList();
                for(int y=0;y<itemLists.size();y++){
                    if(itemLists.get(y).getSpecItemSn().equals(specSnsList[i])){
                        Utils.print(tag,"set x="+x+",y="+y);
                        select_id.set(x,y);
                        break;
                    }
                }
            }
        }

        for(int i=0;i<select_id.size();i++){
            Utils.print(tag,"seleceID===="+i+",pos="+select_id.get(i));
        }


        for (int i = 0; i < item_gallery.size(); i++) {
            int tempID = select_id.get(i);
            if (tempID == -1)
                continue;
            Utils.print(tag, "ss===" + i + ",setid=" + tempID);
            item_gallery.get(i).setSelection(tempID);
        }


        //处理已购买数量
        goods_number = shoppingCartRecord.getBuyNum();

        //检测是否有限购，如果有且超过限购，直接修改购买数量为
        if(shoppingCartRecord.getLimitActivity()!=null && shoppingCartRecord.getLimitActivity().getLimitNum()!=-1){
            int canBuyNumber = shoppingCartRecord.getLimitActivity().getLimitNum()-shoppingCartRecord.getLimitActivity().getBuyNum();
            Utils.print(tag,"canBuyNumber======="+canBuyNumber);
            if(shoppingCartRecord.getBuyNum()>canBuyNumber){
                goods_number = canBuyNumber;
            }
        }

        Utils.print(tag,"num=="+goods_number);
        commodity_number.setText(goods_number+"");
    }


    /**
     * 发送确认并结算或者加入购物车 埋点统计到服务器
     * @param type
     */
    private void sendOnclickStatistics(int type){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec4003");
        String info = "mode="+type;
        simpleMap.put("actionInfo",info);

        EBusinessApplication.getHSApi().addAction(simpleMap);
    }



    @Override
    public void OnItemTypeSelectedListener(int item, int itemPosition, int type) {
        Utils.print(tag, "type=" + type + ",item=" + item + ",itemposition=" + itemPosition);
        for (int i = 0; i < item_focus.size(); i++) {
            item_focus.get(i).setBackgroundResource(R.drawable.select_commodity_item_type_no_focus);
        }
        item_focus.get(item).setBackgroundResource(R.drawable.select_commodity_item_type_focus);
        select_id.set(item, itemPosition);

        item_gallery.get(item).setItemSize(validSpecLists.get(item).getSpecItemList().size()-1);

        updateCommoditySelect();



        String newSelectSpecItemSn = validSpecLists.get(item).getSpecItemList().get(itemPosition).getSpecItemSn();
        Utils.print(tag,"newSelectSpecItemSn=="+newSelectSpecItemSn);
        Utils.print(tag,"select sn="+getSelectTypeSn(item));
        if(!oldSelectSpecItemSn.equals(newSelectSpecItemSn)){
            //update all down adapter
            oldSelectSpecItemSn = newSelectSpecItemSn;
            handAutoSelect(item,getSelectTypeSn(item));
        }


    }







    private String getSelectTypeSn(int item){
        String selectSpecSn = "";
        Utils.print(tag,"getSelectTypeSn item="+item);
        /*for (int i = 0; i <= item; i++) {
            selectSpecSn = selectSpecSn + validSpecLists.get(i).getSpecItemList().get(select_id.get(i)).getSpecItemSn()+",";
        }*/

        for (int i = 0; i < specLists.size(); i++) {
            if(select_id.get(i)!=-1){
                selectSpecSn = selectSpecSn + validSpecLists.get(i).getSpecItemList().get(select_id.get(i)).getSpecItemSn()+",";
            }
        }


        if(selectSpecSn.length()>1){
            selectSpecSn = selectSpecSn.substring(0,selectSpecSn.length()-1);
        }

        return selectSpecSn;
    }


    private void handAutoSelect(int item,String specItemSn){

        Utils.print(tag,"handAutoSelect item="+item);
        //焦点在最后一行不做任何处理
        if(item==specLists.size()-1)
            return;


        List<SpecList> tmpSpecLists = new ArrayList<>();
        tmpSpecLists.addAll(validSpecLists);
        validSpecLists.clear();

        for (int i = 0; i < specLists.size(); i++) {
            Utils.print(tag,"1i=="+i);
            //不更新行数据处理
            if(i!=item+1){
                validSpecLists.add(tmpSpecLists.get(i));
                continue;
            }
            //仅更新当前行的下一个行数据
            Utils.print(tag,"2i=="+i+",specItemSn="+specItemSn);
            String[] itemSns= specItemSn.split(",");
            if(itemSns.length>item+1){
                Utils.print(tag,"is move up scrool");
                specItemSn="";
                for (int j = 0; j < item+1; j++) {
                    specItemSn = specItemSn + itemSns[j] +",";
                }
                specItemSn = specItemSn.substring(0,specItemSn.lastIndexOf(","));
                Utils.print(tag,"new specItemSn="+specItemSn);
            }
            //初始化
            SpecList tmpSpecList = specLists.get(i);
            SpecList newSpecList = new SpecList();
            newSpecList.setName(tmpSpecList.getName());
            newSpecList.setType(tmpSpecList.getType());
            newSpecList.setSpecSn(tmpSpecList.getSpecSn());
            List<SpecItemList> specItemLists = new ArrayList<>();
            newSpecList.setSpecItemList(specItemLists);

            //循环一行当中的多个类型
            for(SpecItemList list:tmpSpecList.getSpecItemList()){
                String itemSN = list.getSpecItemSn();
                if(i==specLists.size()-1){
                    itemSN = specItemSn + "," + itemSN;
                }else{
                    itemSN = specItemSn + "," + itemSN+",";
                }

                Utils.print(tag,"ready compare sn="+itemSN);
                //查找sku是否存在
                for (int j = 0; j < skuLists.size(); j++) {
                    //Utils.print(tag,"22=="+skuLists.get(j).getSpecItemSns());
                    if(skuLists.get(j).getSpecItemSns().contains(itemSN)){
                        Utils.print(tag,"compare ok");
                        newSpecList.getSpecItemList().add(list);
                        break;
                    }
                }
            }

            //print result
            for (int j = 0; j < newSpecList.getSpecItemList().size(); j++) {
                Utils.print(tag,"valid type name="+newSpecList.getSpecItemList().get(j).getName());
            }

            validSpecLists.add(newSpecList);

            if(newSpecList.getType().equals("common")){
                TextAdapter textAdapter = new TextAdapter(validSpecLists.get(i).getSpecItemList(),i);
                item_gallery.get(i).setAdapter(textAdapter);
                textAdapter.notifyDataSetChanged();
            }else if(newSpecList.getType().equals("color")){
                colorAdapter = new ColorAdapter(validSpecLists.get(i).getSpecItemList(),i);
                item_gallery.get(i).setAdapter(colorAdapter);
                colorAdapter.notifyDataSetChanged();
            }

            //默认选中
            Utils.print(tag,"select id="+select_id.get(i));
            if(select_id.get(i)==-1){
                defaultSelection(i);
            }else{
                Utils.print(tag,"count="+item_gallery.get(i).getAdapter().getCount());
                if(select_id.get(i)>=item_gallery.get(i).getAdapter().getCount()){
                    Utils.print(tag,"22="+i);
                    int defaultID = 0;
                    if (validSpecLists.get(i).getSpecItemList().size()%2 == 0) {
                        defaultID = validSpecLists.get(i).getSpecItemList().size()/2-1;
                    }else{
                        defaultID = validSpecLists.get(i).getSpecItemList().size()/2;
                    }
                    select_id.set(i,defaultID);
                    item_gallery.get(i).setSelection(defaultID);
                }else{
                    item_gallery.get(i).setSelection(select_id.get(i));
                }
            }
        }


    }


    private void defaultSelection(int i){
        if (validSpecLists.get(i).getSpecItemList().size()%2 == 0) {
            item_gallery.get(i).setSelection(validSpecLists.get(i).getSpecItemList().size() / 2-1);
        }else{
            item_gallery.get(i).setSelection(validSpecLists.get(i).getSpecItemList().size() / 2);
        }
    }


    private void showAddShoppingCartTips(){
        if(addShoppingCartTipDialog!=null)
            return;
        addShoppingCartTipDialog = new AddShoppingCartTipDialog(context, new AddShoppingCartTipDialog.ConfirmOnClickListener() {
            @Override
            public void onContinuePayment() {
                Utils.print(tag,"onContinuePayment");
                addShoppingCartTipDialog.dismiss();
                addShoppingCartTipDialog=null;
            }

            @Override
            public void onExit() {
                //进入购物车
                ActivityManagerApplication.getInstance().finishActivity(ShoppingCartGrid.class);
                ShoppingCartGrid.launch((CommodityInfomation)context);
                addShoppingCartTipDialog.dismiss();
                addShoppingCartTipDialog=null;
                RxBus.get().post(ConStant.obString_select_commodity_type,"back");
                dismiss();
                ActivityManagerApplication.getInstance().finishActivity(CommodityInfomation.class);
                ActivityManagerApplication.getInstance().finishActivity(PromotionCommodity.class);
            }

            @Override
            public void onDismiss() {
                if(addShoppingCartTipDialog!=null){
                    addShoppingCartTipDialog.dismiss();
                    addShoppingCartTipDialog=null;
                }

            }
        });

        addShoppingCartTipDialog.setTitle(context.getResources().getString(R.string.add_commodity_to_shoppingcart_sucess_title));
        addShoppingCartTipDialog.setOkName(context.getResources().getString(R.string.continue_shopping));
        addShoppingCartTipDialog.setCancelName(context.getResources().getString(R.string.enter_shoppingcart));



        if(addShoppingCartTipDialog!=null && !addShoppingCartTipDialog.isShowing())
           addShoppingCartTipDialog.showUI();
    }



    /**
     * 获取默认地址
     */
    public void getUserDefaultAddress(){
        Utils.print(tag,"getUserAddressList");
        directShoppingCartData = new DirectShoppingCartData();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription s = RetrofitClient.getAddressAPI()
                .httpGetUserAddressListData(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserAddress>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error>>>>"+e.getMessage());
                    }

                    @Override
                    public void onNext(UserAddress userAddress) {
                        Utils.print(tag,"address status="+userAddress.getReturnValue()+",message="+userAddress.getErrorMessage());
                        if(userAddress.getReturnValue()==-1){
                            return;
                        }

                        Utils.print(tag,"address get data=="+userAddress.getErrorMessage());
                        Utils.print(tag,"total count="+userAddress.getData().getTotalCount());
                        List<UserData> userDatas = userAddress.getData().getRecords();


                        for(int i=0;i<userDatas.size();i++){
                            Utils.print(tag,"isdefault=="+userDatas.get(i).getDefault());
                            if(userDatas.get(i).getDefault()){
                                String receiveName = userDatas.get(i).getConsignee();
                                String receivePhone = userDatas.get(i).getPhone();
                                String detailAddress = userDatas.get(i).getAddressProvince()+userDatas.get(i).getAddressTown()+userDatas.get(i).getAddressDetail();

                                String address_info = receiveName+"     "+receivePhone+"\n"+detailAddress;
                                int receiveId = userDatas.get(i).getId();

                                directShoppingCartData.setAddress_info(address_info);
                                directShoppingCartData.setReceiveId(receiveId);

                                Utils.print(tag,"address="+userDatas.get(i).getAddressDetail());
                                Utils.print(tag,"address id="+userDatas.get(i).getId());
                                break;
                            }
                        }
                    }
                });
        addSubscription(s);
    }


    /**
     * 获取直接支付总价等详情信息
     */
    public void getShoppingItemInfoData(){
        Utils.print(tag,"getShoppingItemInfoData");
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("goodsSkuSn",directShoppingCartData.getSkuInfo().getGoodskuSn());
            json.put("quantity",directShoppingCartData.getQuantity());
            json.put("receiveId",directShoppingCartData.getReceiveId());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetShoppingItemInfoData(ConStant.APP_VERSION,input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"22error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartInfoData shoppingCartInfoData) {
                        Utils.print(tag,"shop status=="+shoppingCartInfoData.getErrorMessage()+",value="+shoppingCartInfoData.getReturnValue());
                        stopProgressDialog();
                        if(shoppingCartInfoData.getReturnValue()==-1){
                            ToastUtils.showToast(context,shoppingCartInfoData.getErrorMessage());
                            return;
                        }
                        try{
                            directShoppingCartData.setShoppingCartInfo(shoppingCartInfoData.getData());

                            startDirectCommitOrder();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        addSubscription(s);
    }


    /**
     * 处理每个sku的促销策略展示
     */
    private void handlePromotionView(List<PromotionActiivty> list){
        Utils.print(tag,"handlePromotionView,size="+list.size());
        layout_sale_promotion.removeAllViews();
        if(list==null)
            return;
        for (int i = 0; i < list.size(); i++) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.sku_sale_promotion_item,null);
            TextView promotion_type_name = (TextView)itemView.findViewById(R.id.promotion_type_name);
            TextView promotion_type_description = (TextView)itemView.findViewById(R.id.promotion_type_description);
            promotion_type_name.setText(Utils.promotionTypeTranform(context,list.get(i).getSaleType()));
            promotion_type_description.setText(list.get(i).getTitle());
            layout_sale_promotion.addView(itemView);
        }


        LinearLayout.LayoutParams layoutParams  = (LinearLayout.LayoutParams) commodity_item_information.getLayoutParams();
        if(list.size()<=2){
            layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.information_sale_promotion_item_h1_270);
        }else if(list.size()==3){
            layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.information_sale_promotion_item_h2_295);
        }else if(list.size()==4){
            layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.information_sale_promotion_item_h3_330);
        }
        commodity_item_information.setLayoutParams(layoutParams);
    }


    /**
     * 处理限购view 的提示展示，达到限购条件的直接隐藏购买选项，否则正常显示
     */
    private void handleLimitBuyTips(boolean isShow){
        Utils.print(tag,"2222isShow=="+isShow);
        if(isShow){
            layout_shopping.setVisibility(View.VISIBLE);
            homebuy_tips.setVisibility(View.INVISIBLE);
        }else{
            homebuy_tips.setVisibility(View.VISIBLE);
            layout_shopping.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 促销按钮是否显示
     */
    private void handPromotionButton(){
        Utils.print(tag,"handPromotionButton");
        promotion_info_show.setVisibility(View.VISIBLE);
        promotion_info_show.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT || keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    return true;
                }else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction()==KeyEvent.ACTION_DOWN){
                    Utils.print(tag,"promotion button on key down="+select_id.get(0));
                    //item_gallery.get(0).getChildAt(select_id.get(0)).requestFocus();
                    item_gallery.get(0).setSelection(select_id.get(0));
                }
                return false;
            }
        });
        promotion_info_show.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    for (int i = 0; i < item_focus.size(); i++) {
                        item_focus.get(i).setBackgroundResource(R.drawable.select_commodity_item_type_no_focus);
                    }
                    promotion_info_show.setTextColor(context.getResources().getColor(R.color.white));
                }else{
                    promotion_info_show.setTextColor(context.getResources().getColor(R.color.black));
                    item_focus.get(0).setBackgroundResource(R.drawable.select_commodity_item_type_focus);
                }
            }
        });

        RxView.clicks(promotion_info_show)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Utils.print(tag,"go promotion show");
                        if(actionType==MODIFY_COMMODITY){
                            PromotionInformation.launch((ShoppingCartGrid)context, goods_Sku_sn,1);
                        }else if(actionType==SELECT_COMMODITY){
                            PromotionInformation.launch((CommodityInfomation)context, goods_Sku_sn,1);
                        }
                    }
                });
    }




    /**
     * scrollview滚到底部
     * @return
     */
    private void scrollButtom(){
        Utils.print(tag,"isButtom="+isscrollButtom());
        if(isscrollButtom())
            return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                if(actionType==SELECT_COMMODITY){
                    layout_jiesuan.requestFocus();
                }else if(actionType==MODIFY_COMMODITY){
                    layout_modify.requestFocus();
                }
            }
        },0);
    }

    /**
     * 判断scrollview是否已经滚到底部
     * @return
     */
    private boolean isscrollButtom(){
        View childView = scrollview.getChildAt(0);
        Utils.print(tag,"MeasuredHeight=="+childView.getMeasuredHeight());
        Utils.print(tag,"y=="+scrollview.getScrollY());
        Utils.print(tag,"h=="+scrollview.getHeight());
        if(childView.getMeasuredHeight() <= scrollview.getScrollY() + scrollview.getHeight()){
            return true;
        }
        return false;
    }
}
