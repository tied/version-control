package it.com.asigra.plugins.servlet;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import com.atlassian.jira.functest.framework.FuncTestCase;
import com.atlassian.jira.functest.framework.locator.WebPageLocator;

import java.io.IOException;

import static org.junit.Assert.*;


public class VersionControlFuncTest extends FuncTestCase{

    HttpClient httpClient;
    String baseUrl;
    String servletUrl;

    @Before
    public void setUpTest() {
        /* - Scheme -
         * Alpha - v2
         * Demo - v1
         */

        httpClient = new DefaultHttpClient();
        baseUrl = System.getProperty("baseurl");
//        baseUrl = "http://localhost:2990/jira";
        servletUrl = "/plugins/servlet/versioncontrol";
        administration.restoreData("VersionControlFuncTest.xml");
    }

    @After
    public void tearDownTest() {
        httpClient.getConnectionManager().shutdown();
    }

    /**
     * This particular test checks that the webwork action included in this plugin provides the correct information and
     * error messages.
     */

    @Test
    public void testAccess()
    {
        navigation.logout();
        navigation.gotoPage(servletUrl);
        text.assertTextPresent(new WebPageLocator(tester),
                "You must log in to access this page.");
        navigation.login("admin", "admin");
        navigation.gotoPage(servletUrl);
        text.assertTextPresent(new WebPageLocator(tester),
                "Version Control - Manage Versions");
    }

    @Test
    public void testAdd1VersionTo1Project()
    {
        navigation.gotoPage(servletUrl);
        text.assertTextPresent(new WebPageLocator(tester),
                "Version Control - Manage Versions");
        form.selectOption("projects", "Alpha");
        form.selectOption("versions", "v1");
        tester.clickButton("button-add");
        text.assertTextPresent(new WebPageLocator(tester),
                "Are you sure you want to add these versions?");
        tester.clickButton("dialog-submit-button");
        text.assertTextPresent(new WebPageLocator(tester),
                "[Alpha - v1]: Version was created successfully.");
    }

    public void testAdd1ExistingVersionTo1Project()
    {
        testAdd1VersionTo1Project();

        text.assertTextPresent(new WebPageLocator(tester),
                "Version Control - Manage Versions");
        form.selectOption("projects", "Alpha");
        form.selectOption("versions", "v1");
        tester.clickButton("button-add");
        text.assertTextPresent(new WebPageLocator(tester),
                "Are you sure you want to add these versions?");
        tester.clickButton("dialog-submit-button");
        text.assertTextPresent(new WebPageLocator(tester),
                "[Alpha - v1]: A version with this name already exists in this project.");
    }

}
