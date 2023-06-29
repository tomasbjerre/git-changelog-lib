package se.bjurr.gitchangelog.internal.issues;

import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory.getGitHubService;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.NOISSUE;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.REDMINE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubHelper;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubIssue;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubLabel;
import se.bjurr.gitchangelog.internal.integrations.gitlab.GitLabClient;
import se.bjurr.gitchangelog.internal.integrations.gitlab.GitLabIssue;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClient;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraIssue;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClient;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClientFactory;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineIssue;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.settings.IssuesUtil;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class IssueParser {

  private static final Logger LOG = getLogger(IssueParser.class);

  private final List<GitCommit> commits;
  private final Settings settings;

  public IssueParser(final Settings settings, final List<GitCommit> commits) {
    this.settings = settings;
    this.commits = commits;
  }

  public Settings getSettings() {
    return this.settings;
  }

  public List<GitCommit> getCommits() {
    return this.commits;
  }

  public List<ParsedIssue> parseForIssues(final boolean useIntegrations) {
    final Map<String, ParsedIssue> parsedIssuePerIssue = new HashMap<>();
    final GitHubHelper gitHubHelper = useIntegrations ? this.createGitHubClient() : null;
    final JiraClient jiraClient = useIntegrations ? this.createJiraClient() : null;
    final RedmineClient redmineClient = useIntegrations ? this.createRedmineClient() : null;
    final GitLabClient gitLabClient = useIntegrations ? this.createGitLabClient() : null;
    final List<SettingsIssue> patterns = new IssuesUtil(this.settings).getIssues();

    for (final GitCommit gitCommit : this.commits) {
      boolean commitMappedToAtLeastOneIssue = false;
      for (final SettingsIssue issuePattern : patterns) {
        final Matcher issueMatcher =
            compile(issuePattern.getPattern()).matcher(gitCommit.getMessage());
        while (issueMatcher.find()) {
          final String matchedIssue = issueMatcher.group();
          if (matchedIssue.isEmpty()) {
            continue;
          }
          if (!parsedIssuePerIssue.containsKey(matchedIssue)) {
            ParsedIssue parsedIssue = null;
            if (issuePattern.getType() == GITHUB) {
              parsedIssue = this.createParsedIssue(gitHubHelper, issuePattern, matchedIssue);
            } else if (issuePattern.getType() == GITLAB) {
              final String projectName = this.settings.getGitLabProjectName().get();
              parsedIssue =
                  this.createParsedIssue(gitLabClient, projectName, issuePattern, matchedIssue);
            } else if (issuePattern.getType() == JIRA) {
              parsedIssue = this.createParsedIssue(jiraClient, issuePattern, matchedIssue);
            } else if (issuePattern.getType() == REDMINE) {
              parsedIssue = this.createParsedIssue(redmineClient, issuePattern, matchedIssue);
            } else {
              parsedIssue = this.createParsedIssue(issuePattern, issueMatcher, matchedIssue);
            }
            parsedIssuePerIssue.put(matchedIssue, parsedIssue);
          }
          if (!parsedIssuePerIssue.get(matchedIssue).getGitCommits().contains(gitCommit)) {
            parsedIssuePerIssue.get(matchedIssue).getGitCommits().add(gitCommit);
          }
          commitMappedToAtLeastOneIssue = true;
        }
      }
      if (!commitMappedToAtLeastOneIssue && !this.settings.ignoreCommitsWithoutIssue()) {
        final String issue = null;
        final String link = null;
        final String title = null;
        final String issueType = null;
        final List<String> linkedIssues = null;
        final List<String> labels = null;
        final Map<String, Object> additionalFields = new HashMap<>();

        final ParsedIssue noIssue =
            new ParsedIssue(
                NOISSUE,
                this.settings.getNoIssueName(),
                issue,
                "",
                link,
                title,
                issueType,
                linkedIssues,
                labels,
                additionalFields);
        if (!parsedIssuePerIssue.containsKey(noIssue.getName())) {
          parsedIssuePerIssue.put(noIssue.getName(), noIssue);
        }
        parsedIssuePerIssue.get(noIssue.getName()).addCommit(gitCommit);
      }
    }
    return parsedIssuePerIssue.values().stream()
        .sorted((a, b) -> a.toString().compareTo(b.toString()))
        .collect(toList());
  }

  private ParsedIssue createParsedIssue(
      final GitLabClient gitLabClient,
      final String projectName,
      final SettingsIssue issuePattern,
      String matchedIssueString) {
    String link = "";
    String title = "";
    final List<String> linkedIssues = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    final Map<String, Object> additionalFields = new HashMap<>();
    if (matchedIssueString.startsWith("#")) {
      matchedIssueString = matchedIssueString.substring(1);
    }
    final Integer matchedIssue = Integer.parseInt(matchedIssueString);
    try {
      if (gitLabClient != null && gitLabClient.getIssue(projectName, matchedIssue).isPresent()) {
        final GitLabIssue gitLabIssue = gitLabClient.getIssue(projectName, matchedIssue).get();
        link = gitLabIssue.getLink();
        title = gitLabIssue.getTitle();
        labels = gitLabIssue.getLabels();
      }
    } catch (final GitChangelogIntegrationException e) {
      LOG.error(matchedIssueString, e);
    }
    final String issueType = null;
    return new ParsedIssue( //
        GITLAB, //
        issuePattern.getName(), //
        matchedIssueString, //
        "",
        link, //
        title, //
        issueType, //
        linkedIssues, //
        labels,
        additionalFields);
  }

  private GitLabClient createGitLabClient() {
    GitLabClient client = null;
    if (this.settings.getGitLabServer().isPresent()) {
      final String server = this.settings.getGitLabServer().get();
      final String token = this.settings.getGitLabToken().orElse(null);
      client = new GitLabClient(server, token);
    }
    return client;
  }

  private JiraClient createJiraClient() {
    JiraClient jiraClient = null;
    if (this.settings.getJiraServer().isPresent()) {
      jiraClient = JiraClientFactory.createJiraClient(settings.getJiraServer().get());
      if (this.settings.getJiraUsername().isPresent()) {
        jiraClient.withBasicCredentials(
            this.settings.getJiraUsername().get(), this.settings.getJiraPassword().get());
      } else if (this.settings.getJiraToken().isPresent()) {
        jiraClient.withTokenCredentials(this.settings.getJiraToken().get());
      } else if (this.settings.getJiraBearer().isPresent()) {
        jiraClient.withBearer(this.settings.getJiraBearer().get());
      }
      if (this.settings.getExtendedRestHeaders() != null) {
        jiraClient.withHeaders(this.settings.getExtendedRestHeaders());
      }
      if (!settings.getJiraIssueFieldFilters().isEmpty()) {
        jiraClient.withIssueFieldFilters(settings.getJiraIssueFieldFilters());
      }
      if (!settings.getJiraIssueAdditionalFields().isEmpty()) {
        jiraClient.withIssueAdditionalFields(settings.getJiraIssueAdditionalFields());
      }
    }
    return jiraClient;
  }

  private RedmineClient createRedmineClient() {
    RedmineClient redmineClient = null;
    if (this.settings.getRedmineServer().isPresent()) {
      redmineClient =
          RedmineClientFactory.createRedmineClient(this.settings.getRedmineServer().get());
      if (this.settings.getRedmineUsername().isPresent()) {
        redmineClient.withBasicCredentials(
            this.settings.getRedmineUsername().get(), this.settings.getRedminePassword().get());
      } else if (this.settings.getRedmineToken().isPresent()) {
        redmineClient.withTokenCredentials(this.settings.getRedmineToken().get());
      }
      if (this.settings.getExtendedRestHeaders() != null) {
        redmineClient.withHeaders(this.settings.getExtendedRestHeaders());
      }
    }
    return redmineClient;
  }

  private GitHubHelper createGitHubClient() {
    if (this.settings.getGitHubApi().isPresent()) {
      return new GitHubHelper(
          getGitHubService(this.settings.getGitHubApi().get(), this.settings.getGitHubToken()));
    }
    return null;
  }

  private ParsedIssue createParsedIssue(
      final SettingsIssue issuePattern, final Matcher issueMatcher, final String matchedIssue) {
    final String link = this.render(issuePattern.getLink().orElse(""), issueMatcher, matchedIssue);
    final String title =
        this.render(issuePattern.getTitle().orElse(""), issueMatcher, matchedIssue);
    final String issueType = null;
    final List<String> linkedIssues = null;
    final List<String> labels = null;
    final Map<String, Object> additionalFields = new HashMap<>();
    return new ParsedIssue( //
        CUSTOM, //
        issuePattern.getName(), //
        matchedIssue, //
        "",
        link, //
        title, //
        issueType, //
        linkedIssues, //
        labels,
        additionalFields);
  }

  private ParsedIssue createParsedIssue(
      final JiraClient jiraClient, final SettingsIssue issuePattern, final String matchedIssue) {
    String link = "";
    String title = "";
    String desc = "";
    String issueType = null;
    List<String> linkedIssues = null;
    List<String> labels = null;
    Map<String, Object> additionalFields = null;
    try {
      if (jiraClient != null && jiraClient.getIssue(matchedIssue).isPresent()) {
        final JiraIssue jiraIssue = jiraClient.getIssue(matchedIssue).get();
        link = jiraIssue.getLink();
        title = jiraIssue.getTitle();
        issueType = jiraIssue.getIssueType();
        linkedIssues = jiraIssue.getLinkedIssues();
        labels = jiraIssue.getLabels();
        desc = jiraIssue.getDescription();
        additionalFields = jiraIssue.getAdditionalFields();
      }
    } catch (final GitChangelogIntegrationException e) {
      LOG.error(matchedIssue, e);
    }
    return new ParsedIssue( //
        JIRA, //
        issuePattern.getName(), //
        matchedIssue, //
        desc,
        link, //
        title, //
        issueType, //
        linkedIssues,
        labels,
        additionalFields);
  }

  private ParsedIssue createParsedIssue(
      final RedmineClient redmineClient,
      final SettingsIssue issuePattern,
      final String matchedIssue) {
    String link = "";
    String title = "";
    String desc = "";
    String issueType = null;
    final List<String> linkedIssues = null;
    final List<String> labels = null;
    final Map<String, Object> additionalFields = new HashMap<>();
    try {
      if (redmineClient != null && redmineClient.getIssue(matchedIssue).isPresent()) {
        final RedmineIssue redmineIssue = redmineClient.getIssue(matchedIssue).get();
        link = redmineIssue.getLink();
        title = redmineIssue.getTitle();
        issueType = redmineIssue.getIssueType();
        desc = redmineIssue.getDescription();
      }
    } catch (final GitChangelogIntegrationException e) {
      LOG.error(matchedIssue, e);
    }
    return new ParsedIssue( //
        REDMINE, //
        issuePattern.getName(), //
        matchedIssue, //
        desc,
        link, //
        title, //
        issueType, //
        linkedIssues,
        labels,
        additionalFields);
  }

  private ParsedIssue createParsedIssue(
      final GitHubHelper gitHubHelper,
      final SettingsIssue issuePattern,
      final String matchedIssue) {
    String link = "";
    String title = "";
    final List<String> linkedIssues = new ArrayList<>();
    final List<String> labels = new ArrayList<>();
    final Map<String, Object> additionalFields = new HashMap<>();
    try {
      if (gitHubHelper != null) {
        final java.util.Optional<GitHubIssue> issues = gitHubHelper.getIssueFromAll(matchedIssue);
        if (issues.isPresent()) {
          final GitHubIssue gitHubIssue = issues.get();
          link = gitHubIssue.getLink();
          title = gitHubIssue.getTitle();
          for (final GitHubLabel label : gitHubIssue.getLabels()) {
            labels.add(label.getName());
          }
        }
      }
    } catch (final GitChangelogIntegrationException e) {
      LOG.error(matchedIssue, e);
    }
    final String issueType = null;
    return new ParsedIssue( //
        GITHUB, //
        issuePattern.getName(), //
        matchedIssue, //
        "",
        link, //
        title, //
        issueType, //
        linkedIssues, //
        labels,
        additionalFields);
  }

  private String render(String string, final Matcher matcher, final String matched) {
    string = string.replaceAll("\\$\\{PATTERN_GROUP\\}", matched);
    for (int i = 0; i <= matcher.groupCount(); i++) {
      string =
          string.replaceAll(
              "\\$\\{PATTERN_GROUP_" + i + "\\}", this.firstNonNull(matcher.group(i), ""));
    }
    return string;
  }

  private String firstNonNull(final String a, final String b) {
    if (a == null) {
      return b;
    }
    return a;
  }
}
