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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * This class is a custom view for each element in the item master lookup
 */
public class AdapterViewItemLookup extends LinearLayout {


    // Members variables
    private LinearLayout mRoot, mSubTextDetail;
    private FrameLayout mImageFrame;
    private TextView mItemDesc, mItemPrice;
    private RecyclerView mItemImage;


    public AdapterViewItemLookup(Context context) {
        super(context);
        init(context);
    }

    public AdapterViewItemLookup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Helper method to initialize the view group and the widget
     * @param context   Activity context
     */
    private void init(Context context) {


        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.adapter_view_item_lookup, this);

        // Initialize the view group and widgets
        mRoot = (LinearLayout) mRootView.findViewById(R.id.root);
        mImageFrame = (FrameLayout) mRootView.findViewById(R.id.image_frame);
        mItemImage = (RecyclerView) mRootView.findViewById(R.id.item_image);
        mSubTextDetail = (LinearLayout) mRootView.findViewById(R.id.sub_text_detail);
        mItemDesc = (TextView) mRootView.findViewById(R.id.item_desc);
        mItemPrice = (TextView) mRootView.findViewById(R.id.item_price);

    }

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

    public FrameLayout getmImageFrame() {
        return mImageFrame;
    }

    public void setmImageFrame(FrameLayout mImageFrame) {
        this.mImageFrame = mImageFrame;
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

    public RecyclerView getmItemImage() {
        return mItemImage;
    }

    public void setmItemImage(RecyclerView mItemImage) {
        this.mItemImage = mItemImage;
    }
}
