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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewPayment;
import com.stockita.stockitapointofsales.data.PaymentModel;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.ManageDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This fragment will display the payment detail into the RV
 */
public class PaymentFragment extends Fragment {


    // Constants
    private static final String TAG_LOG = PaymentFragment.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG +  ".KEY_TWO";

    // Member variables
    private String mUserUid;
    private String mPaidSalesHeaderKey;
    private PaymentAdapter mAdapter;

    // View
    @Bind(R.id.list_of_payment)
    RecyclerView mList;
    @Bind(R.id.cash_toolbar)
    Toolbar cashToolbar;


    /**
     * Empty constructor
     */
    public PaymentFragment() {}

    /**
     * factory method to pass in to here
     * @param userUid           The user UID
     * @param headerKey         The header key
     * @return                  This fragment
     */
    public static PaymentFragment newInstance(String userUid, String headerKey) {

        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();

        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, headerKey);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // uid
        mUserUid = getArguments().getString(KEY_ONE);

        // sales header key
        mPaidSalesHeaderKey = getArguments().getString(KEY_TWO);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Hide the cash toolbar
        cashToolbar.setVisibility(View.GONE);

        // Initialize the adapter
        mAdapter = new PaymentAdapter(getActivity(), mUserUid, mPaidSalesHeaderKey);

        // set the adapter
        mList.setAdapter(mAdapter);

        // Set the layout manager
        mList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        // Return the view
        return view;


    }


    /**
     * Adaptr class
     */
    public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {


        private Context mAdapterContext;
        private ArrayList<PaymentModel> mPaymentList;
        private ArrayList<String> mPaymentKeyList;

        private String mUserUid;
        private String mAdapterHeaderKey;

        private DatabaseReference detailRef;
        private ChildEventListener detailListener;

        /**
         * Constructor
         * @param context       The activity context
         * @param userUid       The user's UID
         * @param headerKey     The sales header key
         */
        public PaymentAdapter(Context context, String userUid, String headerKey) {

            mAdapterContext = context;
            mUserUid = userUid;
            mAdapterHeaderKey = headerKey;

            mPaymentKeyList = new ArrayList<>();
            mPaymentList = new ArrayList<>();

            detailRef = FirebaseDatabase.getInstance().getReference()
                    .child(mUserUid)
                    .child(Constants.FIREBASE_PAYMENT_LOCATION)
                    .child(mAdapterHeaderKey);

            // Get the data feed from Firebase database
            getDataFromFirebase();

            // Notify change
            notifyDataSetChanged();

        }


        @Override
        public int getItemViewType(int position) {
            return R.layout.adapter_each_card_in_payment;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Initialize the view object
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            // Return the view holder and pass the view object as argument
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (mPaymentList != null) {

                // Customer name
                String customerName = mPaymentList.get(position).getCustomerName();
                holder.mAdapterViewPayment.getmCustomerName().setText(customerName);

                // Total invoice amount
                String totalInvoiceAmount = mPaymentList.get(position).getTotalInvoiceAmount();
                holder.mAdapterViewPayment.getmTotalInvoiceAmount().setText(totalInvoiceAmount);

                // cash paid
                String cashPaid = mPaymentList.get(position).getCashPaid();
                holder.mAdapterViewPayment.getmCashPaid().setText(cashPaid);

                // change cash
                String changeCash = mPaymentList.get(position).getChangeCash();
                holder.mAdapterViewPayment.getmChangeCash().setText(changeCash);

                // credit card
                String creditCard = mPaymentList.get(position).getCreditCardNumber();
                holder.mAdapterViewPayment.getmCreditCardNumber().setText(creditCard);

                // Server date
                HashMap<String, Object> serverDate = mPaymentList.get(position).getServerDate();
                long longServerDate = (long) serverDate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
                String stringServerDate = new ManageDateTime(longServerDate).getGivenTimeMillisInDateFormat();
                holder.mAdapterViewPayment.getmServerDate().setText(stringServerDate);



            }


        }

        @Override
        public int getItemCount() {
            return mPaymentList != null ? mPaymentList.size() : 0;

        }

        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

            /* Check if it is not null */
            if (mPaymentList != null) {

                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                detailListener = detailRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                        /* Get the model from the dataSnapshot */
                        PaymentModel model = dataSnapshot.getValue(PaymentModel.class);

                        if (model != null) {

                            /*  Pack the POJO into an ArrayList */
                            mPaymentList.add(model);

                            /* Pack the keys into an ArrayList */
                            mPaymentKeyList.add(dataSnapshot.getKey());

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {


                        /* Get the model from the dataSnapshot */
                        PaymentModel model = dataSnapshot.getValue(PaymentModel.class);

                        if (model != null) {

                            /* Get the key for the value that changed */
                            String keyChanged = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mPaymentKeyList.indexOf(keyChanged);

                            /* Revise/Set the value for that index */
                            mPaymentList.set(theIndexOfKey, model);

                            /* Notify this adapter for any data changes */
                            notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                            /* Get the key */
                        String keyRemoved = dataSnapshot.getKey();

                            /* Find the index of that key */
                        int theIndexOfKey = mPaymentKeyList.indexOf(keyRemoved);

                            /* Remove the value from the list */
                        mPaymentList.remove(theIndexOfKey);

                            /* Remove the key from the list */
                        mPaymentKeyList.remove(theIndexOfKey);

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




        /**
         * View holder class
         */
        public class MyViewHolder extends RecyclerView.ViewHolder {

            // View
            @Bind(R.id.adapter_custom_view_payment)
            AdapterViewPayment mAdapterViewPayment;

            /**
             * Constructor
             * @param itemView      view object
             */
            public MyViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }
    }
}
