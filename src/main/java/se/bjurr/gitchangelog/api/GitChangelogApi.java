package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.git.GitRepoDataHelper.removeCommitsWithoutIssue;
import static se.bjurr.gitchangelog.internal.settings.Settings.fromFile;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eclipse.jgit.lib.ObjectId;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.api.helpers.Helpers;
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.GitRepoData;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.model.Transformer;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersion;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersioning;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;
import se.bjurr.gitchangelog.internal.util.ResourceLoader;

public class GitChangelogApi {

  public static GitChangelogApi gitChangelogApiBuilder() {
    return new GitChangelogApi();
  }

  private Settings settings;
  private String templateContent;
  private Handlebars handlebars;
  private final AtomicInteger helperCounter = new AtomicInteger();

  private GitChangelogApi() {
    this.settings = new Settings();
    this.handlebars = new Handlebars();
    this.handlebars.setPrettyPrint(true);
    for (final Entry<String, Helper<?>> helper : Helpers.ALL.entrySet()) {
      this.handlebars.registerHelper(helper.getKey(), helper.getValue());
    }
  }

  private GitChangelogApi(final Settings settings) {
    this.settings = settings;
  }

  /**
   * Get the changelog as data object.
   *
   * @throws GitChangelogRepositoryException
   */
  public Changelog getChangelog() throws GitChangelogRepositoryException {
    return this.getChangelog(true);
  }

