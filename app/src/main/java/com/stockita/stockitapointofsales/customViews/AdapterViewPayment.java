/**
 * MIT License
 *
 * Copyright (c) 2016 Hishmad Abubakar Al-Amudi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stockita.stockitapointofsales.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * Custom view for the payment
 */
public class AdapterViewPayment extends LinearLayout {

    private LinearLayout mRoot;
    private TextView mCustomerName, mTotalInvoiceAmount, mCashPaid, mChangeCash, mCreditCardNumber, mServerDate;


    /**
     * Constrcutor
     * @param context       activity context
     */
    public AdapterViewPayment(Context context) {
        super(context);
        initView(context);
    }

    /**
     * Constructor
     * @param context       activity context
     * @param attrs         AttributeSet object
     */
    public AdapterViewPayment(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        
        // Initialize the layout file which is the root view of all widgets
        View view = inflate(context, R.layout.adapter_view_payment, this);

        mRoot = (LinearLayout) view.findViewById(R.id.root);
        mCustomerName = (TextView) view.findViewById(R.id.payment_customer_name);
        mTotalInvoiceAmount = (TextView) view.findViewById(R.id.payment_total_invoice_amount);
        mCashPaid = (TextView) view.findViewById(R.id.payment_cash_paid);
        mChangeCash = (TextView) view.findViewById(R.id.payment_change_cash);
        mCreditCardNumber = (TextView) view.findViewById(R.id.payment_credit_card_number);
        mServerDate = (TextView) view.findViewById(R.id.payment_server_date);

    }


    public LinearLayout getmRoot() {
        return mRoot;
    }

    public void setmRoot(LinearLayout mRoot) {
        this.mRoot = mRoot;
    }

    public TextView getmCustomerName() {
        return mCustomerName;
    }

    public void setmCustomerName(TextView mCustomerName) {
        this.mCustomerName = mCustomerName;
    }

    public TextView getmTotalInvoiceAmount() {
        return mTotalInvoiceAmount;
    }

    public void setmTotalInvoiceAmount(TextView mTotalInvoiceAmount) {
        this.mTotalInvoiceAmount = mTotalInvoiceAmount;
    }

    public TextView getmCashPaid() {
        return mCashPaid;
    }

    public void setmCashPaid(TextView mCashPaid) {
        this.mCashPaid = mCashPaid;
    }

    public TextView getmChangeCash() {
        return mChangeCash;
    }

    public void setmChangeCash(TextView mChangeCash) {
        this.mChangeCash = mChangeCash;
    }

    public TextView getmCreditCardNumber() {
        return mCreditCardNumber;
    }

    public void setmCreditCardNumber(TextView mCreditCardNumber) {
        this.mCreditCardNumber = mCreditCardNumber;
    }

    public TextView getmServerDate() {
        return mServerDate;
    }

    public void setmServerDate(TextView mServerDate) {
        this.mServerDate = mServerDate;
    }
}
