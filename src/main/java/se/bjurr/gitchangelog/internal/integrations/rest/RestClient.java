package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.ByteStreams.toByteArray;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RestClient {
 private static Logger logger = getLogger(RestClient.class);
 private static RestClient mockedRestClient;
 private final LoadingCache<String, Optional<String>> urlCache;
 private String basicAuthString;

 public RestClient(long duration, TimeUnit cacheExpireAfterAccess) {
  urlCache = newBuilder()//
    .expireAfterAccess(duration, cacheExpireAfterAccess)//
    .build(new CacheLoader<String, Optional<String>>() {
     @Override
     public Optional<String> load(String url) throws Exception {
      return doGet(url);
     }
    });
 }

 public RestClient withBasicAuthCredentials(String username, String password) {
  this.basicAuthString = new String(printBase64Binary((username + ":" + password).getBytes()));
  return this;
 }

 public Optional<String> get(String url) {
  try {
   return urlCache.get(url);
  } catch (ExecutionException e) {
   throw propagate(e);
  }
 }

 private Optional<String> doGet(String urlParam) {
  String response = null;
  try {
   logger.info("GET:\n" + urlParam);
   URL url = new URL(urlParam);
   HttpURLConnection conn = openConnection(url);
   conn.setRequestProperty("Content-Type", "application/json");
   conn.setRequestProperty("Accept", "application/json");
   if (this.basicAuthString != null) {
    conn.setRequestProperty("Authorization", "Basic " + basicAuthString);
   }
   return of(getResponse(conn));
  } catch (Exception e) {
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
   return new String(toByteArray(conn.getInputStream()));
  }
  return mockedRestClient.getResponse(conn);
 }

 public static void mock(RestClient mock) {
  mockedRestClient = mock;
 }
}
