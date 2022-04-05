package se.bjurr.gitchangelog.internal.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class ResourceLoader {
  private ResourceLoader() {}

  public static String getResourceOrFile(final String resourceName) {
    String templateString = null;
    try {
      final Path templatePath = Paths.get(resourceName);
      if (templatePath.toFile().exists()) {
        templateString = new String(Files.readAllBytes(templatePath), StandardCharsets.UTF_8);
      } else {
        InputStream inputStream =
            getResourceFromClassLoader(resourceName, ResourceLoader.class.getClassLoader());
        if (inputStream == null) {
          inputStream =
              getResourceFromClassLoader(
                  resourceName, Thread.currentThread().getContextClassLoader());
        }

        if (inputStream == null) {
          throw new FileNotFoundException(
              "Was unable to find file, or resouce, \"" + resourceName + "\"");
        }
        try (BufferedReader br =
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
          templateString = br.lines().collect(Collectors.joining("\n"));
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(resourceName, e);
    }
    return templateString;
  }

  private static InputStream getResourceFromClassLoader(
      final String resourceName, final ClassLoader classLoader) {
    InputStream inputStream = classLoader.getResourceAsStream(resourceName);
    if (inputStream == null) {
      inputStream = classLoader.getResourceAsStream("/" + resourceName);
    }
    return inputStream;
  }
}
