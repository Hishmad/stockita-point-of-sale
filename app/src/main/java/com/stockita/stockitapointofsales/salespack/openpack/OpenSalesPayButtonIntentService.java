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

package com.stockita.stockitapointofsales.salespack.openpack;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.PaymentModel;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.ManageDateTime;

import java.util.ArrayList;

/**
 * This service is when the user click the pay button
 */
public class OpenSalesPayButtonIntentService extends IntentService {

    private static final String TAG_LOG = OpenSalesPayButtonIntentService.class.getSimpleName();
    public static final String ACTION_INSERT = TAG_LOG + ".INSERT";
    private static final String KEY_ONE = TAG_LOG + "KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + "KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + "KEY_THREE";
    private static final String KEY_FOUR = TAG_LOG + "KEY_FOUR";
    private static final String KEY_FIVE = TAG_LOG + "KEY_FIVE";
    private static final String KEY_SIX = TAG_LOG + "KEY_SIX";
    private static final String KEY_SEVEN = TAG_LOG + "KEY_SEVEN";
    private static final String KEY_EIGHT = TAG_LOG + "KEY_EIGHT";


    /**
     * Constructor
     */
    public OpenSalesPayButtonIntentService() {
        super(TAG_LOG);
    }

    /**
     * Factory method to fire this service and pass data into here from the calling class
     * @param context               The Activity context
     * @param oldHeaderKey          The older Sales Header Key
     * @param userUid               The user's UID
     * @param salesHeaderModel      The {@link SalesHeaderModel} instance
     * @param salesDetailModelList  The ArrayList of type {@link SalesDetailModel}
     * @param creditCardNumber      The credit card number
     * @param creditCardExp         The credit card exp date
     * @param cashPaid              If the customer pay in cash
     * @param changeCash            If there is return money
     */
    public static void insertNewData(Context context,
                                     String oldHeaderKey,
                                     String userUid,
                                     SalesHeaderModel salesHeaderModel,
                                     ArrayList<SalesDetailModel> salesDetailModelList,
                                     String creditCardNumber,
                                     String creditCardExp,
                                     String cashPaid,
                                     String changeCash) {

        Intent intent = new Intent(context, OpenSalesPayButtonIntentService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(KEY_ONE, oldHeaderKey);
        intent.putExtra(KEY_TWO, userUid);
        intent.putExtra(KEY_THREE, salesHeaderModel);
        intent.putExtra(KEY_FOUR, salesDetailModelList);
        intent.putExtra(KEY_FIVE, creditCardNumber);
        intent.putExtra(KEY_SIX, creditCardExp);
        intent.putExtra(KEY_SEVEN, cashPaid);
        intent.putExtra(KEY_EIGHT, changeCash);
        context.startService(intent);

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String oldHeaderKey = intent.getStringExtra(KEY_ONE);
        String userUid = intent.getStringExtra(KEY_TWO);
        SalesHeaderModel salesHeaderModel = intent.getParcelableExtra(KEY_THREE);
        ArrayList<SalesDetailModel> salesDetailModelList = intent.getParcelableArrayListExtra(KEY_FOUR);
        String creditCardNumber = intent.getStringExtra(KEY_FIVE);
        String exp = intent.getStringExtra(KEY_SIX);
        String cashPaid = intent.getStringExtra(KEY_SEVEN);
        String changeCash = intent.getStringExtra(KEY_EIGHT);

        // Helper method to perform the payment process
        performPayment(oldHeaderKey, userUid, salesHeaderModel, salesDetailModelList, creditCardNumber, exp, cashPaid, changeCash);


    }


    /**
     * Helper method to perform the payment process
     * @param oldHeaderKey                  The sales header old key
     * @param userUid                       The user's UID
     * @param salesHeaderModel              {@link SalesHeaderModel} instance
     * @param salesDetailModelList          ArrayList of type {@link SalesDetailModel}
     * @param creditCardNumber              The customer credit card number if any
     * @param creditCardExp                 The credit card exp
     * @param cashPaid                      If the customer pay in cash
     * @param changeCash                    return money to the customer if any
     */
    private void performPayment(String oldHeaderKey,
                                String userUid,
                                SalesHeaderModel salesHeaderModel,
                                ArrayList<SalesDetailModel> salesDetailModelList,
                                String creditCardNumber,
                                String creditCardExp,
                                String cashPaid,
                                String changeCash) {


        // Get the node to /paidSalesHeader
        DatabaseReference salesHeaderRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_PAID_SALES_HEADER_LOCATION);

        // Create the new push() key for the paidSalesHeader
        DatabaseReference salesHeaderKey = salesHeaderRef.push();
        String headerKey = salesHeaderKey.getKey();

        // Pass the salesHeaderModel pojo into /paidSalesHeader/
        salesHeaderKey.setValue(salesHeaderModel);

        // Get the node to /archiveSalesHeader
        DatabaseReference salesHeaderArchive = FirebaseDatabase.getInstance().getReference()
                .child(userUid).child(Constants.FIREBASE_ARCHIVE_SALES_HEADER_LOCATION);

        // convert the timeStamp to month and year in string
        ManageDateTime manageDateTime = new ManageDateTime(System.currentTimeMillis());
        String yearMonth = manageDateTime.getGivenTimeMillisInYearMonth();    //year + month;

        // Add the yearMonth as a child to /archiveSalesHeader/yearMonth...
        // Pass the salesHeaderModel pojo into /archiveSalesHeader/
        salesHeaderArchive.child(yearMonth).child(headerKey).setValue(salesHeaderModel);


        /**
         * Store the {@link SalesDetailModel} into the server
         */

        // The sales detail is already packed in the list, now we need the size of the list
        int sizeOfList = salesDetailModelList.size();

        // Get the node to /paidSalesDetail
        DatabaseReference salesDetailRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_PAID_SALES_DETAIL_LOCATION)
                .child(headerKey);

