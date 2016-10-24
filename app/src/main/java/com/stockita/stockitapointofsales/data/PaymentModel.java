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

package com.stockita.stockitapointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * POJO for Payment
 */
public class PaymentModel implements Parcelable {

    private String customerName;
    private String totalInvoiceAmount;
    private String cashPaid;
    private String changeCash;
    private String creditCardNumber;
    private String creditCardExp;
    private HashMap<String, Object> serverDate;

    /**
     * Empty constructor
     */
    public PaymentModel() {}

    /**
     * Constructor
     * @param customerName          customer name
     * @param totalInvoiceAmount    total invoice amount
     * @param cashPaid              cash paid by the customer
     * @param change                change return to the customer
     * @param creditCardNumber      credit number
     * @param creditCardExp         expired date
     * @param serverDate            {@link com.google.firebase.database.ServerValue}
     */
    public PaymentModel(String customerName,
                        String totalInvoiceAmount,
                        String cashPaid,
                        String change, String creditCardNumber, String creditCardExp, HashMap<String, Object> serverDate) {

        this.customerName = customerName;
        this.totalInvoiceAmount = totalInvoiceAmount;
        this.cashPaid = cashPaid;
        this.changeCash = change;
        this.creditCardNumber = creditCardNumber;
        this.creditCardExp = creditCardExp;
        this.serverDate = serverDate;


    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(String totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }

    public String getCashPaid() {
        return cashPaid;
    }

    public void setCashPaid(String cashPaid) {
        this.cashPaid = cashPaid;
    }

    public String getChangeCash() {
        return changeCash;
    }

    public void setChangeCash(String change) {
        this.changeCash = change;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardExp() {
        return creditCardExp;
    }

    public void setCreditCardExp(String creditCardExp) {
        this.creditCardExp = creditCardExp;
    }

    public HashMap<String, Object> getServerDate() {
        return serverDate;
    }

    public void setServerDate(HashMap<String, Object> serverDate) {
        this.serverDate = serverDate;
    }

    protected PaymentModel(Parcel in) {
        customerName = in.readString();
        totalInvoiceAmount = in.readString();
        cashPaid = in.readString();
        changeCash = in.readString();
        creditCardNumber = in.readString();
        creditCardExp = in.readString();
        serverDate = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerName);
        dest.writeString(totalInvoiceAmount);
        dest.writeString(cashPaid);
        dest.writeString(changeCash);
        dest.writeString(creditCardNumber);
        dest.writeString(creditCardExp);
        dest.writeValue(serverDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PaymentModel> CREATOR = new Parcelable.Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };
}