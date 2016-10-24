
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

package com.stockita.stockitapointofsales.itemmaster;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewMiniLookupItemList;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.interfaces.SalesDetailPendingCallbacks;
import com.stockita.stockitapointofsales.salespack.openpack.OpenSalesAddFormDialogFragment;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingAddFormDialogFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class will display a lookup of item master that the barcode scanner has read
 * but there are more than one matching item, so the user can choose, then send the user click
 * to the {@link SalesPendingAddFormDialogFragment} or {@link OpenSalesAddFormDialogFragment}
 */
public class MiniLookupItemMaster extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    // Constant
    private static final String TAG_LOG = MiniLookupItemMaster.class.getSimpleName();
    private static final String KEY_DATA_USER_UID = TAG_LOG + ".KEY_DATA_USER_UID";
    private static final int LOADER_ID_ONE = 10001;
    private static final String KEY_DATA_REQUEST_CODE = TAG_LOG + ".KEY_DATA_REQUEST_CODE";
    private static final String KEY_DATA_SALES_HEADER_KEY = TAG_LOG + ".KEY_DATA_SALES_HEADER_KEY";


    // Member variables
    private String mUserUid;
    private MyAdapter mMyAdapter;
    private int mRequestCode;
    private String mSalesHeaderKey;

    // Views
    @Bind(R.id.item_master_list)
    RecyclerView mItemMasterList;


    /**
     * Empty constructor
     */
    public MiniLookupItemMaster() {
    }


    /**
     * This is when we instantiate the fragment and pass the data
     *
     * @param userUid     The user's UID
     * @return            This fragment
     */
    public static MiniLookupItemMaster newInstance(int requestCode, String userUid, String salesHeaderKey, ArrayList<String> itemMasterKeyList, ArrayList<ItemModel> itemMasterModelList) {

        // Instantiate this fragment
        MiniLookupItemMaster fragment = new MiniLookupItemMaster();

        // Instantiate the bundle
        Bundle bundle = new Bundle();

        // put the data in the bundle
        bundle.putString(KEY_DATA_USER_UID, userUid);
        bundle.putInt(KEY_DATA_REQUEST_CODE, requestCode);
        bundle.putString(KEY_DATA_SALES_HEADER_KEY, salesHeaderKey);

        // pass the bundle to the fragment
        fragment.setArguments(bundle);

        // return the fragment
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the user encoded email from the activity
        mUserUid = getArguments().getString(KEY_DATA_USER_UID);

        // Get the request code.
        mRequestCode = getArguments().getInt(KEY_DATA_REQUEST_CODE);

        // Get the sales header key if any
        mSalesHeaderKey = getArguments().getString(KEY_DATA_SALES_HEADER_KEY);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Initialize the loader, to get the boss email and the boolean status from local database */
        getLoaderManager().initLoader(LOADER_ID_ONE, null, this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Initialize the view
        final View view = inflater.inflate(R.layout.dialog_mini_item_master, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Set more efficient
        mItemMasterList.setHasFixedSize(true);

        // Set the layout manager for the item master
        mItemMasterList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // return the value
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {


        /* The cursorLoader object */
        CursorLoader loader = null;

        switch (id) {
            case LOADER_ID_ONE:

                /* Query all the data matches the _id 1. */
                loader = new CursorLoader(getActivity(),
                        ContractData.ItemMasterEntry.CONTENT_URI,
                        null, null, null, null);
                break;
        }

        // Return only the loader object
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        /* Get the cursor move to first. */
        cursor.moveToFirst();


        switch (loader.getId()) {
            case LOADER_ID_ONE:

                // Get the number of record in the cursor
                int dataCount = cursor.getCount();

                // Initialize local array for the item model and the item keys
                ArrayList<ItemModel> dataItemList = new ArrayList<>(dataCount);
                ArrayList<String> dataKeyList = new ArrayList<>(dataCount);

                for (int i=0; i< dataCount; i++) {

                    // The item model
                    ItemModel itemModel = new ItemModel();
                    itemModel.setItemNumber(cursor.getString(ContractData.ItemMasterEntry.INDEX_COL_ITEM_NUMBER));
                    itemModel.setItemDesc(cursor.getString(ContractData.ItemMasterEntry.INDEX_COL_ITEM_DESC));
                    itemModel.setUnitOfMeasure(cursor.getString(ContractData.ItemMasterEntry.INDEX_COL_ITEM_UNIT));
                    itemModel.setItemPrice(cursor.getString(ContractData.ItemMasterEntry.INDEX_COL_ITEM_PRICE));
                    dataItemList.add(itemModel);

                    // The push() key
                    String itemPushKey = cursor.getString(ContractData.ItemMasterEntry.INDEX_COL_PUSH_KEY);
                    dataKeyList.add(itemPushKey);

                    // Move the cursor to next
                    cursor.moveToNext();
                }

                // Initialize the adapter, then pass the two arrayList
                mMyAdapter = new MyAdapter(dataKeyList, dataItemList);

                // Set the adapter for the item master
                mItemMasterList.setAdapter(mMyAdapter);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }


    /**
     * Custom recycler view adapter
     */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<String> mAdapterItemKey;
        private ArrayList<ItemModel> mAdapterItemList;

        /**
         * Constructor
         */
        public MyAdapter(ArrayList<String> itemMasterKeyList, ArrayList<ItemModel> itemMasterModelList) {

            this.mAdapterItemKey = itemMasterKeyList;
            this.mAdapterItemList = itemMasterModelList;

            notifyDataSetChanged();

        }


        @Override
        public int getItemViewType(int position) {
            return R.layout.adapter_each_card_in_mini_lookup;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Initialize the view object
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            // Return the view holder and pass the view object as argument
            return new ViewHolder(view);


        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (mAdapterItemList != null) {

                holder.mAdapterView.getmItemNumberLookup().setText(mAdapterItemList.get(position).getItemNumber());
                holder.mAdapterView.getmItemDescLookup().setText(mAdapterItemList.get(position).getItemDesc());
                holder.mAdapterView.getmItemPriceLookup().setText(mAdapterItemList.get(position).getItemPrice());

                /**
                 * If the user clicked on any item he choosed then it will take the data and pass them
                 * to the {@link SalesPendingAddFormDialogFragment} to continue his/her work
                 */
                holder.mAdapterView.getmRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (mRequestCode) {

                            case 1:

                                /**
                                 * When the user click on an item, send the push() key and the model to
                                 * {@link com.stockita.stockitapointofsales.activities.MainActivity#sendItemMasterData(String, ItemModel)}
                                 * To it will be passed to the SalesPendingAddFormDialogFragment
                                 */
                                ((SalesDetailPendingCallbacks) getActivity())
                                        .sendItemMasterData(mAdapterItemKey.get(holder.getAdapterPosition()),
                                                mAdapterItemList.get(holder.getAdapterPosition()));

                                break;

                            case 2:

                                /**
                                 * When the user click on an item, send the {@link ItemModel} to
                                 * {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailActivity}
                                 * so then later will passed to {@link OpenSalesAddFormDialogFragment}
                                 */
                                ((OpenSalesHeaderListCallbacks) getActivity())
                                        .sendItemMasterData(mSalesHeaderKey,
                                                mAdapterItemList.get(holder.getAdapterPosition()));
                                break;
                        }


                        // Dismiss the dialog
                        getDialog().dismiss();

                    }
                });
            }
        }

        @Override
        public int getItemCount() {

            return mAdapterItemList != null ? mAdapterItemList.size() : 0;

        }


        /**
         * ViewHolder class
         */
        public class ViewHolder extends RecyclerView.ViewHolder {


            // View object
            @Bind(R.id.adapter_custom_view_for_mini_lookup)
            AdapterViewMiniLookupItemList mAdapterView;


            /**
             * Constructor
             *
             * @param itemView View object
             */
            public ViewHolder(View itemView) {
                super(itemView);

                // Initialize the butterKnife
                ButterKnife.bind(this, itemView);
            }
        }

    }
}
