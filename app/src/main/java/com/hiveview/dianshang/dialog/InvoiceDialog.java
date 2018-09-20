package com.hiveview.dianshang.dialog;



import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.shoppingcart.OpShoppingCart;
import com.hiveview.dianshang.utils.QrcodeUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.jakewharton.rxbinding.view.RxView;

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
public class InvoiceDialog extends BDialog {


    private String tag = "InvoiceDialog";

    private LayoutInflater mFactory = null;
    private Context context;

    /**
     * 发票类型layout
     */
    @BindView(R.id.layout_invoice_type)
    RelativeLayout layout_invoice_type;
    /**
     * 单位名称layout
     */
    @BindView(R.id.layout_invoice_company)
    LinearLayout layout_invoice_company;
    /**
     * 发票明细layout
     */
    @BindView(R.id.layout_invoice_information)
    RelativeLayout layout_invoice_information;

    /**
     * 纳税人编号layout
     */
    @BindView(R.id.layout_invoice_company_number)
    LinearLayout layout_invoice_company_number;

    /**
     * 左箭头
     */
    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    /**
     * 右箭头
     */
    @BindView(R.id.arrow_right)
    ImageView arrow_right;

    /**
     * 提交按钮
     */
    @BindView(R.id.invoice_submit)
    Button invoice_submit;

    /**
     * 发票提示词语
     */
    @BindView(R.id.invoice_tips)
    TextView invoice_tips;

    /**
     * 输入发票公司名称
     */
    @BindView(R.id.invoice_company)
    EditText invoice_company;

    /**
     * 纳税人识别号
     */
    @BindView(R.id.invoice_company_number)
    EditText invoice_company_number;

    /**
     * 无发票 个人 公司展示
     */
    @BindView(R.id.invoice)
    TextView invoice;

    /**
     * 发票抬头标题
     */
    @BindView(R.id.invoice_head_text)
    TextView invoice_head_text;

    private String[] invoice_type;
    private int typeID = 0;

    private InvoiceKey invoiceKey;

    /**
     * 扫描二维码
     */
    @BindView(R.id.invoice_qrcode)
    SimpleDraweeView invoice_qrcode;

    /**
     * 二维码提示信息
     */
    @BindView(R.id.qr_text)
    TextView qr_text;

    public InvoiceDialog(Context context,InvoiceKey invoiceKey) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.invoiceKey = invoiceKey;

