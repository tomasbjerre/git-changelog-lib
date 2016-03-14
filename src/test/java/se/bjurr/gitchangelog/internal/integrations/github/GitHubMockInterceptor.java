package se.bjurr.gitchangelog.internal.integrations.github;


import okhttp3.*;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.fail;

public class GitHubMockInterceptor implements Interceptor {

 private final Map<String, String> mockedResponses = newHashMap();

 public GitHubMockInterceptor addMockedResponse(String url, String response) {
  mockedResponses.put(url, response);
  return this;
 }

 @Override
 public Response intercept(Chain chain) throws IOException {
  Request original = chain.request();

  // Get Request URI.
  String url = chain.request().url().toString();

  if (mockedResponses.containsKey(url)) {
   return new Response.Builder()
     .code(200)
     .message(mockedResponses.get(url))
     .request(original)
     .protocol(Protocol.HTTP_1_0)
     .body(ResponseBody.create(MediaType.parse("application/json"), mockedResponses.get(url).getBytes()))
     .addHeader("content-type", "application/json")
     .build();
  }

  fail("No mocked response for: " + url);
  return null;
 }
}
