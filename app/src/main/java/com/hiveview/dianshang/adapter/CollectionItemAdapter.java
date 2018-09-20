package com.hiveview.dianshang.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.collection.CollectionMain;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.collection.CollectionRecord;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.List;

import rx.Observable;
import rx.Subscriber;


public class CollectionItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "collection";

    private Context mContext;
    private List<CollectionRecord> mDatas;


    private int oldtotalSize = 0;

    /**
     * 选择ID
     */
    private int selectItemId = -1;

    /**
     * 是否编辑模式
     */
    private boolean isEditMode = false;

    //header布局标志
    private static final int TYPE_HEADER=0;
    //普通布局标志
    public static final int TYPE_NORMAL=1;

    //headerview的布局
    private View mHeaderView;

    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;

    private CollectionMain collectionMain;

    /**
     * 正常模式加载图标，特殊模式不加载图标
     */
    private boolean isLoadIconURL = true;

    private Observable<Boolean> icon_load_observer;

    public CollectionItemAdapter(Context context, List<CollectionRecord> mDatas,OnItemViewSelectedListener itemViewSelectedListener,
                                 OnItemKeyListener onItemKeyListener) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = itemViewSelectedListener;
        this.onItemKeyListener = onItemKeyListener;
        this.collectionMain = (CollectionMain)onItemKeyListener;
        initEvent();
    }


    /**
     * 分页添加的数据
     * @param entities
     */
    public void addData(List<CollectionRecord> entities) {
        if (null != entities) {
            oldtotalSize = mDatas.size();
            this.mDatas.addAll(entities);
            notifyItemRangeChanged(oldtotalSize,entities.size());
        }
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new HeadViewHolder(mHeaderView);

        return new RecyclerViewHolder(View.inflate(mContext, R.layout.collection_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (getItemViewType(position) == TYPE_HEADER) {
            //Utils.print(tag,"p==="+position);
            //获取控制权
            final HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.top_menu.setTag(position+"");

            return;
        }else {
            final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

            CollectionRecord entity = mDatas.get(position-1);
            viewHolder.focusRelativeLayout.setTag(R.id.tag_listview_entity, entity);
            //viewHolder.icon.setTag("item"+(position-1));

            //处理下线商品状态
            if(entity.getMarketStatus()==ConStant.OFF_LINE){
                viewHolder.market_status.setVisibility(View.VISIBLE);
            }else{
                viewHolder.market_status.setVisibility(View.INVISIBLE);
            }

            Utils.print(tag,"pos------------"+position+",selectItemId="+selectItemId);
            viewHolder.focusRelativeLayout.setPostion(position-1);
            viewHolder.focusRelativeLayout.setType(ConStant.OP_COLLECTION);
            viewHolder.focusRelativeLayout.setEditMode(isEditMode);

            viewHolder.updateData(mDatas.get(position-1));

            if(isEditMode){
                if(selectItemId==position-1){
                    viewHolder.collection_delete_status.setVisibility(View.GONE);
                    viewHolder.collection_delete_icon.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.collection_delete_status.setVisibility(View.VISIBLE);
                    viewHolder.collection_delete_icon.setVisibility(View.GONE);
                }
            }else{
                viewHolder.collection_delete_status.setVisibility(View.GONE);
                viewHolder.collection_delete_icon.setVisibility(View.GONE);
            }


            viewHolder.layout_commodity.setVisibility(View.VISIBLE);
            viewHolder.layout_live.setVisibility(View.INVISIBLE);
            //商品图
            //Utils.print(tag, "url---->" + entity.getGoodsUrl());
            if(isLoadIconURL){
                FrescoHelper.setImage(viewHolder.icon2, Uri.parse(entity.getGoodsUrl()), new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.recoment_260_view), mContext.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)));
            }else {
                FrescoHelper.setImageResource(viewHolder.icon2,R.drawable.commodity_big_icon);
            }
            //商品播放器图标
            viewHolder.live_icon.setVisibility(View.INVISIBLE);
            //商品名称
            viewHolder.mName.setText(entity.getGoodsName());
            //Utils.print(tag, "name=" + entity.getGoodsName());
            //商品价格

            viewHolder.mPrice.setText("¥" + Utils.getPrice(entity.getGoodsPrice()));

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
        return mDatas.size() + 1;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView icon; //商品图标
        TextView mName; //商品名称
        TextView mPrice; //商品价格
        FocusRelativeLayout focusRelativeLayout; //焦点

        TextView mVideoName; //流名称
        ImageView live_icon; //流图标
        TextView market_status; //是否下架状态

        LinearLayout layout_commodity; //商品图文
        LinearLayout layout_live;  //商品流


        SimpleDraweeView collection_delete_status;//右上角图标
        SimpleDraweeView collection_delete_icon;//中间图标

        /**
         * 促销图章展示
         */
        TextView promotion_top1;
        TextView promotion_top2;

        SimpleDraweeView icon2;

        CollectionRecord record;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            mPrice = (TextView)itemView.findViewById(R.id.price);

            mVideoName = (TextView)itemView.findViewById(R.id.video_name);
            live_icon = (ImageView)itemView.findViewById(R.id.live_icon);

            layout_commodity = (LinearLayout)itemView.findViewById(R.id.layout_commodity_item);
            layout_live = (LinearLayout)itemView.findViewById(R.id.layout_live);

            market_status = (TextView)itemView.findViewById(R.id.market_status);

            collection_delete_status = (SimpleDraweeView)itemView.findViewById(R.id.delete_status);
            collection_delete_icon = (SimpleDraweeView)itemView.findViewById(R.id.delete_icon);

            promotion_top1 = (TextView)itemView.findViewById(R.id.promotion_top1);
            promotion_top2 = (TextView)itemView.findViewById(R.id.promotion_top2);

            focusRelativeLayout = (FocusRelativeLayout) itemView.findViewById(R.id.focus_layout);
            focusRelativeLayout.setOnItemSelectedListener(onItemViewSelectedListener);
            focusRelativeLayout.setOnClickListener(this);
            focusRelativeLayout.setOnItemkeyListener(onItemKeyListener);

            icon2 = (SimpleDraweeView)itemView.findViewById(R.id.icon2);
        }

        public void updateData(final CollectionRecord item) {
            this.record = item;
        }

        @Override
        public void onClick(View v) {
            if (isEditMode) {
                //cancel collection
                collectionMain.deleteFavoriteCommodity(record);
            } else {
                if (record.getMarketStatus() == ConStant.ON_LINE) {
                    CommodityInfomation.launch((Activity) mContext, record.getGoodsSn(), ConStant.COLLECTION_TO_INFO);
                } else {
                    ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.commodity_off_line));
                }
            }
        }
    }



    /**
     * 设置选中位置
     * @param id
     */
    public void setSelectItemId(int id){
        this.selectItemId = id;
    }


    /**
     * 设置编辑模式
     * @param editMode
     */
    public void setEditMode(boolean editMode){
        this.isEditMode = editMode;
    }


    /**
     * 获取编辑模式
     * @return
     */
    public boolean getEditMode(){
        return this.isEditMode;
    }


    /**
     * 移除数据
     * @param pos
     */
    public void remove(int pos){
        mDatas.remove(pos);
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


    private class HeadViewHolder extends RecyclerView.ViewHolder {


        RelativeLayout top_menu;

        LinearLayout layout_normal_mode;
        LinearLayout layout_edit_mode;

        HeadViewHolder(View itemView) {
            super(itemView);

            top_menu = (RelativeLayout)itemView.findViewById(R.id.top_menu);
            layout_normal_mode = (LinearLayout) itemView.findViewById(R.id.layout_normal_mode);
            layout_edit_mode = (LinearLayout) itemView.findViewById(R.id.layout_edit_mode);

        }
    }


    public List<CollectionRecord> getmDatas(){
        return mDatas;
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

        RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

        String[] promotion_type_title = promotion.split(",");
        //Utils.print(tag,"length="+promotion_type_title.length);
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
