package se.bjurr.gitchangelog.internal.integrations.github;

import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GitHubMockInterceptor implements Interceptor {

  private final Map<String, String> mockedResponses = new TreeMap<>();

  public GitHubMockInterceptor addMockedResponse(final String url, final String response) {
    this.mockedResponses.put(url, response);
    return this;
  }

  @Override
  public Response intercept(final Chain chain) throws IOException {
    final Request original = chain.request();

    // Get Request URI.
    final String url = chain.request().url().toString();

    if (this.mockedResponses.containsKey(url)) {
      return new Response.Builder()
          .code(200)
          .message(this.mockedResponses.get(url))
          .request(original)
          .protocol(Protocol.HTTP_1_0)
          .body(
              ResponseBody.create(
                  MediaType.parse("application/json"), this.mockedResponses.get(url).getBytes()))
          .addHeader("content-type", "application/json")
          .build();
    }

    fail("No mocked response for: " + url);
    return null;
  }
}
