package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.write;
import static com.google.common.io.Resources.getResource;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.git.GitRepoDataHelper.removeCommitsWithoutIssue;
import static se.bjurr.gitchangelog.internal.settings.Settings.fromFile;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.GitRepoData;
import se.bjurr.gitchangelog.internal.git.GitSubmoduleParser;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.integrations.mediawiki.MediaWikiClient;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.model.Transformer;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class GitChangelogApi {
  private static final Logger LOG = getLogger(GitChangelogApi.class);

  public static GitChangelogApi gitChangelogApiBuilder() {
    return new GitChangelogApi();
  }

  private Settings settings;

  private String templateContent;

  private GitChangelogApi() {
    this.settings = new Settings();
  }

  private GitChangelogApi(final Settings settings) {
    this.settings = settings;
  }

  /**
   * Get the changelog as data object.
   *
   * @param useIntegrationIfConfigured true if title/link/labels/issueType should be fetched from
   *     integrations (GitHub, GitLab, Jira) if that is configured.
   * @throws GitChangelogRepositoryException
   */
  public Changelog getChangelog(final boolean useIntegrationIfConfigured)
      throws GitChangelogRepositoryException {
    try (GitRepo gitRepo = new GitRepo(new File(this.settings.getFromRepo()))) {
      return getChangelog(gitRepo, useIntegrationIfConfigured);
    } catch (final IOException e) {
      throw new GitChangelogRepositoryException("", e);
    }
  }

  public Settings getSettings() {
    return this.settings;
  }

  /**
   * Get the changelog.
   *
   * @throws GitChangelogRepositoryException
   */
  public void render(final Writer writer) throws GitChangelogRepositoryException {
    final MustacheFactory mf = new DefaultMustacheFactory();
    final String templateContent = checkNotNull(getTemplateContent(), "No template!");
    final StringReader reader = new StringReader(templateContent);
    final Mustache mustache = mf.compile(reader, this.settings.getTemplatePath());
    try {
      final boolean useIntegrationIfConfigured = shouldUseIntegrationIfConfigured(templateContent);
      final Changelog changelog = this.getChangelog(useIntegrationIfConfigured);
      mustache
          .execute(
              writer, //
              new Object[] {changelog, this.settings.getExtendedVariables()} //
              )
          .flush();
    } catch (final IOException e) {
      // Should be impossible!
      throw new GitChangelogRepositoryException("", e);
    }
  }

  @VisibleForTesting
  static boolean shouldUseIntegrationIfConfigured(final String templateContent) {
    return templateContent.contains("{{type}}") //
        || templateContent.contains("{{link}}") //
        || templateContent.contains("{{title}}") //
        || templateContent.replaceAll("\\r?\\n", " ").matches(".*\\{\\{#?labels\\}\\}.*");
  }

  /** Get the changelog. */
  public String render() throws GitChangelogRepositoryException {
    final Writer writer = new StringWriter();
    render(writer);
    return writer.toString();
  }

  /**
   * Write changelog to file.
   *
   * @throws GitChangelogRepositoryException
   * @throws IOException When file cannot be written.
   */
  public void toFile(final File file) throws GitChangelogRepositoryException, IOException {
    createParentDirs(file);
    write(render().getBytes("UTF-8"), file);
  }

  /**
   * Create MediaWiki page with changelog.
   *
   * @throws GitChangelogRepositoryException
   * @throws GitChangelogIntegrationException
   */
  public void toMediaWiki(
      final String username, final String password, final String url, final String title)
      throws GitChangelogRepositoryException, GitChangelogIntegrationException {
    new MediaWikiClient(url, title, render()) //
        .withUser(username, password) //
        .createMediaWikiPage();
  }

  /**
   * Custom issues are added to support any kind of issue management, perhaps something that is
   * internal to your project. See {@link SettingsIssue}.
   */
  public GitChangelogApi withCustomIssue(
      final String name, final String pattern, final String link, final String title) {
    this.settings.addCustomIssue(new SettingsIssue(name, pattern, link, title));
    return this;
  }

  /** Format of dates, see {@link SimpleDateFormat}. */
  public GitChangelogApi withDateFormat(final String dateFormat) {
    this.settings.setDateFormat(dateFormat);
    return this;
  }

  /**
   * Extended variables is simply a key-value mapping of variables that are made available in the
   * template. Is used, for example, by the Bitbucket plugin to supply some internal variables to
   * the changelog context.
   */
  public GitChangelogApi withExtendedVariables(final Map<String, Object> extendedVariables) {
    this.settings.setExtendedVariables(extendedVariables);
    return this;
  }

  /**
   * Include all commits from here. Any commit hash. There is a constant pointing at the first
   * commit here: reference{GitChangelogApiConstants#ZERO_COMMIT}.
   */
  public GitChangelogApi withFromCommit(final String fromCommit) {
    this.settings.setFromCommit(fromCommit);
    return this;
  }

  /** Include all commits from here. Any tag or branch name. */
  public GitChangelogApi withFromRef(final String fromBranch) {
    this.settings.setFromRef(fromBranch);
    return this;
  }

  /** Folder where repo lives. */
  public GitChangelogApi withFromRepo(final String fromRepo) {
    this.settings.setFromRepo(fromRepo);
    return this;
  }

  /**
   * URL pointing at GitHub API. When configured, the {@link Issue#getTitle()} will be populated
   * with title from GitHub.<br>
   * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
   */
  public GitChangelogApi withGitHubApi(final String gitHubApi) {
    this.settings.setGitHubApi(gitHubApi);
    return this;
  }

  /** Pattern to recognize GitHub:s. <code>#([0-9]+)</code> */
  public GitChangelogApi withGitHubIssuePattern(final String gitHubIssuePattern) {
    this.settings.setGitHubIssuePattern(gitHubIssuePattern);
    return this;
  }

  /**
   * GitHub authentication token. Configure to avoid low rate limits imposed by GitHub in case you
   * have a lot of issues and/or pull requests.<br>
   * <br>
   * You can get one like this:<br>
   * <code>
   * curl -u 'yourgithubuser' -d '{"note":"Git Changelog Lib"}' https://api.github.com/authorizations
   * </code>
   */
  public GitChangelogApi withGitHubToken(final String gitHubToken) {
    this.settings.setGitHubToken(gitHubToken);
    return this;
  }

  /** Pattern to recognize GitHub:s. <code>#([0-9]+)</code> */
  public GitChangelogApi withGitLabIssuePattern(final String gitLabIssuePattern) {
    this.settings.setGitLabIssuePattern(gitLabIssuePattern);
    return this;
  }

  /**
   * In this URL: <code>https://gitlab.com/tomas.bjerre85/violations-test/issues</code> it would be
   * <code>violations-test</code>.
   */
  public GitChangelogApi withGitLabProjectName(final String gitLabProjectName) {
    this.settings.setGitLabProjectName(gitLabProjectName);
    return this;
  }

  /** Example: https://gitlab.com/ */
  public GitChangelogApi withGitLabServer(final String gitLabServer) {
    this.settings.setGitLabServer(gitLabServer);
    return this;
  }

  /** You can create it in the project settings page. */
  public GitChangelogApi withGitLabToken(final String gitLabToken) {
    this.settings.setGitLabToken(gitLabToken);
    return this;
  }

  /**
   * A regular expression that is evaluated on the commit message of each commit. If it matches, the
   * commit will be filtered out and not included in the changelog.<br>
   * <br>
   * To ignore tags creted by Maven and Gradle release plugins, perhaps you want this: <br>
   * <code>
   * ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
   * </code><br>
   * <br>
   * Remember to escape it, if added to the json-file it would look like this:<br>
   * <code>
   * ^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*
   * </code>
   */
  public GitChangelogApi withIgnoreCommitsWithMessage(final String ignoreCommitsIfMessageMatches) {
    this.settings.setIgnoreCommitsIfMessageMatches(ignoreCommitsIfMessageMatches);
    return this;
  }

  /**
   * A date that is evaluated on the author date of each commit. If the commit is older than the
   * point in time given, then it will be filtered out and not included in the changelog. <br>
   * See {@link SimpleDateFormat}.
   */
  public GitChangelogApi withIgnoreCommitsOlderThan(final Date ignoreCommitsIfOlderThan) {
    this.settings.setIgnoreCommitsIfOlderThan(ignoreCommitsIfOlderThan);
    return this;
  }

  /**
   * If a commit cannot be mapped to any issue, it can be added to the virtual " {@link
   * GitChangelogApi#withNoIssueName no issue}"-issue.<br>
   * <br>
   * True means that this issue will be created and populated.<br>
   * <br>
   * False means that it will not be created and commits that cannot be mapped to any issue will not
   * be included in the changelog.
   */
  public GitChangelogApi withIgnoreCommitsWithoutIssue(final boolean ignoreCommitsWithoutIssue) {
    this.settings.setIgnoreCommitsWithoutIssue(ignoreCommitsWithoutIssue);
    return this;
  }

  /**
   * A regular expression that is evaluated on each tag. If it matches, the tag will be filtered out
   * and not included in the changelog.
   */
  public GitChangelogApi withIgnoreTagsIfNameMatches(final String ignoreTagsIfNameMatches) {
    this.settings.setIgnoreTagsIfNameMatches(ignoreTagsIfNameMatches);
    return this;
  }

  /**
   * Pattern to recognize JIRA:s. <code>\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b</code><br>
   * <br>
   * Or escaped if added to json-file:<br>
   * <code>\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b</code>
   */
  public GitChangelogApi withJiraIssuePattern(final String jiraIssuePattern) {
    this.settings.setJiraIssuePattern(jiraIssuePattern);
    return this;
  }

  /** Authenticate to JIRA. */
  public GitChangelogApi withJiraPassword(final String string) {
    this.settings.setJiraPassword(string);
    return this;
  }

  /** Authenticate to JIRA. */
  public GitChangelogApi withJiraBasicAuthString(final String string) {
    this.settings.setJiraToken(string);
    return this;
  }

  /**
   * URL pointing at your JIRA server. When configured, the {@link Issue#getTitle()} will be
   * populated with title from JIRA.<br>
   * <code>https://jiraserver/jira</code>
   */
  public GitChangelogApi withJiraServer(final String jiraServer) {
    this.settings.setJiraServer(jiraServer);
    return this;
  }

  /** Authenticate to JIRA. */
  public GitChangelogApi withJiraUsername(final String string) {
    this.settings.setJiraUsername(string);
    return this;
  }

  /**
   * This is a "virtual issue" that is added to {@link Changelog#getIssues()}. It contains all
   * commits that has no issue in the commit comment. This could be used as a "wall of shame"
   * listing commiters that did not tag there commits with an issue.
   */
  public GitChangelogApi withNoIssueName(final String noIssueName) {
    this.settings.setNoIssueName(noIssueName);
    return this;
  }

  /**
   * Your tags may look something like <code>git-changelog-maven-plugin-1.6</code>. But in the
   * changelog you just want <code>1.6</code>. With this regular expression, the numbering can be
   * extracted from the tag name.<br>
   * <code>/([^/]+?)$</code>
   */
  public GitChangelogApi withReadableTagName(final String readableTagName) {
    this.settings.setReadableTagName(readableTagName);
    return this;
  }

  /**
   * If true, the changelog will not contain the issue in the commit comment. If your changelog is
   * grouped by issues, you may want this to be true. If not grouped by issue, perhaps false.
   */
  public GitChangelogApi withRemoveIssueFromMessageArgument(final boolean removeIssueFromMessage) {
    this.settings.setRemoveIssueFromMessage(removeIssueFromMessage);
    return this;
  }

  /** {@link Settings}. */
  public GitChangelogApi withSettings(final URL url) {
    this.settings = fromFile(url);
    return this;
  }

  /** {@link Settings}. */
  public GitChangelogApi withSettings(final Settings settings) {
    this.settings = settings;
    return this;
  }

  /** Use string as template. {@link #withTemplatePath}. */
  public GitChangelogApi withTemplateContent(final String templateContent) {
    this.templateContent = templateContent;
    return this;
  }

  /**
   * Path of template-file to use. It is a Mustache (https://mustache.github.io/) template. Supplied
   * with the context of {@link Changelog}.
   */
  public GitChangelogApi withTemplatePath(final String templatePath) {
    this.settings.setTemplatePath(templatePath);
    return this;
  }

  /**
   * When date of commits are translated to a string, this timezone is used.<br>
   * <code>UTC</code>
   */
  public GitChangelogApi withTimeZone(final String timeZone) {
    this.settings.setTimeZone(timeZone);
    return this;
  }

  /** Include all commits to here. Any commit hash. */
  public GitChangelogApi withToCommit(final String toCommit) {
    this.settings.setToCommit(toCommit);
    return this;
  }

  /**
   * Include all commits to this reference. Any tag or branch name. There is a constant for master
   * here: reference{GitChangelogApiConstants#REF_MASTER}.
   */
  public GitChangelogApi withToRef(final String toBranch) {
    this.settings.setToRef(toBranch);
    return this;
  }

  /**
   * Some commits may not be included in any tag. Commits that not released yet may not be tagged.
   * This is a "virtual tag", added to {@link Changelog#getTags()}, that includes those commits. A
   * fitting value may be "Next release".
   */
  public GitChangelogApi withUntaggedName(final String untaggedName) {
    this.settings.setUntaggedName(untaggedName);
    return this;
  }

  private Changelog getChangelog(final GitRepo gitRepo, final boolean useIntegrationIfConfigured)
      throws GitChangelogRepositoryException {
    final ObjectId fromId =
        getId(gitRepo, this.settings.getFromRef(), this.settings.getFromCommit()) //
            .or(gitRepo.getCommit(ZERO_COMMIT));
    final Optional<ObjectId> toIdOpt =
        getId(gitRepo, this.settings.getToRef(), this.settings.getToCommit());
    ObjectId toId;
    if (toIdOpt.isPresent()) {
      toId = toIdOpt.get();
    } else {
      toId = gitRepo.getRef(REF_MASTER);
    }
    GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            fromId,
            toId,
            this.settings.getUntaggedName(),
            this.settings.getIgnoreTagsIfNameMatches());

    if (!settings.getGitHubApi().isPresent()) {
      settings.setGitHubApi(gitRepoData.findGitHubApi().orNull());
    }
    if (!settings.getGitLabServer().isPresent()) {
      settings.setGitLabServer(gitRepoData.findGitLabServer().orNull());
      settings.setGitLabProjectName(gitRepoData.findOwnerName().orNull());
    }

    List<GitCommit> diff = gitRepoData.getGitCommits();
    final List<ParsedIssue> issues =
        new IssueParser(this.settings, diff).parseForIssues(useIntegrationIfConfigured);
    if (this.settings.ignoreCommitsWithoutIssue()) {
      gitRepoData = removeCommitsWithoutIssue(issues, gitRepoData);
      diff = gitRepoData.getGitCommits();
    }
    final List<GitTag> tags = gitRepoData.getGitTags();

    HashMap<GitCommit, List<Changelog>> submoduleMap = null;
    if (gitRepo.hasSubmodules()) {
      submoduleMap =
          new GitSubmoduleParser()
              .parseForSubmodules(this, useIntegrationIfConfigured, gitRepo, diff);
    }

    final Transformer transformer = new Transformer(this.settings);
    return new Changelog( //
        transformer.toCommits(diff, submoduleMap), //
        transformer.toTags(tags, issues, submoduleMap), //
        transformer.toAuthors(diff, submoduleMap), //
        transformer.toIssues(issues, submoduleMap), //
        transformer.toIssueTypes(issues, submoduleMap), //
        gitRepoData.findOwnerName().orNull(), //
        gitRepoData.findRepoName().orNull());
  }

  private Optional<ObjectId> getId(
      final GitRepo gitRepo, final Optional<String> ref, final Optional<String> commit)
      throws GitChangelogRepositoryException {
    if (ref.isPresent()) {
      return of(gitRepo.getRef(ref.get()));
    }
    if (commit.isPresent()) {
      return of(gitRepo.getCommit(commit.get()));
    }
    return absent();
  }

  private String getTemplateContent() {
    if (this.templateContent != null) {
      return this.templateContent;
    }
    checkArgument(this.settings.getTemplatePath() != null, "You must specify a template!");
    try {
      return Resources.toString(getResource(this.settings.getTemplatePath()), UTF_8);
    } catch (final Exception e) {
      File file = null;
      try {
        file = new File(this.settings.getTemplatePath());
        return Files.toString(file, UTF_8);
      } catch (final IOException e2) {
        throw new RuntimeException(
            "Cannot find on classpath ("
                + this.settings.getTemplatePath()
                + ") or filesystem ("
                + file.getAbsolutePath()
                + ").",
            e2);
      }
    }
  }
}
