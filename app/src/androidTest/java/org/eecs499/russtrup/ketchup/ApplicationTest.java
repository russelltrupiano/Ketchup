package org.eecs499.russtrup.ketchup;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testBasicXMLParse() {

        TvApi tvapi = new TvApi();

        tvapi.execute("http://services.tvrage.com/feeds/full_search.php?show=Better Call Saul");

        assertTrue(true);
    }
}