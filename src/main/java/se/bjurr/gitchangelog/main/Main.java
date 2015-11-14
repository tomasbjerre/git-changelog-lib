package se.bjurr.gitchangelog.main;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.softhouse.jargo.Arguments.helpArgument;
import static se.softhouse.jargo.Arguments.optionArgument;
import static se.softhouse.jargo.Arguments.stringArgument;
import static se.softhouse.jargo.CommandLineParser.withArguments;

import java.io.File;

import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

public class Main {
 public static final String PARAM_SETTINGS_FILE = "-sf";
 public static final String PARAM_OUTPUT_FILE = "-of";
 public static final String PARAM_OUTPUT_STDOUT = "-std";
 public static final String PARAM_TEMPLATE = "-t";
 private static final String PARAM_REPO = "-r";
 private static final String PARAM_FROM_REF = "-fr";
 private static final String PARAM_TO_REF = "-tr";
 private static final String PARAM_FROM_COMMIT = "-fc";
 private static final String PARAM_TO_COMMIT = "-tc";
 private static final String PARAM_IGNORE_PATTERN = "-ip";
 private static final String PARAM_JIRA_SERVER = "-js";
 private static final String PARAM_JIRA_ISSUE_PATTERN = "-jp";
 private static final String PARAM_GITHUB_SERVER = "-gs";
 private static final String PARAM_GITHUB_PATTERN = "-gp";
 private static final String PARAM_CUSTOM_ISSUE_NAME = "-cn";
 private static final String PARAM_CUSTOM_ISSUE_PATTERN = "-cp";
 private static final String PARAM_CUSTOM_ISSUE_LINK = "-cl";
 private static final String PARAM_UNTAGGED_TAG_NAME = "-ut";

