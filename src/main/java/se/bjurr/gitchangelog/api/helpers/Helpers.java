package se.bjurr.gitchangelog.api.helpers;

import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitBreaking;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitDescription;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitFixes;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitRefs;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitScope;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitScopes;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.commitType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsBreaking;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsTypeOtherThan;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.getDate;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.getMessageParts;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.isReleaseTag;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.revertedCommit;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueType;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.containsIssueTypeOtherThan;
import static se.bjurr.gitchangelog.internal.semantic.ConventionalCommitParser.issueType;

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

    public static final Map<String, Helper<?>> ALL = new TreeMap<>();

    static {
        ALL.put(
                "ifEquals",
                (final Object a, final Options options) -> {
                    final Object b = options.params[0];
                    final boolean equality = a.equals(b);
                    return conditional(options, equality);
                });
        ALL.put(
                "ifMatches",
                (final Object a, final Options options) -> {
                    final String regexp = (String) options.params[0];
                    final boolean equality = a.toString().matches(regexp);
                    return conditional(options, equality);
                });
        ALL.put(
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

        ALL.put(
                "ifReleaseTag",
                (final Tag tag, final Options options) -> {
                    return conditional(options, isReleaseTag(tag));
                });

        ALL.put(
                "tagDate",
                (final Tag tag, final Options options) -> {
                    return getDate(tag.getTagTime());
                });

        ALL.put(
                "commitDate",
                (final Commit commit, final Options options) -> {
                    return getDate(commit.getCommitTime());
                });
        ALL.put(
                "ifContainsType",
                (final List<Commit> commits, final Options options) -> {
                    return conditional(options, containsType(commits, options));
                });
        ALL.put(
                "ifContainsTypeOtherThan",
                (final List<Commit> commits, final Options options) -> {
                    return conditional(options, containsTypeOtherThan(commits, options));
                });
        ALL.put(
                "ifContainsBreaking",
                (final List<Commit> commits, final Options options) -> {
                    return conditional(options, containsBreaking(commits, options));
                });

        ALL.put(
                "ifCommitType",
                (final Commit commit, final Options options) -> {
                    return conditional(options, commitType(commit.getMessage(), options));
                });
        ALL.put(
                "ifCommitTypeOtherThan",
                (final Commit commit, final Options options) -> {
                    return conditional(options, !commitType(commit.getMessage(), options));
                });

        ALL.put(
                "ifCommitBreaking",
                (final Commit commit, final Options options) -> {
                    return conditional(options, commitBreaking(commit.getMessage()));
                });
        ALL.put(
                "ifContainsIssueType",
                (final List<Issue> issues, final Options options) -> {
                    return conditional(options, containsIssueType(issues, options));
                });
        ALL.put(
                "ifContainsIssueTypeOtherThan",
                (final List<Issue> issues, final Options options) -> {
                    return conditional(options, containsIssueTypeOtherThan(issues, options));
                });

        ALL.put(
                "ifIssueType",
                (final Issue issue, final Options options) -> {
                    return conditional(options, issueType(issue.getType(), options));
                });
        ALL.put(
                "ifIssueTypeOtherThan",
                (final Issue issue, final Options options) -> {
                    return conditional(options, !issueType(issue.getType(), options));
                });


        ALL.put(
                "ifCommitScope",
                (final Commit commit, final Options options) -> {
                    return conditional(options, commitScope(commit, options));
                });

        ALL.put(
                "ifCommitHasFooters",
                (final Commit commit, final Options options) -> {
                    return conditional(options, getMessageParts(commit.getMessage()).footers.size() > 0);
                });

        ALL.put(
                "ifCommitHasParagraphs",
                (final Commit commit, final Options options) -> {
                    return conditional(options, getMessageParts(commit.getMessage()).paragraphs.size() > 0);
                });

        ALL.put(
                "eachCommitScope",
                (final Commit commit, final Options options) -> {
                    return each(options, commitScopes(commit.getMessage()));
                });

        ALL.put(
                "commitDescription",
                (final Commit commit, final Options options) -> {
                    return commitDescription(commit.getMessage());
                });

        ALL.put(
                "eachCommitRefs",
                (final Commit commit, final Options options) -> {
                    return each(options, commitRefs(commit.getMessage()));
                });

        ALL.put(
                "eachCommitFixes",
                (final Commit commit, final Options options) -> {
                    return each(options, commitFixes(commit.getMessage()));
                });

        ALL.put(
                "revertedCommit",
                (final Commit commit, final Options options) -> {
                    return revertedCommit(commit.getMessage());
                });

        ALL.put(
                "eachCommitParagraph",
                (final Commit commit, final Options options) -> {
                    return each(options, getMessageParts(commit.getMessage()).paragraphs);
                });

        ALL.put(
                "eachCommitFooter",
                (final Commit commit, final Options options) -> {
                    return each(
                            options, ConventionalCommitParser.getMessageParts(commit.getMessage()).footers);
                });

        ALL.put(
                "ifFooterHasValue",
                (final Footer footer, final Options options) -> {
                    return conditional(options, !footer.value.trim().isEmpty());
                });
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
