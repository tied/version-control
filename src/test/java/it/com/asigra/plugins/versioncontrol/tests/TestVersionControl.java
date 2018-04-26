package it.com.asigra.plugins.versioncontrol.tests;

import it.com.asigra.plugins.versioncontrol.pages.VersionControlPage;
import org.hamcrest.Matcher;
import org.junit.Test;
import com.atlassian.jira.tests.TestBase;
import com.atlassian.jira.pageobjects.config.SmartRestoreJiraData;

public class TestVersionControl extends TestBase{
//    @RestoreData("VersionControlFuncTest.xml")
    @Test
    public void testAdd1VersionTo1Project() {
        SmartRestoreJiraData smartRestoreJiraData = new SmartRestoreJiraData();
        smartRestoreJiraData.restore("VersionControlFuncTest.xml");
        VersionControlPage versionControlPage = jira().gotoLoginPage()
                .loginAsSysAdmin(VersionControlPage.class);
        versionControlPage.selectProject("Alpha");
        versionControlPage.selectVersion("v1");
        versionControlPage.getButtonAdd().click();
        //check confirmMessage
        //click ok
        //check log message
//        Poller.waitUntil(versionControlPage.getUserEmailSuffix(),
//                (Matcher<String>) equalTo("atlassian.com"));
    }
}