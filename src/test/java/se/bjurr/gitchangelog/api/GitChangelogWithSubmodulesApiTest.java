package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GitChangelogWithSubmodulesApiTest {
  private static final String REPOSITORY_PATH = "submodule-test-repo";
  private File gitRepoFile;

  @Before
  public void before() {

    File fileZip =
        new File(Resources.getResource(String.format("%s.zip", REPOSITORY_PATH)).getFile());
    File destDir = fileZip.getParentFile();
    byte[] buffer = new byte[1024];
    try {
      ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        File newFile = new File(destDir, zipEntry.getName());
        if (zipEntry.isDirectory()) {
          newFile.mkdirs();
        } else {
          newFile.getParentFile().mkdirs();
          FileOutputStream fos = new FileOutputStream(newFile);
          int len;
          while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
          }
          fos.close();
        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
      zis.close();
    } catch (FileNotFoundException e) {
      fail("Could not find zipped repository");
    } catch (IOException e) {
      fail("Could not unzip repository");
    }
    this.gitRepoFile = new File(Resources.getResource(REPOSITORY_PATH).getFile());
  }

  @After
  public void after() {
    try {
      FileUtils.deleteDirectory(new File(Resources.getResource(REPOSITORY_PATH).getFile()));
    } catch (IOException e) {
      fail("Could not cleanup repository folder");
    }
  }

  @Test
  public void testThatSubmoduleChangesAreListed() throws Exception {
    final String expected =
        Resources.toString(getResource("templatetest/testThatSubmoduleChangesAreListed.md"), UTF_8)
            .trim();

    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templatetest/testSubmodules.mustache";

    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    final GitChangelogApi changelogApiBuilder =
        gitChangelogApiBuilder()
            .withSettings(settingsFile)
            .withFromRepo(gitRepoFile.getAbsolutePath())
            .withFromCommit(ZERO_COMMIT)
            .withToRef("master")
            .withTemplatePath(templatePath);

    assertEquals(
        "templateContent:\n"
            + templateContent
            + "\nContext:\n"
            + toJson(changelogApiBuilder.getChangelog(true)),
        expected,
        changelogApiBuilder.render().trim());
  }

  @Test
  public void testThatSubmoduleChangesAreNotListedWhenSubmoduleIsRemoved() throws Exception {
    final String expected =
        Resources.toString(
                getResource(
                    "templatetest/testThatSubmoduleChangesAreNotListedWhenSubmoduleIsRemoved.md"),
                UTF_8)
            .trim();

    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templatetest/testSubmodules.mustache";

    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    Git git = Git.open(gitRepoFile);
    git.checkout().setName("RemovedSubmodule").call();

    final GitChangelogApi changelogApiBuilder =
        gitChangelogApiBuilder()
            .withSettings(settingsFile)
            .withFromRepo(gitRepoFile.getAbsolutePath())
            .withFromCommit(ZERO_COMMIT)
            .withToRef("RemovedSubmodule")
            .withTemplatePath(templatePath);

    assertEquals(
        "templateContent:\n"
            + templateContent
            + "\nContext:\n"
            + toJson(changelogApiBuilder.getChangelog(true)),
        expected,
        changelogApiBuilder.render().trim());
  }

  private String toJson(final Object object) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(object);
  }
}
