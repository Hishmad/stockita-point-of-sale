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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * This is a custom view class that combines views for the layout of the
 * user list card, this card will be used to element in the recycler view
 * that bind data for the UserModel from /users/...
 */
public class AdapterViewUsersList extends LinearLayout {

    // Members Views
    private LinearLayout mUsersLinearLayoutCustom;
    private TextView mUsersEmailCustom, mUsersDateCreatedCustom, mUsersUidCustom, mUserNameCustom, mUsersStatusCustom;
    private ImageView mPopupMenuViewObject;
    private PopupMenu mPopupMenu;


    /**
     * Constructor
     * @param context       Activity context
     */
    public AdapterViewUsersList(Context context) {
        super(context);
        initView(context);
    }


    /**
     * Constructor
     * @param context       Activity context
     * @param attrs         AttributeSet object
     */
    public AdapterViewUsersList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    /**
     * Method to initialize all the view object
     * @param context       Activity context
     */
    private void initView(Context context) {

        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.adapter_view_users_list, this);

        // Initialize the view group and widgets
        mUsersLinearLayoutCustom = (LinearLayout) mRootView.findViewById(R.id.users_linear_layout_custom);
        mUsersEmailCustom = (TextView) mRootView.findViewById(R.id.users_email_custom);
        mPopupMenuViewObject = (ImageView) mRootView.findViewById(R.id.contextMenu);
        mUsersDateCreatedCustom = (TextView) mRootView.findViewById(R.id.users_date_created_custom);
        mUsersUidCustom = (TextView) mRootView.findViewById(R.id.users_uid_custom);
        mUserNameCustom = (TextView) mRootView.findViewById(R.id.user_name_custom);
        mUsersStatusCustom = (TextView) mRootView.findViewById(R.id.users_status_custom);

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
     * The code below is the getter and setter...
     */


    public LinearLayout getmUsersLinearLayoutCustom() {
        return mUsersLinearLayoutCustom;
    }


    public TextView getmUsersEmailCustom() {
        return mUsersEmailCustom;
    }

    public void setmUsersEmailCustom(String mUsersEmailCustom) {
        this.mUsersEmailCustom.setText(mUsersEmailCustom);
    }

    public PopupMenu getmPopupMenu() {
        return mPopupMenu;
    }

    public void setmPopupMenu(PopupMenu mPopupMenu) {
        this.mPopupMenu = mPopupMenu;
    }

    public TextView getmUsersDateCreatedCustom() {
        return mUsersDateCreatedCustom;
    }

    public ImageView getmPopupMenuViewObject() {
        return mPopupMenuViewObject;
    }

    public void setmPopupMenuViewObject(ImageView mPopupMenuViewObject) {
        this.mPopupMenuViewObject = mPopupMenuViewObject;
    }

    public void setmUsersDateCreatedCustom(String mUsersDateCreatedCustom) {
        this.mUsersDateCreatedCustom.setText(mUsersDateCreatedCustom);
    }

    public TextView getmUsersUidCustom() {
        return mUsersUidCustom;
    }

    public void setmUsersUidCustom(String mUsersUidCustom) {
        this.mUsersUidCustom.setText(mUsersUidCustom);
    }

    public TextView getmUserNameCustom() {
        return mUserNameCustom;
    }

    public void setmUserNameCustom(String mUserNameCustom) {
        this.mUserNameCustom.setText(mUserNameCustom);
    }

    public TextView getmUsersStatusCustom() {
        return mUsersStatusCustom;
    }

    public void setmUsersStatusCustom(String mUsersStatusCustom) {
        this.mUsersStatusCustom.setText(mUsersStatusCustom);
    }
}
