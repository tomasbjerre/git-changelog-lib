package se.bjurr.gitchangelog.internal.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class ResourceLoader {
  private ResourceLoader() {}

  public static String getResourceOrFile(final String resourceName, final Charset encoding) {
    String templateString = null;
    InputStream inputStream = null;
    try { // NOPMD
      final Path templatePath = Paths.get(resourceName);
      if (templatePath.toFile().exists()) {
        templateString = new String(Files.readAllBytes(templatePath), encoding);
      } else {
        inputStream =
            getResourceFromClassLoader(
                resourceName, ResourceLoader.class.getClassLoader()); // NOPMD
        if (inputStream == null) {
          inputStream =
              getResourceFromClassLoader(
                  resourceName, Thread.currentThread().getContextClassLoader());
        }
        if (inputStream == null) {
          inputStream = getResourceFromURL(resourceName);
        }

        if (inputStream == null) {
          throw new FileNotFoundException(
                  "Was unable to find file, or resource, \"" + resourceName + "\"");
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
          templateString = br.lines().collect(Collectors.joining("\n"));
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(resourceName, e);
    } finally { // NOPMD
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return templateString;
  }

  private static InputStream getResourceFromClassLoader(
      final String resourceName, final ClassLoader classLoader) {
    InputStream inputStream = classLoader.getResourceAsStream(resourceName);
    if (inputStream == null) {
      inputStream = classLoader.getResourceAsStream("/" + resourceName); // NOPMD
    }
    return inputStream;
  }

  private static InputStream getResourceFromURL(final String resourceName) {
    try {
      URL url = new URL(resourceName);
      return url.openStream();
    } catch (IOException e) {
      return null;
    }
  }
}