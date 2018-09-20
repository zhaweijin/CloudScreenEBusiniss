package com.hiveview.dianshang.shoppingcart;

/**
 * Created by carter on 5/4/17.
 */

public class OpShoppingCart {
    private String key;
    private String value;
    private int invoiceType;

    public final static String MODIFY_INVOICE = "modify_invoice";
    public final static String MODIFY_ADDRESS = "modify_address";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }
}
