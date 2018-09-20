package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.AcutionListener;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.base.OnLongKeyUpListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.acution.common.AcutionItemData;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;


public class AcutionItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "ItemAdapter";

    private Context mContext;
    private List<AcutionItemData> mDatas;

    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;
    private OnLongKeyUpListener onLongKeyUpListener;
    private ItemClick itemClick;

    private int selectItemId = -1;

    private LinkedHashMap<Integer,CommodityCountDownTimer> progressMap = new LinkedHashMap<>();

    //header布局标志
    private static final int TYPE_HEADER = 0;
    //普通布局标志
    public static final int TYPE_NORMAL = 1;

    //headerview的布局
    private View mHeaderView;

    /**
     * 记录从那个页面进入详情的,是搜索,首页,收藏还是订单等等
     */
    private int mFromType = -1;

    /**
     * 正常模式加载图标，特殊模式不加载图标
     */
    private boolean isLoadIconURL = true;

    private Observable<Boolean> icon_load_observer;


    private AcutionListener acutionListener;

    /**
     * fragement 调用
     *
     * @param context
     * @param mDatas
     * @param onItemKeyListener
     * @param onItemViewSelectedListener
     * @param mFromType
     */
    public AcutionItemAdapter(Context context, List<AcutionItemData> mDatas,
                              OnItemViewSelectedListener onItemViewSelectedListener, OnItemKeyListener onItemKeyListener,
                              AcutionListener acutionListener,ItemClick itemClick,int mFromType) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemViewSelectedListener = onItemViewSelectedListener;
        this.onItemKeyListener = onItemKeyListener;
        this.acutionListener = acutionListener;
        this.itemClick = itemClick;
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
                onLongKeyUpListener.onLongItemKeyUp();
                if (!isLoadIconURL && aBoolean) {
                    //Utils.print(tag, "notifyItemRangeChanged");
                    notifyItemRangeChanged(1, getItemCount());
                }
                isLoadIconURL = aBoolean;
                Utils.print(tag, "isLoadIconURL==" + isLoadIconURL);
            }
        });

    }


    /**
     * 添加一组数据
     *
     * @param entities
     */
    public void addAllData(List<AcutionItemData> entities) {
        if (null != entities) {
            mDatas.addAll(entities);
        }
    }

    /**
     * 添加单独的数据
     * @param entity
     */
    public void addData(AcutionItemData entity) {
        if (null != entity) {
            mDatas.add(entity);
        }
    }

    /**
     * 添加单独的数据到顶部
     * @param entity
     */
    public void addDataToTop(AcutionItemData entity) {
        if (null != entity) {
            mDatas.add(0,entity);
        }
    }


    public synchronized void replaceData(int pos,AcutionItemData entity){
        mDatas.set(pos,entity);
    }

    /**
     * 刷新所有的
     */
    public void notifyGlobalUpdate(){
        notifyItemRangeChanged(1, getItemCount());
    }


    private boolean test(String url,String name){
        boolean exist = true;
        if (!TextUtils.isEmpty(url)) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            Uri uri = Uri.parse(url);
            if (imagePipeline.isInBitmapMemoryCache(uri)) {
                 Utils.print(tag,name+" bitmap in memory cache");
            }else{
                Utils.print(tag,name+" bitmap not in memory cache");
                exist=false;
            }
        }
        return exist;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER && mFromType == ConStant.HOME_TO_INFO)
            return new HeadViewHolder(mHeaderView);

        return new RecyclerViewHolder(View.inflate(mContext, R.layout.auction_item, null));
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Utils.print(tag, "view type=" + getItemViewType(position) + ",positon=" + position);
        //仅展示文字，没有交互
        if (getItemViewType(position) == TYPE_HEADER) return;


        //获取控制权
        final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
        viewHolder.itemView.setTag(position);


        AcutionItemData entity =  mDatas.get(position - 1);
        if(entity==null){
            return;
        }

        viewHolder.focusRelativeLayout.setTag(R.id.tag_listview_entity, entity);
        viewHolder.focusRelativeLayout.setPostion(position - 1);
        viewHolder.progressbar.setTag(entity.getAuctionVo().getAuctionSn());


        /**
         *  商品图
         */
        //Utils.print(tag, "url---->" + entity.getAuctionVo().getGoodsImg()+",name="+entity.getAuctionVo().getGoodsName());

        if (isLoadIconURL) {
            FrescoHelper.setImage(viewHolder.icon, Uri.parse(entity.getAuctionVo().getGoodsImg()), new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_icon_width),mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_height)));
        } else {
            FrescoHelper.setImageResource(viewHolder.icon, R.drawable.commodity_big_icon);
        }

        //商品名称
        viewHolder.acution_commodity_name.setText(entity.getAuctionVo().getGoodsName());

        /**
         * add price show
         */
        String add_price = Utils.getPrice(entity.getAuctionStatusVo().getAddQuota());
        if(!add_price.contains(".")){
            viewHolder.add_acution_price_dot.setVisibility(View.INVISIBLE);
            viewHolder.add_acution_price.setText("¥"+add_price);
        }else{
            viewHolder.add_acution_price_dot.setVisibility(View.VISIBLE);
            viewHolder.add_acution_price.setText("¥"+add_price.substring(0,add_price.indexOf(".")));
            viewHolder.add_acution_price_dot.setText(add_price.substring(add_price.indexOf("."),add_price.length()));
        }


        /**
         * new price show
         */
        String price = Utils.getPrice(entity.getAuctionStatusVo().getCurrentPrice());
        //Utils.print(tag,"name="+entity.getAuctionVo().getGoodsName()+",price="+price);
        if(!price.contains(".")){
            viewHolder.new_price_dot.setVisibility(View.INVISIBLE);
            viewHolder.new_price.setText("¥"+price);
        }else{
            viewHolder.new_price_dot.setVisibility(View.VISIBLE);
            viewHolder.new_price.setText("¥"+price.substring(0,price.indexOf(".")));
            viewHolder.new_price_dot.setText(price.substring(price.indexOf("."),price.length()));
        }

        /**
         * old price show
         */
        viewHolder.old_price.setText("¥"+Utils.getPrice(entity.getAuctionVo().getOrigPrice()));
        //设置商品价格横线
        viewHolder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


        /**
         * special show
         */
        initTypeView(viewHolder.acution_commodity_type,entity.getAuctionVo().getGoodsSpec().split(","));

        /**
         * status show
         */
        //Utils.print(tag,"111=="+String.valueOf(entity.getAuctionStatusVo().getUserid())+",222==");
        if(String.valueOf(entity.getAuctionStatusVo().getUserid()).equals(ConStant.getInstance(mContext).userID)){
            viewHolder.acution_status_icon.setBackgroundResource(R.drawable.acution_status_icon_top);
        }else{
            if(entity.isJoinAcution()){
                viewHolder.acution_status_icon.setBackgroundResource(R.drawable.acution_status_icon_toping);
            }else {
                viewHolder.acution_status_icon.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            }
        }

        /**
         * 进度条实时展示进度
         */
        if(viewHolder.countDownTimer!=null){
            viewHolder.countDownTimer.cancel();
        }

        Date date = new Date(System.currentTimeMillis());
        long localtime= date.getTime();
        long durationTime = entity.getAuctionScheduleVo().getDurationTime();
        long consumeTime = (localtime-entity.getAuctionScheduleVo().getAuctionTime());
        long TIME = durationTime*entity.getAuctionScheduleVo().getTimes()*1000-consumeTime;

        //Utils.print(tag,"localtime="+localtime+",actionTime="+entity.getAuctionScheduleVo().getAuctionTime()+",consumeTime="+consumeTime+",name="+entity.getAuctionVo().getGoodsName());
        //Utils.print(tag,"TIME===="+TIME+",name="+entity.getAuctionVo().getGoodsName());
        long progressDurationTime = durationTime*10;
        viewHolder.progressbar.setMax((int)progressDurationTime);
        if(TIME>0){
            viewHolder.countDownTimer = new CommodityCountDownTimer(TIME, ConStant.ACUTION_PROGRESS_TIME_INTERVER);
            viewHolder.countDownTimer.setDurationTime(progressDurationTime);
            viewHolder.countDownTimer.setProgressBar(viewHolder.progressbar);
            viewHolder.countDownTimer.start();
            progressMap.put(position,viewHolder.countDownTimer);
        }else{
            viewHolder.progressbar.setProgress(0);
        }

        /*if(entity.getAuctionScheduleVo().getAuctionTime()==0){
            viewHolder.focusRelativeLayout.setValid(false);
            viewHolder.invalid_item.setVisibility(View.VISIBLE);
        }else{
            viewHolder.focusRelativeLayout.setValid(true);
            viewHolder.invalid_item.setVisibility(View.INVISIBLE);
        }*/
        viewHolder.focusRelativeLayout.setValid(true);
        viewHolder.invalid_item.setVisibility(View.INVISIBLE);
    }



    @Override
    public int getItemCount() {
        if(mHeaderView==null)
            return mDatas.size();
        return mDatas.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public class CommodityCountDownTimer extends CountDownTimer {
        
        private long durationTime;
        private ProgressBar progressBar;

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        public void setDurationTime(long durationTime) {
            this.durationTime = durationTime;
        }

        public CommodityCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(acutionListener==null){
                return;
            }
            float time = millisUntilFinished / 1000f;

            time = time*10;
            if(time%durationTime==0){
                //执行完一个轮次了
                progressBar.setProgress((int)durationTime);
            }else {
                progressBar.setProgress((int)(time%durationTime));
            }

            updateProgressBarColor(progressBar);
//            Log.v(tag,"time="+time+",durationTime="+durationTime+",progressvalue="+progressBar.getProgress()+",map size=="+progressMap.size());

        }

        @Override
        public void onFinish() {
            if(acutionListener!=null){
                acutionListener.OnItemAcutionListener(true);
            }
        }
    }


    /**
     * 更新进度条颜色　
     */
    private void updateProgressBarColor(ProgressBar progressbar){
        //Utils.print(tag,"pro=="+progressbar.getProgress());
        if(progressbar.getProgress()>0.3*progressbar.getMax()){
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_green_color));
        }else if(progressbar.getProgress()<=0.3*progressbar.getMax() && progressbar.getProgress()>=0.1*progressbar.getMax()){
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_yellow_color));
        }else if(progressbar.getProgress()<0.1*progressbar.getMax()){
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_red_color));
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        SimpleDraweeView icon; //商品图标
        TextView add_acution_price; //我要拍价格
        TextView add_acution_price_dot; //我要拍价格小数部分
        TextView acution_commodity_name; //商品名称
        LinearLayout acution_commodity_type; //商品规格类型
        TextView new_price; //商品现价
        TextView new_price_dot; //商品现价位数部分
        TextView old_price; //商品原价
        FocusRelativeLayout focusRelativeLayout; //焦点


        ImageView acution_status_icon;//右侧顶部的皇冠状态
        ProgressBar progressbar;//拍卖进度条

        RelativeLayout invalid_item;

        private CommodityCountDownTimer countDownTimer;

        View itemView;

        public SimpleDraweeView getIcon() {
            return icon;
        }

        public View getItemView() {
            return itemView;
        }

        RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            icon = (SimpleDraweeView) itemView.findViewById(R.id.icon);
            add_acution_price = (TextView) itemView.findViewById(R.id.add_acution_price);
            add_acution_price_dot = (TextView)itemView.findViewById(R.id.add_acution_price_dot);
            acution_commodity_name = (TextView) itemView.findViewById(R.id.acution_commodity_name);
            acution_commodity_type = (LinearLayout) itemView.findViewById(R.id.acution_commodity_type);
            new_price = (TextView) itemView.findViewById(R.id.new_price);
            new_price_dot = (TextView) itemView.findViewById(R.id.new_price_dot);
            old_price = (TextView) itemView.findViewById(R.id.old_price);

            acution_status_icon = (ImageView) itemView.findViewById(R.id.acution_status_icon);
            progressbar = (ProgressBar) itemView.findViewById(R.id.progressbar);

            invalid_item = (RelativeLayout)itemView.findViewById(R.id.invalid_item);

            focusRelativeLayout = (FocusRelativeLayout) itemView.findViewById(R.id.focus_layout);
            focusRelativeLayout.setOnItemSelectedListener(onItemViewSelectedListener);
            focusRelativeLayout.setOnClickListener(this);
            focusRelativeLayout.setType(ConStant.ACUTION_TO_INFO);
            focusRelativeLayout.setOnItemkeyListener(onItemKeyListener);
        }

        @Override
        public void onClick(View v) {
            Utils.print(tag, "onclick acution");
            if(((FocusRelativeLayout)v).isValid()){
                Utils.print(tag, "onclick acution go");
                itemClick.onItemClick(selectItemId);
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


    /**
     * 设置当前选择的商品位置
     *
     * @param id
     */
    public void setSelectItemId(int id) {
        this.selectItemId = id;
    }

    /**
     * 判断当前位置是否是头部
     *
     * @param position
     * @return
     */
    public boolean isHeader(int position) {
        return position == 0;
    }



    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;//将header插入到顶部
        return TYPE_NORMAL;
    }


    /**
     * 取消注册
     */
    public void unRegisterObserver() {
        RxBus.get().unregister(ConStant.obString_item_load_icon_url, icon_load_observer);
    }


    /**
     * 设置拍卖商品规格
     */
    private void initTypeView(ViewGroup root,String[] names){
        root.removeAllViews();
        for (int i = 0; i < names.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.acution_commitity_type_description, null);
            TextView tv = (TextView) view.findViewById(R.id.type);
            TextView div_icon = (TextView) view.findViewById(R.id.div_icon);
            tv.setText(names[i]);
            if (i == names.length - 1) {
                div_icon.setVisibility(View.GONE);
            }

            if (i != 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
                lp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.length_6), 0, 0, 0);
            }
            root.addView(view);
        }
    }

    /**
     * 获取本地数据
     * @param pos
     * @return
     */
    public AcutionItemData getItemData(int pos){
        if(pos>=mDatas.size())
            return null;
        return mDatas.get(pos);
    }

    /**
     * 刷新新的数据
     * @param data
     */
    public void setItemData(AcutionItemData data){
        for (int i = 0; i < mDatas.size(); i++) {
            if(data.getAuctionStatusVo().getGoodsSn().equals(mDatas.get(i).getAuctionStatusVo().getGoodsSn())){
                data.setProgressValue(mDatas.get(i).getProgressValue());
                mDatas.set(i,data);
                break;
            }
        }
    }

    /**
     * 复位拍卖时间
     * @param data
     */
    public void resetAcutionTime(String acutionSn){
        for (int i = 0; i < mDatas.size(); i++) {
            if(acutionSn.equals(mDatas.get(i).getAuctionStatusVo().getAuctionSn())){
                mDatas.get(i).getAuctionScheduleVo().setAuctionTime(0l);
                break;
            }
        }
    }

    /**
     * 获取本地数据列表
     * @return
     */
    public List<AcutionItemData> getListDatas(){
        return mDatas;
    }



    public interface ItemClick{
        void onItemClick(int pos);
    }


    /**
     *
     * @return
     */
    public boolean checkDataExistList(AcutionItemData data){
        boolean value = false;
        //Utils.print(tag,"tt name="+data.getAuctionVo().getGoodsName());
        for (int i = 0; i < mDatas.size(); i++) {
            //Utils.print(tag,"tt compare name="+mDatas.get(i).getAuctionVo().getGoodsName());
            //Utils.print(tag,"tt acutionSN=="+data.getAuctionVo().getAuctionSn()+",compare sn="+mDatas.get(i).getAuctionVo().getAuctionSn());
            if(data.getAuctionVo().getAuctionSn().equals(mDatas.get(i).getAuctionVo().getAuctionSn())){
                value = true;
                break;
            }
        }
        //Utils.print(tag,"tt value="+value);
        return value;
    }


    /**
     * 停止所有计时器
     */
    public void release(){
        Iterator iterator = progressMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,CommodityCountDownTimer> entry = (Map.Entry)iterator.next();
            entry.getValue().cancel();
        }
        progressMap.clear();
    }


    /**
     * 关闭非当前能看见的定时器
     * @param first
     * @param last
     */
    public void closeInactivationTimer(int first,int last){
        Iterator it=progressMap.keySet().iterator();
        while(it.hasNext()) {
            Integer key=(Integer) it.next();
            if(key<first || key>last){
                Utils.print(tag,"remove map key=="+key);
                progressMap.get(key).cancel();
                it.remove();
            }
        }
    }

    public void setOnLongKeyUpListener(OnLongKeyUpListener onLongKeyUpListener) {
        this.onLongKeyUpListener = onLongKeyUpListener;
    }
}