  private Changelog getChangelog(final boolean useIntegrations)
      throws GitChangelogRepositoryException {
    try (GitRepo gitRepo = new GitRepo(new File(this.settings.getFromRepo()))) {
      return this.getChangelog(gitRepo, useIntegrations);
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
    Template template = null;
    final String templateString = this.getTemplateString();

    if (this.settings.getTemplateBaseDir() != null) {
      this.handlebars.with(
          new FileTemplateLoader(
              this.settings.getTemplateBaseDir(), this.settings.getTemplateSuffix()));
    }

    try {
      template = this.handlebars.compileInline(templateString);
    } catch (final IOException e) {
      throw new RuntimeException("Cannot render:\n\n" + templateString, e);
    }

    try {
      final Changelog changelog = this.getChangelog(this.settings.isUseIntegrations());
      final Map<String, Object> extendedVariables = this.settings.getExtendedVariables();
      if (extendedVariables == null) {
        throw new IllegalStateException("extendedVariables cannot be null");
      }
      final Context changelogContext = Context.newContext(changelog).combine(extendedVariables);
      template.apply(changelogContext, writer);
    } catch (final IOException e) {
      // Should be impossible!
      throw new GitChangelogRepositoryException("", e);
    }
  }

  public String getTemplateString() {
    if (this.templateContent != null) {
      return this.templateContent;
    }
    final String resourceName = this.settings.getTemplatePath();
    return ResourceLoader.getResourceOrFile(resourceName);
  }

  /** Get the changelog. */
  public String render() throws GitChangelogRepositoryException {
    final Writer writer = new StringWriter();
    this.render(writer);
    return writer.toString();
  }

  /**
   * Write changelog to file.
   *
   * @throws GitChangelogRepositoryException
   * @throws IOException When file cannot be written.
   */
  public void toFile(final File file) throws GitChangelogRepositoryException, IOException {
    final File parentFile = file.getParentFile();
    if (parentFile != null) {
      final boolean folderExists = parentFile.exists() || parentFile.mkdirs();
      if (!folderExists) {
        throw new RuntimeException("Folder " + parentFile.getAbsolutePath() + " cannot be created");
      }
    }
    Files.write(file.toPath(), this.render().getBytes(UTF_8));
  }

  /** Prepend the changelog to the given file. */
  public void prependToFile(final File file) throws GitChangelogRepositoryException, IOException {
    if (!file.exists()) {
      this.toFile(file);
      return;
    }
    final String prepend = this.render();
    final String changelogContent = new String(Files.readAllBytes(file.toPath()));
    Files.write(file.toPath(), (prepend + "\n" + changelogContent).getBytes(UTF_8));
  }

  /**
   * Get next semantic version. This requires version-pattern and major/minor/patch patterns to have
   * been configured.
   */
  public SemanticVersion getNextSemanticVersion() throws GitChangelogRepositoryException {
    final boolean fromGiven =
        this.settings.getFromRef().isPresent() || this.settings.getFromCommit().isPresent();
    final SemanticVersion highestSemanticVersion = this.getHighestSemanticVersion();
    if (!fromGiven) {
      final Optional<String> tag = highestSemanticVersion.findTag();
      if (tag.isPresent()) {
        this.withFromRef(tag.get());
      }
    }
    final Changelog changelog = this.getChangelog(false);
    final List<String> tags = this.getTagsAsStrings(changelog);
    final List<String> commits = this.getCommitMessages(changelog);
    final String majorVersionPattern = this.settings.getSemanticMajorPattern().orElse(null);
    final String minorVersionPattern = this.settings.getSemanticMinorPattern();
    final String patchVersionPattern = this.settings.getSemanticPatchPattern();
    final SemanticVersioning semanticVersioning =
        new SemanticVersioning(
            tags, commits, majorVersionPattern, minorVersionPattern, patchVersionPattern);
    return semanticVersioning.getNextVersion(highestSemanticVersion);
  }

  public SemanticVersion getHighestSemanticVersion() throws GitChangelogRepositoryException {
    final Changelog changelog = this.getChangelog(false);
    final List<String> tags = this.getTagsAsStrings(changelog);
    return SemanticVersioning.getHighestVersion(tags);
  }

  private List<String> getCommitMessages(final Changelog changelog) {
    return changelog.getCommits().stream()
        .map((it) -> it.getMessage())
        .collect(Collectors.toList());
  }

  private List<String> getTagsAsStrings(final Changelog changelog) {
    return changelog.getTags().stream().map((it) -> it.getName()).collect(Collectors.toList());
  }

  /** Will be used to determine next semantic version. */
  public GitChangelogApi withSemanticMajorVersionPattern(final String major)
      throws GitChangelogRepositoryException {
    this.settings.setSemanticMajorPattern(major);
    return this;
  }

  /** Will be used to determine next semantic version. */
  public GitChangelogApi withSemanticMinorVersionPattern(final String minor)
      throws GitChangelogRepositoryException {
    this.settings.setSemanticMinorPattern(minor);
    return this;
  }

  /** Will be used to determine next semantic version. */
  public GitChangelogApi withSemanticPatchVersionPattern(final String patch)
      throws GitChangelogRepositoryException {
    this.settings.setSemanticPatchPattern(patch);
    return this;
  }

  /**
   * Registers (Javscript) Handlebars helper to use in template.
   *
   * @see https://github.com/jknack/handlebars.java/tree/master#with-plain-javascript
   */
  public GitChangelogApi withHandlebarsHelper(final String javascriptHelper)
      throws GitChangelogRepositoryException, IOException {
    final int helperIndex = this.helperCounter.getAndIncrement();
    this.handlebars.registerHelpers("helper-" + helperIndex, javascriptHelper);
    return this;
  }

  /**
   * Registers (Java) Handlebars helper to use in template.
   *
   * @return
   * @see https://github.com/jknack/handlebars.java/tree/master#with-plain-javascript
   */
  public GitChangelogApi withHandlebarsHelper(final String name, final Helper<?> helper)
      throws GitChangelogRepositoryException, IOException {
    this.handlebars.registerHelper(name, helper);
    return this;
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
   * Extended headers is simply a key-value mapping of headers that will be passed to REST request.
   * Is used, for example, to bypass 2-factor authentication.
   */
  public GitChangelogApi withExtendedHeaders(final Map<String, String> extendedHeaders) {
    this.settings.setExtendedRestHeaders(extendedHeaders);
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

  public GitChangelogApi withFromRepo(final File fromRepo) {
    this.settings.setFromRepo(fromRepo.getAbsolutePath());
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

  /** Authenticate to JIRA. */
  public GitChangelogApi withJiraBearer(final String string) {
    this.settings.setJiraBearer(string);
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

  /** Pattern to recognize Redmine:s. <code>#([0-9]+)</code> */
  public GitChangelogApi withRedmineIssuePattern(final String redmineIssuePattern) {
    this.settings.setRedmineIssuePattern(redmineIssuePattern);
    return this;
  }

  /** Authenticate to Redmine. */
  public GitChangelogApi withRedminePassword(final String string) {
    this.settings.setRedminePassword(string);
    return this;
  }

  /** Authenticate to Redmine with API_KEY */
  public GitChangelogApi withRedmineToken(final String string) {
    this.settings.setRedmineToken(string);
    return this;
  }

  /**
   * URL pointing at your Redmine server. When configured, the {@link Issue#getTitle()} will be
   * populated with title from Redmine.<br>
   * <code>https://redmineserver/</code>
   */
  public GitChangelogApi withRedmineServer(final String redmineServer) {
    this.settings.setRedmineServer(redmineServer);
    return this;
  }

  /** Authenticate to Redmine. */
  public GitChangelogApi withRedmineUsername(final String string) {
    this.settings.setRedmineUsername(string);
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

  /** Path to the base directory for template partial files */
  public GitChangelogApi withTemplateBaseDir(final String templateBaseDir) {
    this.settings.setTemplateBaseDir(templateBaseDir);
    return this;
  }

  /** Suffix of the template partial files */
  public GitChangelogApi withTemplateSuffix(final String templateSuffix) {
    this.settings.setTemplateSuffix(templateSuffix);
    return this;
  }

  public GitChangelogApi withUseIntegrations(final boolean useIntegrations) {
    this.settings.setUseIntegrations(useIntegrations);
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
   * Filter commits using the provided path filter, analogous to using the cli command git log --
   * 'pathFilter'
   */
  public GitChangelogApi withPathFilter(final String pathFilter) {
    this.settings.setPathFilter(pathFilter);
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

  private Changelog getChangelog(final GitRepo gitRepo, final boolean useIntegrations)
      throws GitChangelogRepositoryException {
    gitRepo.setTreeFilter(this.settings.getSubDirFilter());
    final ObjectId fromId =
        this.getId(gitRepo, this.settings.getFromRef(), this.settings.getFromCommit()) //
            .orElse(gitRepo.getCommit(ZERO_COMMIT));
    final Optional<ObjectId> toIdOpt =
        this.getId(gitRepo, this.settings.getToRef(), this.settings.getToCommit());
    ObjectId toId;
    if (toIdOpt.isPresent()) {
      toId = toIdOpt.get();
    } else {
      final Optional<ObjectId> headOpt = gitRepo.findRef(REF_HEAD);
      if (headOpt.isPresent()) {
        toId = headOpt.get();
      } else {
        toId = gitRepo.getRef(REF_MASTER);
      }
    }
    GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            fromId,
            toId,
            this.settings.getUntaggedName(),
            this.settings.getIgnoreTagsIfNameMatches());

    if (!this.settings.getGitHubApi().isPresent()) {
      this.settings.setGitHubApi(gitRepoData.findGitHubApi().orElse(null));
    }
    if (!this.settings.getGitLabServer().isPresent()) {
      this.settings.setGitLabServer(gitRepoData.findGitLabServer().orElse(null));
      this.settings.setGitLabProjectName(gitRepoData.findOwnerName().orElse(null));
    }

    List<GitCommit> diff = gitRepoData.getGitCommits();
    final List<ParsedIssue> issues =
        new IssueParser(this.settings, diff).parseForIssues(useIntegrations);
    if (this.settings.ignoreCommitsWithoutIssue()) {
      gitRepoData = removeCommitsWithoutIssue(issues, gitRepoData);
      diff = gitRepoData.getGitCommits();
    }
    final List<GitTag> tags = gitRepoData.getGitTags();
    final Transformer transformer = new Transformer(this.settings);
    return new Changelog( //
        transformer.toCommits(diff), //
        transformer.toTags(tags, issues), //
        transformer.toAuthors(diff), //
        transformer.toIssues(issues), //
        transformer.toIssueTypes(issues), //
        gitRepoData.findOwnerName().orElse(null), //
        gitRepoData.findRepoName().orElse(null),
        gitRepoData.getUrlPartsList());
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
    return empty();
  }

  public GitChangelogApi withJiraEnabled(final boolean b) {
    this.settings.setJiraEnabled(b);
    return this;
  }

  public GitChangelogApi withGitLabEnabled(final boolean b) {
    this.settings.setGitLabEnabled(b);
    return this;
  }

  public GitChangelogApi withGitHubEnabled(final boolean b) {
    this.settings.setGitHubEnabled(b);
    return this;
  }

  public GitChangelogApi withRedmineEnabled(final boolean b) {
    this.settings.setRedmineEnabled(b);
    return this;
  }
}
