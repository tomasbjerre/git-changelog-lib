package se.bjurr.gitchangelog.internal.mediawiki;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.net.URLEncoder.encode;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.slf4j.Logger;

import com.google.common.annotations.VisibleForTesting;
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

 private String username;
 private String password;
 private final String url;
 private final String title;
 private final String text;

 public MediaWikiClient(String url, String title, String text) {
  this.url = url;
  this.title = title;
  this.text = text;
 }

 public MediaWikiClient withUser(String username, String password) {
  this.username = username;
  this.password = password;
  return this;
 }

 public void createMediaWikiPage() {
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

 private void useAnonymousToken(HttpState httpState) {
  httpState.setEditToken(DEFAULT_ANONYMOUS_EDIT_TOKEN);
 }

 private void createPage(HttpState httpState, String url, String title, String text) throws Exception {
  URL wikiurl = new URL(url + "/api.php");
  HttpURLConnection conn = openConnection(wikiurl);
  try {
   conn.setRequestMethod("POST");

   Map<String, String> params = newHashMap();
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

   StringBuilder querySb = new StringBuilder();
   for (Entry<String, String> e : params.entrySet()) {
    querySb.append("&" + e.getKey() + "=" + encode(e.getValue(), UTF_8.name()));
   }
   String query = querySb.toString().substring(1);

   OutputStream output = conn.getOutputStream();
   output.write(query.getBytes(UTF_8.name()));
   String response = getResponse(conn);
   logger.info("Got: " + response);
  } finally {
   conn.disconnect();
  }

 }

 private void getEditToken(HttpState httpState, String url, String title) throws Exception {
  String response = postToWiki(httpState, url
    + "/api.php?action=query&prop=info%7Crevisions&intoken=edit&rvprop=timestamp&titles=" + encode(title, UTF_8.name())
    + "&format=json");
  logger.info("Response:\n" + response);
  String token = getValueOf("edittoken", response);
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

 private void doAuthenticate(HttpState httpState, String url, String username, String password) throws Exception {
  String response = postToWiki(httpState, url + "/api.php?action=login&lgname=" + username + "&lgpassword=" + password
    + "&format=json");
  getWikiToken(httpState, response);
  response = postToWiki(httpState, url + "/api.php?action=login&lgname=" + username + "&lgpassword=" + password
    + "&format=json&lgtoken=" + httpState.getWikiToken());
 }

 private void getWikiToken(HttpState httpState, String response) {
  logger.info("Response:\n" + response);
  String token = getValueOf("token", response);
  logger.info("Using wikitoken: " + token);
  httpState.setWikiToken(token);
 }

 private String getValueOf(String attribute, String json) {
  String regexp = "\"" + attribute + "\"[^\"]*\"([^\"]*)";
  try {
   Matcher matcher = compile(regexp).matcher(json);
   matcher.find();
   return matcher.group(1);
  } catch (Exception e) {
   throw new RuntimeException("Could not find \"" + attribute + "\" with regexp " + regexp + " in \"" + json + "\"", e);
  }
 }

 private String postToWiki(HttpState httpState, String addr) throws Exception {
  HttpURLConnection conn = openConnection(new URL(addr));
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

   return getResponse(conn);
  } finally {
   conn.disconnect();
  }
 }

 @VisibleForTesting
 HttpURLConnection openConnection(URL url) throws Exception {
  return (HttpURLConnection) url.openConnection();
 }

 @VisibleForTesting
 String getResponse(HttpURLConnection conn) throws Exception {
  return new String(toByteArray(conn.getInputStream()));
 }

 private boolean shouldAuthenticate(String username, String password) {
  return !isNullOrEmpty(username) && !isNullOrEmpty(password);
 }

}
