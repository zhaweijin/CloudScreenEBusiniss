package com.hiveview.dianshang.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.home.LivePlay;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.search.OpSearchKey;
import com.hiveview.dianshang.showcommodity.CommodityCategory;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.QrcodeUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.FocusRelativeLayout;
import com.hiveview.dianshang.view.HiveRecyclerView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hiveview.libcommon.SimpleTableConstant.TAG;


public class SearchMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "SearchMainAdapter";

    private Context mContext;
    private List<CommodityRecored> mDatas;
    private int oldtotalSize = 0;

    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;

    private int selectItemId = -1;

    //header布局标志
    private static final int TYPE_HEADER=0;
    //普通布局标志
    public static final int TYPE_NORMAL=1;

    //headerview的布局
    private View mHeaderView;

    /**
     * 历史关键字
     */
    private List<String> keydatas = new ArrayList<>();
    /**
     * 记录从那个页面进入详情的,是搜索,首页,收藏还是订单等等
     */
    private int mFromType=-1;

    private boolean defaultRequestFocus=false;

    private OpSearchKey opSearchKey;


    HiveRecyclerView mTvRecyclerView;

    /**
     * 正常模式加载图标，特殊模式不加载图标
     */
    private boolean isLoadIconURL = true;

    private Observable<Boolean> icon_load_observer;

    public void setmTvRecyclerView(HiveRecyclerView recyclerView){
        this.mTvRecyclerView = recyclerView;
    }

    /**
     * activity 调用
     * @param context
     * @param mDatas
     * @param mFromType
     */
    public SearchMainAdapter(Context context, List<CommodityRecored> mDatas, int mFromType) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = (OnItemViewSelectedListener)mContext;
        this.onItemKeyListener = (OnItemKeyListener)mContext;
        this.mFromType = mFromType;
        opSearchKey = new OpSearchKey();
        initEvent();
    }

    /**
     * fragement 调用
     * @param context
     * @param mDatas
     * @param onItemKeyListener
     * @param onItemViewSelectedListener
     * @param mFromType
     */
    public SearchMainAdapter(Context context, List<CommodityRecored> mDatas, OnItemKeyListener onItemKeyListener, OnItemViewSelectedListener onItemViewSelectedListener, int mFromType) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = onItemViewSelectedListener;
        this.onItemKeyListener = onItemKeyListener;
        this.mFromType = mFromType;
        opSearchKey = new OpSearchKey();
        initEvent();
    }

    public void initEvent() {
        icon_load_observer = RxBus.get().register(ConStant.obString_item_load_icon_url, Boolean.class);
        icon_load_observer.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                //恢复显示图片的时候刷新当前图片，使得看到的都显示
                if(!isLoadIconURL && aBoolean){
                    Utils.print(tag,"notifyItemRangeChanged");
                    notifyItemRangeChanged(1,getItemCount());
                }
                isLoadIconURL = aBoolean;
                Utils.print(tag,"isLoadIconURL=="+isLoadIconURL);
            }
        });

    }


    /**
     * 分页添加的数据
     * @param entities
     */
    public void addData(List<CommodityRecored> entities) {
        if (null != entities) {
            oldtotalSize = mDatas.size();
            this.mDatas.addAll(entities);
            notifyItemRangeChanged(oldtotalSize,entities.size());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER )
            return new SearchMainHeadViewHolder(mHeaderView);

        return new RecyclerViewHolder(View.inflate(mContext, R.layout.recycle_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Utils.print(tag,"search p==="+position);
        //搜索主页展示
        if (getItemViewType(position) == TYPE_HEADER){
            //获取控制权
            final SearchMainHeadViewHolder mainHeadViewHolder = (SearchMainHeadViewHolder) holder;

            //搜索动作
            RxView.clicks(mainHeadViewHolder.action_search)
                    .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                    .filter(new Func1<Void, Boolean>() {
                        @Override
                        public Boolean call(Void aVoid) {
                            if(mainHeadViewHolder.search_key.getText().toString().trim().equals("")){
                                ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_search_null_input));
                                return false;
                            }
                            return true;
                        }
                    })
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Utils.print(TAG,"search start");
                            opSearchKey.setKey(ConStant.SEARCH_RESULT);
                            opSearchKey.setValue(mainHeadViewHolder.search_key.getText().toString().trim());
                            RxBus.get().post(ConStant.obString_opt_search_natigation,opSearchKey);
                        }
                    });

            //搜索输入框软键盘直接搜索处理
            mainHeadViewHolder.search_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Utils.print(tag,"1111start search..."+actionId);

                    if (actionId == EditorInfo.IME_ACTION_SEARCH){
                        Utils.print(tag,"start search...");
                        InputMethodManager mng = (InputMethodManager)mContext
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                        mng.hideSoftInputFromWindow(mainHeadViewHolder.search_key.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if(mainHeadViewHolder.search_key.getText().toString().trim().equals("")){
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_search_null_input));
                            return false;
                        }
                        opSearchKey.setKey(ConStant.SEARCH_RESULT);
                        opSearchKey.setValue(mainHeadViewHolder.search_key.getText().toString().trim());
                        RxBus.get().post(ConStant.obString_opt_search_natigation,opSearchKey);
                        return true;
                    }
                    return false;
                }
            });

            //搜索输入框焦点处理
            mainHeadViewHolder.search_key.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Utils.print(tag,"search_key>>>>"+ "hasFoucs="+hasFocus);
                    if(hasFocus){
                        mTvRecyclerView.smoothScrollToPosition(0);
                        mainHeadViewHolder.layout_search_key.setBackgroundResource(R.drawable.search_input_bg);
                    }else{
                        mainHeadViewHolder.layout_search_key.setBackgroundResource(R.drawable.search_input_no_focus_bg);
                    }
                }
            });

            mainHeadViewHolder.action_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        mTvRecyclerView.smoothScrollToPosition(0);
                        mainHeadViewHolder.action_search.setTextColor(mContext.getResources().getColor(R.color.white));
                    }else{
                        mainHeadViewHolder.action_search.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                }
            });

            //处理返回事件
            mainHeadViewHolder.search_key.setOnKeyListener(onBackKeyListener);
            mainHeadViewHolder.action_search.setOnKeyListener(onBackKeyListener);


            initKeyView(mainHeadViewHolder.layout_contain_search_key);

        }else{  //普通商品展示

            //获取控制权
            final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            CommodityRecored entity = mDatas.get(position-1);
            viewHolder.focusRelativeLayout.setTag(R.id.tag_listview_entity, entity);


            viewHolder.icon.setBackgroundResource(R.drawable.commodity_big_icon);

            if (entity.getProductType() == ConStant.LIVE) {
                viewHolder.layout_commodity.setVisibility(View.INVISIBLE);
                viewHolder.layout_live.setVisibility(View.VISIBLE);
                //商品流图
                if(isLoadIconURL){
                    FrescoHelper.setImage(viewHolder.icon, Uri.parse(entity.getProductImages()), new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.recoment_260_view), mContext.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)));
                }else {
                    FrescoHelper.setImageResource(viewHolder.icon,R.drawable.commodity_big_icon);
                }
                //显示播放器图标
                viewHolder.live_icon.setVisibility(View.VISIBLE);
                //显示商品流名称
                viewHolder.mVideoName.setText(entity.getName());
            } else if (entity.getProductType() == ConStant.COMMODITY) {
                viewHolder.layout_commodity.setVisibility(View.VISIBLE);
                viewHolder.layout_live.setVisibility(View.INVISIBLE);
                //商品图
                //Utils.print(tag, "url---->" + entity.getProductImages());
                if(isLoadIconURL){
                    FrescoHelper.setImage(viewHolder.icon, Uri.parse(entity.getProductImages()), new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.recoment_260_view), mContext.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)));
                }else{
                    FrescoHelper.setImageResource(viewHolder.icon,R.drawable.commodity_big_icon);
                }
                //商品播放器图标
                viewHolder.live_icon.setVisibility(View.INVISIBLE);
                //商品名称
                viewHolder.mName.setText(entity.getName());
                //Utils.print(tag, "name=" + entity.getName());
                //商品价格

                viewHolder.mPrice.setText("¥" + Utils.getPrice(entity.getPrice()));


                if(entity.isPromotion()){
                    handPromotionShow(holder,entity.getPromotionType());
                }else{
                    viewHolder.promotion_top1.setVisibility(View.INVISIBLE);
                    viewHolder.promotion_top2.setVisibility(View.INVISIBLE);
                }
            }

            viewHolder.focusRelativeLayout.setPostion(position-1);
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size()+1;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        SimpleDraweeView icon; //商品图标
        TextView mName; //商品名称
        TextView mPrice; //商品价格
        FocusRelativeLayout focusRelativeLayout; //焦点

        TextView mVideoName; //流名称
        ImageView live_icon; //流图标
        TextView market_status; //是否下架状态

        LinearLayout layout_commodity; //商品图文
        LinearLayout layout_live;  //商品流

        /**
         * 促销图章展示
         */
        TextView promotion_top1;
        TextView promotion_top2;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            icon = (SimpleDraweeView)itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            mPrice = (TextView)itemView.findViewById(R.id.price);

            mVideoName = (TextView)itemView.findViewById(R.id.video_name);
            live_icon = (ImageView)itemView.findViewById(R.id.live_icon);

            layout_commodity = (LinearLayout)itemView.findViewById(R.id.layout_commodity_item);
            layout_live = (LinearLayout)itemView.findViewById(R.id.layout_live);

            promotion_top1 = (TextView)itemView.findViewById(R.id.promotion_top1);
            promotion_top2 = (TextView)itemView.findViewById(R.id.promotion_top2);

            focusRelativeLayout = (FocusRelativeLayout) itemView.findViewById(R.id.focus_layout);
            focusRelativeLayout.setOnItemSelectedListener(onItemViewSelectedListener);
            focusRelativeLayout.setOnClickListener(this);
            focusRelativeLayout.setOnItemkeyListener(onItemKeyListener);
        }

        @Override
        public void onClick(View v) {
            CommodityRecored commodityRecored = mDatas.get(selectItemId);
            Utils.print(tag,"type="+commodityRecored.getProductType());
            Utils.print(tag,"11sn=="+(commodityRecored).getGoodsSn());
            if(commodityRecored.getProductType()==ConStant.LIVE){
                if(mContext instanceof MainActivity){
                    LivePlay.launch((MainActivity) mContext, commodityRecored,mFromType);
                }else if(mContext instanceof CommodityCategory){
                    LivePlay.launch((CommodityCategory) mContext, commodityRecored,mFromType);
                }
            }else{
                CommodityInfomation.launch((Activity) mContext,commodityRecored.getGoodsSn(),mFromType);
            }

        }
    }



    public class SearchMainHeadViewHolder extends RecyclerView.ViewHolder {

        /**
         * 搜索输入
         */
        private EditText search_key;

        /**
         * 搜索条背景
         */
        private LinearLayout layout_search_key;

        /**
         * 搜索按钮
         */
        private Button action_search;

        /**
         * 历史搜索词容器
         */
        private RelativeLayout layout_contain_search_key;


        SearchMainHeadViewHolder(View itemView) {
            super(itemView);
            search_key = (EditText)itemView.findViewById(R.id.search_key);
            action_search = (Button)itemView.findViewById(R.id.action_search);
            layout_contain_search_key = (RelativeLayout)itemView.findViewById(R.id.layout_contain_search_key);
            layout_search_key = (LinearLayout)itemView.findViewById(R.id.layout_search_key);

        }
    }



    /**
     * 设置当前选择的商品位置
     * @param id
     */
    public void setSelectItemId(int id){
        this.selectItemId = id;
    }

    /**
     * 判断当前位置是否是头部
     * @param position
     * @return
     */
    public boolean isHeader(int position) {
        return position == 0;
    }


    public View getmHeaderView() {
        return mHeaderView;
    }

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
    }

    public View getSearchRecommendView(){
        return mHeaderView.findViewById(R.id.search_main_view);
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView==null) return TYPE_NORMAL;
        if(position==0)return TYPE_HEADER;//将header插入到顶部
        return TYPE_NORMAL;
    }


    /**
     * 设置搜索历史词
     * @param datas
     */
    public void setSearchKeyData(List<String> datas){
        this.keydatas = datas;
    }


    View.OnKeyListener onBackKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //Utils.print(tag,"onback");
                if(mTvRecyclerView.getVerticalScrollbarPosition()!=0){
                    Utils.print(tag,"scrool top");
                    mTvRecyclerView.smoothScrollToPosition(0);
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    RxBus.get().post(ConStant.obString_search_recommend_back,"back");
                    return  true;
                }
            }
            return false;
        }
    };


    /**
     * 处理超长key显示的效果，超过7个字符．增加...号，然后焦点选择时滚动显示
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                ((Button)v).setTextColor(mContext.getResources().getColor(android.R.color.white));
            }else{
                ((Button)v).setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
        }
    };


    /**
     * 历史搜索词点击
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            opSearchKey.setKey(ConStant.SEARCH_RESULT);
            opSearchKey.setValue(((Button)v).getText().toString().trim());
            RxBus.get().post(ConStant.obString_opt_search_natigation,opSearchKey);
        }
    };


    private void initKeyView(RelativeLayout view){
        List<Button> button_keys = new ArrayList<>();
        TextView search_no_key=null;
        for (int i = 0; i < view.getChildCount(); i++) {
            if(view.getChildAt(i) instanceof LinearLayout){
                LinearLayout layout1 = (LinearLayout) view.getChildAt(i);
                button_keys.add((Button) layout1.getChildAt(0));
                button_keys.add((Button) layout1.getChildAt(1));
                button_keys.add((Button) layout1.getChildAt(2));
                button_keys.add((Button) layout1.getChildAt(3));
            }else if(view.getChildAt(i) instanceof TextView){
                search_no_key = (TextView) view.getChildAt(i);
            }
        }


        Utils.print(tag,"button size=="+button_keys.size());
        //展示没有历史搜索词
        if(keydatas.size()<=0){
            for (int i = 0; i < button_keys.size(); i++) {
                button_keys.get(i).setVisibility(View.GONE);
            }
            if(search_no_key!=null)
                search_no_key.setVisibility(View.VISIBLE);
        }


        //展示搜索词
        for (int i = 0; i < keydatas.size(); i++) {
            button_keys.get(i).setText(keydatas.get(i));
            button_keys.get(i).setOnClickListener(onClickListener);
            button_keys.get(i).setOnKeyListener(onBackKeyListener);
            button_keys.get(i).setOnFocusChangeListener(onFocusChangeListener);
        }

        if(keydatas.size()<8){
            for (int i = keydatas.size(); i < 8; i++) {
                button_keys.get(i).setVisibility(View.GONE);
            }
        }

    }


    public boolean isLoadIconURL() {
        return isLoadIconURL;
    }

    public void setLoadIconURL(boolean loadIconURL) {
        isLoadIconURL = loadIconURL;
    }

    /**
     * 取消注册
     */
    public void unRegisterObserver(){
        RxBus.get().unregister(ConStant.obString_item_load_icon_url,icon_load_observer);
    }


    /**
     * 处理促销图章的展示
     * @param holder
     * @param promotion
     */
    private void handPromotionShow(RecyclerView.ViewHolder holder, String promotion){

        SearchMainAdapter.RecyclerViewHolder viewHolder = (SearchMainAdapter.RecyclerViewHolder) holder;

        String[] promotion_type_title = promotion.split(",");
        Utils.print(tag,"length="+promotion_type_title.length);
        if(promotion_type_title.length>=2){
            viewHolder.promotion_top1.setVisibility(View.VISIBLE);
            viewHolder.promotion_top2.setVisibility(View.VISIBLE);

            viewHolder.promotion_top1.setText(Utils.promotionTypeTranform(mContext,promotion_type_title[0]));
            viewHolder.promotion_top2.setText(Utils.promotionTypeTranform(mContext,promotion_type_title[1]));
        }else if(promotion_type_title.length==1){
            viewHolder.promotion_top1.setVisibility(View.INVISIBLE);
            viewHolder.promotion_top2.setVisibility(View.VISIBLE);
            viewHolder.promotion_top2.setText(Utils.promotionTypeTranform(mContext,promotion_type_title[0]));
        }else {
            viewHolder.promotion_top1.setVisibility(View.INVISIBLE);
            viewHolder.promotion_top2.setVisibility(View.INVISIBLE);
        }
    }
}
