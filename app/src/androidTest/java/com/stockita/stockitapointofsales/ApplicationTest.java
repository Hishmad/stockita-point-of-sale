package com.stockita.stockitapointofsales;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.stockita.stockitapointofsales.utilities.Utility;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);

    }


    public void testIsValidEmail() {

        boolean actual = Utility.isEmailValid("hishmad@yahoo.com");

        boolean expectedResult = true;

        assertEquals(true, actual);

    }

}