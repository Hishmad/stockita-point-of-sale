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

import java.util.ArrayList;

/**
 * This call will calculate a show the monthly sales
 */
public class MonthlySalesModel {


    private String yearMonth;
    private ArrayList<String> listOfInvoiceGrandTotals;


    /**
     * Constructor
     * @param yearMonth                         Year/Month
     * @param listOfInvoiceGrandTotals          list of sum of invoice amount during a month
     */
    public MonthlySalesModel(String yearMonth, ArrayList<String> listOfInvoiceGrandTotals){

        this.yearMonth = yearMonth;
        this.listOfInvoiceGrandTotals = listOfInvoiceGrandTotals;

    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public ArrayList<String> getListOfInvoiceGrandTotals() {
        return listOfInvoiceGrandTotals;
    }

    public void setListOfInvoiceGrandTotals(ArrayList<String> listOfInvoiceGrandTotals) {
        this.listOfInvoiceGrandTotals = listOfInvoiceGrandTotals;
    }

    public double calcSumResult() {

        double invoiceGrandTotal = 0;
        double sum = 0;
        for (String amount:listOfInvoiceGrandTotals) {

            try {
                invoiceGrandTotal = Double.parseDouble(amount);
            } catch (Exception e) {
                invoiceGrandTotal = 0;
            }

            sum += invoiceGrandTotal;
        }

        
        return sum;
    }

}
