package se.bjurr.gitchangelog.api.helpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.approvaltests.Approvals;
import org.junit.Test;
import se.bjurr.gitchangelog.test.AutoApproveReporter;

public class CommitHelperTest {

  private final List<String> commits =
      Arrays.asList( //
          "feat: subject", //
          "chore(dependencies): subject", //
          "chore(dependencies:formatting): subject", //
          "chore: subject (refs ABC-123)", //
          "chore: subject (refs ABC-123 DEF-456)", //
          "chore: subject (fixes ABC-123" //
          );

  @Test
  public void testCommit() throws IOException {

    final StringBuilder sb = new StringBuilder();

    for (final String commit : this.commits) {
      sb.append("----------------------------\n");
      sb.append(commit + "\n\n");
      for (final Entry<String, Helper<?>> helper : Helpers.COMMIT_HELPERS.entrySet()) {
        final Options options = mock(Options.class);
        when(options.hash("type")).thenReturn("feat");
        final Helper<String> value = (Helper<String>) helper.getValue();
        final String actual = value.apply(commit, options).toString();
        sb.append(helper.getKey() + ": " + actual + "\n\n");
      }
    }

    Approvals.verify(
        sb.toString(),
        new org.approvaltests.core.Options().withReporter(new AutoApproveReporter()));
  }
}
