package se.bjurr.gitchangelog.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.QuietReporter;
import se.bjurr.gitchangelog.api.GitChangelogApi;

public class ApprovalsWrapper {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String SEPARATOR = "\n\n---------------------------------------------\n\n";

  public static void verify(final GitChangelogApi given) throws Exception {
    final String changelogContext = GSON.toJson(given.getChangelog());
    final String changelog = given.render();
    final Object actual =
        new Object() {
          @Override
          public String toString() {
            return "template:\n\n"
                + given.getTemplateString()
                + SEPARATOR
                + "settings:\n\n"
                + GSON.toJson(given.getSettings())
                + SEPARATOR
                + "changelog:\n\n"
                + changelog
                + SEPARATOR
                + "context:\n\n"
                + changelogContext;
          }
        };
    /**
     * {@link QuietReporter} writes the actual value to a *.received.txt next to the approved file
     * and fails the test. It must not be replaced with a reporter that has "approval power", like
     * AutoApproveReporter, because such a reporter silently rewrites the approved file and reports
     * the verification as successful. That makes every approval test unable to fail.
     *
     * <p>To accept an intended change, diff the *.received.txt against the *.approved.txt and, if
     * the new output is correct, move it over the approved file.
     */
    Approvals.verify(actual, new Options().withReporter(new QuietReporter()));
  }
}
