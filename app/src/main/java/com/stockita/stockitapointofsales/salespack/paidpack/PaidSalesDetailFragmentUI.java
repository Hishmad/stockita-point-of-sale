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

package com.stockita.stockitapointofsales.salespack.paidpack;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewOpenSalesDetailList;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This call is the fragment that will populate the paidSalesDetail into the RV
 */
public class PaidSalesDetailFragmentUI extends Fragment {

    // Constants
    private static final String TAG_LOG = PaidSalesDetailFragmentUI.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";

    // Member variables
    private String mUserUid;
    private String mPaidSalesHeaderKey;
    private DatabaseReference mCashCalc;
    private ValueEventListener mCashCalcListener;
    private Adapter mAdapter;


    // Views
    @Bind(R.id.cash_toolbar)
    Toolbar mCashToolbar;
    @Bind(R.id.total_toolbar)
    TextView mTotalToolbar;
    @Bind(R.id.list_of_sales_detail)
    RecyclerView mList;


    /**
     * Empty Constructor
     */
    public PaidSalesDetailFragmentUI() {}


    /**
     * Factory method to pass in to here
     * @param userUid               The user's UID
     * @param paidSalesHeaderKey    The /paidSalesHeader/pushKey
     * @return                      This fragment
     */
    public static PaidSalesDetailFragmentUI newInstance(String userUid, String paidSalesHeaderKey) {

        PaidSalesDetailFragmentUI fragment = new PaidSalesDetailFragmentUI();
        Bundle args = new Bundle();

        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, paidSalesHeaderKey);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // encoded email
        mUserUid = getArguments().getString(KEY_ONE);

