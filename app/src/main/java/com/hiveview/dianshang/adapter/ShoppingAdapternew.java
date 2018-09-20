package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.ConfirmDialog;
import com.hiveview.dianshang.dialog.SelectCommodityType;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.shoppingcart.ActivityInfoData;
import com.hiveview.dianshang.entity.shoppingcart.FullCutBean;
import com.hiveview.dianshang.entity.shoppingcart.GiftData;
import com.hiveview.dianshang.entity.shoppingcart.GroupData;
import com.hiveview.dianshang.entity.shoppingcart.LimitActivityData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountData;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountType;
import com.hiveview.dianshang.entity.shoppingcart.discount.FullCutSkuData;
import com.hiveview.dianshang.entity.shoppingcart.discount.PostCartItemInfo;
import com.hiveview.dianshang.shoppingcart.ShoppingCartGrid;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.showcommodity.PromotionCommodity;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ThinkPad on 2017/12/16.
 */

public class ShoppingAdapternew extends BaseAdapter {
    private Context context;
    private List<GroupData> groupDataList;
    private List<ShoppingCartRecord> list;
    private ListView listView;
    private Button goshopping;
    private List<Long> selectedIdList;
    private LinearLayout layout_empty;
    private RelativeLayout layout_has;
    private TextView price;
    private TextView discount;
    private LinearLayout layout_shoppingcart_discount;
    private TextView count;
    private TextView totalprice;
    private Button btn_select;
    private TextView goodscount;
    //屏幕的高度
    private int height;
    //全选左键，落入普通商品的Item，点击失效的问题
    public boolean isItemFocus=false;


    LayoutInflater inflater;
    final int VIEW_TYPE = 4;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;
    final int TYPE_4 = 3;
    ViewHolder1 holder1=null;
    ViewHolder2 holder2=null;
    ViewHolder3 holder3=null;
    ViewHolder4 holder4=null;

    private int select_item = -1;

    /*
    * 标识Ok键显示的是确定还是取消,默认是确定
    * */
    private boolean isOk=true;
    //确定清空无效商品对话框
    private ConfirmDialog clearInvalidDialog;
    private String tag="ShoppingAdapternew";
    private int totalCommodityCount;
    private boolean iscover=false;

    public ShoppingAdapternew(Context context, List<GroupData> groupDataList,ListView listView,List<Long> selectedIdList,Button goshopping,LinearLayout layout_empty,RelativeLayout layout_has,TextView price,TextView discount,TextView count,TextView totalprice,Button btn_select,TextView goodscount,LinearLayout layout_shoppingcart_discount) {
        this.context = context;
        this.groupDataList = groupDataList;
        this.listView=listView;
        this.selectedIdList=selectedIdList;
        this.goshopping=goshopping;
        this.layout_empty=layout_empty;
        this.layout_has=layout_has;
        this.price=price;
        this.discount=discount;
        this.layout_shoppingcart_discount = layout_shoppingcart_discount;
        this.count=count;
        this.totalprice=totalprice;
        this.btn_select=btn_select;
        this.goodscount=goodscount;
        inflater = LayoutInflater.from(context);

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getHeight();

        getList();

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }


