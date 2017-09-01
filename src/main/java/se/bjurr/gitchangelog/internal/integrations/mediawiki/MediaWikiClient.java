package se.bjurr.gitchangelog.internal.integrations.mediawiki;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.jayway.jsonpath.JsonPath.read;
import static java.net.URLEncoder.encode;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.base.Optional;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

public class MediaWikiClient extends RestClient {

  private static final String DEFAULT_ANONYMOUS_EDIT_TOKEN = "+\\";
  private static Logger logger = getLogger(MediaWikiClient.class);

  private static class HttpState {
    private String cookieString;
    private String editToken;
    private String loginToken;

    public HttpState() {}

    public void setCookieString(String cookieString) {
      this.cookieString = cookieString;
    }

    public Optional<String> getCookieString() {
      return fromNullable(cookieString);
    }

    public String getEditToken() {
      return editToken;
    }

    public void setEditToken(String token) {
      this.editToken = token;
    }

    public String getLoginToken() {
      return loginToken;
    }

    public void setLoginToken(String loginToken) {
      this.loginToken = loginToken;
    }
  }

  private String username;
  private String password;
  private final String url;
  private final String title;
  private final String text;

  public MediaWikiClient(String url, String title, String text) {
    super(0, MINUTES);
    this.url = url;
    this.title = title;
    this.text = text;
  }

  public MediaWikiClient withUser(String username, String password) {
    this.username = username;
    this.password = password;
    return this;
  }

  public void createMediaWikiPage() throws GitChangelogIntegrationException {
    checkNotNull(title, "No title set for MediaWiki");
    try {
      final HttpState httpState = new HttpState();
      if (shouldAuthenticate(username, password)) {
        logger.info("Authenticating to " + url);
        doAuthenticate(httpState, url, username, password);
        getEditToken(httpState, url, title);
      } else {
        useAnonymousToken(httpState);
      }
      logger.info("Using edit token " + httpState.getEditToken());
      createPage(httpState, url, title, text);
      logger.info("Created " + url + "/index.php/" + title);
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException(url + " " + title, e);
    }
  }

  private void useAnonymousToken(HttpState httpState) {
    httpState.setEditToken(DEFAULT_ANONYMOUS_EDIT_TOKEN);
  }

  private void createPage(HttpState httpState, String url, String title, String text)
      throws Exception {
    final URL wikiurl = new URL(url + "/api.php");
    final HttpURLConnection conn = openConnection(wikiurl);
    try {
      conn.setRequestMethod("POST");

      final Map<String, String> params = newHashMap();
      params.put("format", "json");
      params.put("token", httpState.getEditToken());
      params.put("action", "edit");
      params.put("title", title);
      params.put("summary", "Git Changelog Plugin");
      params.put("text", escapeXml(text));
      if (httpState.getCookieString().isPresent()) {
        conn.setRequestProperty("Cookie", httpState.getCookieString().get());
      }
      conn.setDoOutput(true);
      conn.connect();

      final StringBuilder querySb = new StringBuilder();
      for (final Entry<String, String> e : params.entrySet()) {
        querySb.append("&" + e.getKey() + "=" + encode(e.getValue(), UTF_8.name()));
      }
      final String query = querySb.toString().substring(1);

      final OutputStream output = conn.getOutputStream();
      output.write(query.getBytes(UTF_8.name()));
      final String response = getResponse(conn);
      logger.info("Got: " + response);
    } finally {
      conn.disconnect();
    }
  }

  private void getEditToken(HttpState httpState, String url, String title) throws Exception {
    final String response =
        postToWiki(
            httpState,
            url
                + "/api.php?action=query&meta=tokens&prop=info%7Crevisions&meta=tokens&rvprop=timestamp&titles="
                + encode(title, UTF_8.name())
                + "&format=json");
    logger.info("Response edit:\n" + response);
    final String token = doReadFirst(response, "$..csrftoken");
    httpState.setEditToken(unEscapeJson(token));
  }

  private String unEscapeJson(String token) {
    return token.replaceAll("\\\\\\\\", "\\\\");
  }

  private String escapeXml(String s) {
    return s //
        .replaceAll("\\u00C3\\u00A5", "&aring;") //
        .replaceAll("\\u00C3\\u00A4", "&auml;") //
        .replaceAll("\\u00C3\\u00B6", "&ouml;") //
        .replaceAll("\\u00C3\\u0085", "&Aring;") //
        .replaceAll("\\u00C3\\u0084", "&Auml;") //
        .replaceAll("\\u00C3\\u0096", "&Ouml;") //
        .replaceAll("(?m)^ +", "");
  }

  private void doAuthenticate(HttpState httpState, String url, String username, String password)
      throws Exception {
    String response =
        postToWiki(httpState, url + "/api.php?action=query&meta=tokens&format=json&type=login");
    getLoginToken(httpState, response);

    final String postContent =
        "lgname="
            + encode(username, UTF_8.name())
            + "&lgpassword="
            + encode(password, UTF_8.name())
            + "&lgtoken="
            + encode(httpState.getLoginToken(), UTF_8.name());
    response = postToWiki(httpState, url + "/api.php?action=login&format=json", postContent);
    doRead(response, "$.login.token");
  }

  private void getLoginToken(HttpState httpState, String response) {
    final String firstElementString = doReadFirst(response, "$..logintoken");
    logger.info("Using logintoken: " + firstElementString);
    httpState.setLoginToken(firstElementString);
  }

  private String doReadFirst(String response, String jsonPath) {
    try {
      final JSONArray tokens = read(response, jsonPath);
      final String firstElementString = tokens.get(0).toString();
      return firstElementString;
    } catch (final Exception e) {
      throw new RuntimeException(response + " " + jsonPath, e);
    }
  }

  private String doRead(String response, String jsonPath) {
    try {
      return read(response, jsonPath);
    } catch (final Exception e) {
      throw new RuntimeException(response + " " + jsonPath, e);
    }
  }

  private String postToWiki(HttpState httpState, String addr, String postContent)
      throws GitChangelogIntegrationException {
    try {
      logger.info("Posting to: " + addr);
      final HttpURLConnection conn = openConnection(new URL(addr));
      try {
        conn.setRequestMethod("POST");
        if (httpState.getCookieString().isPresent()) {
          logger.info("Using cookie: " + httpState.getCookieString().get());
          conn.setRequestProperty("Cookie", httpState.getCookieString().get());
        }

        if (postContent != null) {
          final String postContentObf =
              postContent.replaceAll("lgpassword=([^&]+)", "lgpassword=*");
          logger.info("Post content: " + postContentObf);
          conn.setDoOutput(true);
          conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          conn.setRequestProperty("charset", "utf-8");
          conn.setRequestProperty("Content-Length", postContent.length() + "");
          conn.setUseCaches(false);
          try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.writeUTF(postContent);
          }
        }

        if (conn.getHeaderFields().get("Set-Cookie") != null
            && conn.getHeaderFields().get("Set-Cookie").size() > 0
            && !httpState.getCookieString().isPresent()) {
          httpState.setCookieString(conn.getHeaderFields().get("Set-Cookie").get(0));
          logger.info("Got cookie: " + httpState.getCookieString().orNull());
        }

        final String response = getResponse(conn);
        logger.info("Response: " + response);
        return response;
      } finally {
        conn.disconnect();
      }
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException(addr, e);
    }
  }

  private String postToWiki(HttpState httpState, String addr)
      throws GitChangelogIntegrationException {
    return postToWiki(httpState, addr, null);
  }

  private boolean shouldAuthenticate(String username, String password) {
    return !isNullOrEmpty(username) && !isNullOrEmpty(password);
  }
}
