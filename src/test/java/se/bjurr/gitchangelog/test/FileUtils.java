package se.bjurr.gitchangelog.test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import se.bjurr.gitchangelog.api.TemplatesTest;

public class FileUtils {
  public static String readResource(final String name) throws Exception {
    return new String(
        Files.readAllBytes(Paths.get(TemplatesTest.class.getResource(name).toURI())),
        StandardCharsets.UTF_8);
  }
}
