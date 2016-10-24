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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewItemLookup;
import com.stockita.stockitapointofsales.data.ItemImageModel;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.interfaces.SalesDetailPendingCallbacks;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class will show a dialog fragment to lookup into a list of item master
 * so the user can click one of them to be added into the sales dialog
 */
public class LookupItemMasterListDialogFragment extends DialogFragment {

    // Constant
    private static final String TAG_LOG = LookupItemMasterListDialogFragment.class.getSimpleName();
    private static final String KEY_DATA_USER_UID = TAG_LOG +  ".KEY_DATA_USER_UID";
    private static final String KEY_DATA_REQUEST_CODE = TAG_LOG + ".KEY_DATA_REQUEST_CODE";
    private static final String KEY_DATA_ITEM_MASTER_KEY_LIST = TAG_LOG + ".KEY_DATA_ITEM_MASTER_KEY_LIST";
    private static final String KEY_DATA_SALES_HEADER_KEY = TAG_LOG + ".KEY_DATA_SALES_HEADER_KEY";
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";
    private static final String KEY_FOUR = TAG_LOG + ".KEY_FOUR";


    // Member variables
    private int mRequestCode;
    private String mUserUid;
    private MyAdapter mMyAdapter;
    private ArrayList<String> mItemMasterKeyList;
    private String mSalesHeaderKey;


    // Views
    @Bind(R.id.item_master_list)
    RecyclerView mItemMasterList;


    /**
     * Empty constructor
     */
    public LookupItemMasterListDialogFragment() {

    }


    /**
     * This is when we instantiate the fragment and pass the data
     *
     * @param requestCode       To know from where and to where to go
     * @param userUid           The user's UID
     * @param itemMasterKeyList The list of item master push() keys
     * @param salesHeaderKey    The Sales Header push() key
     * @return This fragment
     */
    public static LookupItemMasterListDialogFragment newInstance(int requestCode, String userUid, ArrayList<String> itemMasterKeyList, String salesHeaderKey) {

        // Instantiate this fragment
        LookupItemMasterListDialogFragment fragment = new LookupItemMasterListDialogFragment();

        // Instantiate the bundle
        Bundle bundle = new Bundle();

        // put the data in the bundle
        bundle.putString(KEY_DATA_USER_UID, userUid);
        bundle.putInt(KEY_DATA_REQUEST_CODE, requestCode);
        bundle.putStringArrayList(KEY_DATA_ITEM_MASTER_KEY_LIST, itemMasterKeyList);
        bundle.putString(KEY_DATA_SALES_HEADER_KEY, salesHeaderKey);

        // pass the bundle to the fragment
        fragment.setArguments(bundle);

        // return the fragment
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // Get the user encoded email from the activity
            mUserUid = getArguments().getString(KEY_DATA_USER_UID);

            // Get the request code
            mRequestCode = getArguments().getInt(KEY_DATA_REQUEST_CODE);

            // Get the list of push() key
            mItemMasterKeyList = getArguments().getStringArrayList(KEY_DATA_ITEM_MASTER_KEY_LIST);

            // Get SalesHeader key
            mSalesHeaderKey = getArguments().getString(KEY_DATA_SALES_HEADER_KEY);
        }

