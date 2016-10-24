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

package com.stockita.stockitapointofsales.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Helper class to convert time in millis to other form or format
 */
public class ManageDateTime {

    // State
    private long currentTimeMillis;
    private long givenTimeMillis;

    /**
     * Constructor
     * @param timestamp         any time in long millis
     */
    public ManageDateTime(long timestamp) {
        this.givenTimeMillis = timestamp;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public long getGivenTimeMillis() {
        return givenTimeMillis;
    }

    public void setGivenTimeMillis(long givenTimeMillis) {
        this.givenTimeMillis = givenTimeMillis;
    }


    /**
     * This will convert from long millis to int year
     * @return              year in type int.
     */
    public int getGivenTimeMillisYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimeMillis);

        return calendar.get(Calendar.YEAR);
    }


    /**
     * This method will convert from long millis to int month
     * @return              month in type int.
     */
    public int getGivenTimeMillisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimeMillis);

        return calendar.get(Calendar.MONTH);
    }


    /**
     * This method will convert from long millis to int day
     * @return              day in type int.
     */
    public int getGivenTimeMillisDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimeMillis);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get the full formated date and time in string
     * @return              full formated date and time
     */
    public String getGivenTimeMillisInDateFormat() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimeMillis);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

        return dateFormat.format(calendar.getTime());
    }


    /**
     * Get the year and month format
     * @return          year and month in string
     */
    public String getGivenTimeMillisInYearMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimeMillis);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMM");

        return dateFormat.format(calendar.getTime());
    }
}
