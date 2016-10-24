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

package com.stockita.stockitapointofsales.interfaces;

import com.stockita.stockitapointofsales.data.ItemModel;

/**
 * This interface has two method is to communicate between the listFragment and the dialog fragment
 * via main activity and back again.
 */
public interface SalesDetailPendingCallbacks {


    // This method from adapter to MainActivity
    void onSalesEditDialogCallbacks(int requestCode, String userUid, String pushKeyDetail, String pushKeyHeader, Object model);

    // This will be used by lookup fragment to pass item master data to the caller
    void sendItemMasterData(String itemMasterPushKey, ItemModel model);

    // This is for the checkout dialog
    void getSalesCheckoutDialog(String userUid, String total);
}