        if (savedInstanceState != null) {

            mUserUid = savedInstanceState.getString(KEY_ONE);
            mRequestCode = savedInstanceState.getInt(KEY_TWO);
            mItemMasterKeyList = savedInstanceState.getStringArrayList(KEY_THREE);
            mSalesHeaderKey = savedInstanceState.getString(KEY_FOUR);

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ONE, mUserUid);
        outState.putInt(KEY_TWO, mRequestCode);
        outState.putStringArrayList(KEY_THREE, mItemMasterKeyList);
        outState.putString(KEY_FOUR, mSalesHeaderKey);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        final View view = inflater.inflate(R.layout.dialog_lookup_item_master, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Set more efficient
        mItemMasterList.setHasFixedSize(true);


        /**
         * Code below is for the number of span depend device (Phone / tablet)
         * and orientation (portrait / landscape)
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        if (isTablet && isLandscape) {
            columnCount = 4;
        } else if (!isTablet && isLandscape) {
            columnCount = 3;
        } else if (isTablet && !isLandscape) {
            columnCount = 3;
        } else {
            columnCount = 2;
        }


        // Initialize the LayoutManager for item master
        GridLayoutManager layoutManager =
                new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        // Set the layout manager for the item master
        mItemMasterList.setLayoutManager(layoutManager);

        // Return the view object
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Server reference location item master
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_ITEM_MASTER_LOCATION);

        // Initialize the adapter
        mMyAdapter = new MyAdapter(ItemModel.class, R.layout.adapter_each_card_in_item_lookup,
                ViewHolder.class, databaseReference, mUserUid) {

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final ItemModel model, int position) {

                // Get the pushKey()
                DatabaseReference keyRef = getRef(viewHolder.getLayoutPosition());

                // Convert the keyRef to type String, then pass it to the UI
                final String pushKey = keyRef.getKey();

                // Get the item number
                String itemNumber = model.getItemNumber();

                // Get the item desc
                String itemDesc = model.getItemDesc();
                viewHolder.mAdapterView.getmItemDesc().setText(itemDesc);

                // Get Unit of measure
                String itemUnit = model.getUnitOfMeasure();

                // Get item price
                String itemPrice = model.getItemPrice();
                viewHolder.mAdapterView.getmItemPrice().setText(itemPrice);

                // the single click listener, the user will select an item.
                viewHolder.mAdapterView.getmRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (mRequestCode) {

                            case Constants.REQUEST_CODE_SALES_ADD_ITEM_LOOPUP:

                                /** When the user click on an item, send the push() key and the model to
                                 * {@link com.stockita.stockitapointofsales.activities.MainActivity#sendItemMasterData(String, ItemModel)}
                                 * To it will be passed to the {@link SalesPendingAddFormDialogFragment}
                                 */
                                ((SalesDetailPendingCallbacks) getActivity()).sendItemMasterData(pushKey, model);
                                break;

                            case Constants.REQUEST_CODE_OPEN_SALES_ADD_ITEM_LOOKUP:
                                /** When the user click on an item, send the push() key and the model to
                                 * {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailActivity#sendItemMasterData(String, ItemModel)}
                                 * To it will be passed to the {@link SalesOpenAddFormDialogFragment}
                                 */
                                ((OpenSalesHeaderListCallbacks) getActivity()).sendItemMasterData(mSalesHeaderKey, model);
                                break;
                        }


                        // cleanup, to remove the listener.
                        cleanup();

