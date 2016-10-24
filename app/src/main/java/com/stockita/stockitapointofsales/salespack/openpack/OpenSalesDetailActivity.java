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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.BaseActivity;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.itemmaster.MiniLookupItemMaster;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


/**
 * Activity to host the sales detail
 */
public class OpenSalesDetailActivity extends BaseActivity implements OpenSalesHeaderListCallbacks,
        OpenSalesCheckoutDialogFragment.PaymentButtonFromOpenSalesCheckoutDialogFragment,
        OpenSalesDetailFragmentUI.DeleteMultiOpenSalesDetail{

    private static final String TAG_LOG = OpenSalesDetailActivity.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG +  ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";
    private static final String KEY_FOUR = TAG_LOG + ".KEY_FOUR";
    private static final String KEY_FIVE = TAG_LOG + ".KEY_FIVE";
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_OPEN_EDIT = "FRAGMENT_DIALOG_SALES_DETAIL_OPEN_EDIT";
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_OPEN_ADD = "FRAGMENT_DIALOG_SALES_DETAIL_OPEN_ADD";
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_OPEN_CHECKOUT = "FRAGMENT_DIALOG_SALES_DETAIL_OPEN_CHECKOUT";
    private static final int MY_SCAN_REQUEST_CODE = 101;
    private static final String KEY_MM_ONE = TAG_LOG + ".KEY_MM_ONE";
    private static final String KEY_MM_TWO = TAG_LOG + ".KEY_MM_TWO";
    private static final String KEY_MM_THREE = TAG_LOG + ".KEY_MM_THREE";
    private static final String KEY_MM_FOUR = TAG_LOG + ".KEY_MM_FOUR";
    private static final String KEY_MM_FIVE = TAG_LOG + ".KEY_MM_FIVE";
    private static final String KEY_MM_SIX = TAG_LOG + ".KEY_MM_SIX";
    private static final String KEY_MM_SEVEN = TAG_LOG + ".KEY_MM_SEVEN";


    // Member variables
    private String mUserUID;
    private String mHeaderKey;
    private OpenSalesDetailFragmentUI fragment;

    // Field
    private String qqOldHeaderKey;
    private String qqUserUid;
    private SalesHeaderModel qqSalesHeaderModel;
    private ArrayList<SalesDetailModel> qqSalesDetailModelList;
    private String qqCashPaid;
    private String qqChangeCash;

    // Related to ActionMode
    private android.view.ActionMode mActionMode;
    private ArrayList<String> mListOfDeleteOpenSalesDetailIds;
    private HashMap<String, OpenSalesDetailFragmentUI.ViewHolder> mMapOfViewHolderOpenSalesDetail;

    //View
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Factory method to call this activity from other activity
     * {@link com.stockita.stockitapointofsales.activities.MainActivity#callSalesDetailActivity(String, String)}
     */
    public static void openSalesDetailActivity(Context context, String userUid, String headerKey) {

        // use intent to pass data to other activity
        Intent intent = new Intent(context, OpenSalesDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FOUR, userUid);
        bundle.putString(KEY_FIVE, headerKey);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_sales_detail);

        ButterKnife.bind(this);

        // Initialize the toolbar
        setSupportActionBar(toolbar);

        // Show the back button on the top left
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // When user press the back arrow on the toolbar it will hit the back button
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        // Get the data from the intent
        if (savedInstanceState == null) {

            // Get the data from the intent/bundle
            Intent i = getIntent();
            Bundle bundle = i.getExtras();
            mUserUID = bundle.getString(KEY_FOUR);
            mHeaderKey = bundle.getString(KEY_FIVE);

            // Instantiate the fragment
            fragment = OpenSalesDetailFragmentUI.newInstance(mUserUID, mHeaderKey);

            // Pass the fragment into the container
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        if (savedInstanceState != null) {
            // Restore the fragment instance state

            fragment =
                    (OpenSalesDetailFragmentUI) getFragmentManager().getFragment(savedInstanceState, KEY_ONE);
            mUserUID = savedInstanceState.getString(KEY_TWO);
            mHeaderKey = savedInstanceState.getString(KEY_THREE);


            qqOldHeaderKey = savedInstanceState.getString(KEY_MM_ONE);
            qqUserUid = savedInstanceState.getString(KEY_MM_TWO);
            qqSalesHeaderModel = savedInstanceState.getParcelable(KEY_MM_THREE);
            qqSalesDetailModelList = savedInstanceState.getParcelableArrayList(KEY_MM_FOUR);
            qqCashPaid = savedInstanceState.getString(KEY_MM_FIVE);
            qqChangeCash = savedInstanceState.getString(KEY_MM_SIX);
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        // toolbar title & subtitle
        toolbar.setTitle("Stockita Point of Sale");
        toolbar.setSubtitle("Detail Sales");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /**
         * Code below to save all the fragment's state
         */

        if (fragment != null) {
            getFragmentManager().putFragment(outState, KEY_ONE, fragment);
        }

        outState.putString(KEY_TWO, mUserUID);
        outState.putString(KEY_THREE, mHeaderKey);

        if (qqOldHeaderKey != null) {
            outState.putString(KEY_MM_ONE, qqOldHeaderKey);
        }

        if (qqUserUid != null) {
            outState.putString(KEY_MM_TWO, qqUserUid);
        }

        if (qqSalesHeaderModel != null) {
            outState.putParcelable(KEY_MM_THREE, qqSalesHeaderModel);
        }

        if (qqSalesDetailModelList != null) {
            outState.putParcelableArrayList(KEY_MM_FOUR, qqSalesDetailModelList);
        }

        if (qqCashPaid != null) {
            outState.putString(KEY_MM_FIVE, qqCashPaid);
        }

        if (qqChangeCash != null) {
            outState.putString(KEY_MM_SIX, qqChangeCash);
        }

    }


    @OnClick(R.id.fab)
    void setFabClick() {

        // Instantiate the dialog fragment object and pass the encoded email as an argument
        OpenSalesAddFormDialogFragment salesOpenAddFormDialogFragment =
                OpenSalesAddFormDialogFragment.newInstance(mUserUID, null, mHeaderKey, null);

        // Show the Dialog Fragment on the screen
        salesOpenAddFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_OPEN_ADD);

    }


    @Override
    public void callSalesDetailActivity(String encodedEmail, String headerKey) {
        // do nothing, not for use here, it is meant to be used in MainActivity
    }


    /**
     * This callback will call the {@link OpenSalesDetailEditFormDialogFragment} so the user can
     * edit the sales detail.
     */
    @Override
    public void onSalesEditDialogCallbacks(int requestCode, String userUid, String pushKeyDetail, String pushKeyHeader, Object model) {

        switch (requestCode) {
            /**
             * This is when the user click the edit item on the popup {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailFragmentUI}
             * it will trigger a dialog to edit the open detail sales
             */
            case Constants.REQUEST_CODE_SALES_DIALOG_ONE:

                // Instantiate the dialog fragment object and pass the userUid, pushKeyDetail, pushKeyHeader, and the SalesDetailModel as an argument
                OpenSalesDetailEditFormDialogFragment salesOpenEditFormDialogFragment =
                        OpenSalesDetailEditFormDialogFragment.newInstance(userUid, pushKeyDetail, pushKeyHeader, (SalesDetailModel) model);

                // Show the Dialog Fragment on the screen
                salesOpenEditFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_OPEN_EDIT);

                break;
        }
    }


    /**
     * This when the user use the loopUp item master
     * This callback coming from {@link com.stockita.stockitapointofsales.itemmaster.LookupItemMasterListDialogFragment}
     * and {@link MiniLookupItemMaster}
     * @param salesHeaderKey            sales header key
     * @param model                     {@link ItemModel} instance
     */
    @Override
    public void sendItemMasterData(String salesHeaderKey, ItemModel model) {

        /**
         * The following code will show a DialogFragment of {@link OpenSalesAddFormDialogFragment}
         */

        // Instantiate the dialog fragment object and pass the encoded email, the item push() key and itemModel as an argument
        OpenSalesAddFormDialogFragment salesOpen =
                OpenSalesAddFormDialogFragment.newInstance(mUserUID, null, salesHeaderKey, model);

        // Show the Dialog Fragment on the screen
        salesOpen.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_OPEN_ADD);

    }


    /**
     * The following code will show {@link OpenSalesCheckoutDialogFragment} this callback is coming
     * from {@link OpenSalesDetailFragmentUI}
     * @param userUid               The user's UID
     * @param total                 The sum of item totals
     */
    @Override
    public void getSalesCheckoutDialog(String userUid, String total, String salesHeaderKey) {

        // Instantiate the dialog fragment object and pass the encoded email as an argument
        OpenSalesCheckoutDialogFragment checkoutDialogFragment = OpenSalesCheckoutDialogFragment.newInstance(userUid, total, salesHeaderKey);

        // Show the Dialog Fragment on the screen
        checkoutDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_OPEN_CHECKOUT);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            /**
             * Credit card scanner, this is coming from {@link #onPayButtonCreditCard()}
             */
            case MY_SCAN_REQUEST_CODE:
                String number = "";
                String expMonth = "";
                String expYear = "";

                if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                    number = scanResult.getFormattedCardNumber();
                    expMonth = String.valueOf(scanResult.expiryMonth);
                    expYear = String.valueOf(scanResult.expiryYear);


                } else {
                    Toast.makeText(this, "scan cancel", Toast.LENGTH_SHORT).show();

                }

                // Pass the data again with credit card information to OpenSalesPayButtonIntentService
                OpenSalesPayButtonIntentService.insertNewData(this, qqOldHeaderKey, qqUserUid, qqSalesHeaderModel, qqSalesDetailModelList, number, expMonth+"/"+expYear, qqCashPaid, qqChangeCash);

                finish();
                break;
        }

    }


    /**
     * Callback from {@link OpenSalesCheckoutDialogFragment}
     * @param oldHeaderKey          The old sales header key, this important to delete the old data
     * @param userUid               The user's UID
     * @param salesHeaderModel      The sales header model
     * @param salesDetailModelList  The sales detail model in list
     * @param cashPaid              The customer paid in cash
     * @param changeCash            The cash returned to the customer
     */
    @Override
    public void onPayButton(String oldHeaderKey,
                            String userUid,
                            SalesHeaderModel salesHeaderModel,
                            ArrayList<SalesDetailModel> salesDetailModelList,
                            String cashPaid,
                            String changeCash) {

        // Pass the data into field variable for later use
        qqOldHeaderKey =oldHeaderKey;
        qqUserUid = userUid;
        qqSalesHeaderModel = salesHeaderModel;
        qqSalesDetailModelList = salesDetailModelList;
        qqCashPaid = cashPaid;
        qqChangeCash = changeCash;


        // The credit card reader
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }


    /**
     * Callbacks from {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailFragmentUI.Adapter}
     * To pass the selected items for delete
     * @param pushKeyList       The open sales detail push()
     * @param viewHolderMap     The view holder object
     */
    @Override
    public void sendDeleteMultiOpenSalesDetail(ArrayList<String> pushKeyList, HashMap<String, OpenSalesDetailFragmentUI.ViewHolder> viewHolderMap) {

        // Pass the reference
        mListOfDeleteOpenSalesDetailIds = new ArrayList<>();
        mListOfDeleteOpenSalesDetailIds.clear();
        mListOfDeleteOpenSalesDetailIds.addAll(pushKeyList);

        mMapOfViewHolderOpenSalesDetail = new HashMap<>();
        mMapOfViewHolderOpenSalesDetail.clear();
        mMapOfViewHolderOpenSalesDetail.putAll(viewHolderMap);

    }


    /**
     * Activate the ActionMode and perform the delete
     * @param headerKey         OpenSalesHeader push key
     */
    @Override
    public void onTurnActionModeOnOpenSalesDetail(final String headerKey) {

        mActionMode = (this).startActionMode(new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_action_mode, menu);
                fragment.mAdapter.isContextualMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.action_delete:

                        // Iterate and delete
                        int listSize = mListOfDeleteOpenSalesDetailIds.size();
                        for (int i = 0; i < listSize; i++) {

                            // Get the reference
                            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference()
                                    .child(mUserUid)
                                    .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                                    .child(headerKey)
                                    .child(mListOfDeleteOpenSalesDetailIds.get(i));

                            // Pass null to delete
                            itemRef.setValue(null);

                        }


                        mActionMode.finish();

                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {

                // return the color to normal
                for (int i = 0; i < mListOfDeleteOpenSalesDetailIds.size(); i++) {
                    String pushKey = mListOfDeleteOpenSalesDetailIds.get(i);

                    mMapOfViewHolderOpenSalesDetail.get(pushKey).mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                    mMapOfViewHolderOpenSalesDetail.get(pushKey).mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));

                }

                // Free resources
                fragment.mAdapter.isContextualMode = false;
                mActionMode = null;
                mListOfDeleteOpenSalesDetailIds = null;
                mMapOfViewHolderOpenSalesDetail = null;

            }
        });


        // Display the number of items clicked by the user as a counter on the toolbar
        if (mActionMode != null) {
            int size = mListOfDeleteOpenSalesDetailIds.size();
            mActionMode.setTitle(size + " Item(s) will be deleted");

        }
    }


    /**
     * Getter for the {@link android.view.ActionMode} instance
     * @return          {@link android.view.ActionMode}
     */
    @Override
    public android.view.ActionMode getActionModeOpenSalesDetail() {
        return mActionMode;
    }
}
