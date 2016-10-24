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

package com.stockita.stockitapointofsales.salespack.pendingpack;

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
 * This service, for payment and open bill, insert into server database
 */
public class SalesPendingPayButtonIntentService extends IntentService {

    // Constant
    private static final String TAG_LOG = SalesPendingPayButtonIntentService.class.getSimpleName();
    public static final String ACTION_INSERT_PAY = TAG_LOG + ".INSERT_PAY";
    public static final String ACTION_INSERT_OPEN_BILL = TAG_LOG + ".INSERT_OPEN_BILL";
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";
    private static final String KEY_FOUR = TAG_LOG + ".KEY_FOUR";
    private static final String KEY_FIVE = TAG_LOG + ".KEY_FIVE";
    private static final String KEY_SIX = TAG_LOG + ".KEY_SIX";
    private static final String KEY_SEVEN = TAG_LOG + ".KEY_SEVEN";


    /**
     * Constructor
     */
    public SalesPendingPayButtonIntentService() {
        super(TAG_LOG);
    }


    /**
     * Static method to fire the intent service and pass data into here
     *
     * @param context              Activity context
     * @param userUid              The user's uid
     * @param salesHeaderModel     {@link SalesHeaderModel}
     * @param salesDetailModelList ArrayList of {@link SalesDetailModel}
     */
    public static void insertPay(Context context,
                                 String userUid,
                                 SalesHeaderModel salesHeaderModel,
                                 ArrayList<SalesDetailModel> salesDetailModelList,
                                 String creditCardNumber,
                                 String creditCardExp,
                                 String cashPaid,
                                 String changeCash) {

        Intent intent = new Intent(context, SalesPendingPayButtonIntentService.class);
        intent.setAction(ACTION_INSERT_PAY);
        intent.putExtra(KEY_ONE, userUid);
        intent.putExtra(KEY_TWO, salesHeaderModel);
        intent.putExtra(KEY_THREE, salesDetailModelList);
        intent.putExtra(KEY_FOUR, creditCardNumber);
        intent.putExtra(KEY_FIVE, creditCardExp);
        intent.putExtra(KEY_SIX, cashPaid);
        intent.putExtra(KEY_SEVEN, changeCash);
        context.startService(intent);

    }


    /**
     * Static method to fire the intent service and pass data into here
     * @param context                   Activity context
     * @param userUid                   The user's uid
     * @param salesHeaderModel          {@link SalesHeaderModel} instance
     * @param salesDetailModelList      {@link SalesDetailModel} arrayList instance
     */
    public static void insertOpenBill(Context context,
                                      String userUid,
                                      SalesHeaderModel salesHeaderModel,
                                      ArrayList<SalesDetailModel> salesDetailModelList) {

        Intent intent = new Intent(context, SalesPendingPayButtonIntentService.class);
        intent.setAction(ACTION_INSERT_OPEN_BILL);
        intent.putExtra(KEY_ONE, userUid);
        intent.putExtra(KEY_TWO, salesHeaderModel);
        intent.putExtra(KEY_THREE, salesDetailModelList);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            // If the user clicked on pay button
            if (ACTION_INSERT_PAY.equals(intent.getAction())) {
                String userUid = intent.getStringExtra(KEY_ONE);
                SalesHeaderModel salesHeaderModel = intent.getParcelableExtra(KEY_TWO);
                ArrayList<SalesDetailModel> salesDetailModelList = intent.getParcelableArrayListExtra(KEY_THREE);
                String creditCardNumber = intent.getStringExtra(KEY_FOUR);
                String exp = intent.getStringExtra(KEY_FIVE);
                String cashPaid = intent.getStringExtra(KEY_SIX);
                String changeCash = intent.getStringExtra(KEY_SEVEN);
                performPayment(userUid, salesHeaderModel, salesDetailModelList, creditCardNumber, exp, cashPaid, changeCash);
            }

            // If the user clicked in openBill button
            if (ACTION_INSERT_OPEN_BILL.equals(intent.getAction())) {
                String userUid = intent.getStringExtra(KEY_ONE);
                SalesHeaderModel salesHeaderModel = intent.getParcelableExtra(KEY_TWO);
                ArrayList<SalesDetailModel> salesDetailModelList = intent.getParcelableArrayListExtra(KEY_THREE);
                performOpenBill(userUid, salesHeaderModel, salesDetailModelList);
            }
        }
    }


    /**
     * Helper method to proceed the open bill and pass data into the server
     * @param userUid                       The user's uid
     * @param salesHeaderModel              {@link SalesHeaderModel} instance
     * @param salesDetailModelList          {@link SalesDetailModel} arrayList instance
     */
    private void performOpenBill(String userUid, SalesHeaderModel salesHeaderModel, ArrayList<SalesDetailModel> salesDetailModelList) {

        // Get the node to /paidSalesHeader
        DatabaseReference salesHeaderRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION);

        // Create the push() key
        DatabaseReference salesHeaderKey = salesHeaderRef.push();
        String headerKey = salesHeaderKey.getKey();

        // Pass the pojo
        salesHeaderKey.setValue(salesHeaderModel);


        /**
         * Store the {@link SalesDetailModel} into the server
         */

        // The sales detail is already packed in the list, now we need the size of the list
        int sizeOfList = salesDetailModelList.size();

        // Get the node to /paidSalesDetail
        DatabaseReference salesDetailRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid).child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                .child(headerKey);

        // Iterate
        for (int i = 0; i < sizeOfList; i++) {

            // Get the model form the list
            SalesDetailModel salesDetailModel = salesDetailModelList.get(i);

            // create a new push() key for each element in the list
            salesDetailRef.push().setValue(salesDetailModel);

        }

        // Now delete all data in the /salesDetailPending node.
        DatabaseReference salesDetailPendingRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION);

        // set the value to null means delete all
        salesDetailPendingRef.setValue(null);


        // Delete all data in the local database after
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        contentResolver.delete(ContractData.SalesDetailPendingEntry.CONTENT_URI, null, null);

    }


    /**
     * Helper method to proceed the payment and insert data into server
     *
     * @param userUid              The user's uid
     * @param salesHeaderModel     {@link SalesHeaderModel} instance
     * @param salesDetailModelList {@link SalesDetailModel} ArrayList of instance
     * @param creditCardNumber     The customer credit card number
     * @param creditCardExp        The customer credit card exp
     * @param cashPaid             If the customer paid in cash
     * @param changeCash           The the cash returned to the customer if any
     */
    private void performPayment(String userUid,
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

        // Create the push() key in the /paidSalesHeader
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

        // Now delete all data in the /salesDetailPending node.
        DatabaseReference salesDetailPendingRef = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION);

        // set the value to null means delete all
        salesDetailPendingRef.setValue(null);

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
                .child(userUid).child(Constants.FIREBASE_PAYMENT_LOCATION)
                .child(headerKey);

        // Create push() key
        DatabaseReference paymentPushKey = paymentRef.push();
        String paymentPushKeyString = paymentPushKey.getKey();

        // Store the pojo to the /payment/...
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
