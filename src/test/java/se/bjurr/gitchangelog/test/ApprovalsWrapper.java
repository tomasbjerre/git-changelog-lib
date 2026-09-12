package se.bjurr.gitchangelog.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.AutoApproveReporter;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

public class ApprovalsWrapper {
  private static final ObjectWriter JSON_WRITER =
      JsonMapper.builder()
          .changeDefaultVisibility(
              v ->
                  v.withFieldVisibility(Visibility.ANY)
                      .withGetterVisibility(Visibility.NONE)
                      .withIsGetterVisibility(Visibility.NONE))
          .changeDefaultPropertyInclusion(v -> v.withValueInclusion(Include.NON_NULL))
          .build()
          .writerWithDefaultPrettyPrinter();
  private static final String SEPARATOR = "\n\n---------------------------------------------\n\n";

  public static void verify(final GitChangelogApi given) throws Exception {
    final String changelogContext = JSON_WRITER.writeValueAsString(given.getChangelog());
    final String changelog = given.render();
    final Object actual =
        new Object() {
          @Override
          public String toString() {
            return "template:\n\n"
                + given.getTemplateString()
                + SEPARATOR
                + "settings:\n\n"
                + JSON_WRITER.writeValueAsString(given.getSettings())
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
