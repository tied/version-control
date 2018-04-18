package com.asigra.plugins.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.Enumeration;
//import java.lang.Long;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Maps;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.jira.user.ApplicationUser;
//import com.atlassian.crowd.embedded.api.User;

//import com.atlassian.confluence.core.ConfluenceActionSupport;

@Component
public class VersionControl extends HttpServlet{
    @ComponentImport
    private TemplateRenderer templateRenderer;

    @ComponentImport
    private final I18nResolver i18nResolver;

    @ComponentImport
    private VersionManager versionManager;

    @ComponentImport
    private VersionService versionService;

    @ComponentImport
    private UserManager userManager;

    @ComponentImport
    private com.atlassian.jira.user.util.UserManager jiraUserManager;

    private static final String UI_TEMPLATE = "/templates/versioncontrol.vm";

    private static final Logger log = LoggerFactory.getLogger(VersionControl.class);

    @Autowired
    public VersionControl(TemplateRenderer templateRenderer, I18nResolver i18nResolver, VersionManager versionManager,
                          VersionService versionService, UserManager userManager, com.atlassian.jira.user.util.UserManager jiraUserManager) {
        this.templateRenderer = templateRenderer;
        this.i18nResolver = i18nResolver;
        this.versionManager = versionManager;
        this.versionService = versionService;
        this.userManager = userManager;
        this.jiraUserManager = jiraUserManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

            // Create an empty context map to pass into the render method
            Map<String, Object> context = Maps.newHashMap();
            // Make sure to set the contentType otherwise bad things happen
            resp.setContentType("text/html;charset=utf-8");
            context.put("title", i18nResolver.getText("version-control.addpage.title"));
            context.put("versions", getAllVersions()); //REPLACE WITH UNIQUE VERSION SET!
            // Render the velocity template. Since the template doesn't need to render any dynamic content, we just pass it an empty context
            templateRenderer.render(UI_TEMPLATE, context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map params = req.getParameterMap();
        String[] versions = req.getParameterValues("versions");
        String[] projects = req.getParameterValues("projects");
        Collection<Version> versionsToAdd = new HashSet();
        ApplicationUser user = getCurrentUser(req);

        for (String version : versions) {
            // get version object by id and put it into a collection or array
            versionsToAdd.add(versionManager.getVersion(Long.parseLong(version)));
        }
        for (String project : projects) {
            for (Version version : versionsToAdd) {
                VersionBuilder versionBuilder = versionService.newVersionBuilder(version);
                versionBuilder.projectId(Long.parseLong(project));
                VersionService.VersionBuilderValidationResult result = versionService.validateCreate(user, versionBuilder);
//                try {
//
////                    versionManager.createVersion(version.getName(), version.getStartDate(), version.getReleaseDate(), version.getDescription(), Long.parseLong(project), null, version.isReleased());
//                } catch (CreateException e) {
//                    log.error("CreateException: " + e.getMessage());
//                }
                if (result.getErrorCollection().hasAnyErrors()) {
                    // If the validation fails, we re-render the edit page with the errors in the context
                    Map<String, Object> context = Maps.newHashMap();
//                    context.put("issue", issue);
                    context.put("errors", result.getErrorCollection().getErrors());
                    resp.setContentType("text/html;charset=utf-8");
                    templateRenderer.render(UI_TEMPLATE, context, resp.getWriter());
                } else {
                    // If the validation passes, we perform the update then redirect the user back to the
                    // page with the list of issues
                    versionService.create(user, result);
                    resp.sendRedirect("versioncontrol");
                }
            }
        }
    }

    //get unique versions from the global list
    private Collection<Version> getAllVersions() {
        Collection<Version> versions = versionManager.getAllVersions();
        Collection<Version> uniqueVersions = new HashSet();
        boolean found;
        //filter out the duplicates
        for (Version version : versions) {
            found = false;
            for (Version uniqueVersion : uniqueVersions) {
                if(version.getName().equals(uniqueVersion.getName())){
                    found = true;
                    break;
                }
            }
            //if version is not in uniqueVersion list, then add it to it
            if(!found){
                uniqueVersions.add(version);
            }
        }
        return uniqueVersions;
    }


    private ApplicationUser getCurrentUser(HttpServletRequest req) {
        // To get the current user, we first get the username from the session.
        // Then we pass that over to the jiraUserManager in order to get an
        // actual User object.
        return jiraUserManager.getUserByName(userManager.getRemoteUsername(req));
    }

}