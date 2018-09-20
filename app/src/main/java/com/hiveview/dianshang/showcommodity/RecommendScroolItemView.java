/**
 * @Title PageItemMaker.java
 * @Package com.hiveview.cloudscreen.video.view
 * @author haozening
 * @date 2015-5-5 上午9:45:02
 * @Description
 * @version V1.0
 */
package com.hiveview.dianshang.showcommodity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.recommend.RecommendItemData;
import com.hiveview.dianshang.home.LivePlay;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.RecommentVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 专题或推荐
 */
@SuppressLint("NewApi")
public class RecommendScroolItemView extends FrameLayout implements OnFocusChangeListener,View.OnKeyListener {

    /**
     * 所有样式种类1560x810 390x270 780x270 390x540 1560x540 五种规格
     */
    private static final String tag = "RecommendScroolItemView";
    private Context context;

    protected List<RecommendItemData> entities = new ArrayList<>();


    private RelativeLayout[] layout;

    private int selectPos = 0;
    private int itemTotalCount = 0;

    private int item_width = 0;
    private int item_height = 0;

    private int currentPage = 0;

    private String templateSn;
    private String templateName;

    private OnItemKeyListener itemKeyListener;


    private View focusView;


    public RecommendScroolItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecommendScroolItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendScroolItemView(Context context) {
        super(context);
    }

    public void setTemplate(String name,String sn){
        this.templateName = name;
        this.templateSn = sn;
    }



