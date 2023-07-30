package se.bjurr.gitchangelog.internal.integrations.rest;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RestClient {
  private static Logger logger = getLogger(RestClient.class);
  private static RestClient mockedRestClient;
  private final Map<String, Optional<String>> urlCache = new ConcurrentHashMap<>();
  private String basicAuthString;
  private String bearer;
  private Map<String, String> headers;

  public RestClient() {}

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

  public RestClient withBearer(final String bearer) {
    this.bearer = bearer;
    return this;
  }

  public RestClient withHeaders(final Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public Optional<String> get(final String url) throws GitChangelogIntegrationException {
    try {
      if (!this.urlCache.containsKey(url)) {
        final Optional<String> content = RestClient.this.doGet(url);
        this.urlCache.put(url, content);
      }
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
      if (this.bearer != null) {
        conn.setRequestProperty("Authorization", "Bearer " + this.bearer);
      } else if (this.basicAuthString != null) {
        conn.setRequestProperty("Authorization", "Basic " + this.basicAuthString);
      }
      return Optional.of(this.getResponse(conn));
    } catch (final Exception e) {
      logger.error("Got:\n" + response, e);
      return Optional.empty();
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
      final InputStream inputStream = conn.getInputStream();
      final InputStreamReader inputStreamReader =
          new InputStreamReader(inputStream, StandardCharsets.UTF_8);
      try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
        return bufferedReader.readLine();
      }
    }
    return mockedRestClient.getResponse(conn);
  }

  public static void mock(final RestClient mock) {
    mockedRestClient = mock;
  }
}
