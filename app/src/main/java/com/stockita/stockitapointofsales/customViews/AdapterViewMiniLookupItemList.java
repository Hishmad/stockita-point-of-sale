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
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * This class is a custom view for the mini lookup item list card
 */
public class AdapterViewMiniLookupItemList extends LinearLayout {

    // Member variables
    private LinearLayout mRoot;
    private TextView mItemNumberLookup, mItemDescLookup, mItemPriceLookup;

    /**
     * Constructor
     * @param context
     */
    public AdapterViewMiniLookupItemList(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public AdapterViewMiniLookupItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Helper method to initialize the view group and the widget
     * @param context   Activity context
     */
    private void init(Context context) {


        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.adapter_view_mini_lookup, this);

        // Initialize the view group and widgets
        mRoot = (LinearLayout) mRootView.findViewById(R.id.root);
        mItemNumberLookup = (TextView) mRootView.findViewById(R.id.item_number_lookup);
        mItemDescLookup = (TextView) mRootView.findViewById(R.id.item_desc_lookup);
        mItemPriceLookup = (TextView) mRootView.findViewById(R.id.item_price_lookup);

    }

    public LinearLayout getmRoot() {
        return mRoot;
    }

    public void setmRoot(LinearLayout mRoot) {
        this.mRoot = mRoot;
    }

    public TextView getmItemNumberLookup() {
        return mItemNumberLookup;
    }

    public void setmItemNumberLookup(TextView mItemNumberLookup) {
        this.mItemNumberLookup = mItemNumberLookup;
    }

    public TextView getmItemDescLookup() {
        return mItemDescLookup;
    }

    public void setmItemDescLookup(TextView mItemDescLookup) {
        this.mItemDescLookup = mItemDescLookup;
    }

    public TextView getmItemPriceLookup() {
        return mItemPriceLookup;
    }

    public void setmItemPriceLookup(TextView mItemPriceLookup) {
        this.mItemPriceLookup = mItemPriceLookup;
    }
}
