package se.bjurr.gitchangelog.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.AutoApproveReporter;
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
    Approvals.verify(actual, new Options().withReporter(new AutoApproveReporter()));
  }
}
