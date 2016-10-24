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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * This is a custom view class that combines views for the layout of the
 * sales detail card, this card will be used to element in the recycler view
 */
public class AdapterViewSalesDetailList extends LinearLayout {


    // View
    private LinearLayout mRoot;
    private TextView mItemDesc, mItemQuantity, mItemPrice, mItemAmount;
    private ImageButton mItemPopupMenu;
    private PopupMenu mPopupMenu;




    /**
     * Constructor
     * @param context       Activity context
     */
    public AdapterViewSalesDetailList(Context context) {
        super(context);
        initView(context);
    }

    
    /**
     * Constructor
     * @param context       Activity context
     * @param attrs         Attr
     */
    public AdapterViewSalesDetailList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    /**
     * Helper method to initialize all the views and widget
     * @param context       Activity context
     */
    private void initView(Context context) {

        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.adapter_view_sales_detail_list, this);

        mRoot = (LinearLayout) mRootView.findViewById(R.id.sales_detail_linear_layout);
        mItemDesc = (TextView) mRootView.findViewById(R.id.item_desc);
        mItemQuantity = (TextView) mRootView.findViewById(R.id.item_quantity);
        mItemPrice = (TextView) mRootView.findViewById(R.id.item_price);
        mItemAmount = (TextView) mRootView.findViewById(R.id.item_amount);
        mItemPopupMenu = (ImageButton) mRootView.findViewById(R.id.item_popup_menu);


    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Initialize the PopupMenu object, and pass the View object in the second argument
        mPopupMenu = new PopupMenu(getContext(), mItemPopupMenu);

        // Get the menu inflater to inflate the xml file
        mPopupMenu.getMenuInflater().inflate(R.menu.popup_menu_adapter_view_sales_detail, mPopupMenu.getMenu());

        /**
         * Now we need to add a click listener on the view object, so when the user click
         * this view object the menu will popup, this can be done using show() method.
         */
        mItemPopupMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Invoke show() to popup the menu.
                mPopupMenu.show();
            }
        });
    }


    public LinearLayout getmRoot() {
        return mRoot;
    }

    public void setmRoot(LinearLayout mRoot) {
        this.mRoot = mRoot;
    }

    public TextView getmItemDesc() {
        return mItemDesc;
    }

    public void setmItemDesc(TextView mItemDesc) {
        this.mItemDesc = mItemDesc;
    }

    public TextView getmItemQuantity() {
        return mItemQuantity;
    }

    public void setmItemQuantity(TextView mItemQuantity) {
        this.mItemQuantity = mItemQuantity;
    }

    public TextView getmItemPrice() {
        return mItemPrice;
    }

    public void setmItemPrice(TextView mItemPrice) {
        this.mItemPrice = mItemPrice;
    }

    public TextView getmItemAmount() {
        return mItemAmount;
    }

    public void setmItemAmount(TextView mItemAmount) {
        this.mItemAmount = mItemAmount;
    }

    public ImageButton getmItemPopupMenu() {
        return mItemPopupMenu;
    }

    public void setmItemPopupMenu(ImageButton mItemPopupMenu) {
        this.mItemPopupMenu = mItemPopupMenu;
    }

    public PopupMenu getmPopupMenu() {
        return mPopupMenu;
    }

    public void setmPopupMenu(PopupMenu mPopupMenu) {
        this.mPopupMenu = mPopupMenu;
    }
}
