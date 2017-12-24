package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.ByteStreams.toByteArray;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RestClient {
  private static Logger logger = getLogger(RestClient.class);
  private static RestClient mockedRestClient;
  private final LoadingCache<String, Optional<String>> urlCache;
  private String basicAuthString;

  public RestClient(long duration, TimeUnit cacheExpireAfterAccess) {
    urlCache =
        newBuilder() //
            .expireAfterAccess(duration, cacheExpireAfterAccess) //
            .build(
                new CacheLoader<String, Optional<String>>() {
                  @Override
                  public Optional<String> load(String url) throws Exception {
                    return doGet(url);
                  }
                });
  }

  public RestClient withBasicAuthCredentials(String username, String password) {
    try {
      this.basicAuthString =
          new String(printBase64Binary((username + ":" + password).getBytes("UTF-8")));
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public Optional<String> get(String url) throws GitChangelogIntegrationException {
    try {
      return urlCache.get(url);
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException("Problems invoking " + url, e);
    }
  }

  private Optional<String> doGet(String urlParam) {
    final String response = null;
    try {
      logger.info("GET:\n" + urlParam);
      final URL url = new URL(urlParam);
      final HttpURLConnection conn = openConnection(url);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      if (this.basicAuthString != null) {
        conn.setRequestProperty("Authorization", "Basic " + basicAuthString);
      }
      return of(getResponse(conn));
    } catch (final Exception e) {
      logger.error("Got:\n" + response, e);
      return absent();
    }
  }

  @VisibleForTesting
  protected HttpURLConnection openConnection(URL url) throws Exception {
    if (mockedRestClient == null) {
      return (HttpURLConnection) url.openConnection();
    }
    return mockedRestClient.openConnection(url);
  }

  @VisibleForTesting
  protected String getResponse(HttpURLConnection conn) throws Exception {
    if (mockedRestClient == null) {
      return new String(toByteArray(conn.getInputStream()), "UTF-8");
    }
    return mockedRestClient.getResponse(conn);
  }

  public static void mock(RestClient mock) {
    mockedRestClient = mock;
  }
}
