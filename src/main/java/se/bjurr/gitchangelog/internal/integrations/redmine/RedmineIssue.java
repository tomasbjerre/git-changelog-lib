package se.bjurr.gitchangelog.internal.integrations.redmine;

public class RedmineIssue {
    private final String title;
    private final String link;
    private final String issue;
    private final String issueType;
    private final String description;
    

    public RedmineIssue(String title, String description, String link, String issue, String issueType ) {
        this.title = title;
        this.link = link;
        this.issue = issue;
        this.issueType = issueType;
        this.description = description;
    }

    public String getIssue() {
        return issue;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getDescription() {

        return description;
    }

    @Override
    public String toString() {
        return "RedmineIssue [title=" + title + ", link=" + link + ", issue=" + issue + ", issueType=" + issueType + "]";
    }
}
