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
 * This class is the model for Sales header which contains the
 * Total invoice, Tax, discount, total quantity, grand total, and customer name.
 */
public class SalesHeaderModel implements Parcelable {

    private String customerName;
    private String invoiceNumber;
    private String invoiceDate;
    private String totalAmount;
    private String discountAmount;
    private String serviceCharge;
    private String taxAmount;
    private String grandTotal;
    private HashMap<String, Object> serverDate;

    /**
     * Empty constructor
     */
    public SalesHeaderModel() {}


    /**
     * Constructor
     * @param customerName      customer name if any
     * @param invoiceNumber     invoice number if any
     * @param invoiceDate       invoice data
     * @param totalAmount       total amount before discount and tax
     * @param discountAmount    discount amount is % . total amount
     * @param serviceCharge     service amount is % . total amount after discount
     * @param taxAmount         tax amount is % . total amount after discount & service
     * @param grandTotal        grand total is total amount before discount add discount add tax
     * @param serverDate        ServerValue
     */
    public SalesHeaderModel(String customerName,
                            String invoiceNumber,
                            String invoiceDate,
                            String totalAmount,
                            String discountAmount,
                            String serviceCharge,
                            String taxAmount,
                            String grandTotal, HashMap<String, Object> serverDate) {

        this.customerName = customerName;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.serviceCharge = serviceCharge;
        this.taxAmount = taxAmount;
        this.grandTotal = grandTotal;
        this.serverDate = serverDate;

    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public HashMap<String, Object> getServerDate() {
        return serverDate;
    }

    public void setServerDate(HashMap<String, Object> serverDate) {
        this.serverDate = serverDate;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    protected SalesHeaderModel(Parcel in) {
        customerName = in.readString();
        invoiceNumber = in.readString();
        invoiceDate = in.readString();
        totalAmount = in.readString();
        discountAmount = in.readString();
        serviceCharge = in.readString();
        taxAmount = in.readString();
        grandTotal = in.readString();
        serverDate = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerName);
        dest.writeString(invoiceNumber);
        dest.writeString(invoiceDate);
        dest.writeString(totalAmount);
        dest.writeString(discountAmount);
        dest.writeString(serviceCharge);
        dest.writeString(taxAmount);
        dest.writeString(grandTotal);
        dest.writeValue(serverDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SalesHeaderModel> CREATOR = new Parcelable.Creator<SalesHeaderModel>() {
        @Override
        public SalesHeaderModel createFromParcel(Parcel in) {
            return new SalesHeaderModel(in);
        }

        @Override
        public SalesHeaderModel[] newArray(int size) {
            return new SalesHeaderModel[size];
        }
    };
}