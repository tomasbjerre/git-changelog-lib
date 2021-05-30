package se.bjurr.gitchangelog.internal.integrations.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RestClientMock extends RestClient {
  private final Map<String, String> mockedResponses = new TreeMap<>();

  public RestClientMock() {}

  public RestClientMock addMockedResponse(final String url, final String response) {
    this.mockedResponses.put(url, response);
    return this;
  }

  @Override
  public String getResponse(final HttpURLConnection conn) throws Exception {
    final String key = conn.getURL().getPath() + "?" + conn.getURL().getQuery();
    if (this.mockedResponses.containsKey(key)) {
      return this.mockedResponses.get(key);
    } else {
      throw new RuntimeException(
          "Could not find mock for \""
              + key
              + "\" available mocks are:\n"
              + this.mockedResponses.keySet().stream().collect(Collectors.joining("\n")));
    }
  }

  @Override
  public HttpURLConnection openConnection(final URL addr) throws Exception {
    return new HttpURLConnection(addr) {
      @Override
      public Map<String, List<String>> getHeaderFields() {
        final Map<String, List<String>> map = new TreeMap<>();
        map.put("Set-Cookie", Arrays.asList("thesetcookie"));
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
          public void write(final int b) throws IOException {}
        };
      }
    };
  }
}