    //每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        ShoppingCartRecord bean=list.get(position);
        if(bean.getType().equals(ConStant.INVALID)){//下半部分(失效商品)
            if(bean.isTitleBoolean()){//失效商品且是首个title
                return TYPE_3;
            }else{//失效商品但不是首个
                return TYPE_4;
            }

        }else{//非失效商品
            if(bean.isTitleBoolean()){//上半部分的首个title
                return TYPE_1;
            }else{
                return TYPE_2;
            }

        }

    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        int type=getItemViewType(position);
        if(convertView==null){//无convertView，需要new出各个控件
            //按当前所需的样式，确定new的布局
            switch(type){
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.shoppingnew_item_title, parent, false);
                    holder1 = new ViewHolder1();
                    holder1.wayname = (TextView)convertView.findViewById(R.id.wayname);
                    holder1.waycontent = (TextView)convertView.findViewById(R.id.waycontent);
                    holder1.btn_gather=(Button)convertView.findViewById(R.id.btn_gather);
                    holder1.container=(LinearLayout)convertView.findViewById(R.id.container);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.shoppingnew_item_up, parent, false);
                    holder2 = new ViewHolder2();
                    holder2.container = (LinearLayout) convertView.findViewById(R.id.container);
                    holder2.line=(TextView)convertView.findViewById(R.id.line);
                    holder2.divide_line1=(TextView)convertView.findViewById(R.id.divide_line1);
                    holder2.divide_line2=(TextView)convertView.findViewById(R.id.divide_line2);
                    holder2.layout_item=(RelativeLayout)convertView.findViewById(R.id.layout_item);
                    holder2.image=(SimpleDraweeView)convertView.findViewById(R.id.image);
                    holder2.invalid_layout=(LinearLayout)convertView.findViewById(R.id.invalid_layout);
                    holder2.invalid_text=(TextView)convertView.findViewById(R.id.invalid_text);
                    holder2.layout_okflag=(LinearLayout)convertView.findViewById(R.id.layout_okflag);
                    holder2.name=(TextView)convertView.findViewById(R.id.name);
                    holder2.content=(TextView)convertView.findViewById(R.id.content);
                    holder2.count=(TextView)convertView.findViewById(R.id.count);
                    holder2.limit=(TextView)convertView.findViewById(R.id.limit);
                    holder2.price=(TextView)convertView.findViewById(R.id.price);
                    holder2.layout_function=(LinearLayout)convertView.findViewById(R.id.layout_function);
                    holder2.btn_back=(Button)convertView.findViewById(R.id.btn_back);
                    holder2.btn_details=(Button)convertView.findViewById(R.id.btn_details);
                    holder2.btn_ok=(Button)convertView.findViewById(R.id.btn_ok);
                    holder2.btn_modify=(Button)convertView.findViewById(R.id.btn_modify);
                    holder2.btn_delete=(Button)convertView.findViewById(R.id.btn_delete);
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.shoppingnew_item_titleinvalid, parent, false);
                    holder3 = new ViewHolder3();
                    holder3.clear_invalid = (Button) convertView.findViewById(R.id.clear_invalid);
                    holder3.line=(TextView)convertView.findViewById(R.id.line);
                    convertView.setTag(holder3);
                    break;
                case TYPE_4:
                    convertView = inflater.inflate(R.layout.shoppingnew_item_down, parent, false);
                    holder4 = new ViewHolder4();
                    holder4.layout_item=(RelativeLayout)convertView.findViewById(R.id.layout_item);
                    holder4.image=(SimpleDraweeView)convertView.findViewById(R.id.image);
                    holder4.invalid_layout=(LinearLayout)convertView.findViewById(R.id.invalid_layout);
                    holder4.invalid_text=(TextView)convertView.findViewById(R.id.invalid_text);
                    holder4.name=(TextView)convertView.findViewById(R.id.name);
                    holder4.content=(TextView)convertView.findViewById(R.id.content);
                    holder4.count=(TextView)convertView.findViewById(R.id.count);
                    holder4.limit=(TextView)convertView.findViewById(R.id.limit);
                    holder4.price=(TextView)convertView.findViewById(R.id.price);
                    holder4.layout_function=(LinearLayout)convertView.findViewById(R.id.layout_function);
                    holder4.btn_back=(Button)convertView.findViewById(R.id.btn_back);
                    holder4.btn_details=(Button)convertView.findViewById(R.id.btn_details);
                    holder4.btn_ok=(Button)convertView.findViewById(R.id.btn_ok);
                    holder4.btn_modify=(Button)convertView.findViewById(R.id.btn_modify);
                    holder4.btn_delete=(Button)convertView.findViewById(R.id.btn_delete);
                    convertView.setTag(holder4);
                    break;
            }

        }else{//有convertView，按样式，取得不用的布局
              switch(type){
                  case TYPE_1:
                      holder1 = (ViewHolder1) convertView.getTag();
                      break;
                  case TYPE_2:
                      holder2 = (ViewHolder2) convertView.getTag();
                      break;
                  case TYPE_3:
                      holder3 = (ViewHolder3) convertView.getTag();
                      break;
                  case TYPE_4:
                      holder4 = (ViewHolder4) convertView.getTag();
                      break;
              }
        }

        //设置资源
        //映射商品总数
        totalCommodityCount=getListTotalCount2();
        goodscount.setText(""+totalCommodityCount);
          switch(type) {
              case TYPE_1:
                  if(list.get(position).getType().equals(ConStant.FULL_GIFTS)){//满赠
                      holder1.wayname.setText(context.getResources().getString(R.string.giveaway));
                      String wayTextView="";
                      if(selectedIdList.size()!=0){
                          if(list.get(position).getActivecontent()!=null&&!list.get(position).getActivecontent().equals("")){
                              wayTextView=list.get(position).getActivecontent();
                          }else{
                              wayTextView=context.getResources().getString(R.string.giveawaytip1)+list.get(position).getLimitPrice()+context.getResources().getString(R.string.giveawaytip2);
                          }
                      }else{
                          wayTextView=context.getResources().getString(R.string.giveawaytip1)+list.get(position).getLimitPrice()+context.getResources().getString(R.string.giveawaytip2);
                      }

                      holder1.waycontent.setText(wayTextView);
                      holder1.container.removeAllViews();

                      if(selectedIdList.size()!=0){
                          if(list.get(position).isShow()){
                              List<ActivityInfoData> infoList=list.get(position).getPromotionInfo();
                              if(infoList!=null){
                                  for(int j=0;j<infoList.size();j++){
                                      ActivityInfoData infoData=infoList.get(j);
                                      View giveView = LayoutInflater.from(context).inflate(R.layout.shoppingnew_giveview, null);
                                      if(j==0){
                                          ((LinearLayout)giveView).getChildAt(0).setVisibility(View.VISIBLE);
                                      }else{
                                          ((LinearLayout)giveView).getChildAt(0).setVisibility(View.INVISIBLE);
                                      }
                                      ((TextView)((LinearLayout)giveView).getChildAt(1)).setText(infoData.getGiftName());
                                      ((TextView)((LinearLayout)giveView).getChildAt(2)).setText("X "+infoData.getGiftNum());
                                      holder1.container.addView(giveView);

                                  }
                              }else{
                                    holder1.container.removeAllViews();
                              }
                          }else{
                              holder1.container.removeAllViews();
                          }

                      }


                  }else if(list.get(position).getType().equals(ConStant.FULL_CUT)){//满减
                      holder1.wayname.setText(context.getResources().getString(R.string.losemoney));
                      holder1.container.removeAllViews();

                      String fullcut_tv="";
                      List<ActivityInfoData> fullcutListAll=list.get(position).getPromotionInfo();
                      for(int j=0;j<fullcutListAll.size();j++){
                          if(j!=fullcutListAll.size()-1){//非最后一个
                              fullcut_tv=fullcut_tv+context.getResources().getString(R.string.fullcuttip1)+fullcutListAll.get(j).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullcutListAll.get(j).getCutPrice()+context.getResources().getString(R.string.fullgifttip4)+",";
                          }else{
                              fullcut_tv=fullcut_tv+context.getResources().getString(R.string.fullcuttip1)+fullcutListAll.get(j).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullcutListAll.get(j).getCutPrice()+context.getResources().getString(R.string.fullgifttip4);
                          }

                      }

                      String wayTextView="";
                      if(selectedIdList.size()!=0){
                          if(list.get(position).getActivecontent()!=null&&!list.get(position).getActivecontent().equals("")){
                              wayTextView=list.get(position).getActivecontent();
                          }else{
                              wayTextView=fullcut_tv;
                          }
                      }else{
                          wayTextView=fullcut_tv;
                      }
                      holder1.waycontent.setText(wayTextView);

                  }

                holder1.btn_gather.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            //Toast.makeText(context, "点击凑单=="+list.get(ShoppingCartGrid.select_item).getType()+",,,"+ShoppingCartGrid.select_item, Toast.LENGTH_SHORT).show();
                            //目前操作，购物车数据无需刷新
                                ShoppingCartGrid.isRefresh=false;
                                String promotionSn=list.get(ShoppingCartGrid.select_item).getPromotionSn();  //需要传输促销sn
                                PromotionCommodity.launch((ShoppingCartGrid)context,promotionSn);
                                Log.i("=====33333=====","promotionSn==="+promotionSn);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                     /*
                  *
                  *
                  * 为焦点在去凑单时，按返回键的监听
                  *
                  * */
                  holder1.btn_gather.setOnKeyListener(onKeyListener);
                /*  holder1.btn_gather.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          Log.i("==22sss==","holder1.btn_gather==hasFocus=="+hasFocus);
                          Log.i("==22sss==","holder1.btn_gather==select_item=="+select_item);
                          Log.i("==22sss==","holder1.btn_gather===="+listView.getFirstVisiblePosition());
                      }
                  });*/
                  break;
              case TYPE_2:
                  //要显示买赠的赠品数据
                  // list.get(position).getSkuGifts()
                  //买赠赠品的容器
                  holder2.container.removeAllViews();
                  List<GiftData> giftDataList=list.get(position).getSkuGifts();
                  if(giftDataList!=null){
                      holder2.container.setVisibility(View.VISIBLE);
                      for(int i=0;i<giftDataList.size();i++){
                          GiftData giftData=giftDataList.get(i);
                          View giveView = LayoutInflater.from(context).inflate(R.layout.shoppingnew_giveview, null);
                          if(i==0){
                              ((LinearLayout)giveView).getChildAt(0).setVisibility(View.VISIBLE);
                          }else{
                              ((LinearLayout)giveView).getChildAt(0).setVisibility(View.INVISIBLE);
                          }
                          ((TextView)((LinearLayout)giveView).getChildAt(1)).setText(giftData.getGiftName());
                          ((TextView)((LinearLayout)giveView).getChildAt(2)).setText("X "+giftData.getGiftNum());
                          holder2.container.addView(giveView);
                      }
                  }else{
                      holder2.container.setVisibility(View.GONE);
                  }
                  //如果是所有数据的最后一个或分组的最后一个，则显示分割下划线
                  if(position==list.size()-1||(((position+1)<list.size())&&(!list.get(position).getType().equals(list.get(position+1).getType())))){//最后一项
                      if((position+1)<list.size()&&list.get(position+1).getType().equals(ConStant.INVALID)){
                          holder2.line.setVisibility(View.INVISIBLE);
                          holder2.divide_line1.setVisibility(View.GONE);
                          holder2.divide_line2.setVisibility(View.GONE);
                      }else{
                          holder2.line.setVisibility(View.VISIBLE);
                          holder2.divide_line1.setVisibility(View.INVISIBLE);
                          holder2.divide_line2.setVisibility(View.INVISIBLE);
                      }
                  }else{
                      holder2.line.setVisibility(View.INVISIBLE);
                      holder2.divide_line1.setVisibility(View.GONE);
                      holder2.divide_line2.setVisibility(View.GONE);
                  }
                  if(list.get(position).getSquareUrl()!=null){
                      FrescoHelper.setImage(holder2.image, Uri.parse(list.get(position).getSquareUrl()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.width_shopping_240),context.getResources().getDimensionPixelSize(R.dimen.height_shopping_167)));
                  }

                  if(list.get(position).isStock()){//有库存，不用提示
                      if(list.get(position).getStockNum()<list.get(position).getBuyNum()){
                          holder2.invalid_layout.setVisibility(View.VISIBLE);
                          holder2.invalid_text.setText(context.getResources().getString(R.string.outofstock));
                      }else{
                          holder2.invalid_layout.setVisibility(View.GONE);
                      }

                  }else{//无库存，提示用户
                      holder2.invalid_layout.setVisibility(View.VISIBLE);
                      holder2.invalid_text.setText(context.getResources().getString(R.string.outofstock));
                  }
                  holder2.name.setText(list.get(position).getGoodsName());
                  holder2.content.setText(list.get(position).getSpecValue());
                  holder2.count.setText(context.getResources().getString(R.string.quantity)+list.get(position).getBuyNum());
                  if(list.get(position).getLimitActivity()!=null){
                      if(list.get(position).getLimitActivity().isLimitFlag()){
                          holder2.limit.setVisibility(View.VISIBLE);
                          holder2.limit.setText(context.getResources().getString(R.string.limitbuytip1)+list.get(position).getLimitActivity().getLimitNum()+context.getResources().getString(R.string.limitbuytip2)+context.getResources().getString(R.string.limitbuytip3)+(list.get(position).getLimitActivity().getLimitNum()-list.get(position).getLimitActivity().getBuyNum())+context.getResources().getString(R.string.limitbuytip2));
                      }else{
                          holder2.limit.setVisibility(View.INVISIBLE);
                      }

                  }else{
                      holder2.limit.setVisibility(View.INVISIBLE);
                  }
                  Log.i("=====55555=====","oo=="+list.get(position).getOriginalPrice());
                  holder2.price.setText(Utils.getPrice(list.get(position).getOriginalPrice()));

                  //根据判断，决定是否显示选中标识
                  //layout_okflag
                  if(selectedIdList.contains(list.get(position).getCartItemId())){//如果该商品选中
                      holder2.layout_okflag.setVisibility(View.VISIBLE);
                  }else{//没选中
                      holder2.layout_okflag.setVisibility(View.GONE);
                  }

                  holder2.layout_item.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          select_item=ShoppingCartGrid.select_item;
                         // if(((RelativeLayout)((RelativeLayout)((LinearLayout)listView.getChildAt(select_item)).getChildAt(0)).getChildAt(0)).getChildAt(1).getVisibility()==View.VISIBLE){
                              ShoppingCartGrid.isFocus=false;
                              Log.i("==4444==","select_item"+select_item);
                              notifyDataSetChanged();
                         // }

                      }
                  });
                  holder2.layout_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          Log.i("==444422==","holder2.layout_item==hasFocus=="+hasFocus);
                          Log.i("==4444==","holder2.layout_item==select_item=="+select_item);
                          if(hasFocus&&select_item==0&&iscover){
                              listView.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      listView.setSelection(0);
                                  }
                              });
                              iscover=false;
                          }

                          if(!hasFocus&&isItemFocus){
                              Log.i("==444422==","重新落入焦点");
                              ShoppingCartGrid.isFocus=true;
                              notifyDataSetChanged();
                              isItemFocus=false;
                          }

                      }
                  });

                  /*
                  *
                  * 点击功能键监听
                  *
                  * */
                  holder2.btn_back.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ShoppingCartGrid.isFocus=true;
                          notifyDataSetChanged();
                      }
                  });
                  holder2.btn_delete.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          deleteShoppingCartData(list.get(ShoppingCartGrid.select_item),ShoppingCartGrid.select_item);
                      }
                  });
                  holder2.btn_details.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          //目前操作，购物车数据无需刷新
                          ShoppingCartGrid.isRefresh=false;
                          CommodityInfomation.launch((ShoppingCartGrid) context, list.get(ShoppingCartGrid.select_item).getGoodsSn(), ConStant.GOUWUCHE_TO_INFO);
                      }
                  });
                  holder2.btn_modify.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          SelectCommodityType selectCommodityType = new SelectCommodityType(context,R.style.SelectTypeCustomFullScreenDialog,SelectCommodityType.MODIFY_COMMODITY);
                          selectCommodityType.showFullScreenUI(list.get(ShoppingCartGrid.select_item));
                      }
                  });
                  holder2.btn_ok.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          if(!isOk){
                              Log.i("ssssssssssss","取消！！！");
                              //取消选中  selectedPositionList
                             selectedIdList.remove(list.get(ShoppingCartGrid.select_item).getCartItemId());
                              ShoppingCartGrid.isFocus=true;
                              getList();
                              notifyDataSetChanged();
                              //检测全选按钮的文本显示
                              if(btn_select.getText().toString().equals(context.getResources().getString(R.string.cancel))){
                                  btn_select.setText(context.getResources().getString(R.string.checkall));
                                  Drawable img= context.getResources().getDrawable(R.drawable.select_icon);
                                  //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                  img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                  btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                  btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                              }


                          }else{//选中
                              //将该选中的商品的SN记录在集合中
                              ShoppingCartRecord recordBean=list.get(ShoppingCartGrid.select_item);
                              int buyNum=recordBean.getBuyNum();
                              int stokeNum=recordBean.getStockNum();
                              LimitActivityData limitData=recordBean.getLimitActivity();
                              boolean isToast=true;
                              if(limitData!=null){//存在限购策略
                                  int buyedNum=recordBean.getLimitActivity().getBuyNum();
                                  int limitNum=recordBean.getLimitActivity().getLimitNum();
                                  //Log.i("====22sss====","550buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                  if((buyNum+buyedNum>limitNum)&&recordBean.getLimitActivity().isLimitFlag()){//超出限购数量
                                     // Log.i("====22sss====","552buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                      ToastUtils.showToast(context, context.getResources().getString(R.string.limittip));
                                      isToast=false;
                                  }else{
                                     // Log.i("====22sss====","556buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                      if(buyNum>stokeNum){//库存不足
                                         // Log.i("====22sss====","558buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                          ToastUtils.showToast(context, context.getResources().getString(R.string.stocklimit));
                                      }else{
                                         // Log.i("====22sss====","561buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                          selectedIdList.add(list.get(ShoppingCartGrid.select_item).getCartItemId());
                                          ShoppingCartGrid.isFocus=true;
                                          getList();
                                          notifyDataSetChanged();
                                      }


                                  }
                              }else{//不存在限购策略
                                  if(buyNum>stokeNum){//库存不足
                                     // Log.i("====22sss====","572buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                      ToastUtils.showToast(context, context.getResources().getString(R.string.stocklimit));
                                  }else{
                                     // Log.i("====22sss====","575buyNum=="+buyNum+",,,stokeNum=="+stokeNum);
                                      selectedIdList.add(list.get(ShoppingCartGrid.select_item).getCartItemId());
                                      ShoppingCartGrid.isFocus=true;
                                      getList();
                                      notifyDataSetChanged();
                                  }

                              }


                              //检测全选按钮的文本显示
                              if(btn_select.getText().toString().equals(context.getResources().getString(R.string.checkall))&&selectedIdList.size()==getListTotalCount()){
                                  btn_select.setText(context.getResources().getString(R.string.cancel));
                                  Drawable img= context.getResources().getDrawable(R.drawable.cancel_icon);
                                  //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                  img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                  btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                  btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                              }

                          }

                          for(int i=0;i<selectedIdList.size();i++){
                              Log.i("===sss===","sn"+ShoppingCartGrid.select_item+"==="+selectedIdList.get(i));
                          }
                          // 因选中商品状态发生变化，满赠的赠品，满赠，满减的策略，总价，优惠价，数量，合计，全选文本显示都需要随之变化
                          if(selectedIdList.size()==0){
                              //原始总价
                              price.setText("¥0.00");
                              //优惠总价
                              totalprice.setText("¥0.00");
                              //优惠了的总价
                              discount.setText("-¥0.00");
                              count.setText("0");
                          }else{
                              getShoppingCartDiscountRefrsh(getCartItemIdList());
                          }

                      }
                  });

                  /*
                  *
                  *
                  * 为焦点在功能键时，按返回键的监听
                  *
                  * */
                  holder2.btn_back.setOnKeyListener(onKeyListener);
                  holder2.btn_details.setOnKeyListener(onKeyListener);
                  holder2.btn_ok.setOnKeyListener(onKeyListener);
                  holder2.btn_modify.setOnKeyListener(onKeyListener);
                  holder2.btn_delete.setOnKeyListener(onKeyListener);
                  holder2.layout_item.setOnKeyListener(onKeyListener);


                  break;
              case TYPE_3:
                  holder3.line.setVisibility(View.VISIBLE);
                  holder3.clear_invalid.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          showClearInvalidGoodsDialog();
                      }
                  });
                       /*
                  *
                  *
                  * 为焦点在清空失效商品按钮时，按返回键的监听
                  *
                  * */
                  holder3.clear_invalid.setOnKeyListener(onKeyListener);
                  break;
              case TYPE_4:
                  FrescoHelper.setImage(holder4.image, Uri.parse(list.get(position).getSquareUrl()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.width_shopping_240),context.getResources().getDimensionPixelSize(R.dimen.height_shopping_167)));
                  holder4.invalid_layout.setVisibility(View.VISIBLE);
                  holder4.invalid_text.setText(context.getResources().getString(R.string.invalid));
                  holder4.name.setText(list.get(position).getGoodsName());
                  holder4.content.setText(list.get(position).getSpecValue());
                  holder4.count.setText(context.getResources().getString(R.string.quantity)+list.get(position).getBuyNum());
                  holder4.limit.setVisibility(View.INVISIBLE);
                  holder4.price.setText(Utils.getPrice(list.get(position).getRatePrice()));

                  holder4.layout_item.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ShoppingCartGrid.isFocus=false;
                          getList();
                          notifyDataSetChanged();
                      }
                  });
                  holder4.layout_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          if(!hasFocus&&isItemFocus){
                              Log.i("==444422==","vh4重新落入焦点");
                              ShoppingCartGrid.isFocus=true;
                              notifyDataSetChanged();
                              isItemFocus=false;
                          }
                      }
                  });
                  holder4.btn_details.setVisibility(View.GONE);
                  holder4.btn_modify.setVisibility(View.GONE);
                  holder4.btn_ok.setVisibility(View.GONE);
                  /*
                  * 功能键的监听
                  *
                  * */
                  holder4.btn_back.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ShoppingCartGrid.isFocus=true;
                          notifyDataSetChanged();
                      }
                  });
                  holder4.btn_delete.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          deleteShoppingCartData(list.get(ShoppingCartGrid.select_item),ShoppingCartGrid.select_item);
                      }
                  });

                        /*
                  *
                  *
                  * 为焦点在功能键时，按返回键的监听
                  *
                  * */
                  holder4.btn_back.setOnKeyListener(onKeyListener);
                  holder4.btn_delete.setOnKeyListener(onKeyListener);
                 // holder4.layout_item.setOnKeyListener(onKeyListener);


                  break;
          }
        if(ShoppingCartGrid.isFocus){//选中时，展现焦点效果
            this.select_item = ShoppingCartGrid.select_item;
            Log.i("==22222==","adapter669"+select_item);
            Log.i("====6666====","===select_item=="+select_item);
            try{
                if(this.select_item == position){//选中
                    switch(type){
                        case TYPE_1:
                            Log.i("==4444==","===621=="+position);
                            holder1.btn_gather.setFocusable(true);
                            holder1.btn_gather.requestFocus();
                            break;
                        case TYPE_2:
                            Log.i("==4444==","===626"+position);
                            holder2.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_selector));
                            holder2.layout_item.setFocusable(true);
                            holder2.layout_item.requestFocus();
                            holder2.layout_function.setVisibility(View.GONE);
                            break;
                        case TYPE_3:
                            Log.i("==4444==","===633"+position);
                            holder3.clear_invalid.setFocusable(true);
                            holder3.clear_invalid.requestFocus();
                            break;
                        case TYPE_4:
                            Log.i("==4444==","===638=="+position);
                            holder4.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_selector));
                            holder4.layout_item.setFocusable(true);
                            holder4.layout_item.requestFocus();
                            holder4.layout_function.setVisibility(View.GONE);
                            break;
                    }
                }else{//未选中
                    switch(type){
                        case TYPE_1:
                            Log.i("==4444==","===648=="+position);
                            holder1.btn_gather.setFocusable(false);
                            break;
                        case TYPE_2:
                            Log.i("==4444==","===652=="+position);
                            holder2.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_selector));
                            holder2.layout_item.setFocusable(false);
                            holder2.layout_function.setVisibility(View.GONE);
                            break;
                        case TYPE_3:
                            Log.i("==4444==","===658=="+position);
                            holder3.clear_invalid.setFocusable(false);
                            break;
                        case TYPE_4:
                            Log.i("==4444==","===662=="+position);
                            holder4.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_selector));
                            holder4.layout_item.setFocusable(false);
                            holder4.layout_function.setVisibility(View.GONE);
                            break;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{//点击时，弹出功能键
            Log.i("====6666====","===点击=="+position);
            this.select_item = ShoppingCartGrid.select_item;
            Log.i("==22222==","adapter729");
            try{
                if(this.select_item == position){//点击
                    switch(type){
                        case TYPE_2:
                            //对于是否选中，ok键的文本显示不同的处理，选中状态-->取消，，，未选中状态-->选中
                            Log.i("====6666====","===679=="+position);
                            if(holder2.layout_okflag.getVisibility()==View.VISIBLE){//有选中标识
                                holder2.btn_ok.setText(context.getResources().getString(R.string.cancel));
                                isOk=false;
                            }else{//无选中标识
                                holder2.btn_ok.setText(context.getResources().getString(R.string.selected));
                                isOk=true;
                            }
                            holder2.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_shape));
                            holder2.layout_function.setVisibility(View.VISIBLE);
                            holder2.layout_item.setFocusable(false);
                            holder2.btn_ok.setFocusable(true);
                            holder2.btn_ok.requestFocus();
                            break;
                        case TYPE_4:
                            Log.i("====6666====","===694=="+position);
                            holder4.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_shape));
                            holder4.layout_function.setVisibility(View.VISIBLE);
                            holder4.layout_item.setFocusable(false);
                            holder4.btn_back.setFocusable(true);
                            holder4.btn_back.requestFocus();
                            break;
                    }
                }else{//未点击
                    switch(type){
                        case TYPE_2:
                            Log.i("====6666====","===705=="+position);
                            holder2.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_shape));
                            holder2.layout_function.setVisibility(View.GONE);
                            holder2.layout_item.setFocusable(false);
                            break;
                        case TYPE_4:
                            Log.i("====6666====","===711=="+position);
                            holder4.layout_item.setBackground(context.getResources().getDrawable(R.drawable.shoppingnew_shape));
                            holder4.layout_function.setVisibility(View.GONE);
                            holder4.layout_item.setFocusable(false);
                            break;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }


        return convertView;
    }

    //各个布局的控件资源
    class ViewHolder1{//上半部分的首个title
        //策略名称
        TextView wayname;
        //策略的具体内容
        TextView waycontent;
        //凑单按钮
        Button btn_gather;
        //满赠商品的容器
        LinearLayout container;
    }
    class ViewHolder2{//上半部分的非首个
        //买赠赠品的容器
        LinearLayout container;
        //分割下划线
        TextView line;
        TextView divide_line1;
        TextView divide_line2;
        //对应item的商品展示框
        RelativeLayout layout_item;
        //商品的图片
        SimpleDraweeView image;
        //无库存商品的图标
        LinearLayout invalid_layout;
        //无库存商品的文本
        TextView invalid_text;
        //选中标识
        LinearLayout layout_okflag;
        //商品的名称
        TextView name;
        //规格内容
        TextView content;
        //商品数量
        TextView count;
        //限制数量
        TextView limit;
        //商品价格
        TextView price;
        //呼出功能键布局
        LinearLayout layout_function;
        //返回按钮
        Button btn_back;
        //详情按钮
        Button btn_details;
        //选中按钮
        Button btn_ok;
        //修改按钮
        Button btn_modify;
        //删除按钮
        Button btn_delete;

    }
    class ViewHolder3{//下半部分的首个
        //清空无效商品的按钮
        Button clear_invalid;
        TextView line;
    }
    class ViewHolder4{//下半部分的非首个
        //对应item的商品展示框
        RelativeLayout layout_item;
        //商品的图片
        SimpleDraweeView image;
        //失效商品的图标
        LinearLayout invalid_layout;
        //失效商品的文本
        TextView invalid_text;
        //商品的名称
        TextView name;
        //规格内容
        TextView content;
        //商品数量
        TextView count;
        //限制数量
        TextView limit;
        //商品价格
        TextView price;
        //呼出功能键布局
        LinearLayout layout_function;
        //返回按钮
        Button btn_back;
        //详情按钮
        Button btn_details;
        //选中按钮
        Button btn_ok;
        //修改按钮
        Button btn_modify;
        //删除按钮
        Button btn_delete;
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //正在交互
                if(((ShoppingCartGrid)context).mProgressDialog!=null && ((ShoppingCartGrid)context).mProgressDialog.isShowing())
                    return true;

                if(holder3!=null&&holder3.clear_invalid!=null&&(v.getId()) == (holder3.clear_invalid.getId())){
                    isItemFocus=false;
                }
                if(holder1!=null&&holder1.btn_gather!=null&&(v.getId()) == (holder1.btn_gather.getId())){
                    isItemFocus=false;
                }
                if(holder2!=null&&holder2.layout_item!=null&&(v.getId()) == (holder2.layout_item.getId())){
                    isItemFocus=false;
                }
                if(holder4!=null&&holder4.layout_item!=null&&(v.getId()) == (holder4.layout_item.getId())){
                    isItemFocus=false;
                }


                if(holder2!=null&&holder2.btn_back!=null&&(v.getId()) == (holder2.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//焦点在返回键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder2!=null&&holder2.btn_back!=null&&(v.getId()) == (holder2.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//焦点在返回键时按上键
                    return true;
                }else if(holder2!=null&&holder2.btn_back!=null&&(v.getId()) == (holder2.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//焦点在返回键时按下键
                    return true;
                }else if(holder2!=null&&holder2.btn_back!=null&&(v.getId()) == (holder2.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){//焦点在返回键时按左键
                    return true;
                }else if(holder2!=null&&holder2.btn_details!=null&&(v.getId()) == (holder2.btn_details.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//焦点在详情键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder2!=null&&holder2.btn_details!=null&&(v.getId()) == (holder2.btn_details.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//焦点在详情键时按上键
                    return true;
                }else if(holder2!=null&&holder2.btn_details!=null&&(v.getId()) == (holder2.btn_details.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//焦点在详情键时按下键
                    return true;
                }else if(holder2!=null&&holder2.btn_ok!=null&&(v.getId()) == (holder2.btn_ok.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//焦点在Ok键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder2!=null&&holder2.btn_ok!=null&&(v.getId()) == (holder2.btn_ok.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//焦点在Ok键时按上键
                    return true;
                }else if(holder2!=null&&holder2.btn_ok!=null&&(v.getId()) == (holder2.btn_ok.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//焦点在Ok键时按下键
                    return true;
                }else if(holder2!=null&&holder2.btn_modify!=null&&(v.getId()) == (holder2.btn_modify.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//焦点在修改键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder2!=null&&holder2.btn_modify!=null&&(v.getId()) == (holder2.btn_modify.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//焦点在修改键时按上键
                    return true;
                }else if(holder2!=null&&holder2.btn_modify!=null&&(v.getId()) == (holder2.btn_modify.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//焦点在修改键时按下键
                    return true;
                }else if(holder2!=null&&holder2.btn_delete!=null&&(v.getId()) == (holder2.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//焦点在删除键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder2!=null&&holder2.btn_delete!=null&&(v.getId()) == (holder2.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//焦点在删除键时按上键
                    return true;
                }else if(holder2!=null&&holder2.btn_delete!=null&&(v.getId()) == (holder2.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//焦点在删除键时按下键
                    return true;
                }else if(holder2!=null&&holder2.btn_delete!=null&&(v.getId()) == (holder2.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){//焦点在删除键时按右键
                    return true;
                }else if(holder4!=null&&holder4.btn_back!=null&&(v.getId()) == (holder4.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//失效商品中，焦点在返回键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder4!=null&&holder4.btn_back!=null&&(v.getId()) == (holder4.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//失效商品中，焦点在返回键时按上键
                    return true;
                }else if(holder4!=null&&holder4.btn_back!=null&&(v.getId()) == (holder4.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//失效商品中，焦点在返回键时按下键
                    return true;
                }else if(holder4!=null&&holder4.btn_back!=null&&(v.getId()) == (holder4.btn_back.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){//失效商品中，焦点在返回键时按左键
                    return true;
                }else if(holder4!=null&&holder4.btn_delete!=null&&(v.getId()) == (holder4.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_BACK){//失效商品中，焦点在删除键时按返回键
                    ShoppingCartGrid.isFocus=true;
                    notifyDataSetChanged();
                    return true;
                }else if(holder4!=null&&holder4.btn_delete!=null&&(v.getId()) == (holder4.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//失效商品中，焦点在删除键时按上键
                    return true;
                }else if(holder4!=null&&holder4.btn_delete!=null&&(v.getId()) == (holder4.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){//失效商品中，焦点在删除键时按下键
                    return true;
                }else if(holder4!=null&&holder4.btn_delete!=null&&(v.getId()) == (holder4.btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){//失效商品中，焦点在删除键时按右键
                    return true;
                }else if(holder1!=null&&holder1.btn_gather!=null&&(v.getId()) == (holder1.btn_gather.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//holder1.btn_gather
                    if(select_item==1){
                        iscover=true;
                    }
                    if(ShoppingCartGrid.select_item==0){
                        return true;
                    }

                }else if(holder1!=null&&holder1.btn_gather!=null&&(v.getId()) == (holder1.btn_gather.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){//holder1.btn_gather


                }else if(holder2!=null&&holder2.layout_item!=null&&(v.getId()) == (holder2.layout_item.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//holder2.layout_item
                    if(select_item==1){
                        iscover=true;
                    }
                    if(ShoppingCartGrid.select_item==0){
                        return true;
                    }
                }else if(holder3!=null&&holder3.clear_invalid!=null&&(v.getId()) == (holder3.clear_invalid.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){//holder3.clear_invalid
                    if(select_item==1){
                        iscover=true;
                    }
                    if(ShoppingCartGrid.select_item==0){
                        return true;
                    }

                }
            }
            return false;
        }
    };
    public void addAll(Collection<?extends GroupData> collection){
        if (collection!=null){
            groupDataList.clear();
            groupDataList.addAll(collection);
            getList();
            notifyDataSetChanged();
        }
    }
    public void notity(){
        getList();
        notifyDataSetChanged();
    }

    public void getList(){
        //处理数据格式
        list=new ArrayList<ShoppingCartRecord>();
        if(groupDataList!=null){
            for(int i=0;i<groupDataList.size();i++){//分成满减，满赠，普通，失效四组
                String totalType=groupDataList.get(i).getPromotionType();
                List<ShoppingCartRecord> records = groupDataList.get(i).getSkuList();
                for(int j=0;j<records.size();j++){

                    if(j==0){
                        records.get(j).setFirst(true);
                        if(!totalType.equals(ConStant.COMMON)){
                            ShoppingCartRecord data=new ShoppingCartRecord();
                            data.setTitleBoolean(true);
                            data.setType(totalType);
                            //每个Item加入满赠，满减的sn
                            Log.i("=====33333=====","PromotionSn1=="+groupDataList.get(i).getPromotionSn());
                            if(totalType.equals(ConStant.FULL_GIFTS)||totalType.equals(ConStant.FULL_CUT)){
                                Log.i("=====33333=====","PromotionSn2=="+groupDataList.get(i).getPromotionSn());
                                data.setPromotionSn(groupDataList.get(i).getPromotionSn());
                            }
                            if(totalType.equals(ConStant.FULL_GIFTS)){
                                data.setLimitPrice(groupDataList.get(i).getLimitPrice());
                                data.setActivecontent(groupDataList.get(i).getActivecontent());
                                data.setPromotionInfo(groupDataList.get(i).getPromotionInfo());
                                data.setShow(groupDataList.get(i).isShow());
                            }else if(totalType.equals(ConStant.FULL_CUT)){
                                data.setActivecontent(groupDataList.get(i).getActivecontent());
                                data.setPromotionInfo(groupDataList.get(i).getPromotionInfo());

                            }
                            list.add(data);
                        }

                    }else{
                        records.get(j).setFirst(false);
                    }
                    records.get(j).setTitleBoolean(false);
                    records.get(j).setType(totalType);
                    Log.i("=====55555=====","price=="+records.get(j).getOriginalPrice());
                    list.add(records.get(j));
                }
            }
        }


    }

    public List<ShoppingCartRecord> getAllList(){
        return list;
    }

    /**
     * 清空失效商品对话框
     */
    private void showClearInvalidGoodsDialog() {
        //检测网络状态
        if (!Utils.isConnected(context)) {
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context, error_tips);
            return;
        }

        //正在交互
        if (((ShoppingCartGrid)context).mProgressDialog != null && ((ShoppingCartGrid)context).mProgressDialog.isShowing())
            return;

        clearInvalidDialog = new ConfirmDialog(context, new ConfirmDialog.ConfirmOnClickListener() {
            @Override
            public void onOk() {
                Utils.print(tag, "onOk");
                deleteAllInvalidCart(list.get(list.size()-1).getCartSn());

            }

            @Override
            public void onCancel() {
                Utils.print(tag, "onCancel");
                clearInvalidDialog.dismiss();
            }

            @Override
            public void onDismiss() {
                Utils.print(tag, "onDismiss");
            }
        });
        clearInvalidDialog.setTitle(context.getResources().getString(R.string.clearinvalidtip));
        clearInvalidDialog.setOkButton(context.getResources().getString(R.string.ok));
        clearInvalidDialog.setCancelButton(context.getResources().getString(R.string.cancel));
        clearInvalidDialog.showUI();
    }
    /**
     * 删除购物车的商品
     */
    public void deleteShoppingCartData(ShoppingCartRecord bean,int pos){
        Utils.print(tag,"deleteShoppingCartData");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(((ShoppingCartGrid)context).mProgressDialog!=null && ((ShoppingCartGrid)context).mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+((ShoppingCartGrid)context).op_status);
        if(((ShoppingCartGrid)context).op_status)
            return;
        ((ShoppingCartGrid)context).op_status = true;

        ((ShoppingCartGrid)context).startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("cartItemId",bean.getCartItemId());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpDeleteShoppingcartData(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ((ShoppingCartGrid)context).stopProgressDialog();
                        ((ShoppingCartGrid)context).op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        ((ShoppingCartGrid)context).stopProgressDialog();
                        ((ShoppingCartGrid)context).op_status=false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }

                        //服务器删除成功后的处理

                        //选中集合的处理
                        if(selectedIdList.contains(list.get(pos).getCartItemId())){
                            selectedIdList.remove(list.get(pos).getCartItemId());
                        }

                       //焦点数据的处理
                        getShoppingCartCommodity(pos);

                    }
                });
        ((ShoppingCartGrid)context).addSubscription(s);
    }

    /**
     * 获取购物车商品列表
     */
    public void getShoppingCartCommodity(int pos) {
        Utils.print(tag, "getShoppingCartCommodity");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(((ShoppingCartGrid)context).mProgressDialog!=null && ((ShoppingCartGrid)context).mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+((ShoppingCartGrid)context).op_status);
        if(((ShoppingCartGrid)context).op_status)
            return;
        ((ShoppingCartGrid)context).op_status = true;


        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartData(ConStant.APP_VERSION,input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartData>() {
                    @Override
                    public void onCompleted() {
                        //刷新一下价格等变量
                        if(selectedIdList.size()==0){
                            //原始总价
                            price.setText("¥0.00");
                            //优惠总价
                            totalprice.setText("¥0.00");
                            //优惠了的总价
                            discount.setText("-¥0.00");
                            count.setText("0");
                        }else{
                            getShoppingCartDiscountRefrsh(getCartItemIdList());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ((ShoppingCartGrid)context).op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartData shoppingCartData) {
                        Utils.print(tag, "status==" + shoppingCartData.getErrorMessage() + ",value=" + shoppingCartData.getReturnValue());
                        ((ShoppingCartGrid)context).op_status=false;
                        if (shoppingCartData.getReturnValue() == -1){
                            ToastUtils.showToast(context,shoppingCartData.getErrorMessage());
                            return;
                        }


                        groupDataList= shoppingCartData.getData();
                        //发删除的广播，处理因删除引起的主页变化
                        Intent mIntent = new Intent("com.dianshang.damai.shoppingnew");
                        mIntent.putExtra("tag","delete");
                        mIntent.putParcelableArrayListExtra("list",(ArrayList)groupDataList);
                        //发送广播
                        context.sendBroadcast(mIntent);
                        if(groupDataList==null){
                            layout_has.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            goshopping.requestFocus();
                            select_item=ShoppingCartGrid.select_item=-1;
                            selectedIdList.clear();

                        }
                        if(groupDataList!=null&&groupDataList.size()==0){
                            layout_has.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            goshopping.requestFocus();
                            select_item=ShoppingCartGrid.select_item=-1;
                            selectedIdList.clear();

                        }
                        getList();
                        if(pos<list.size()){
                            select_item=ShoppingCartGrid.select_item=pos;
                            Log.i("==22222==","adapter1272");
                        }else{
                            if(pos!=0){
                                select_item=ShoppingCartGrid.select_item=pos-1;
                                Log.i("==22222==","adapter1276");
                            }else{//购物车为空
                                select_item=ShoppingCartGrid.select_item=pos-1;
                                Log.i("==22222==","adapter1279");
                                layout_has.setVisibility(View.GONE);
                                layout_empty.setVisibility(View.VISIBLE);
                                goshopping.requestFocus();
                            }

                        }
                        if(select_item!=-1){
                            ShoppingCartGrid.isFocus=true;
                            getList();
                            notifyDataSetChanged();
                        }

                            /*
                        * 检测一下，记录选中集合里是否存在失效商品
                        * */
                        for(int i=0;i<groupDataList.size();i++){
                            if(groupDataList.get(i).getPromotionType().equals(ConStant.INVALID)){
                                List<ShoppingCartRecord> invalidList=groupDataList.get(i).getSkuList();
                                for(int j=0;j<invalidList.size();j++){
                                    if(selectedIdList.contains(invalidList.get(j).getCartItemId())){//选中集合中含有无效商品，则删除
                                        selectedIdList.remove(invalidList.get(j).getCartItemId());
                                    }
                                }

                            }
                        }
                        /*
                        * 存在有效商品已结算提交，购物车不存在此商品
                        * */
                        List<Long> selectedIdListTotal=new ArrayList<Long>();
                        List<Long> selectedIdListStoke=new ArrayList<Long>();
                        for(int i=0;i<list.size();i++){
                            selectedIdListTotal.add(list.get(i).getCartItemId());
                            if(list.get(i).getBuyNum()>list.get(i).getStockNum()){//库存不足
                                selectedIdListStoke.add(list.get(i).getCartItemId());
                            }
                        }
                        List<Long> selectedIdListTotalDelete=new ArrayList<Long>();
                        for(int i=0;i<selectedIdList.size();i++){
                            if(!selectedIdListTotal.contains(selectedIdList.get(i))){
                                //selectedIdList.remove(selectedIdList.get(i));
                                selectedIdListTotalDelete.add(selectedIdList.get(i));

                            }
                        }
                        if(selectedIdListTotalDelete.size()!=0){
                            selectedIdList.removeAll(selectedIdListTotalDelete);
                            //检测全选按钮的文本显示
                            if(btn_select.getText().toString().equals(context.getResources().getString(R.string.cancel))){
                                btn_select.setText(context.getResources().getString(R.string.checkall));
                                Drawable img= context.getResources().getDrawable(R.drawable.select_icon);
                                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                            }
                        }
                           /*
                        * 重新回到购物车页面，存在有效商品中库存不足的情况，若选中状态，要取消选中。
                        * */
                        //selectedIdListStoke
                        for(int i=0;i<selectedIdListStoke.size();i++){
                            if(selectedIdList.contains(selectedIdListStoke.get(i))){
                                selectedIdList.remove(selectedIdListStoke.get(i));
                                //检测全选按钮的文本显示
                                if(btn_select.getText().toString().equals(context.getResources().getString(R.string.cancel))){
                                    btn_select.setText(context.getResources().getString(R.string.checkall));
                                    Drawable img= context.getResources().getDrawable(R.drawable.select_icon);
                                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                    btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                    btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                                }
                            }
                        }

                        int countValid=0;
                        for(int i=0;i<groupDataList.size();i++){
                            if(!groupDataList.get(i).getPromotionType().equals(ConStant.INVALID)){
                                List<ShoppingCartRecord> validList=groupDataList.get(i).getSkuList();
                                for(int j=0;j<validList.size();j++){
                                    countValid=countValid+1;
                                }
                            }
                        }
                        if(selectedIdList.size()!=countValid){
                            //检测全选按钮的文本显示
                            if(btn_select.getText().toString().equals(context.getResources().getString(R.string.cancel))){
                                btn_select.setText(context.getResources().getString(R.string.checkall));
                                Drawable img= context.getResources().getDrawable(R.drawable.select_icon);
                                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                            }
                        }else{
                            if(btn_select.getText().toString().equals(context.getResources().getString(R.string.checkall))){
                                btn_select.setText(context.getResources().getString(R.string.cancel));
                                Drawable img= context.getResources().getDrawable(R.drawable.cancel_icon);
                                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                btn_select.setPadding(context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,context.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                            }

                        }




                    }
                });
    }

    /**
     * 删除所有失效的购物车商品
     */
    public void deleteAllInvalidCart(String cartSn) {
        Utils.print(tag, "deleteAllInvalidCart");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(((ShoppingCartGrid)context).mProgressDialog!=null && ((ShoppingCartGrid)context).mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+((ShoppingCartGrid)context).op_status);
        if(((ShoppingCartGrid)context).op_status)
            return;
        ((ShoppingCartGrid)context).op_status = true;

        ((ShoppingCartGrid)context).startProgressDialog();
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("cartSn", cartSn); //购物车id
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpDeleteAllInvalidCart(input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ((ShoppingCartGrid)context).stopProgressDialog();
                        ((ShoppingCartGrid)context).op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        ((ShoppingCartGrid)context).stopProgressDialog();
                        ((ShoppingCartGrid)context).op_status=false;
                        if (statusData.getReturnValue() == -1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }
                        clearInvalidDialog.dismiss();
                        //list去除无效商品
                        List<ShoppingCartRecord> invalidList=new ArrayList<ShoppingCartRecord>();
                        for(int i=0;i<list.size();i++){
                            if(list.get(i).getType().equals(ConStant.INVALID)){
                                invalidList.add(list.get(i));
                            }
                        }
                        list.removeAll(invalidList);
                        //总集合去除无效商品
                        for(int i=0;i<groupDataList.size();i++){
                            if(groupDataList.get(i).getPromotionType().equals(ConStant.INVALID)){
                                groupDataList.remove(i);
                                break;
                            }
                        }

                        //发删除的广播，处理因删除引起的主页变化
                        Intent mIntent = new Intent("com.dianshang.damai.shoppingnew");
                        mIntent.putExtra("tag","delete");
                        mIntent.putParcelableArrayListExtra("list",(ArrayList)groupDataList);
                        //发送广播
                        context.sendBroadcast(mIntent);

                        //定焦点
                        if(list.size()==0){//购物车为空
                            select_item=ShoppingCartGrid.select_item=-1;
                            layout_has.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            goshopping.requestFocus();

                        }else{//购物车非空
                            select_item=ShoppingCartGrid.select_item=list.size()-1;
                            Log.i("==22222==","adapter1391");
                            ShoppingCartGrid.isFocus=true;
                            //getList();
                            notifyDataSetChanged();

                        }


                    }
                });
    }

    /*
    *
    * 获取由specSn选中集合的cartItemId的list集合形式
    * */
    public List<Long> getCartItemIdList(){
        List<Long> cartItemIdList= new ArrayList<Long>();
        for(int i=0;i<selectedIdList.size();i++){
            for(int j=0;j<list.size();j++){
                if(!list.get(j).isTitleBoolean()&&list.get(j).getCartItemId()==(selectedIdList.get(i))){//正常商品数据，非头部数据
                    cartItemIdList.add(list.get(j).getCartItemId());
                    break;
                }
            }
        }
        return cartItemIdList;

    }


    /**
     * 获取选中情况而需要更新的变量
     */
    public void getShoppingCartDiscountRefrsh(List<Long> cartItemIds) {
        Utils.print(tag, "getShoppingCartDiscountRefrsh");
        //检测网络状态
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }

        //正在交互
        if(((ShoppingCartGrid)context).mProgressDialog!=null &&((ShoppingCartGrid)context).mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+((ShoppingCartGrid)context).op_status);
        if(((ShoppingCartGrid)context).op_status)
            return;
        ((ShoppingCartGrid)context).op_status = true;


        String input = "";
        try {
            Gson json = new Gson();
            PostCartItemInfo postCartInfo = new PostCartItemInfo();
            postCartInfo.setUserid(ConStant.getInstance(context).userID);
            postCartInfo.setItemIdList(cartItemIds);
            input = json.toJson(postCartInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartDiscount(ConStant.APP_VERSION,input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DiscountData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ((ShoppingCartGrid)context).op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(DiscountData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        ((ShoppingCartGrid)context).op_status=false;
                        if(data.getReturnValue()==-1){
                            ToastUtils.showToast(context,data.getErrorMessage());
                            return;
                        }else if(data.getReturnValue()==-4){//需要刷新数据，存在失效商品
                            ToastUtils.showToast(context,data.getErrorMessage());
                            //发刷新的广播，处理因失效引起的主页变化
                            Intent mIntent = new Intent("com.dianshang.damai.shoppingnew");
                            mIntent.putExtra("tag","refresh");
                            mIntent.putParcelableArrayListExtra("list",(ArrayList)groupDataList);
                            //发送广播
                            context.sendBroadcast(mIntent);
                            return;
                        }
                        //根据获取的数据计算
                        List<DiscountType> discountTypeList=data.getData();
                           /*
                        * 总价的计算对应14
                        * */
                        double originalTotalPrice=0;
                          /*
                        * 优惠总价对应15
                        * */
                        double discountPrice=0;
                         /*
                        * 数量对应16
                        * */
                        int buyNumTotal=0;
                        /*
                        * 满赠是否显示赠品
                        * */
                        boolean isShow=false;
                        /*
                        * 满赠里有商品
                        * */
                        boolean ishas=false;
                         /*
                        * 满减里有商品
                        * */
                        boolean ishasfullcut=false;
                         /*
                        * 满减是否满足条件
                        * */
                        boolean isShowfullcut=false;

                        //计算满赠里的商品优惠后的总价
                        double fullgiftPrice=0;
                        //计算满减里的商品优惠后的总价
                        double fullcutPrice=0;
                        //先初始化数据
                        for(int j=0;j<groupDataList.size();j++){
                            if(groupDataList.get(j).getPromotionType().equals(ConStant.FULL_GIFTS)){//该满赠分组中，没有选中的商品
                                ishas=false;
                                //根本就不存在满赠商品（购满XX元得赠品，赠完即止）
                                groupDataList.get(j).setActivecontent(context.getResources().getString(R.string.giveawaytip1)+groupDataList.get(j).getLimitPrice()+context.getResources().getString(R.string.giveawaytip2));
                                groupDataList.get(j).setShow(false);
                            }
                            if(groupDataList.get(j).getPromotionType().equals(ConStant.FULL_CUT)){//该满减分组中，没有选中的商品
                                ishasfullcut=false;
                                //根本就不存在满赠商品（满xx元减yy元,用逗号隔开）
                                //满减总梯度
                                List<ActivityInfoData> fullcutBeanTotalList=groupDataList.get(j).getPromotionInfo();
                                String fullcut_tv="";
                                for(int k=0;k<fullcutBeanTotalList.size();k++){
                                    if(k!=fullcutBeanTotalList.size()-1){//非最后一个
                                        fullcut_tv=fullcut_tv+context.getResources().getString(R.string.fullcuttip1)+fullcutBeanTotalList.get(k).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullcutBeanTotalList.get(k).getCutPrice()+context.getResources().getString(R.string.fullgifttip4)+",";
                                    }else{
                                        fullcut_tv=fullcut_tv+context.getResources().getString(R.string.fullcuttip1)+fullcutBeanTotalList.get(k).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullcutBeanTotalList.get(k).getCutPrice()+context.getResources().getString(R.string.fullgifttip4);
                                    }

                                }
                                groupDataList.get(j).setActivecontent(fullcut_tv);

                            }

                        }
                        for(int j=0;j<groupDataList.size();j++){
                        for(int i=0;i<discountTypeList.size();i++){
                                if(discountTypeList.get(i).getPromotionType().equals(ConStant.FULL_GIFTS)&&discountTypeList.get(i).getPromotionSn().equals(groupDataList.get(j).getPromotionSn())){
                                    ishas=true;
                                    //计算刷新接口满赠商品的优惠总价格
                                    List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                                    for(int k=0;k<skuList.size();k++){
                                        fullgiftPrice=addValue(fullgiftPrice,skuList.get(k).getSumRatePrice());
                                    }

                                    if(discountTypeList.get(i).isReachCondition()){
                                        isShow=true;
                                    }else{
                                        isShow=false;
                                    }
                                    groupDataList.get(j).setShow(isShow);


                                    if(isShow){//满足条件（已购满XX元，可得赠品，赠完即止）
                                        groupDataList.get(j).setActivecontent(context.getResources().getString(R.string.fullgifttip1)+groupDataList.get(j).getLimitPrice()+context.getResources().getString(R.string.fullgifttip2));
                                    }else{//不满足（购满XX元得赠品，赠完即止，还差yy元。）
                                        if(ishas){//有满赠商品但不满足条件
                                            groupDataList.get(j).setActivecontent(context.getResources().getString(R.string.giveawaytip1)+groupDataList.get(j).getLimitPrice()+context.getResources().getString(R.string.giveawaytip2)+context.getResources().getString(R.string.fullgifttip3)+sub(groupDataList.get(j).getLimitPrice(),fullgiftPrice)+context.getResources().getString(R.string.fullgifttip4));
                                        }/*else{//根本就不存在满赠商品（购满XX元得赠品，赠完即止）
                                            groupDatas.get(j).setActivecontent(mContext.getResources().getString(R.string.giveawaytip1)+groupDatas.get(j).getLimitPrice()+mContext.getResources().getString(R.string.giveawaytip2));
                                        }*/

                                    }

                                }

                                fullgiftPrice=0;


                                if(discountTypeList.get(i).getPromotionType().equals(ConStant.FULL_CUT)&&discountTypeList.get(i).getPromotionSn().equals(groupDataList.get(j).getPromotionSn())){
                                    ishasfullcut=true;
                                    //选中满减商品里的总优惠价格
                                    List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                                    for(int k=0;k<skuList.size();k++){
                                        fullcutPrice=addValue(fullcutPrice,skuList.get(k).getSumRatePrice());
                                    }


                                    if(discountTypeList.get(i).isReachCondition()){
                                        isShowfullcut=true;
                                    }else{
                                        isShowfullcut=false;
                                    }
                                    //得到刷新接口里的满减梯度
                                    List<FullCutBean> fullCutBeanList=null;
                                    fullCutBeanList=discountTypeList.get(i).getReachActInfo();

                                    Log.i("====2222====","isShowfullcut==="+isShowfullcut);
                                    //当前满减的策略
                                    if(isShowfullcut){//满足条件（满xx元减yy元，已优惠yy元。满zz元减mm元。）
                                        String fullcut_tv="";
                                        for(int k=0;k<fullCutBeanList.size();k++){
                                            if(k==0){
                                                fullcut_tv=context.getResources().getString(R.string.fullcuttip1)+fullCutBeanList.get(0).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(0).getCutPrice()+context.getResources().getString(R.string.fullcuttip3)+fullCutBeanList.get(0).getCutPrice()+context.getResources().getString(R.string.fullgifttip4);
                                            }else if(k==1){
                                                fullcut_tv=fullcut_tv+context.getResources().getString(R.string.fullcuttip4)+fullCutBeanList.get(1).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(1).getCutPrice()+context.getResources().getString(R.string.fullgifttip4);
                                            }
                                        }
                                        groupDataList.get(j).setActivecontent(fullcut_tv);
                                    }else{//不满足（满xx元减yy元，还差mm元。）//fullcutPrice
                                        if(ishasfullcut){//有满赠商品但不满足条件
                                            String fullcut_tv="";
                                            for(int k=0;k<fullCutBeanList.size();k++){
                                                if(k==0){
                                                    fullcut_tv=context.getResources().getString(R.string.fullcuttip1)+fullCutBeanList.get(0).getEnoughPrice()+context.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(0).getCutPrice()+context.getResources().getString(R.string.fullcuttip5)+sub(fullCutBeanList.get(0).getEnoughPrice(),fullcutPrice)+context.getResources().getString(R.string.fullgifttip4);
                                                    break;
                                                }
                                            }
                                            groupDataList.get(j).setActivecontent(fullcut_tv);
                                        }/*else{//根本就不存在满赠商品（满xx元减yy元,用逗号隔开）

                                        }*/

                                    }


                                }

                                fullcutPrice=0;



                            }

                        }

                        for(int i=0;i<discountTypeList.size();i++){
                            // originalTotalPrice=originalTotalPrice+discountTypeList.get(i).getSumPrice();
                            originalTotalPrice=addValue(originalTotalPrice,discountTypeList.get(i).getSumPrice());
                            List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                            for(int j=0;j<skuList.size();j++){
                                discountPrice=addValue(discountPrice,skuList.get(j).getSumRatePrice());
                                buyNumTotal=buyNumTotal+skuList.get(j).getBuyNum();
                            }

                        }

                        Log.i("===cccc===","sumPrice=="+originalTotalPrice);
                        //原始总价
                        price.setText("¥"+Utils.getPrice(originalTotalPrice));
                        Log.i("===cccc===","sumRatePrice=="+discountPrice);
                        //优惠总价
                        totalprice.setText("¥"+Utils.getPrice(discountPrice));
                        //优惠了的总价
                        discount.setText("-¥"+Utils.getPrice(sub(originalTotalPrice,discountPrice)));
                        Log.i("====4444====","==="+discount.getText().toString());
                        if(discount.getText().toString().equals("-¥0.00")||discount.getText().toString().equals("-¥0.0")){
                            discount.setText("-¥0.00");
                        }
                        count.setText(""+buyNumTotal);

                        Utils.print(tag,"hasCutFull="+hasCutFull(data));
                        if(hasCutFull(data)){
                            if(!(discount.getText().toString().equals("-¥0.00")||discount.getText().toString().equals("-¥0.0"))) {
                                layout_shoppingcart_discount.setVisibility(View.VISIBLE);
                            }
                        }else {
                            discount.setText("-¥0.00");
                        }

                        getList();
                        notifyDataSetChanged();
                    }
                });
    }


    /**
     * 判断是否有满减活动
     * @param data
     * @return
     */
    private boolean hasCutFull(DiscountData data){
        boolean result = false;
        for (int i = 0; i < data.getData().size(); i++) {
            DiscountType discountType = data.getData().get(i);
            if(discountType.getPromotionType().equals(ConStant.FULL_CUT)){
                result = true;
                break;
            }
        }
        return result;
    }

    public int getListTotalCount(){
        int countList=0;
        for(int i=0;i<list.size();i++){
            if(!list.get(i).isTitleBoolean()&&!list.get(i).getType().equals(ConStant.INVALID)){
                countList=countList+1;
            }
        }
        return countList;
    }
    public int getListTotalCount2(){
        int countList=0;
        for(int i=0;i<list.size();i++){
            if(!list.get(i).isTitleBoolean()){
                countList=countList+1;
            }
        }
        return countList;
    }

    public double addValue(double d1,double d2){
        BigDecimal b1=new BigDecimal(Double.toString(d1));
        BigDecimal b2=new BigDecimal(Double.toString(d2));
        return b1.add(b2).doubleValue();
    }

    public static double sub(double d1,double d2){
        BigDecimal b1=new BigDecimal(Double.toString(d1));
        BigDecimal b2=new BigDecimal(Double.toString(d2));
        return b1.subtract(b2).doubleValue();

    }




}