        // Get the node to /archiveSalesDetail
        DatabaseReference salesDetailArchive = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_ARCHIVE_SALES_DETAIL_LOCATION)
                .child(yearMonth)
                .child(headerKey);

        // Iterate
        for (int i = 0; i < sizeOfList; i++) {

            // Get the model form the list
            SalesDetailModel salesDetailModel = salesDetailModelList.get(i);

            // Create the sales detail push() key
            DatabaseReference detailpushKey = salesDetailRef.push();
            String detailPushKeyString = detailpushKey.getKey();

            // Pass the salesDetailModel to /paidSalesDetail/headerKey/...
            detailpushKey.setValue(salesDetailModel);

            // Path the salesDetailModel to /archiveSalesDetail/headerKey... use the same detailPushKey
            salesDetailArchive.child(detailPushKeyString).setValue(salesDetailModel);

        }

        // Now delete all data in the /openSalesDetail node. use the mSalesHeaderKey because it is old
        // don't use the headerKey because it is new only for the paidSalesHeader and paidSalesDetail and payment node
        DatabaseReference salesDetailOpenRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                .child(oldHeaderKey);

        // set the value to null means delete all
        salesDetailOpenRef.setValue(null);

        // Now delete all the data in the /openSalesHeader node.
        DatabaseReference salesHeaderOpenRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION)
                .child(oldHeaderKey);

        // set the value to null means delete all
        salesHeaderOpenRef.setValue(null);

        // Delete all data in the local database after
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        contentResolver.delete(ContractData.SalesDetailPendingEntry.CONTENT_URI, null, null);

        /**
         * Register the yearMonth into a new node /yearMonth/ with push() key
         * for later use
         */

        // Add the yearMonth into the node /yearMonth for later use
        DatabaseReference yearMonthRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_YEAR_MONTH_LOCATION)
                .child(yearMonth);

        // Pass the push() key as value
        yearMonthRef.setValue(yearMonthRef.push().getKey());

        /**
         * Pass the {@link com.stockita.stockitapointofsales.data.PaymentModel} to the server
         */

        // Instantiate the PaymentModel
        PaymentModel paymentModel = new PaymentModel();

        // Get the payment from the UI
        paymentModel.setServerDate(salesHeaderModel.getServerDate());
        paymentModel.setCustomerName(salesHeaderModel.getCustomerName());
        paymentModel.setTotalInvoiceAmount(salesHeaderModel.getGrandTotal());
        if (creditCardNumber.length() > 0) {
            paymentModel.setCashPaid(salesHeaderModel.getGrandTotal());
            paymentModel.setChangeCash("0");
        } else {
            paymentModel.setCashPaid(cashPaid);
            paymentModel.setChangeCash(changeCash);
        }
        paymentModel.setCreditCardNumber(creditCardNumber);
        paymentModel.setCreditCardExp(creditCardExp);

        // Get the node to /payment
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_PAYMENT_LOCATION)
                .child(headerKey);

        // Create push() key
        DatabaseReference paymentPushKey = paymentRef.push();
        String paymentPushKeyString = paymentPushKey.getKey();


        // Store the pojo to the server
        paymentPushKey.setValue(paymentModel);


        // Get the node to /archivePayment/salesHeaderKey/yearMonth/...
        DatabaseReference paymentArchive = FirebaseDatabase.getInstance().getReference()
                .child(userUid).child(Constants.FIREBASE_ARCHIVE_PAYMENT_LOCATION)
                .child(yearMonth)
                .child(headerKey)
                .child(paymentPushKeyString);

        // Store the pojo to the /archivePayment/...
        paymentArchive.setValue(paymentModel);

    }
}