        typeID = invoiceKey.key;


        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.layout_invoice, null);

        invoice_type = context.getResources().getStringArray(R.array.invoice_type);


        setContentView(mView);
        ButterKnife.bind(this);

        initView();
        initEvent();


        getInvoiceQr();

    }


    private void initEvent(){
        RxView.clicks(invoice_submit)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Utils.print(tag,"submit");

                        OpShoppingCart opShoppingCart = new OpShoppingCart();
                        opShoppingCart.setKey(OpShoppingCart.MODIFY_INVOICE);
                        if(typeID==0 || typeID==1){
                            opShoppingCart.setValue(invoice_type[typeID]);
                        }else{
                            if(invoice_company.getText().toString().trim().equals("") || invoice_company.getText().toString().trim().equals(context.getResources().getString(R.string.invoice_input_company_name))){
                                ToastUtils.showToast(context,context.getResources().getString(R.string.error_invoice_null));
                                return;
                            }

                            if(Utils.getStringLength(invoice_company.getText().toString())>50){
                                ToastUtils.showToast(context,context.getResources().getString(R.string.invoice_head_tips));
                                return;
                            }

                            int length = invoice_company_number.getText().toString().trim().length();
                            Utils.print(tag,"len=="+length);
                            if(length!=15 && length!=17 && length!=18 && length!=20){
                                ToastUtils.showToast(context,context.getResources().getString(R.string.error_invoice_length_exceed));
                                return;
                            }

                            if(!Utils.StringFilter(invoice_company_number.getText().toString().trim())) {
                                ToastUtils.showToast(context,context.getResources().getString(R.string.invoice_company_number_error));
                                return;
                            }

                            int len = invoice_company_number.getText().toString().trim().length();
                            if(len!=15 && len!=17 && len!=18 && len!=20) {
                                ToastUtils.showToast(context,context.getResources().getString(R.string.invoice_company_number_error));
                                return;
                            }


                            opShoppingCart.setValue(invoice_type[typeID]+
                                    "#"+invoice_company.getText().toString().trim() +
                                    "#"+invoice_company_number.getText().toString().trim() +
                                    "#"+context.getResources().getString(R.string.invoice_head_info_data));
                        }
                        opShoppingCart.setInvoiceType(typeID);
                        RxBus.get().post(ConStant.obString_modify_shopping_cart,opShoppingCart);
                        dismiss();
                    }
                });


        layout_invoice_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    arrow_left.setBackgroundResource(R.drawable.reduce_commodity_white);
                    arrow_right.setBackgroundResource(R.drawable.add_commodity_white);
                    invoice_head_text.setTextColor(context.getResources().getColor(android.R.color.white));
                    invoice.setTextColor(context.getResources().getColor(android.R.color.white));
                }else{
                    arrow_left.setBackgroundResource(R.drawable.reduce_commodity);
                    arrow_right.setBackgroundResource(R.drawable.add_commodity);
                    invoice_head_text.setTextColor(context.getResources().getColor(android.R.color.black));
                    invoice.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            }
        });


        invoice_company.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    invoice_company.setTextColor(context.getResources().getColor(android.R.color.white));
                    invoice_company.setHintTextColor(context.getResources().getColor(android.R.color.white));
                    layout_invoice_company.setBackgroundResource(R.drawable.invoice_selected);
                    if(invoice_company.getText().toString().equals(context.getResources().getString(R.string.invoice_input_company_name))){
                        invoice_company.setText("");
                    }else{
                        invoice_company.setSelection(invoice_company.getText().toString().length());
                    }
                }else{
                    invoice_company.setHintTextColor(context.getResources().getColor(android.R.color.black));
                    invoice_company.setTextColor(context.getResources().getColor(android.R.color.black));
                    layout_invoice_company.setBackgroundResource(R.drawable.invoice_white_color);
                }
            }
        });


        invoice_company_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    invoice_company_number.setHintTextColor(context.getResources().getColor(android.R.color.white));
                    invoice_company_number.setTextColor(context.getResources().getColor(android.R.color.white));
                    layout_invoice_company_number.setBackgroundResource(R.drawable.invoice_selected);
                    if(invoice_company_number.getText().toString().equals(context.getResources().getString(R.string.invoice_input_company_number))){
                        invoice_company_number.setText("");
                    }else{
                        invoice_company_number.setSelection(invoice_company_number.getText().toString().length());
                    }
                }else{
                    invoice_company_number.setHintTextColor(context.getResources().getColor(android.R.color.black));
                    invoice_company_number.setTextColor(context.getResources().getColor(android.R.color.black));
                    layout_invoice_company_number.setBackgroundResource(R.drawable.invoice_white_color);
                }
            }
        });


        invoice_submit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    invoice_submit.setTextColor(context.getResources().getColor(android.R.color.white));
                }else{
                    invoice_submit.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            }
        });

        invoice_company.setOnKeyListener(mCompanyEditTextOnKeyListener);
        invoice_company_number.setOnKeyListener(mCompanyNumberEditTextOnKeyListener);

    }

    /**
     * 公司 EditText 事件
     */
    View.OnKeyListener mCompanyEditTextOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction() == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                    EditText text = (EditText)view;
                    if(text.getText().toString().equals(context.getResources().getString(R.string.invoice_input_company_name))){
                        invoice_company.setText("");
                    }else{
                        invoice_company.setSelection(invoice_company.getText().toString().length());
                    }
                }
            }
            return false;
        }
    };



    /**
     * 公司纳税人编号 EditText 事件
     */
    View.OnKeyListener mCompanyNumberEditTextOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction() == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                    EditText text = (EditText)view;
                    if(text.getText().toString().equals(context.getResources().getString(R.string.invoice_input_company_number))){
                        invoice_company_number.setText("");
                    }else{
                        invoice_company_number.setSelection(invoice_company_number.getText().toString().length());
                    }
                }
            }
            return false;
        }
    };


    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        show();
    }




    private void initView(){

        if(invoiceKey.getCompanyInfo()!=null && !invoiceKey.getCompanyInfo().equals("")){
            invoice_company.setText(invoiceKey.getCompanyInfo());
        }
        if(invoiceKey.getCompanynumber()!=null && !invoiceKey.getCompanynumber().equals("")){
            invoice_company_number.setText(invoiceKey.getCompanynumber());
        }

        layout_invoice_information.setVisibility(View.INVISIBLE);
        layout_invoice_company.setVisibility(View.INVISIBLE);
        layout_invoice_company_number.setVisibility(View.INVISIBLE);


        layout_invoice_type.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && layout_invoice_type.isFocused()) {
                    if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                        typeID = (typeID + 1) % invoice_type.length;
                        Utils.print(tag,"left name="+invoice_type[typeID]);
                    }else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                        typeID = (typeID + 1) % invoice_type.length;
                        Utils.print(tag,"right name="+invoice_type[typeID]);
                    }
                    invoice.setText(invoice_type[typeID]);
                    handLayoutShow();
                }

                return false;
            }
        });

        invoice.setText(invoice_type[typeID]);
        handLayoutShow();

    }


    /**
     * 根据不同的发票类型，显示不同的UI
     */
    private void handLayoutShow(){
        if(typeID==0){
            layout_invoice_company.setVisibility(View.INVISIBLE);
            layout_invoice_information.setVisibility(View.INVISIBLE);
            layout_invoice_company_number.setVisibility(View.INVISIBLE);
            invoice_tips.setVisibility(View.INVISIBLE);
        }else if(typeID==1){
            layout_invoice_company.setVisibility(View.INVISIBLE);
            layout_invoice_information.setVisibility(View.INVISIBLE);
            layout_invoice_company_number.setVisibility(View.INVISIBLE);
            invoice_tips.setVisibility(View.VISIBLE);
        }else{
            layout_invoice_company.setVisibility(View.VISIBLE);
            layout_invoice_information.setVisibility(View.VISIBLE);
            layout_invoice_company_number.setVisibility(View.VISIBLE);
            invoice_tips.setVisibility(View.VISIBLE);
        }
    }



    /**
     * 获取发票二维码地址
     */
    public void getInvoiceQr() {
        Utils.print(tag, "getInvoiceQr");
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
                .httpGetInvoiceQr(input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag, "status==" + qrData.getErrorMessage() + ",value=" + qrData.getReturnValue());
                        if (qrData.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "qr==" + qrData.getData().getQrData());
                        Utils.print(tag, "qr==" + qrData.getData().getTimeStamp());

                        invoice_qrcode.setBackground(new QrcodeUtil().generateQRCode(context,qrData.getData().getQrData()));
                        qr_text.setVisibility(View.VISIBLE);
                    }
                });
    }



    public static class InvoiceKey{
        int key;
        String companyInfo; //公司名称
        String companynumber;//纳税人识别号

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getCompanyInfo() {
            return companyInfo;
        }

        public void setCompanyInfo(String companyInfo) {
            this.companyInfo = companyInfo;
        }

        public String getCompanynumber() {
            return companynumber;
        }

        public void setCompanynumber(String companynumber) {
            this.companynumber = companynumber;
        }
    }




}
