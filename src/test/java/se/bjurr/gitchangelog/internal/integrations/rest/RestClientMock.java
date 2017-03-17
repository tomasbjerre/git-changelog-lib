package se.bjurr.gitchangelog.internal.integrations.rest;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RestClientMock extends RestClient {
  private final Map<String, String> mockedResponses = newHashMap();

  public RestClientMock() {
    super(0, MINUTES);
  }

  public RestClientMock addMockedResponse(String url, String response) {
    mockedResponses.put(url, response);
    return this;
  }

  @Override
  public String getResponse(HttpURLConnection conn) throws Exception {
    String key = conn.getURL().getPath() + "?" + conn.getURL().getQuery();
    if (mockedResponses.containsKey(key)) {
      return mockedResponses.get(key);
    } else {
      throw new RuntimeException(
          "Could not find mock for \""
              + key
              + "\" available mocks are:\n"
              + on("\n").join(mockedResponses.keySet()));
    }
  }

  @Override
  public HttpURLConnection openConnection(URL addr) throws Exception {
    return new HttpURLConnection(addr) {
      @Override
      public Map<String, List<String>> getHeaderFields() {
        Map<String, List<String>> map = newHashMap();
        map.put("Set-Cookie", newArrayList("thesetcookie"));
        return map;
      }

      @Override
      public void connect() throws IOException {}

      @Override
      public boolean usingProxy() {
        return false;
      }

      @Override
      public void disconnect() {}

      @Override
      public OutputStream getOutputStream() throws IOException {
        return new OutputStream() {
          @Override
          public void write(int b) throws IOException {}
        };
      }
    };
  }
}
