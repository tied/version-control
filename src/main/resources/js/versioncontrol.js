AJS.toInit(function() {
    AJS.$.ajax('/jira/rest/api/2/project', {
        dataType : 'json',
    }).done(function(res){
        var projSelect = AJS.$('#projects');
        AJS.$.each(res, function(idx, item){
            projSelect.append('<option value="' + item.id +'">' + item.name + '</option>');
        });
        //projSelect.auiSelect2();
    });

    // Hides the dialog
    AJS.$("#dialog-close-button").click(function(e) {
        e.preventDefault();
        AJS.dialog2("#demo-dialog").hide();
        AJS.$("#action").val("");
    });

    // Hides the dialog
    AJS.$("#dialog-submit-button").click(function(e) {
        e.preventDefault();
        AJS.dialog2("#demo-dialog").hide();
        AJS.$("#admin").submit();
    });
});

// Shows the dialog when an action button is clicked
function dialog(action){
    var valid = true;
    if(!AJS.$("#versions").val()){
        valid = false;
        //AJS.$("#versions").prop("data-aui-notification-field");
        AJS.$("#versions-error").text("Please select a value");
    }
    else{
        AJS.$("#versions-error").text("");
    }
    if(!AJS.$("#projects").val()){
        valid = false;
        AJS.$("#projects-error").text("Please select a value");
    }
    else{
        AJS.$("#projects-error").text("");
    }
    if(valid){
        AJS.$('#affected-count').html("");
        if(action == "delete"){
            getAffectedIssueCount();
        }

        var template = AJS.$("#confirm-message-text").val();
        var pos = template.indexOf("*");
        var message = template.substr(0, pos) + "<b>" + action + "</b>" + template.substr(pos+1);

        AJS.$("#confirm-message").html(message);

        AJS.dialog2("#demo-dialog").show();
        AJS.$("#action").val(action);
    }
}

//get the number of issues affected by the deletion of the selected versions
function getAffectedIssueCount(){
    var count = 0;
    var affected = AJS.$('#affected-count');
    affected.html('<br><h3>Affected Issues:</h3><p>If you don\'t need another version swapped in in the affected issues, proceed with deletion here. Otherwise, resolve these conflicts through the tool that is activated when you manually delete a version in a project. This will allow you to re-map the versions in those issues to another version. </p>');
    var selectedVersions = getSelectValues(AJS.$("#versions"), "text");
    var selectedProjects = getSelectValues(AJS.$("#projects"), "value");
    var selectedProjectsLabels = getSelectValues(AJS.$("#projects"), "text");
    var projectMap = {};
    var projectHeadingList = {};

    selectedProjects.forEach(function (k, i) {
        projectMap[k] = selectedProjectsLabels[i];
    });

    var projectVersions = getProjectVersions(selectedProjects);

    if(!projectVersions){
        affected.html("<br>No issues will be affected");
        return;
    }

    generateProjectHeadings(affected, projectMap);

    //for each selected version
    AJS.$.each(selectedVersions, function(idx, version){
        //find the corresponding versions in projects
        var affectedVersionCopies = projectVersions.filter(function( obj ) {
          return obj.name == version;
        });
        //for each corresponding version in projects, get affected issues
        AJS.$.each(affectedVersionCopies, function(idx, affectedVersion){
            AJS.$.ajax('/jira/rest/api/2/version/'+affectedVersion.id+'/relatedIssueCounts', {
                dataType : 'json',
                async : false,
            }).done(function(res){
                var issuesAffected = res.issuesFixedCount + res.issuesAffectedCount;
                if(issuesAffected > 0){
                    projectHeadingList = affected.find("#"+affectedVersion.projectId).find("ul");
                    projectHeadingList.append("<li>Version <b>"+affectedVersion.name+"</b> is used <b>"+ issuesAffected +"</b> times in issues.</li>");
                    count++;
                }
            });
        });
    });
    if(count == 0){
        affected.html("<br>No issues will be affected");
    }
    else {
        cleanUpProjectHeadings(affected, projectMap);
    }
}

//generate project heading structure for every selected project
function generateProjectHeadings(el, map){
    AJS.$.each(map, function(id, name){
        el.append("<br><div id='"+id+"'><h4>"+name+"</h4><ul></ul></div>");
    });
}

//remove the project headings that do no have any content
function cleanUpProjectHeadings(el, map){
    AJS.$.each(map, function(id, name){
        var headingDiv = el.find("#"+id);
        if(headingDiv.find("ul").is(':empty')){
            headingDiv.html("");
        }
    });
}

//get list of versions for the specified projects
function getProjectVersions(selectedProjects){
    var projectVersions = [];
    AJS.$.each(selectedProjects, function(idx, project){
        AJS.$.ajax('/jira/rest/api/2/project/'+project+'/versions', {
            dataType : 'json',
            async: false
        }).done(function(res){
            projectVersions = projectVersions.concat(res);
        });
    });
    return projectVersions;
}

//get the selected values of a select list
function getSelectValues(selectElement, valueSelector) {
  var result = [];
  var options = selectElement && selectElement[0].options;
  var opt;

  for (var i=0, iLen=options.length; i<iLen; i++) {
    opt = options[i];

    if (opt.selected) {
        if(valueSelector == "text"){
          result.push(opt.text);
        }
        else if(valueSelector == "value"){
          result.push(opt.value);
        }
    }
  }
  return result;
}