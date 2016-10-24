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

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.ServerValue;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingListFragmentUI;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is the checkout dialog for open bill
 */
public class OpenSalesCheckoutDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Constant
    private static final String TAG_LOG = OpenSalesCheckoutDialogFragment.class.getSimpleName();
    private static final int LOADER_IDE_ONE = 1;
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";

    /**
     * The user's login UID
     */
    private String mUserUid;

    /**
     * The total amount before invoice discount and invoice tax,
     * this is passed from the {@link SalesPendingListFragmentUI}
     */
    private String mTotalAmount;

    /**
     * We need this to delete the data from /openSalesDetail/.. and
     * /openSalesHeader/..
     */
    private String mSalesHeaderKey;

    /**
     * An arrayList of type {@link SalesDetailModel}, the cursor will pack
     * the data into this list, so later we can pass them to the server
     */
    private ArrayList<SalesDetailModel> mSalesDetailPendingList;

    /**
     * An arrayList of type String, to pack the push() key of each salesDetail
     * from the /salesDetailPending node
     */
    private ArrayList<String> mSalesDetailPendingKeyList;



    @Bind(R.id.checkbox_customer_name_form)
    EditText mCustomerNameForm;

    @Bind(R.id.checkbox_number_of_items_form)
    TextView mNumberOfItemsForm;

    @Bind(R.id.checkbox_total_amount_form)
    TextView mTotalAmountForm;

    @Bind(R.id.checkout_discount_value_form)
    TextView mDiscountAmountForm;

    @Bind(R.id.checkbox_service_charge_form)
    TextView mServiceChargeForm;

    @Bind(R.id.checkbox_tax_value_form)
    TextView mTaxAmountForm;

    @Bind(R.id.checkout_grand_total)
    TextView mGrandTotalForm;

    @Bind(R.id.checkout_cash_received)
    EditText mCashReceivedForm;

    @Bind(R.id.checkout_change)
    TextView mChangeForm;

    @Bind(R.id.open_bill_button)
    TextView mOpenBillButton;



    /**
     * Empty constructor
     */
    public OpenSalesCheckoutDialogFragment() {}


    /**
     * Statics method as constructor to get the data pass into here
     *
     * @param userUid       The user's UID
     * @return              This fragment
     */
    public static OpenSalesCheckoutDialogFragment newInstance(String userUid, String total, String salesHeaderKey) {

        OpenSalesCheckoutDialogFragment fragment = new OpenSalesCheckoutDialogFragment();
        Bundle args = new Bundle();

        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, total);
        args.putString(KEY_THREE, salesHeaderKey);

        fragment.setArguments(args);

        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get it from newInstance
        mUserUid = getArguments().getString(KEY_ONE);

        // Get it from SharedPreference
        mTotalAmount = Utility.getAnyString(getActivity(), "checkout_total_open", "0");

        // Get it from newInstance
        mSalesHeaderKey = getArguments().getString(KEY_THREE);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Start the loader, and initialize them.
        getLoaderManager().initLoader(LOADER_IDE_ONE, null, this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view and the layout
        View view = inflater.inflate(R.layout.dialog_fragment_sales_checkout, container);

        // Bind the view with ButterKnife
        ButterKnife.bind(this, view);

        // Hide the open bill because we don't need it here.
        mOpenBillButton.setVisibility(View.GONE);

        mTotalAmountForm.setText(mTotalAmount);

        // Calculate all the numbers then update the UI
        calculate();

        mCashReceivedForm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                // Get the users typed
                String getTheUserTyped = editable.toString();

                /**
                 * Calculate the change below..
                 */

                double getTheUserTypedInDouble;
                try {
                    getTheUserTypedInDouble = Double.parseDouble(getTheUserTyped);
                } catch (Exception e) {
                    getTheUserTypedInDouble = 0;
                }
                double getTheGrandTotal;
                try {
                    getTheGrandTotal = Double.parseDouble(mGrandTotalForm.getText().toString());
                } catch (Exception e) {
                    getTheGrandTotal = 0;
                }
                double calculateTheChange = getTheUserTypedInDouble - getTheGrandTotal;

                // Update the UI
                mChangeForm.setText(String.valueOf(calculateTheChange));

            }
        });

        // Return the view
        return view;
    }


    /**
     * Calculate all the number then update the UI
     */
    private void calculate() {

        // SharedPreferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        /**
         * Calculate the discount
         */

        // Get the total amount from the UI parse them to double
        double totalAmountFromTheUi;
        try {
            totalAmountFromTheUi = Double.parseDouble(mTotalAmountForm.getText().toString());
        } catch (Exception e) {
            totalAmountFromTheUi = 0;
        }

        // Get the discount rate from the sharedPreferences
        double discountRateFromSharedPreferences;
        try {
            discountRateFromSharedPreferences = Double.parseDouble(pref.getString("pref_discount", "0"));
        } catch (Exception e) {
            discountRateFromSharedPreferences = 0;
        }

        // Calculate the discount amount
        double discountAmount;
        try {
            discountAmount = totalAmountFromTheUi * discountRateFromSharedPreferences / 100;
        } catch (Exception e) {
            discountAmount = 0;
        }

        // Update the UI for discount amount
        mDiscountAmountForm.setText(String.valueOf(discountAmount));

        /**
         * Calculate the service charges
         */

        // Get the total amount after discount
        double totalAmountAfterDiscount;
        try {
            totalAmountAfterDiscount = totalAmountFromTheUi - discountAmount;
        } catch (Exception e) {
            totalAmountAfterDiscount = 0;
        }

        // Get the service charge rate from the SharedPreferences


        double serviceChargeRateFromSharedPreferences;
        try {
            serviceChargeRateFromSharedPreferences = Double.parseDouble(pref.getString("pref_service", "0"));;
        } catch (Exception e) {
            serviceChargeRateFromSharedPreferences = 0;
        }

        // Calculate the service charge amount
        double serviceChargeAmount;
        try {
            serviceChargeAmount = totalAmountAfterDiscount * serviceChargeRateFromSharedPreferences / 100;
        } catch (Exception e) {
            serviceChargeAmount = 0;
        }

        // Update the UI for service charge amount
        mServiceChargeForm.setText(String.valueOf(serviceChargeAmount));

        /**
         * Calculate the Tax
         */

        // Get the total amount after service charge
        double totalAmountAfterServiceCharges;
        try {
            totalAmountAfterServiceCharges = totalAmountAfterDiscount + serviceChargeAmount;
        } catch (Exception e) {
            totalAmountAfterServiceCharges = 0;
        }

        // Get the tax rate from the SharedPreferences
        double taxRateFromSharedPreferences;
        try {
            taxRateFromSharedPreferences = Double.parseDouble(pref.getString("pref_tax", "0"));
        } catch (Exception e) {
            taxRateFromSharedPreferences = 0;
        }

        // Calculate the tax amount
        double taxAmount;
        try {
            taxAmount = totalAmountAfterServiceCharges * taxRateFromSharedPreferences / 100;
        } catch (Exception e) {
            taxAmount = 0;
        }

        // Update the UI for the tax amount
        mTaxAmountForm.setText(String.valueOf(taxAmount));

        /**
         * Calculate the grand total
         */

        // Get the total amount after discount after server charge after tax
        double grandTotal;
        try {
            grandTotal = totalAmountAfterServiceCharges + taxAmount;
        } catch (Exception e) {
            grandTotal = 0;
        }

        // Update the Grand Total UI
        mGrandTotalForm.setText(String.valueOf(grandTotal));

    }


    /**
     * Helper method when the user click pay button
     */
    private void onPayButton() {

        if (mSalesDetailPendingList != null && mSalesDetailPendingList.size() > 0) {

            // Get data from the UI
            String customerName = mCustomerNameForm.getText().toString();
            String totalAmount = mTotalAmountForm.getText().toString();
            String discountAmount = mDiscountAmountForm.getText().toString();
            String serviceCharge = mServiceChargeForm.getText().toString();
            String taxAmount = mTaxAmountForm.getText().toString();
            String grandTotal = mGrandTotalForm.getText().toString();
            String cashPaid = mCashReceivedForm.getText().toString();
            String changeCash = mChangeForm.getText().toString();


            // Instantiate the Sales Header Model
            SalesHeaderModel salesHeaderModel = new SalesHeaderModel();

            // Get data from the UI then pass them into a model
            salesHeaderModel.setCustomerName(customerName);
            salesHeaderModel.setTotalAmount(totalAmount);
            salesHeaderModel.setDiscountAmount(discountAmount);
            salesHeaderModel.setServiceCharge(serviceCharge);
            salesHeaderModel.setTaxAmount(taxAmount);
            salesHeaderModel.setGrandTotal(grandTotal);

            // Create a HashMap for the server time stamp
            HashMap<String, Object> timeStamp = new HashMap<>();
            timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
            // Pass the map into the pojo
            salesHeaderModel.setServerDate(timeStamp);

            // Pass the data to the activity for background process
            ((PaymentButtonFromOpenSalesCheckoutDialogFragment) getActivity()).onPayButton(mSalesHeaderKey, mUserUid, salesHeaderModel, mSalesDetailPendingList, cashPaid, changeCash);

            // Dismiss this dialog
            getDialog().dismiss();

        }

    }



    /**
     * When the user click pay button
     *
     */
    @OnClick(R.id.pay_button)
    void clickPayButton() {
        onPayButton();

    }


    /**
     * When the user click cancel
     */
    @OnClick(R.id.cancel_button)
    void clickCancelButton() {

        // Just dismiss the dialog, and have some fun.
        getDialog().dismiss();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {


        // Return new cursor with the following query parameters.
        CursorLoader loader = null;

        switch (id) {
            case LOADER_IDE_ONE:
                loader = new CursorLoader(getActivity(),
                        ContractData.SalesDetailPendingEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // A switch-case is useful when dealing with multiple Loader/IDs
        switch (loader.getId()) {
            case LOADER_IDE_ONE:
                // The asynchronous load is complete and the data
                // is now available for use. Only now we can associate
                // the required Cursor with the adapter.

                // Set the cursor to first position;
                data.moveToFirst();

                // Get the number of rows.
                int dataCount = data.getCount();

                // Instantiate new ArrayList<ModelMovie>
                mSalesDetailPendingList = new ArrayList<>();
                mSalesDetailPendingKeyList = new ArrayList<>();


                // Add each row into an array element.
                for (int i = 0; i < dataCount; i++) {

                    SalesDetailModel model = new SalesDetailModel();
                    model.setItemNumber(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_NUMBER));
                    model.setItemDesc(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_DESC));
                    model.setItemUnit(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_UNIT));
                    model.setItemPrice(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_PRICE));
                    model.setItemQuantity(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_QUANTITY));
                    model.setItemDiscount(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_DISCOUNT));
                    model.setItemDiscountAmout(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_DISCOUNT_AMOUNT));
                    model.setItemAmount(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_ITEM_AMOUNT));

                    // Pack the model into a list
                    mSalesDetailPendingList.add(model);

                    // Pack the key into a list
                    mSalesDetailPendingKeyList.add(data.getString(ContractData.SalesDetailPendingEntry.INDEX_COL_PUSH_KEY));

                    // Make the cursor move to next if any
                    data.moveToNext();
                }


                /**
                 * Update the UI below
                 */

                // calculate the number of items then update the UI
                int numberOfItems = mSalesDetailPendingList.size();
                mNumberOfItemsForm.setText(String.valueOf(numberOfItems));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        loader = null;

    }


    /**
     * Interface to fire a callbacks when the user clicked a payment button
     */
    public interface PaymentButtonFromOpenSalesCheckoutDialogFragment {

        /**
         * When the user clicked a payment button this method will be invoked
         * @param oldHeaderKey              The old Sales Header Key
         * @param userUid                   The user's UID
         * @param salesHeaderModel          The {@link SalesDetailModel} object
         * @param salesDetailModelList      The {@link SalesDetailModel} ArrayList
         * @param cashPaid                  The amount paid in cash
         * @param changeCash                The amount returned to the customer
         */
        void onPayButton(String oldHeaderKey,
                         String userUid,
                         SalesHeaderModel salesHeaderModel,
                         ArrayList<SalesDetailModel> salesDetailModelList,
                         String cashPaid,
                         String changeCash);

    }

}
