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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.utilities.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This dialog will edit the sales Detail in the /openSalesDetail node
 */
public class OpenSalesDetailEditFormDialogFragment extends DialogFragment {

    // Constant
    private static final String TAG_LOG = OpenSalesDetailEditFormDialogFragment.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";
    private static final String KEY_FOUR = TAG_LOG + ".KEY_FOUR";


    /**
     * The user's login UID
     */
    private String mUserUid;


    /**
     * The push() key of the {@link SalesDetailModel} stored in the server
     * ../openSalesDetail/headerPushKey/detailPushKey()
     */
    private String mPushKeyDetail;


    /**
     * The push() key of the {@link com.stockita.stockitapointofsales.data.SalesHeaderModel} stored
     * in the server /openSalesHeader/push()key
     */
    private String mPushKeyHeader;


    /**
     * The instance of {@link SalesDetailModel}
     */
    private SalesDetailModel mModel;


    // Views
    @Bind(R.id.item_number_form)
    TextView mItemNumberForm;
    @Bind(R.id.barcode_scanner)
    ImageButton mBarcodeScanner;
    @Bind(R.id.item_desc_form)
    TextView mItemDescForm;
    @Bind(R.id.item_unit_form)
    TextView mItemUnitForm;
    @Bind(R.id.item_price_form)
    TextView mItemPriceForm;
    @Bind(R.id.item_qty_form)
    EditText mItemQtyForm;
    @Bind(R.id.item_discount_form)
    EditText mItemDiscountForm;


    /**
     * Empty constructor
     */
    public OpenSalesDetailEditFormDialogFragment() {}

    /**
     * Statics method as constructor to get the data pass into here
     * @param userUid               The user's UID
     * @param pushKeyDetail         The sales detail for openSalesDetail node
     * @param pushKeyHeader         The sales header for openSalesHeader node
     * @param model                 The {@link SalesDetailModel}
     * @return                      This fragment object
     */
    public static OpenSalesDetailEditFormDialogFragment newInstance(String userUid, String pushKeyDetail, String pushKeyHeader, SalesDetailModel model) {

        // Instantiate the model
        OpenSalesDetailEditFormDialogFragment fragment = new OpenSalesDetailEditFormDialogFragment();

        // put the variables in a bundle
        Bundle args = new Bundle();
        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, pushKeyDetail);
        args.putString(KEY_THREE, pushKeyHeader);
        args.putParcelable(KEY_FOUR, model);
        fragment.setArguments(args);

        // return this fragment
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the variable from the bundle, pass them to the field
        mUserUid = getArguments().getString(KEY_ONE);
        mPushKeyDetail = getArguments().getString(KEY_TWO);
        mPushKeyHeader = getArguments().getString(KEY_THREE);
        mModel = getArguments().getParcelable(KEY_FOUR);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view and the layout
        View view = inflater.inflate(R.layout.dialog_fragment_sales_detail_form, container);

        // initialize butterKnife
        ButterKnife.bind(this, view);

        // Hide the barcode scanner, since we don't use them here
        mBarcodeScanner.setVisibility(View.INVISIBLE);

        // Update the UI
        mItemNumberForm.setText(mModel.getItemNumber());
        mItemDescForm.setText(mModel.getItemDesc());
        mItemUnitForm.setText(mModel.getItemUnit());
        mItemPriceForm.setText(mModel.getItemPrice());
        mItemQtyForm.setText(mModel.getItemQuantity());
        mItemDiscountForm.setText(mModel.getItemDiscount());

        // return the view
        return view;

    }


    /**
     * When the user click the save button
     */
    @OnClick(R.id.save_button)
    void setSaveButton() {

        // Send the data to the server
        DatabaseReference locationOpenSalesDetail = FirebaseDatabase.getInstance().getReference();

        // Get the user edit data from the UI
        String itemNumber = mItemNumberForm.getText().toString();
        String itemDesc = mItemDescForm.getText().toString();
        String itemUnit = mItemUnitForm.getText().toString();
        String itemPrics = mItemPriceForm.getText().toString();
        String itemQty = mItemQtyForm.getText().toString();
        String itemDiscount = mItemDiscountForm.getText().toString();

        /**
         * Lets calculate the discount, and the itemAmount
         */

        // Get the qty from the UI
        double qty;
        try {
            qty = Double.parseDouble(itemQty);
        } catch (Exception e) {
            qty = 1;
        }

        // Get the price from the UI
        double price;
        try {
            price = Double.parseDouble(itemPrics);
        } catch (Exception e) {
            price = 0;
        }

        // Calculate the price . qty
        double subTotalOne = qty * price;

        // get the Discount %
        double discount;
        try {
            discount = Double.parseDouble(itemDiscount);
        } catch (Exception e) {
            discount = 0;
        }

        double discountAmount;
        try {
            discountAmount = (subTotalOne * discount) / 100;
        } catch (Exception e) {
            discountAmount = 0;
        }

        // itemAmount
        double amount = subTotalOne - discountAmount;

        // now pack the following to the object
        String itemDiscountAmount = String.valueOf(discountAmount);
        String itemAmount = String.valueOf(amount);

        // Pack to the object, using constructor
        SalesDetailModel salesDetailModel = new SalesDetailModel(itemNumber, itemDesc, itemUnit, itemPrics, String.valueOf(qty), String.valueOf(discount), itemDiscountAmount, itemAmount);

        // Get to the location in the server use the same push() key detail
        locationOpenSalesDetail
                .child(mUserUid)
                .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                .child(mPushKeyHeader)
                .child(mPushKeyDetail)
                .setValue(salesDetailModel);

        // Dismiss the dialog
        getDialog().dismiss();
    }

    @OnClick(R.id.cancel_button)
    void setCancelBUtton() {

        // Dismiss the dialog
        getDialog().dismiss();
    }

}
