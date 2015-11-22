package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RestClient {
 private static Logger logger = getLogger(RestClient.class);
 private static RestClient mockedRestClient;
 private final LoadingCache<String, String> urlCache;

 public RestClient(long duration, TimeUnit cacheExpireAfterAccess) {
  if (duration != 0) {
   urlCache = newBuilder()//
     .expireAfterAccess(duration, cacheExpireAfterAccess)//
     .build(new CacheLoader<String, String>() {
      @Override
      public String load(String url) throws Exception {
       return doGet(url);
      }
     });
  } else {
   urlCache = null;
  }
 }

 public String get(String url) {
  try {
   if (urlCache != null) {
    return urlCache.get(url);
   } else {
    return doGet(url);
   }
  } catch (ExecutionException e) {
   throw propagate(e);
  }
 }

 private String doGet(String urlParam) {
  String response = null;
  try {
   logger.info("GET:\n" + urlParam);
   URL url = new URL(urlParam);
   HttpURLConnection conn = openConnection(url);
   response = getResponse(conn);
   return response;
  } catch (Exception e) {
   logger.error("Got:\n" + response);
   throw propagate(e);
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