                        // Dismiss the dialog
                        getDialog().dismiss();
                    }
                });


                /**
                 * Nested recycler view for item images
                 */


                // Get the reference to the Item Image
                DatabaseReference imageRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child(mAdapterUserUid)
                                .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                                .child(pushKey);


                // This adapter will display only image for each element that matches the item master pushKey
                mNestedImageAdapter =
                        new FirebaseRecyclerAdapter<ItemImageModel, NestedImageViewHolder>(ItemImageModel.class, R.layout.adapter_each_card_nested_image, NestedImageViewHolder.class, imageRef) {


                            @Override
                            protected void populateViewHolder(final NestedImageViewHolder viewHolder, ItemImageModel itemImageInstance, int position) {


                                // Get the pushKey() for this image in this position
                                DatabaseReference imageKeyRef = getRef(viewHolder.getLayoutPosition());

                                // Convert the keyRef to type String, then pass it to the UI
                                final String imageKeyRefString = imageKeyRef.getKey();


                                /**
                                 * Code below will find the image from the local or server then populate them in the UI
                                 */

                                // Initialize storage
                                StorageReference imageStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://stockitapointofsales.appspot.com/");
                                StorageReference imageStorageRefForUser = imageStorageRef.child(mAdapterUserUid).child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION).child(pushKey);

                                // Get the file path from the model
                                String filePath = itemImageInstance.getImageUrl();

                                // Get the file Uri
                                Uri file = Uri.fromFile(new File(filePath));

                                // Get the local file
                                File localFile = new File(filePath);


                                // Check if the image available in local
                                if (localFile.exists()) {
                                    try {
                                        // Display the image
                                        Glide.with(getActivity())
                                                .load("file://" + localFile)
                                                .centerCrop()
                                                .crossFade()
                                                .into(viewHolder.mItemImageNested);
                                    } catch (Exception e) {
                                        Log.e(TAG_LOG, e.getMessage());
                                    }


                                } else {

                                    // If not available in local then download from the server
                                    imageStorageRefForUser.child(file.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            try {
                                                // Display the image
                                                Glide.with(getActivity())
                                                        .load(uri)
                                                        .centerCrop()
                                                        .crossFade()
                                                        .into(viewHolder.mItemImageNested);
                                            } catch (Exception e) {
                                                Log.e(TAG_LOG, e.getMessage());
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG_LOG, e.getMessage());
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onBindViewHolder(NestedImageViewHolder holder, int position, List<Object> payloads) {
                                super.onBindViewHolder(holder, position, payloads);

                                // Add animation here..
                            }


                            @Override
                            public int getItemViewType(int position) {

                                // The layout for each element on the list
                                return R.layout.adapter_each_card_nested_image;

                            }


                            @Override

                            public NestedImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                                // Initialize the view object
                                View nested = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
                                return new NestedImageViewHolder(nested);

                            }

                        }; // End of nested image adapter


                // For the nested item image
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setHasFixedSize(true);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setLayoutManager(linearLayoutManager);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setAdapter(mNestedImageAdapter);

            }


            @Override
            public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
                // Add animation here..
            }


            @Override
            public int getItemViewType(int position) {

                // The layout for each element in the list
                return R.layout.adapter_each_card_in_item_lookup;
            }


            /**
             * It will crash if we don't override this method, like the following code
             * @param parent        ViewGroup object
             * @param viewType      The layout xml frmo the method getItemViewType
             * @return View holder type
             */
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                // Initialize the view object
                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                // Return the view holder and pass the view object as argument
                return new ViewHolder(view);

            }
        };


        // Set the adapter for the item master
        mItemMasterList.setAdapter(mMyAdapter);


    }


    @Override
    public void onStop() {
        super.onStop();
        // Cleanup the listeners
        if (mMyAdapter != null) {
            mMyAdapter.cleanup();
        }
    }


    /**
     * This method is to receive messages from the MainActivity inform of other
     * than onActivityForResult().
     *
     * @param message any message
     */
    public void setAnyResult(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }


    /**
     * View holder class for Item master
     */
    public class ViewHolder extends RecyclerView.ViewHolder {


        // Declare the custom view adapter
        @Bind(R.id.adapter_custom_view_for_item_lookup)
        AdapterViewItemLookup mAdapterView;

        /**
         * Constructor
         *
         * @param itemView Object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the ButterKnife
            ButterKnife.bind(this, itemView);
        }
    }


    /**
     * Nested view holder class for the item image
     */
    public class NestedImageViewHolder extends RecyclerView.ViewHolder {

        // Views
        @Bind(R.id.item_image_nested)
        ImageView mItemImageNested;

        /**
         * Constructor
         *
         * @param itemView
         */
        public NestedImageViewHolder(View itemView) {
            super(itemView);

            // Initialize the butterKnife
            ButterKnife.bind(this, itemView);

        }
    }


    /**
     * Abstract class for the adapter
     */
    public abstract class MyAdapter extends FirebaseRecyclerAdapter<ItemModel, ViewHolder> {

        /**
         * The user login encoded email
         */
        protected String mAdapterUserUid;

        /**
         * This is an instance of a nested adapter to display the images
         */
        protected FirebaseRecyclerAdapter<ItemImageModel, NestedImageViewHolder> mNestedImageAdapter;


        /**
         * constructor
         *
         * @param userUid        The user's sign in UID
         */
        public MyAdapter(Class<ItemModel> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, String userUid) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            this.mAdapterUserUid = userUid;

        }


        /**
         * Call super and then invoke cleanup() for the nested adapter
         */
        @Override
        public void cleanup() {
            super.cleanup();

            // invoke cleanup() on the nested image adapter
            if (mNestedImageAdapter != null) {
                mNestedImageAdapter.cleanup();
            }
        }
    }
}