        // sales header key
        mPaidSalesHeaderKey = getArguments().getString(KEY_TWO);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_list_of_paid_sales_detail, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);


        /**
         * The code below is for the sales detail recycler view
         */

        // Set more efficient
        mList.setHasFixedSize(true);

        // Initialize the cash toolbar
        initCashToolbar();

        // Initialize the adapter
        mAdapter = new Adapter(getActivity(), mUserUid, mPaidSalesHeaderKey);

        // set the adapter
        mList.setAdapter(mAdapter);



        /**
         * Code below is for the number of span depend device (Phone / tablet)
         * and orientation (portrait / landscape)
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        if (isTablet && isLandscape) {
            columnCount = 3;
        } else if (!isTablet && isLandscape) {
            columnCount = 2;
        } else if (isTablet && !isLandscape) {
            columnCount = 2;
        } else {
            columnCount = 1;
        }

        // Initialize the LayoutManager for item master
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        // Set the layout manager
        mList.setLayoutManager(gridLayoutManager);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove listener from adapter
        if (mAdapter != null) {
            mAdapter.removeListeners();
        }

        // Remove listeners
        if (mCashCalc != null && mCashCalcListener != null) {
            mCashCalc.removeEventListener(mCashCalcListener);
        }


    }


    /**
     * Helper method to initialize the cash toolbar
     * which is a second toolbar that displays the total amount and a checkout menu item
     */
    private void initCashToolbar() {

        // Inflate the menu into the cash_toolbar
        mCashToolbar.inflateMenu(R.menu.menu_cash_toolbar);

        // Remove the checkout
        mCashToolbar.getMenu().findItem(R.id.menu_cash_checkout).setVisible(false);

        // Set the background color
        mCashToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));


        // Display the sum of amount

        /**
         * Code below is to update the cash amount on the red cash toolbar
         */
        mCashCalc = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_PAID_SALES_DETAIL_LOCATION)
                .child(mPaidSalesHeaderKey);

        // Setup the listener
        mCashCalcListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Initialize the calculate
                double calculate = 0;

                // Iterate
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the instance of salesDetailModel from the dataSnapshot
                    SalesDetailModel salesDetailModel = data.getValue(SalesDetailModel.class);

                    // Do the calculation
                    try {
                        double calc = Double.parseDouble(salesDetailModel.getItemAmount());
                        calculate += calc;
                    } catch (Exception e) {
                        Log.e(TAG_LOG, e.getMessage());
                    }

                }

                // Display the cash amount
                mTotalToolbar.setText(String.valueOf(calculate));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());

            }
        };

        // Attach the listener
        mCashCalc.addValueEventListener(mCashCalcListener);

    }


    /**
     * Class adapter
     */
    public class Adapter extends RecyclerView.Adapter<ViewHolder> {


        private Context mAdapterContext;
        private ArrayList<SalesDetailModel> mSalesPaidList;
        private ArrayList<String> mSalesPaidKeyList;

        private String mAdapterUserUid;
        private String mAdapterHeaderKey;

        private DatabaseReference detailRef;
        private ChildEventListener detailListener;

        /**
         * Constructor
         * @param context       The activity context
         * @param userUid       The user uid
         * @param headerKey     The sales header key
         */
        public Adapter(Context context, String userUid, String headerKey) {

            mAdapterContext = context;
            mAdapterUserUid = userUid;
            mAdapterHeaderKey = headerKey;

            mSalesPaidKeyList = new ArrayList<>();
            mSalesPaidList = new ArrayList<>();

            detailRef = FirebaseDatabase.getInstance().getReference()
                    .child(mAdapterUserUid)
                    .child(Constants.FIREBASE_PAID_SALES_DETAIL_LOCATION)
                    .child(mAdapterHeaderKey);

            // Get the data feed from Firebase database
            getDataFromFirebase();

            // Notify change
            notifyDataSetChanged();

        }


        @Override
        public int getItemViewType(int position) {
            return R.layout.adapter_each_card_in_sales_detail_open;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Initialize the view object
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            // Return the view holder and pass the view object as argument
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            // Get the key for the current detail
            final String pushKeyDetail = mSalesPaidKeyList.get(position);

            // Get the model instance from the list
            final SalesDetailModel model = mSalesPaidList.get(position);

            // Get the field from the model
            String itemNumber = model.getItemNumber();
            String itemDesc = model.getItemDesc();
            String itemUnit = model.getItemUnit();
            String itemPrice = model.getItemPrice();
            String itemQuantity = model.getItemQuantity();
            String itemDiscount = model.getItemDiscount();
            String itemAmount = model.getItemAmount();

            // update the UI
            viewHolder.mAdapterView.getmItemDesc().setText(itemDesc);
            viewHolder.mAdapterView.getmItemPrice().setText(itemPrice);
            viewHolder.mAdapterView.getmItemQuantity().setText(itemQuantity);
            viewHolder.mAdapterView.getmItemAmount().setText(itemAmount);

            viewHolder.mAdapterView.getmItemPopupMenu().setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return mSalesPaidList != null ? mSalesPaidList.size() : 0;

        }


        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

            /* Check if it is not null */
            if (mSalesPaidList != null) {

                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                detailListener = detailRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                        /* Get the model from the dataSnapshot */
                        SalesDetailModel model = dataSnapshot.getValue(SalesDetailModel.class);

                        if (model != null) {

                            /*  Pack the POJO into an ArrayList */
                            mSalesPaidList.add(model);

                            /* Pack the keys into an ArrayList */
                            mSalesPaidKeyList.add(dataSnapshot.getKey());

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {


                        /* Get the model from the dataSnapshot */
                        SalesDetailModel model = dataSnapshot.getValue(SalesDetailModel.class);

                        if (model != null) {

                            /* Get the key for the value that changed */
                            String keyChanged = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mSalesPaidKeyList.indexOf(keyChanged);

                            /* Revise/Set the value for that index */
                            mSalesPaidList.set(theIndexOfKey, model);

                            /* Notify this adapter for any data changes */
                            notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {



                            /* Get the key */
                        String keyRemoved = dataSnapshot.getKey();

                            /* Find the index of that key */
                        int theIndexOfKey = mSalesPaidKeyList.indexOf(keyRemoved);

                            /* Remove the value from the list */
                        mSalesPaidList.remove(theIndexOfKey);

                            /* Remove the key from the list */
                        mSalesPaidKeyList.remove(theIndexOfKey);

                            /* Notify this adapter for any data changes */
                        notifyDataSetChanged();

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
            }
        }

        /**
         * Helper method to remove all listeners, this must be public
         * because it will be invoked later by
         */
        public void removeListeners() {
            if (detailRef != null && detailListener != null) {
                detailRef.removeEventListener(detailListener);
            }
        }


    }


    /**
     * View holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_custom_view_for_open_sales_detail_list)
        AdapterViewOpenSalesDetailList mAdapterView;


        /**
         * Constructor
         *
         * @param itemView View object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // initialize the butter knife
            ButterKnife.bind(this, itemView);

        }
    }
}
