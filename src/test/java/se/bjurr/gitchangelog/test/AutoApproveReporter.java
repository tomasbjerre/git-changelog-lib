package se.bjurr.gitchangelog.test;

import com.spun.util.io.FileUtils;
import java.io.File;
import org.approvaltests.core.ApprovalFailureReporter;

/**
 * Will be available in next Approvals version:
 * https://github.com/approvals/ApprovalTests.Java/commit/f2ba2b91a24854c9ef3d757bd20bf2d7e0409e13
 */
public class AutoApproveReporter implements ApprovalFailureReporter {
  @Override
  public void report(final String received, final String approved) {
    final File a = new File(approved);
    if (a.exists()) {
      a.delete();
    }
    final File r = new File(received);
    FileUtils.copyFile(r, a);
  }
}
