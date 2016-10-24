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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stockita.stockitapointofsales.data.ItemImageModel;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * This is a Service class that will perform task in the background for item master pack
 */
public class ItemMasterIntentService extends IntentService {

    // Constants
    private static final String TAG_LOG = ItemMasterIntentService.class.getSimpleName();
    private static final String ACTION_DELETE_IMAGE = TAG_LOG + ".DELETE_IMAGE";
    private static final String ACTION_DELETE_ITEM = TAG_LOG + ".DELETE_ITEM";
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";


    /**
     * Constructor
     */
    public ItemMasterIntentService() {
        super(TAG_LOG);
    }


    /**
     * Static method to fire the intent service and pass data into here
     * This factory method is to delete the item master including all of its images
     *
     * @param context           The activity context
     * @param userUid           The user's UID
     * @param itemMasterKey     The item master push() key
     */
    public static void deleteOneItemMaster(Context context, String userUid, String itemMasterKey) {

        Intent intent = new Intent(context, ItemMasterIntentService.class);
        intent.setAction(ACTION_DELETE_ITEM);
        intent.putExtra(KEY_ONE, userUid);
        intent.putExtra(KEY_TWO, itemMasterKey);
        context.startService(intent);

    }


    /**
     * Static method to fire the intent service and pass data into here
     * This factory method is to delete one item image only
     *
     * @param context               The activity context
     * @param userUid          The user login encoded email
     * @param itemMasterKey         Item master push() key
     * @param itemImageKey          Item image push() key
     */
    public static void deleteOneItemImage(Context context, String userUid, String itemMasterKey, String itemImageKey) {

        Intent intent = new Intent(context, ItemMasterIntentService.class);
        intent.setAction(ACTION_DELETE_IMAGE);
        intent.putExtra(KEY_ONE, userUid);
        intent.putExtra(KEY_TWO, itemMasterKey);
        intent.putExtra(KEY_THREE, itemImageKey);
        context.startService(intent);


    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            // Delete item image
            if (ACTION_DELETE_IMAGE.equals(intent.getAction())) {
                String userUid = intent.getStringExtra(KEY_ONE);
                String itemMasterKey = intent.getStringExtra(KEY_TWO);
                String itemImageKey = intent.getStringExtra(KEY_THREE);
                deleteOneItemImage(userUid, itemMasterKey, itemImageKey);
            }

            // Delete item master
            if (ACTION_DELETE_ITEM.equals(intent.getAction())) {
                String userUid = intent.getStringExtra(KEY_ONE);
                String itemMasterKey = intent.getStringExtra(KEY_TWO);
                deleteOneItemMaster(userUid,  itemMasterKey);
            }

        }
    }


    /**
     * Helper method to delete item master and all its images
     * @param userUid                User login UID
     * @param itemMasterKey         Item master push() key
     */
    private void deleteOneItemMaster(final String userUid, final String itemMasterKey) {


        // Instantiate the server database object for itemMaster
        DatabaseReference locationItem = FirebaseDatabase.getInstance().getReference();

        // Get to the location /encodedEmail/itemMaster + itemKey + then pass null to delete
        locationItem.child(userUid)
                .child(Constants.FIREBASE_ITEM_MASTER_LOCATION)
                .child(itemMasterKey).setValue(null);

        // Instantiate the server database object for itemImage
        DatabaseReference locationItemImage = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                .child(itemMasterKey);


        // Get the image file name then delete the images in the storage
        locationItemImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Container
                ArrayList<ItemImageModel> list = new ArrayList<>();

                // Iterate to pack the list with itemImageModel
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    // Instantiate the item image
                    ItemImageModel itemImageModel = snap.getValue(ItemImageModel.class);

                    // Pack the itemImageModel into the list
                    list.add(itemImageModel);

                }


                // Iterate to delete each file from the storage
                for (ItemImageModel image : list) {

                    // Get the imageUrl
                    String imageName = image.getImageUrl();

                    // Get the file Uri
                    Uri file = Uri.fromFile(new File(imageName));


                    // Initialize storage
                    StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference()
                            .child(userUid)
                            .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                            .child(itemMasterKey);


                    // Delete the image from the storage
                    imageStorageRef.child(file.getLastPathSegment()).delete();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());

            }
        });

        // Get to the location /encodedEmail/itemImage + itemKey + then pass null to delete
        locationItemImage.setValue(null);

    }


    /**
     * Helper method to perform delete one item master from the realtime database and from the realtime storage
     * @param userUid                   The user login UID
     * @param itemMasterKey             The item master push() key
     * @param itemImageKey              The item image push() key
     */
    private void deleteOneItemImage(final String userUid, final String itemMasterKey, String itemImageKey) {


        /**
         * Do the delete here...
         */

        // Instantiate the server database object for itemImage
        DatabaseReference locationItemImage = FirebaseDatabase.getInstance().getReference()
                .child(userUid)
                .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                .child(itemMasterKey);

        // Query to this specific image that the user chose to delete
        Query queryOneImage = locationItemImage.orderByKey().equalTo(itemImageKey);

        // Get the image file name then delete the image from the storage
        queryOneImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Container
                ArrayList<ItemImageModel> list = new ArrayList<>();

                // Iterate to pack the list with itemImageModel
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    // Instantiate the item image
                    ItemImageModel itemImageModel = snap.getValue(ItemImageModel.class);

                    // Pack the itemImageModel into the list
                    list.add(itemImageModel);

                }


                // Iterate to delete each file from the storage
                for (ItemImageModel image : list) {

                    // Get the imageUrl
                    String imageName = image.getImageUrl();

                    // Get the file Uri
                    Uri file = Uri.fromFile(new File(imageName));

                    // Initialize storage
                    StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference()
                            .child(userUid)
                            .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                            .child(itemMasterKey);


                    // Delete the image from the storage
                    imageStorageRef.child(file.getLastPathSegment()).delete();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());

            }
        });

        // Get to the location /userUid/itemImage + itemKey + imageKey +then pass null to delete
        locationItemImage.child(itemImageKey).setValue(null);

    }


}