    public void init(Context context, int pagePosition, List<RecommendItemData> recommendItemDatas) {

        this.context = context;
        this.entities = recommendItemDatas;
        this.itemTotalCount = entities.size();
        this.currentPage = pagePosition;
        //启用子视图排序功能
        setChildrenDrawingOrderEnabled(true);

        Utils.print(tag, "init size=" + itemTotalCount+",pagePosition="+pagePosition);


        layout = new RelativeLayout[itemTotalCount];
        LayoutParams layoutParams;// = new LinearLayout.LayoutParams(100,40);
        for (int p = 0; p < itemTotalCount; p++) {
            Log.v(tag, "p==" + p);
            if(entities.get(p).getRefType()==ConStant.RECOMMENT_SPECIAL){
                layout[p] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recomment_special_item, null);
            }else if (entities.get(p).getRefType()==ConStant.RECOMMENT_COMMODITY){
                if(entities.get(p).getType()==ConStant.COMMODITY){
                    layout[p] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recomment_commodity_image_item, null);
                }else if(entities.get(p).getType()==ConStant.LIVE){
                    layout[p] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recomment_commodity_live_item, null);
                    getVideoView(layout[p]).setFocusable(false);
                }else{
                    layout[p] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recomment_commodity_image_item, null);
                }
            }else {
                layout[p] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.recomment_commodity_image_item, null);
            }

            layout[p].setTag(R.id.tag_listview_item_position, "home" + (p + currentPage * itemTotalCount));
            //Log.v(tag,"pos=="+(p+currentPage*itemTotalCount));

            layout[p].setOnFocusChangeListener(this);
            layout[p].setOnKeyListener(this);
            layout[p].setOnClickListener(onClickListener);

            item_height = entities.get(p).getLayouth() * context.getResources().getDimensionPixelSize(R.dimen.recoment_180_view);
            item_width = entities.get(p).getLayoutw() * context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view);

            handleItemDefaultBg(layout[p],item_width,item_height);

            //Log.v(tag, "w==" + item_width + ",h==" + item_height);
            //Log.v(tag, "layoutx=" + entities.get(p).getLayoutx() + ",layouty=" + entities.get(p).getLayouty());
            layoutParams = new LayoutParams(item_width, item_height);
            int left = (entities.get(p).getLayoutx() - 1) * context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view);
            int top = (entities.get(p).getLayouty() - 1) * context.getResources().getDimensionPixelSize(R.dimen.recoment_180_view);
            //Log.v(tag, "left==" + left + ",top=" + top);
            layoutParams.setMargins(left, top, 0, 0);
            addView(layout[p], layoutParams);


        }

        updateData();
        Utils.print(tag,"curpage="+currentPage);

    }


    public int getViewIndex(View view) {
        String stringtag = (String) view.getTag(R.id.tag_listview_item_position);
        Log.v(tag, "tag>>>>" + stringtag);
        if (stringtag != null) {
            //Log.v(tag,"selectpos>>>>"+stringtag);
            selectPos = Integer.parseInt(stringtag.substring(stringtag.indexOf("home") + 4, stringtag.length()));
            //Log.v(tag,"selectpos>>>>"+selectPos);
            selectPos = selectPos % itemTotalCount;
            //Log.v(tag,"selectpos>>>>"+selectPos);
        }
        return selectPos;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int position = selectPos;
        //Log.v(tag,"selectPos==="+selectPos);
        if (position < 0) {
            return i;
        } else {
            if (i == childCount - 1) {
                if (position > i) {
                    position = i;
                }
                return position;
            }
            if (i == position) {
                return childCount - 1;
            }
        }
        //Log.v(tag,"i===="+i+",count="+childCount);
        return i;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Utils.print(tag, "hasFocus : " + hasFocus);
        if (hasFocus) {
            Utils.print(tag, "onFocusChange index : " + selectPos);
            //Utils.print(tag, "lastPosition index : " + lastPosition);
            //TODO 这里的交互还需要在细化
            handViewPos(v);
            Utils.print(tag,"ready big");
            focusView=v;
            invalidate();
            getFocusView(v).setVisibility(View.VISIBLE);
            handScaleView(v, true);
        } else {

            getFocusView(v).setVisibility(View.INVISIBLE);

            handScaleView(v, false);
        }
    }


    private void handScaleView(View v, boolean isScale) {

        String stringtag = (String) v.getTag(R.id.tag_listview_item_position);
        int position = 0;
        if (stringtag != null) {
            //position = Integer.parseInt(stringtag.substring(stringtag.indexOf("home")+4,stringtag.length()));
            position = selectPos % itemTotalCount;
        }
        Utils.print(tag, "top=" + v.getTop() + ",left=" + v.getLeft() + ",right=" + v.getRight() + ",buttom=" + v.getBottom());
        Utils.print(tag, "id=" + position + ",isScale=" + isScale);
        Utils.print(tag, "h=" + v.getHeight());

        float bigScale;

        if (v.getWidth() == context.getResources().getDimensionPixelSize(R.dimen.recoment_1040_view)){
            bigScale = 1.02f;
        }else if(v.getWidth()==context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view)){
            bigScale = 1.08f;
        }else if(v.getWidth()==context.getResources().getDimensionPixelSize(R.dimen.recoment_520_view)){
            bigScale = 1.05f;
        }else {
            bigScale = 1.08f;
        }

        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(ConStant.SCALE_DURATION);
        if (isScale) {
            animator.scaleX(bigScale).scaleY(bigScale);
        } else {
            animator.scaleX(1.0f).scaleY(1.0f);
        }

        animator.start();
    }


    private void handViewPos(View view) {
        String stringtag = (String) view.getTag(R.id.tag_listview_item_position);
        //Log.v(tag,"tag>>>>"+stringtag);
        if (stringtag != null) {
            //Log.v(tag,"selectpos>>>>"+stringtag);
            selectPos = Integer.parseInt(stringtag.substring(stringtag.indexOf("home") + 4, stringtag.length()));
            //Log.v(tag,"selectpos>>>>"+selectPos);
            selectPos = selectPos % itemTotalCount;
            //Log.v(tag,"selectpos>>>>"+selectPos);
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Utils.print(tag, "action==" + event.getAction());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Utils.print(tag, "keycode==" + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    Utils.print(tag, "left number=" + v.getLeft());
                    if (v.getLeft() == 0)
                        v.setNextFocusLeftId(R.id.layout_home);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    /*if (v.getTop() == 0 && currentPage==0){
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        return true;
                    }*/
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    Utils.print(tag,">>>"+v.getRight());
                    if (v.getRight()==context.getResources().getDimensionPixelSize(R.dimen.recoment_1040_view)){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public void setOnItemKeyListener(OnItemKeyListener listener) {
        itemKeyListener = listener;
    }


    public SimpleDraweeView getCommodityView(View v) {
        return (SimpleDraweeView) v.findViewById(R.id.image);
    }

    public RelativeLayout getItemView(View v){
        return (RelativeLayout) v.findViewById(R.id.item);
    }


    /**
     * 获取videoview
     *
     * @param v
     * @return
     */
    public RecommentVideoView getVideoView(View v) {
        return (RecommentVideoView) v.findViewById(R.id.video);
    }

    public ImageView getFocusView(View v) {
        return (ImageView) v.findViewById(R.id.focus_view);
    }



    /**
     * 获取播放图标
     *
     * @param v
     * @return
     */
    public ImageView getPlayStatusIcon(View v) {
        return (ImageView) v.findViewById(R.id.video_play_icon);
    }


    /**
     * 获取商品名称
     *
     * @param v
     * @return
     */
    public TextView getNameView(View v) {
        return (TextView) v.findViewById(R.id.name);
    }

    /**
     * 专题描述
     * @param v
     * @return
     */
    public TextView getDiscribeView(View v) {
        return (TextView) v.findViewById(R.id.describe);
    }

    /**
     * 获取商品价格
     *
     * @param v
     * @return
     */
    public TextView getPriceView(View v) {
        return (TextView) v.findViewById(R.id.price);
    }


    /**
     * 点击
     */
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.print(tag, "onclick");
            RecommendItemData entity = (RecommendItemData) v.getTag(R.id.tag_listview_entity);

            if(entity.getOnlineStatus()==ConStant.OFF_LINE){
                ToastUtils.showToast(context,context.getResources().getString(R.string.commodity_off_line));
                return;
            }
            Utils.print(tag, "sn==" + entity.getGoodsSn());
            sendSpecialStatistics(entity);

            Utils.print(tag, "sn==2222=="+entity.getRefType());
            if(entity.getRefType()==ConStant.RECOMMENT_SPECIAL){

                if(entity.getOnlineStatus()==ConStant.SPECIAL_OFF_LINE){
                    ToastUtils.showToast(context,context.getResources().getString(R.string.special_off_line));
                    return;
                }

                CommoditySpecial.launch((MainActivity) context,entity.getSpecName(),entity.getSpecSn());

            }else if(entity.getRefType()==ConStant.RECOMMENT_COMMODITY){

                if (entity.getType() == ConStant.LIVE) {
                    LivePlay.launch((MainActivity) context, entity,ConStant.TUIJIANWEI_TO_INFO);
                } else if (entity.getType() == ConStant.COMMODITY) {
                    CommodityInfomation.launch((Activity) context, entity.getGoodsSn(), entity.getRefType() == 2 ? ConStant.SPECIAL_TO_INFO : ConStant.TUIJIANWEI_TO_INFO);
                }
            }
        }
    };


    public View getSubviewAt(int index) {
        return layout[index];
    }


    public void updateData() {
        Utils.print(tag,"view size="+layout.length);
        for (int i = 0; i < layout.length; i++) {
            if (layout[i] != null) {
                //Utils.print(tag, "set data");
                item_height = entities.get(i).getLayouth() * context.getResources().getDimensionPixelSize(R.dimen.recoment_180_view);
                item_width = entities.get(i).getLayoutw() * context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view);
                //handleType(entities.get(i), layout[i]);
                layout[i].setTag(R.id.tag_listview_entity, entities.get(i));

                if(entities.get(i).getRefType()==ConStant.RECOMMENT_SPECIAL){
                    //专题图
                    SimpleDraweeView icon = getCommodityView(layout[i]);
                    FrescoHelper.setImage(icon,Uri.parse(entities.get(i).getSpecUrl()),new ResizeOptions(item_width,item_height));

                    //专题名称
                    TextView name = getNameView(layout[i]);
                    name.setText(entities.get(i).getSpecName());

                    //专题描述
                    TextView discribe = getDiscribeView(layout[i]);
                    discribe.setText(entities.get(i).getRemark());
                }else if(entities.get(i).getRefType()==ConStant.RECOMMENT_COMMODITY){
                    if(entities.get(i).getType()==ConStant.COMMODITY){
                        //商品图
                        SimpleDraweeView icon = getCommodityView(layout[i]);
                        FrescoHelper.setImage(icon,Uri.parse(entities.get(i).getProductImages()),new ResizeOptions(item_width,item_height));

                        //商品名
                        TextView name = getNameView(layout[i]);
                        name.setText(entities.get(i).getGoodsName());

                        //商品价格
                        TextView price = getPriceView(layout[i]);
						//RMB换￥
                        price.setText("¥"+Utils.getPrice(entities.get(i).getPrice()));
                    }else if(entities.get(i).getType()==ConStant.LIVE){
                        //商品流默认图
                        SimpleDraweeView icon = getCommodityView(layout[i]);
                        FrescoHelper.setImage(icon,Uri.parse(entities.get(i).getProductImages()),new ResizeOptions(item_width,item_height));

                        //商品流名称
                        TextView name = getNameView(layout[i]);
                        name.setText(entities.get(i).getGoodsName());
                    }
                }
            }
        }
    }


    /**
     * 根据推荐位的长宽设定不同的默认占位图
     *
     * @param view
     * @param w
     * @param h
     */
    private void handleItemDefaultBg(View view, int w, int h) {
        //Utils.print(tag, "handleItemDefaultBg w,h==" + w + "==" + h);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)getItemView(view).getLayoutParams();
        int margin = context.getResources().getDimensionPixelSize(R.dimen.recoment_item_margin_10);
        if (w == context.getResources().getDimensionPixelSize(R.dimen.recoment_1040_view) &&
                h == context.getResources().getDimensionPixelSize(R.dimen.recoment_540_view)) {
            getCommodityView(view).setBackgroundResource(R.drawable.default_icon1);
            //layoutParams.setMargins(margin,margin,margin,margin);
        } else if (w == context.getResources().getDimensionPixelSize(R.dimen.recoment_1040_view) &&
                h == context.getResources().getDimensionPixelSize(R.dimen.recoment_360_view)) {
            getCommodityView(view).setBackgroundResource(R.drawable.default_icon2);
            //layoutParams.setMargins(margin,margin,margin,margin);
        } else if (w == context.getResources().getDimensionPixelSize(R.dimen.recoment_520_view) &&
                h == context.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)) {
            getCommodityView(view).setBackgroundResource(R.drawable.default_icon3);
            //layoutParams.setMargins(margin,margin,margin,margin);
        } else if (w == context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view) &&
                h == context.getResources().getDimensionPixelSize(R.dimen.recoment_360_view)) {
            getCommodityView(view).setBackgroundResource(R.drawable.default_icon4);
            //layoutParams.setMargins(margin,margin,margin,margin);
        } else if (w == context.getResources().getDimensionPixelSize(R.dimen.recoment_260_view) &&
                h == context.getResources().getDimensionPixelSize(R.dimen.recoment_180_view)) {
            //Utils.print(tag,"260,180");
            getCommodityView(view).setBackgroundResource(R.drawable.commodity_big_icon);
            //layoutParams.setMargins(margin,margin,margin,margin);
        }

        getItemView(view).setLayoutParams(layoutParams);

    }


    /**
     * 发送推荐位点击埋点数据到后台
     *
     * @param data
     */
    private void sendSpecialStatistics(RecommendItemData data) {
        HashMap<String, String> simpleMap = new HashMap<String, String>();

        simpleMap.put("tabNo", "");
        simpleMap.put("actionType", "Ec1001");
        String info = "";
        if(data.getRefType()==ConStant.RECOMMENT_SPECIAL){
            info = info + "recommendName=" + data.getSpecName() + "&";  //商品名称
            info = info + "recommendType=3&";  //直播流,图片,专题
        }else{
            if(data.getType()==ConStant.COMMODITY){
                info = info + "recommendName=" + data.getGoodsName() + "&";  //商品名称
                info = info + "recommendType=1&";  //直播流,图片,专题
            }else if(data.getType()==ConStant.LIVE){
                info = info + "recommendName=" + data.getGoodsName() + "&";  //商品名称
                info = info + "recommendType=2&";  //直播流,图片,专题
            }
        }
        info = info + "matrixId=" + templateSn + "&"; //来自后台该矩阵的id
        info = info + "matrixName=" + templateName + "&"; //来自后台该矩阵的名称
        info = info + "displayType=" + data.getScreenNumber() + "&"; //第几屏
        info = info + "positionId=" + data.getPositionseq(); //位置ID
        simpleMap.put("actionInfo", info);
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }


    /**
     * 用户推荐位返回，默认焦点
     */
    public void requestDefualtFocus(){
        layout[selectPos].requestFocus();
    }


    public void requestPositionFocus(int postion){
        if(postion<layout.length-1){
            layout[postion].requestFocus();
        }
    }


    public View getFocusView(){
        return focusView;
    }
}
