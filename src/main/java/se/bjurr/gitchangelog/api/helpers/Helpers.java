package se.bjurr.gitchangelog.api.helpers;

import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitBreaking;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitDescription;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitFixes;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitRefs;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitScope;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitScopes;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsBreaking;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueLabel;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueLabelOtherThan;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueTypeOtherThan;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsTypeOtherThan;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.getDate;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.getMessageParts;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.isReleaseTag;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.issueLabel;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.issueType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.revertedCommit;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;
import com.github.jknack.handlebars.helper.EachHelper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser;
import se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.Footer;

public class Helpers {

  public static Map<String, Helper<?>> getAll() {
    final TreeMap<String, Helper<?>> helpers = new TreeMap<>();
    helpers.put(
        "ifEquals",
        (final Object a, final Options options) -> {
          final Object b = options.params[0];
          final boolean equality = a.equals(b);
          return conditional(options, equality);
        });
    helpers.put(
        "ifMatches",
        (final Object a, final Options options) -> {
          final String regexp = (String) options.params[0];
          final boolean equality = a.toString().matches(regexp);
          return conditional(options, equality);
        });
    helpers.put(
        "subString",
        (final Object a, final Options options) -> {
          final Integer from = (Integer) options.params[0];
          if (options.params.length == 1) {
            return a.toString().substring(from);
          } else {
            final Integer to = (Integer) options.params[1];
              return a.toString().substring(from, to);
          } 
        });

    helpers.put(
        "ifReleaseTag",
        (final Tag tag,final Options options) -> {
          return conditional(options, isReleaseTag(tag));
        });

    helpers.put(
        "tagDate",
        (final Tag tag, final Options options) -> {
          return getDate(tag.getTagTime());
        });

    helpers.put(
        "commitDate",
        (final Commit commit, final Options options) -> {
          return getDate(commit.getCommitTime());
        });
    helpers.put(
        "ifContainsType",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsType(commits, options));
        });
    helpers.put(
        "ifContainsTypeOtherThan",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsTypeOtherThan(commits, options));
        });
    helpers.put(
        "ifContainsBreaking",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsBreaking(commits, options));
        });

    helpers.put(
        "ifCommitType",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitType(commit.getMessage(), options));
        });
    helpers.put(
        "ifCommitTypeOtherThan",
        (final Commit commit, final Options options) -> {
          return conditional(options, !commitType(commit.getMessage(), options));
        });

    helpers.put(
        "ifCommitBreaking",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitBreaking(commit.getMessage()));
        });
    helpers.put(
        "ifContainsIssueType",
        (final List<Issue> issues, final Options options) -> {
          return conditional(options, containsIssueType(issues, options));
        });
    helpers.put(
        "ifContainsIssueTypeOtherThan",
        (final List<Issue> issues, final Options options) -> {
          return conditional(options, containsIssueTypeOtherThan(issues, options));
        });
    helpers.put(
        "ifContainsIssueLabel",
        (final List<Issue> issues, final Options options) -> {
          return conditional(options, containsIssueLabel(issues, options));
        });
    helpers.put(
        "ifContainsIssueLabelOtherThan",
        (final List<Issue> issues, final Options options) -> {
          return conditional(options, containsIssueLabelOtherThan(issues, options));
        });

    helpers.put(
        "ifIssueType",
        (final Issue issue, final Options options) -> {
          return conditional(options, issueType(issue.getType(), options));
        });
    helpers.put(
        "ifIssueLabel",
        (final Issue issue, final Options options) -> {
          return conditional(options, issueLabel(issue, options));
        });

    helpers.put(
        "ifIssueTypeOtherThan",
        (final Issue issue, final Options options) -> {
          return conditional(options, !issueType(issue.getType(), options));
        });

    helpers.put(
        "ifCommitScope",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitScope(commit, options));
        });

    helpers.put(
        "ifCommitHasFooters",
        (final Commit commit, final Options options) -> {
          return conditional(options, getMessageParts(commit.getMessage()).footers.size() > 0);
        });

    helpers.put(
        "ifCommitHasParagraphs",
        (final Commit commit, final Options options) -> {
          return conditional(options, getMessageParts(commit.getMessage()).paragraphs.size() > 0);
        });

    helpers.put(
        "eachCommitScope",
        (final Commit commit, final Options options) -> {
          return each(options, commitScopes(commit.getMessage()));
        });

    helpers.put(
        "commitDescription",
        (final Commit commit, final Options options) -> {
          return commitDescription(commit.getMessage());
        });

    helpers.put(
        "eachCommitRefs",
        (final Commit commit, final Options options) -> {
          return each(options, commitRefs(commit.getMessage()));
        });

    helpers.put(
        "eachCommitFixes",
        (final Commit commit, final Options options) -> {
          return each(options, commitFixes(commit.getMessage()));
        });

    helpers.put(
        "revertedCommit",
        (final Commit commit, final Options options) -> {
          return revertedCommit(commit.getMessage());
        });

    helpers.put(
        "eachCommitParagraph",
        (final Commit commit, final Options options) -> {
          return each(options, getMessageParts(commit.getMessage()).paragraphs);
        });

    helpers.put(
        "eachCommitFooter",
        (final Commit commit, final Options options) -> {
          return each(
              options, ConventionalCommitParser.getMessageParts(commit.getMessage()).footers);
        });

    helpers.put(
        "ifFooterHasValue",
        (final Footer footer, final Options options) -> {
          return conditional(options, !footer.value.trim().isEmpty());
        });
    return helpers;
  }

  private static Object each(final Options options, final List<?> elements) throws IOException {
    return new EachHelper().apply(elements, options);
  }

  private static Buffer conditional(final Options options, final boolean condition)
      throws IOException {
    final Buffer buffer = options.buffer();
    if (condition) {
      buffer.append(options.fn());
    } else {
      buffer.append(options.inverse());
    }
    return buffer;
  }
}
