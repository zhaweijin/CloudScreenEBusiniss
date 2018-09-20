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
import com.hiveview.dianshang.entity.special.SpecialRecored;
import com.hiveview.dianshang.home.LivePlay;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.showcommodity.CommoditySpecial;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.List;

import rx.Observable;
import rx.Subscriber;


public class SpecialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "ItemAdapter";

    private Context mContext;
    private List<SpecialRecored> mDatas;
    private int oldtotalSize = 0;

    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;

    private int selectItemId = -1;

    /**
     * 记录从那个页面进入详情的,是搜索,首页,收藏还是订单等等
     */
    private int mFromType=-1;


    //header布局标志
    private static final int TYPE_HEADER=0;
    //普通布局标志
    public static final int TYPE_NORMAL=1;

    //headerview的布局
    private View mHeaderView;


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
    public SpecialAdapter(Context context, List<SpecialRecored> mDatas,int mFromType) {
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
    public SpecialAdapter(Context context, List<SpecialRecored> mDatas, OnItemKeyListener onItemKeyListener, OnItemViewSelectedListener onItemViewSelectedListener, int mFromType) {
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
    public void addData(List<SpecialRecored> entities) {
        if (null != entities) {
            oldtotalSize = mDatas.size();
            this.mDatas.addAll(entities);
            notifyItemRangeChanged(oldtotalSize,entities.size());
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new HeadViewHolder(mHeaderView);
        return new RecyclerViewHolder(View.inflate(mContext, R.layout.recycle_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //仅展示文字，没有交互
        if (getItemViewType(position) == TYPE_HEADER) return;

        final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

        SpecialRecored entity = mDatas.get(position-1);
        viewHolder.focusRelativeLayout.setTag(R.id.tag_listview_entity, entity);
        viewHolder.focusRelativeLayout.setPostion(position-1);
        viewHolder.icon.setBackgroundResource(R.drawable.commodity_big_icon);

        if (entity.getProductType() == ConStant.LIVE) {
            viewHolder.layout_commodity.setVisibility(View.INVISIBLE);
            viewHolder.layout_live.setVisibility(View.VISIBLE);
            //商品流图
            if(isLoadIconURL){
                FrescoHelper.setImage(viewHolder.icon, Uri.parse(entity.getProductImages()), new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.recoment_260_view), mContext.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)));
            }else{
                FrescoHelper.setImageResource(viewHolder.icon,R.drawable.commodity_big_icon);
            }
            //显示播放器图标
            viewHolder.live_icon.setVisibility(View.VISIBLE);
            //显示商品流名称
            viewHolder.mVideoName.setText(entity.getGoodsName());
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
            viewHolder.mName.setText(entity.getGoodsName());
            //Utils.print(tag, "name=" + entity.getGoodsName());
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
            SpecialRecored specialRecored = mDatas.get(selectItemId);
            Utils.print(tag,"type="+specialRecored.getProductType());
            Utils.print(tag,"11sn=="+(specialRecored).getGoodsSn());
            if(specialRecored.getMarketStatus()==ConStant.OFF_LINE){
                ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.commodity_off_line));
                return;
            }
            if(specialRecored.getProductType()==ConStant.LIVE){
                LivePlay.launch((CommoditySpecial) mContext, specialRecored,mFromType);
            }else{
                CommodityInfomation.launch((Activity) mContext,specialRecored.getGoodsSn(),mFromType);
            }
        }
    }




    public void setSelectItemId(int id){
        this.selectItemId = id;
    }


    /**
     * 普通holder 仅展示文字提示，没有交互
     */
    private class HeadViewHolder extends RecyclerView.ViewHolder {
        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView==null) return TYPE_NORMAL;
        if(position==0)return TYPE_HEADER;//将header插入到顶部
        return TYPE_NORMAL;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }


    public View getmHeaderView() {
        return mHeaderView;
    }

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
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

        SpecialAdapter.RecyclerViewHolder viewHolder = (SpecialAdapter.RecyclerViewHolder) holder;

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
