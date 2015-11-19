package se.bjurr.gitchangelog.internal.mediawiki;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.net.URLEncoder.encode;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.google.common.base.Optional;

public class MediaWikiClient {

 private static final String DEFAULT_ANONYMOUS_EDIT_TOKEN = "+\\";
 private static Logger logger = getLogger(MediaWikiClient.class);

 private static class HttpState {
  private String cookieString;
  private String editToken;
  private String wikiToken;

  public HttpState() {
  }

  public void setCookieString(String cookieString) {
   this.cookieString = cookieString;
  }

  public Optional<String> getCookieString() {
   return fromNullable(cookieString);
  }

  public void setWikiToken(String wikiToken) {
   this.wikiToken = wikiToken;
  }

  public String getEditToken() {
   return editToken;
  }

  public void setEditToken(String token) {
   this.editToken = token;
  }

  public String getWikiToken() {
   return wikiToken;
  }
 }

 public static void createMediaWikiPage(String username, String password, String url, String title, String text) {
  checkNotNull(title, "No title set for MediaWiki");
  try {
   HttpState httpState = new HttpState();
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
  } catch (Exception e) {
   propagate(e);
  }
 }

 private static void useAnonymousToken(HttpState httpState) {
  httpState.setEditToken(DEFAULT_ANONYMOUS_EDIT_TOKEN);
 }

 private static void createPage(HttpState httpState, String url, String title, String text) throws Exception {
  URL wikiurl = new URL(url + "/api.php");
  HttpURLConnection conn = (HttpURLConnection) wikiurl.openConnection();
  try {
   conn.setRequestMethod("POST");

   Map<String, String> params = newHashMap();
   params.put("format", "json");
   params.put("token", httpState.getEditToken());
   params.put("action", "edit");
   params.put("title", title);
   params.put("summary", "Git Changelog Plugin");
   params.put("text", escapeXml(text));
   conn.setRequestProperty("Cookie", httpState.getCookieString().get());
   conn.setDoOutput(true);
   conn.connect();

   StringBuilder querySb = new StringBuilder();
   for (Entry<String, String> e : params.entrySet()) {
    querySb.append("&" + e.getKey() + "=" + encode(e.getValue(), UTF_8.name()));
   }
   String query = querySb.toString().substring(1);

   OutputStream output = conn.getOutputStream();
   output.write(query.getBytes(UTF_8.name()));
   InputStream is = conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream();
   try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
     stringBuilder.append(line + "\n");
    }
    logger.info("Got: " + stringBuilder.toString());
   } catch (Exception e) {
    propagate(e);
   }
  } finally {
   conn.disconnect();
  }

 }

 private static void getEditToken(HttpState httpState, String url, String title) throws Exception {
  String response = postToWiki(httpState, url
    + "/api.php?action=query&prop=info%7Crevisions&intoken=edit&rvprop=timestamp&titles=" + encode(title, UTF_8.name())
    + "&format=json");
  logger.info("Response:\n" + response);
  String befinAfter = "\"edittoken\":\"";
  String beginningRemoved = response.substring(response.lastIndexOf(befinAfter) + befinAfter.length());
  String token = beginningRemoved.substring(0, beginningRemoved.indexOf("\""));
  httpState.setEditToken(unEscapeJson(token));
 }

 private static String unEscapeJson(String token) {
  return token.replaceAll("\\\\\\\\", "\\\\");
 }

 private static String escapeXml(String s) {
  return s //
    .replaceAll("<", "&lt;") //
    .replaceAll(">/", "&gt;") //
    .replaceAll("\\u00C3\\u00A5", "&aring;") //
    .replaceAll("\\u00C3\\u00A4", "&auml;") //
    .replaceAll("\\u00C3\\u00B6", "&ouml;") //
    .replaceAll("\\u00C3\\u0085", "&Aring;") //
    .replaceAll("\\u00C3\\u0084", "&Auml;") //
    .replaceAll("\\u00C3\\u0096", "&Ouml;") //
    .replaceAll("(?m)^ +", "");
 }

 private static void doAuthenticate(HttpState httpState, String url, String username, String password) throws Exception {
  String response = postToWiki(httpState, url + "/api.php?action=login&lgname=" + username + "&lgpassword=" + password
    + "&format=json");
  getWikiToken(httpState, response);
  response = postToWiki(httpState, url + "/api.php?action=login&lgname=" + username + "&lgpassword=" + password
    + "&format=json&lgtoken=" + httpState.getWikiToken());
 }

 private static void getWikiToken(HttpState httpState, String response) {
  String befinAfter = "\"token\":\"";
  String beginningRemoved = response.substring(response.lastIndexOf(befinAfter) + befinAfter.length());
  String token = beginningRemoved.substring(0, beginningRemoved.indexOf("\""));
  logger.info("Response:\n" + response);
  logger.info("Using wikitoken: " + token);
  httpState.setWikiToken(token);
 }

 private static String postToWiki(HttpState httpState, String addr) throws Exception {
  HttpURLConnection conn = (HttpURLConnection) new URL(addr).openConnection();
  try {
   conn.setRequestMethod("POST");
   if (httpState.getCookieString().isPresent()) {
    conn.setRequestProperty("Cookie", httpState.getCookieString().get());
   }

   if (conn.getHeaderFields().get("Set-Cookie") != null && conn.getHeaderFields().get("Set-Cookie").size() > 0
     && !httpState.getCookieString().isPresent()) {
    httpState.setCookieString(conn.getHeaderFields().get("Set-Cookie").get(0));
    logger.info("Got edit cookie: " + httpState.getCookieString().orNull());
   }

   return new String(toByteArray(conn.getInputStream()));
  } finally {
   conn.disconnect();
  }
 }

 private static boolean shouldAuthenticate(String username, String password) {
  return !isNullOrEmpty(username) && !isNullOrEmpty(password);
 }

}
