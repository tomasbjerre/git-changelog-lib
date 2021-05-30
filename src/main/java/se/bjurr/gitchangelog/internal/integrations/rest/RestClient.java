package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RestClient {
  private static Logger logger = getLogger(RestClient.class);
  private static RestClient mockedRestClient;
  private final LoadingCache<String, Optional<String>> urlCache;
  private String basicAuthString;

  private Map<String, String> headers;

  public RestClient(final long duration, final TimeUnit cacheExpireAfterAccess) {
    this.urlCache =
        newBuilder() //
            .expireAfterAccess(duration, cacheExpireAfterAccess) //
            .build(
                new CacheLoader<String, Optional<String>>() {
                  @Override
                  public Optional<String> load(final String url) throws Exception {
                    return RestClient.this.doGet(url);
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

  public RestClient withTokenAuthCredentials(final String token) {
    this.basicAuthString = token;
    return this;
  }

  public RestClient withHeaders(final Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public Optional<String> get(final String url) throws GitChangelogIntegrationException {
    try {
      return this.urlCache.get(url);
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
      conn = this.openConnection(url);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      if (this.headers != null) {
        for (final Entry<String, String> entry : this.headers.entrySet()) {
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }
      if (this.basicAuthString != null) {
        conn.setRequestProperty("Authorization", "Basic " + this.basicAuthString);
      }
      return of(this.getResponse(conn));
    } catch (final Exception e) {
      logger.error("Got:\n" + response, e);
      return empty();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  protected HttpURLConnection openConnection(final URL url) throws Exception {
    if (mockedRestClient == null) {
      return (HttpURLConnection) url.openConnection();
    }
    return mockedRestClient.openConnection(url);
  }

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
