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

package com.stockita.stockitapointofsales.customViews;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;


/**
 * This is a custom view class that combines views for the layout of the
 * item list card, this card will be used to element in the recycler view
 * that bind data for the ItemModel from /item/...
 */
public class AdapterViewItemList extends LinearLayout {


    // Members variables
    private LinearLayout mRoot, mSubTextDetail;
    private FrameLayout mImageFrame;
    private TextView mItemDesc, mItemPrice;
    private RecyclerView mItemImage;
    private ImageButton mPopupMenuViewObject;
    private PopupMenu mPopupMenu;


    /**
     * Constructor
     * @param context       Activity context
     */
    public AdapterViewItemList(Context context) {
        super(context);
        init(context);
    }


    /**
     * Constructor
     * @param context       Activity context
     * @param attrs         AttributeSet object
     */
    public AdapterViewItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    /**
     * Helper method to initialize the view group and the widget
     * @param context   Activity context
     */
    private void init(Context context) {


        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.adapter_view_item_list, this);

        // Initialize the view group and widgets
        mRoot = (LinearLayout) mRootView.findViewById(R.id.root);
        mImageFrame = (FrameLayout) mRootView.findViewById(R.id.image_frame);
        mItemImage = (RecyclerView) mRootView.findViewById(R.id.item_image);
        mSubTextDetail = (LinearLayout) mRootView.findViewById(R.id.sub_text_detail);
        mItemDesc = (TextView) mRootView.findViewById(R.id.item_desc);
        mItemPrice = (TextView) mRootView.findViewById(R.id.item_price);
        mPopupMenuViewObject = (ImageButton) mRootView.findViewById(R.id.item_popup_menu);
        mPopupMenuViewObject.setBackgroundColor(Color.TRANSPARENT);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Initialize the PopupMenu object, and pass the View object in the second argument
        mPopupMenu = new PopupMenu(getContext(), mPopupMenuViewObject);

        // Get the menu inflater to inflate the xml file
        mPopupMenu.getMenuInflater().inflate(R.menu.popup_menu_adapter_view_item_list, mPopupMenu.getMenu());

        /**
         * Now we need to add a click listener on the view object, so when the user click
         * this view object the menu will popup, this can be done using show() method.
         */
        mPopupMenuViewObject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Invoke show() to popup the menu.
                mPopupMenu.show();
            }
        });

    }


    /**
     * Code below are the getters and setters
     */

    public LinearLayout getmRoot() {
        return mRoot;
    }

    public void setmRoot(LinearLayout mRoot) {
        this.mRoot = mRoot;
    }

    public LinearLayout getmSubTextDetail() {
        return mSubTextDetail;
    }

    public void setmSubTextDetail(LinearLayout mSubTextDetail) {
        this.mSubTextDetail = mSubTextDetail;
    }

    public TextView getmItemDesc() {
        return mItemDesc;
    }

    public void setmItemDesc(TextView mItemDesc) {
        this.mItemDesc = mItemDesc;
    }


    public TextView getmItemPrice() {
        return mItemPrice;
    }

    public void setmItemPrice(TextView mItemPrice) {
        this.mItemPrice = mItemPrice;
    }

    public ImageView getmPopupMenuViewObject() {
        return mPopupMenuViewObject;
    }

    public void setmPopupMenuViewObject(ImageButton mPopupMenuViewObject) {
        this.mPopupMenuViewObject = mPopupMenuViewObject;
    }

    public PopupMenu getmPopupMenu() {
        return mPopupMenu;
    }

    public void setmPopupMenu(PopupMenu mPopupMenu) {
        this.mPopupMenu = mPopupMenu;
    }

    public RecyclerView getmItemImage() {
        return mItemImage;
    }

    public void setmItemImage(RecyclerView mItemImage) {
        this.mItemImage = mItemImage;
    }

    public FrameLayout getmImageFrame() {
        return mImageFrame;
    }

    public void setmImageFrame(FrameLayout mImageFrame) {
        this.mImageFrame = mImageFrame;
    }
}
