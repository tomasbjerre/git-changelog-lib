package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RestClient {
  private static Logger logger = getLogger(RestClient.class);
  private static RestClient mockedRestClient;
  private final LoadingCache<String, Optional<String>> urlCache;
  private String basicAuthString;

  public RestClient(final long duration, final TimeUnit cacheExpireAfterAccess) {
    urlCache =
        newBuilder() //
            .expireAfterAccess(duration, cacheExpireAfterAccess) //
            .build(
                new CacheLoader<String, Optional<String>>() {
                  @Override
                  public Optional<String> load(final String url) throws Exception {
                    return doGet(url);
                  }
                });
  }

  public RestClient withBasicAuthCredentials(final String username, final String password) {
    try {
      this.basicAuthString =
          Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public Optional<String> get(final String url) throws GitChangelogIntegrationException {
    try {
      return urlCache.get(url);
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException("Problems invoking " + url, e);
    }
  }

  private Optional<String> doGet(final String urlParam) {
    final String response = null;
    HttpURLConnection conn = null;
    try {
      logger.info("GET:\n" + urlParam);
      final URL url = new URL(urlParam);
      conn = openConnection(url);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      if (this.basicAuthString != null) {
        conn.setRequestProperty("Authorization", "Basic " + basicAuthString);
      }
      return of(getResponse(conn));
    } catch (final Exception e) {
      logger.error("Got:\n" + response, e);
      return absent();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  @VisibleForTesting
  protected HttpURLConnection openConnection(final URL url) throws Exception {
    if (mockedRestClient == null) {
      return (HttpURLConnection) url.openConnection();
    }
    return mockedRestClient.openConnection(url);
  }

  @VisibleForTesting
  protected String getResponse(final HttpURLConnection conn) throws Exception {
    if (mockedRestClient == null) {
      return new String(toByteArray(conn.getInputStream()), "UTF-8");
    }
    return mockedRestClient.getResponse(conn);
  }

  public static void mock(final RestClient mock) {
    mockedRestClient = mock;
  }
}
