package com.hiveview.dianshang.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryRecored;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.showcommodity.CommodityCategory;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;


public class RootCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "RootCategoryAdapter";

    private Context mContext;
    private List<OneLevelCategoryRecored> mDatas;
    private int oldtotalSize = 0;

    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;

    private int selectItemId = -1;


    /**
     * 正常模式加载图标，特殊模式不加载图标
     */
    private boolean isLoadIconURL = true;

    private Observable<Boolean> icon_load_observer;


    /**
     * fragement 调用
     *
     * @param context
     * @param mDatas
     * @param onItemKeyListener
     * @param onItemViewSelectedListener
     * @param mFromType
     */
    public RootCategoryAdapter(Context context, List<OneLevelCategoryRecored> mDatas, OnItemKeyListener onItemKeyListener, OnItemViewSelectedListener onItemViewSelectedListener) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = onItemViewSelectedListener;
        this.onItemKeyListener = onItemKeyListener;
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
                if (!isLoadIconURL && aBoolean) {
                    Utils.print(tag, "notifyItemRangeChanged");
                    notifyItemRangeChanged(0, getItemCount());
                }
                isLoadIconURL = aBoolean;
                Utils.print(tag, "isLoadIconURL==" + isLoadIconURL);
            }
        });

    }


    /**
     * 分页添加的数据
     *
     * @param entities
     */
    public void addData(List<OneLevelCategoryRecored> entities) {
        if (null != entities) {
            oldtotalSize = mDatas.size();
            this.mDatas.addAll(entities);
            notifyItemRangeChanged(oldtotalSize, entities.size());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(View.inflate(mContext, R.layout.category_main_view_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder)holder;
        OneLevelCategoryRecored entity = mDatas.get(position);
        recyclerViewHolder.focusLayout.setTag(R.id.tag_listview_entity, entity);
        recyclerViewHolder.focusLayout.setPostion(position);
        recyclerViewHolder.icon.setBackgroundResource(R.drawable.category_main_item_bg);

        if(isLoadIconURL){
            FrescoHelper.setImage(recyclerViewHolder.icon,Uri.parse(mDatas.get(position).getImgUrl()),new ResizeOptions(300,300));
        }else{
            FrescoHelper.setImageResource(recyclerViewHolder.icon,R.drawable.category_main_item_bg);
        }

        recyclerViewHolder.mName.setText(mDatas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        SimpleDraweeView icon; //商品分类图标
        TextView mName; //商品分类名称
        FocusRelativeLayout focusLayout;


        RecyclerViewHolder(View itemView) {
            super(itemView);
            focusLayout = (FocusRelativeLayout)itemView.findViewById(R.id.focus_layout);
            icon = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mName = (TextView) itemView.findViewById(R.id.text);
            focusLayout.setType(ConStant.CATEGORY_TO_INFO);
            focusLayout.setOnItemkeyListener(onItemKeyListener);
            focusLayout.setOnItemSelectedListener(onItemViewSelectedListener);

            focusLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OneLevelCategoryRecored entity = mDatas.get(selectItemId);

            Utils.print(tag, "onclick==" + entity.getName());
            Utils.print(tag, "sn==" + entity.getCategorySn());
            sendStatistics(entity);
            CommodityCategory.launch((MainActivity) mContext, entity.getNavImgUrl(), entity.getName(), entity.getCategorySn(), entity.getHeadImgUrl());

        }
    }

    /**
     * 发送一级分类标签埋点统计
     *
     * @param data
     */
    private void sendStatistics(OneLevelCategoryRecored data) {
        HashMap<String, String> simpleMap = new HashMap<String, String>();

        simpleMap.put("tabNo", "");
        simpleMap.put("actionType", "Ec3001");
        String info = "";
        info = info + "firstTabId=" + data.getCategorySn() + "&";  //商品分类SN
        info = info + "firstTabName=" + data.getName();  //商品名称
        simpleMap.put("actionInfo", info);
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }


    /**
     * 设置当前选择的商品位置
     *
     * @param id
     */
    public void setSelectItemId(int id) {
        this.selectItemId = id;
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
    public void unRegisterObserver() {
        RxBus.get().unregister(ConStant.obString_item_load_icon_url, icon_load_observer);
    }


}
