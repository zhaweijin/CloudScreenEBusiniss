package com.hiveview.dianshang.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.home.LivePlay;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.showcommodity.CommodityCategory;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;


public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "ItemAdapter";

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

    //搜索结果
    private String search_key_name;
    private int search_key_value;

    //分类广告图
    private String category_img_url;

    /**
     * 记录从那个页面进入详情的,是搜索,首页,收藏还是订单等等
     */
    private int mFromType=-1;

    /**
     * 正常模式加载图标，特殊模式不加载图标
     */
    private boolean isLoadIconURL = true;

    private Observable<Boolean> icon_load_observer;


    /**
     * activity 调用
     * @param context
     * @param mDatas
     * @param mFromType
     */
    public ItemAdapter(Context context, List<CommodityRecored> mDatas,int mFromType) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = (OnItemViewSelectedListener)mContext;
        this.onItemKeyListener = (OnItemKeyListener)mContext;
        this.mFromType = mFromType;
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
    public ItemAdapter(Context context, List<CommodityRecored> mDatas,OnItemKeyListener onItemKeyListener,OnItemViewSelectedListener onItemViewSelectedListener,int mFromType) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = onItemViewSelectedListener;
        this.onItemKeyListener = onItemKeyListener;
        this.mFromType = mFromType;
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
        if (mHeaderView != null && viewType == TYPE_HEADER && mFromType==ConStant.HOME_TO_INFO)
            return new HeadViewHolder(mHeaderView);

        if (mHeaderView != null && viewType == TYPE_HEADER && mFromType==ConStant.PROMOTION_TO_INFO)
            return new HeadViewHolder(mHeaderView);

        if (mHeaderView != null && viewType == TYPE_HEADER && mFromType==ConStant.SEARCH_RESULT_TO_INFO)
            return new SearchResultHeadViewHolder(mHeaderView);

        if (mHeaderView != null && viewType == TYPE_HEADER && mFromType==ConStant.CATEGORY_TO_INFO)
            return new CategoryHeadViewHolder(mHeaderView);

        return new RecyclerViewHolder(View.inflate(mContext, R.layout.recycle_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Utils.print(tag,"view type="+getItemViewType(position)+",positon="+position);
        //仅展示文字，没有交互
        if (getItemViewType(position) == TYPE_HEADER && mFromType==ConStant.HOME_TO_INFO) return;

        if (getItemViewType(position) == TYPE_HEADER && mFromType==ConStant.PROMOTION_TO_INFO) return;

        //搜索结果展示
        if (getItemViewType(position) == TYPE_HEADER && mFromType==ConStant.SEARCH_RESULT_TO_INFO){
            //获取控制权
            final SearchResultHeadViewHolder resultHeadViewHolder = (SearchResultHeadViewHolder) holder;
            //搜索结果名字
            if(Utils.getStringLength(search_key_name)<=ConStant.SEARCH_RESULT_NAME_MAX_LENGTH){
                resultHeadViewHolder.result_key.setText("“  "+search_key_name+" ” ");
            }else{
                resultHeadViewHolder.result_key.setText(Utils.getShowKeyString(search_key_name,ConStant.SEARCH_RESULT_NAME_MAX_LENGTH));
            }
            //搜索结果数量
            String value = mContext.getResources().getString(R.string.search_result_number,search_key_value);
            resultHeadViewHolder.result_number.setText(value);

        }else if(getItemViewType(position) == TYPE_HEADER && mFromType==ConStant.CATEGORY_TO_INFO){
            //获取控制权
            final CategoryHeadViewHolder categoryHeadViewHolder = (CategoryHeadViewHolder) holder;
            if(category_img_url!=null){
                FrescoHelper.setImage(categoryHeadViewHolder.advertisment_icon, Uri.parse(category_img_url));
            }
        }else{  //普通商品展示

            //获取控制权
            final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            CommodityRecored entity = mDatas.get(position-1);
            viewHolder.focusRelativeLayout.setTag(R.id.tag_listview_entity, entity);
            viewHolder.focusRelativeLayout.setPostion(position-1);
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
                    FrescoHelper.setImage(viewHolder.icon, Uri.parse(entity.getProductImages()), new ResizeOptions(260, 180));
                }else {
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


    /**
     * 普通holder 仅展示文字提示，没有交互
     */
    private class HeadViewHolder extends RecyclerView.ViewHolder {
        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }



    private class SearchResultHeadViewHolder extends RecyclerView.ViewHolder {
        /**
         * 搜索关键字
         */
        TextView result_key;

        /**
         * 搜索结果商品数量
         */
        TextView result_number;

        SearchResultHeadViewHolder(View itemView) {
            super(itemView);

            result_key = (TextView) itemView.findViewById(R.id.result_key);
            result_number = (TextView)itemView.findViewById(R.id.result_number);
        }
    }



    private class CategoryHeadViewHolder extends RecyclerView.ViewHolder {


        SimpleDraweeView advertisment_icon;

        CategoryHeadViewHolder(View itemView) {
            super(itemView);

            advertisment_icon = (SimpleDraweeView) itemView.findViewById(R.id.advertisment_icon);

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


    @Override
    public int getItemViewType(int position) {
        if(mHeaderView==null) return TYPE_NORMAL;
        if(position==0)return TYPE_HEADER;//将header插入到顶部
        return TYPE_NORMAL;
    }


    /**
     * 搜索结果参数
     * @param search_key_name
     * @param search_key_value
     */
    public void setSearchResultData(String search_key_name,int search_key_value){
        this.search_key_name = search_key_name;
        this.search_key_value = search_key_value;
    }

    /**
     * 分类顶部广播图url
     * @param img_url
     */
    public void setCategory_img_url(String img_url){
        category_img_url = img_url;
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

        ItemAdapter.RecyclerViewHolder viewHolder = (ItemAdapter.RecyclerViewHolder) holder;

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