 public static void main(String args[]) throws Exception {
  Argument<?> helpArgument = helpArgument("-h", "--help");

  Argument<String> settingsArgument = stringArgument(PARAM_SETTINGS_FILE, "--settings-file")//
    .description("Use settings from file.")//
    .build();
  Argument<Boolean> outputStdoutArgument = optionArgument(PARAM_OUTPUT_STDOUT, "--stdout")//
    .description("Print builder to <STDOUT>.")//
    .defaultValue(true)//
    .build();
  Argument<String> outputFileArgument = stringArgument(PARAM_OUTPUT_FILE, "--output-file")//
    .description("Write output to file.")//
    .build();

  Argument<String> templatePathArgument = stringArgument(PARAM_TEMPLATE, "--template")//
    .description("Template to use. A default template will be used if not specified.")//
    .defaultValue(null)//
    .build();

  Argument<String> untaggedTagNameArgument = stringArgument(PARAM_UNTAGGED_TAG_NAME, "--untaggedName")//
    .description(
      "When listing commits per tag, this will by the name of a virtual tag that contains commits not available in any git tag.")//
    .defaultValue(null)//
    .build();

  Argument<String> fromRepoArgument = stringArgument(PARAM_REPO, "--repo")//
    .description("Repository.")//
    .build();
  Argument<String> fromRefArgument = stringArgument(PARAM_FROM_REF, "--fromRef")//
    .description("From ref.")//
    .build();
  Argument<String> toRefArgument = stringArgument(PARAM_TO_REF, "--toRef")//
    .description("To ref.")//
    .build();
  Argument<String> fromCommitArgument = stringArgument(PARAM_FROM_COMMIT, "--fromCommit")//
    .description("From commit.")//
    .build();
  Argument<String> toCommitArgument = stringArgument(PARAM_TO_COMMIT, "--toCommit")//
    .description("To commit.")//
    .build();

  Argument<String> ignoreCommitsIfMessageMatchesArgument = stringArgument(PARAM_IGNORE_PATTERN, "--ignorePattern")//
    .description("Ignore commits where pattern matches message.")//
    .build();

  Argument<String> jiraServerArgument = stringArgument(PARAM_JIRA_SERVER, "--jiraServer")//
    .description("Jira server. When a Jira server is given, the title of the Jira issues can be used in the changelog.")//
    .build();
  Argument<String> jiraIssuePatternArgument = stringArgument(PARAM_JIRA_ISSUE_PATTERN, "--jiraPattern")//
    .description("Jira issue pattern.")//
    .build();

  Argument<String> githubServerArgument = stringArgument(PARAM_GITHUB_SERVER, "--githubServer")//
    .description(
      "Github server. When a Github server is given, the title of the Github issues can be used in the changelog.")//
    .build();
  Argument<String> githubIssuePatternArgument = stringArgument(PARAM_GITHUB_PATTERN, "--githubPattern")//
    .description("Github pattern.")//
    .build();

  Argument<String> customIssueNameArgument = stringArgument(PARAM_CUSTOM_ISSUE_NAME, "--customIssueName")//
    .description("Custom issue name.")//
    .build();
  Argument<String> customIssuePatternArgument = stringArgument(PARAM_CUSTOM_ISSUE_PATTERN, "--customIssuePattern")//
    .description("Custom issue pattern.")//
    .build();
  Argument<String> customIssueLinkArgument = stringArgument(PARAM_CUSTOM_ISSUE_LINK, "--customIssueLink")//
    .description("Custom issue link.")//
    .build();

  try {
   ParsedArguments arg = withArguments(helpArgument, settingsArgument, outputStdoutArgument, outputFileArgument,
     templatePathArgument, fromCommitArgument, fromRefArgument, fromRepoArgument, toCommitArgument, toRefArgument,
     untaggedTagNameArgument, jiraIssuePatternArgument, jiraServerArgument, ignoreCommitsIfMessageMatchesArgument,
     githubIssuePatternArgument, githubServerArgument, customIssueLinkArgument, customIssueNameArgument,
     customIssuePatternArgument)//
     .parse(args);

   GitChangelogApi changelogApiBuilder = gitChangelogApiBuilder();
   if (arg.wasGiven(settingsArgument)) {
    changelogApiBuilder.withSettings(new File(arg.get(settingsArgument)).toURI().toURL());
   }
   if (arg.wasGiven(fromCommitArgument)) {
    changelogApiBuilder.withFromCommit(arg.get(fromCommitArgument));
    changelogApiBuilder.withFromRef(null);
   }
   if (arg.wasGiven(fromRefArgument)) {
    changelogApiBuilder.withFromCommit(null);
    changelogApiBuilder.withFromRef(arg.get(fromRefArgument));
   }
   if (arg.wasGiven(fromRepoArgument)) {
    changelogApiBuilder.withFromRepo(arg.get(fromRepoArgument));
   }
   if (arg.wasGiven(toCommitArgument)) {
    changelogApiBuilder.withToCommit(arg.get(toCommitArgument));
    changelogApiBuilder.withToRef(null);
   }
   if (arg.wasGiven(toRefArgument)) {
    changelogApiBuilder.withToCommit(null);
    changelogApiBuilder.withToRef(arg.get(toRefArgument));
   }
   if (arg.wasGiven(untaggedTagNameArgument)) {
    changelogApiBuilder.withUntaggedName(arg.get(untaggedTagNameArgument));
   }
   if (arg.wasGiven(ignoreCommitsIfMessageMatchesArgument)) {
    changelogApiBuilder.withIgnoreCommitsWithMesssage(arg.get(ignoreCommitsIfMessageMatchesArgument));
   }
   if (arg.wasGiven(templatePathArgument)) {
    changelogApiBuilder.withTemplatePath(arg.get(templatePathArgument));
   }
   if (arg.wasGiven(jiraIssuePatternArgument)) {
    changelogApiBuilder.withJiraIssuePattern(arg.get(jiraIssuePatternArgument));
   }
   if (arg.wasGiven(jiraServerArgument)) {
    changelogApiBuilder.withJiraServer(arg.get(jiraServerArgument));
   }
   if (arg.wasGiven(githubIssuePatternArgument)) {
    changelogApiBuilder.withGithubIssuePattern(arg.get(githubIssuePatternArgument));
   }
   if (arg.wasGiven(githubServerArgument)) {
    changelogApiBuilder.withGithubServer(arg.get(githubServerArgument));
   }
   if ( //
   arg.wasGiven(customIssueNameArgument) && //
     arg.wasGiven(customIssuePatternArgument) && //
     arg.wasGiven(customIssueLinkArgument)) {
    changelogApiBuilder.withCustomIssues(//
      arg.get(customIssueNameArgument),//
      arg.get(customIssuePatternArgument),//
      arg.get(customIssueLinkArgument));
   }

   if (arg.get(outputStdoutArgument)) {
    changelogApiBuilder.toStdout();
   }

   if (arg.wasGiven(outputFileArgument)) {
    String filePath = arg.get(outputFileArgument);
    changelogApiBuilder.toFile(filePath);
   }

  } catch (ArgumentException exception) {
   System.out.println(exception.getMessageAndUsage());
   System.exit(1);
  }
 }
}
