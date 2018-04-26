package it.com.asigra.plugins.versioncontrol.pages;
import com.atlassian.pageobjects.elements.ElementBy;
import com.atlassian.pageobjects.elements.PageElement;
import com.atlassian.pageobjects.elements.SelectElement;
import com.atlassian.pageobjects.elements.Options;
import com.atlassian.pageobjects.elements.query.TimedCondition;

import com.atlassian.jira.pageobjects.pages.AbstractJiraPage;

public class VersionControlPage extends AbstractJiraPage {
    @ElementBy(id = "projects")
    protected SelectElement projectsList;

    @ElementBy(id = "versions")
    protected SelectElement versionsList;

    @ElementBy(id = "button-add")
    protected PageElement buttonAdd;

    @ElementBy(id = "button-archive")
    protected PageElement buttonArchive;

    @ElementBy(id = "button-unarchive")
    protected PageElement buttonUnarchive;

    @ElementBy(id = "button-delete")
    protected PageElement buttonDelete;

    @ElementBy(id = "button-cancel")
    protected PageElement buttonCancel;

    @ElementBy(id = "dialog-submit-button")
    protected PageElement buttonSubmit;

    @ElementBy(id = "dialog-close-button")
    protected PageElement buttonClose;

    @ElementBy(id = "confirm-message")
    protected PageElement confirmMessage;

    public PageElement getButtonSubmit() {
        return buttonSubmit;
    }

    public PageElement getButtonClose() {
        return buttonClose;
    }

    public SelectElement getProjectsList() {
        return projectsList;
    }

    public SelectElement getVersionsList() {
        return versionsList;
    }

    public PageElement getButtonAdd() {
        return buttonAdd;
    }

    public PageElement getButtonArchive() {
        return buttonArchive;
    }

    public PageElement getButtonUnarchive() {
        return buttonUnarchive;
    }

    public PageElement getButtonDelete() {
        return buttonDelete;
    }

    public PageElement getButtonCancel() {
        return buttonCancel;
    }

    public void selectProject(String projectName)
    {
        projectsList.select(Options.value(projectName)); // you can also build option representations for text or id
    }

    public void selectVersion(String versionName)
    {
        versionsList.select(Options.value(versionName)); // you can also build option representations for text or id
    }

    @Override
    public TimedCondition isAt() {
        return projectsList.timed().isPresent();
    }

    @Override
    public String getUrl() {
        return "/plugins/servlet/versioncontrol";
    }
}
