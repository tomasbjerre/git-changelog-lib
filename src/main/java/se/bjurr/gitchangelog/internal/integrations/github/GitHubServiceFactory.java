package se.bjurr.gitchangelog.internal.integrations.github;


import com.google.common.base.Optional;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;

public class GitHubServiceFactory {

 private static GitHubService gitHubService = null;

 public static void reset() {
  gitHubService = null;
 }

 public static GitHubService getGitHubService(String api, final Optional<String> token) {
  return getGitHubService(api, token, null);
 }

 public static GitHubService getGitHubService(String api, final Optional<String> token, final Interceptor interceptor) {
  if (!api.endsWith("/")) {
   api += "/";
  }
  if (gitHubService == null) {
   File cacheDir = new File(".okhttpcache");
   cacheDir.mkdir();
   Cache cache = new Cache(cacheDir, 1024 * 1024 * 10);

   OkHttpClient.Builder builder = new OkHttpClient.Builder().cache(cache);

   if (token != null && token.isPresent() && !token.get().isEmpty()) {
    builder
      .addInterceptor(new Interceptor() {
       @Override
       public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
          .addHeader("Authorization", "token " + token.get())
          .method(original.method(), original.body())
          .build();
        return chain.proceed(request);
       }
      });
   }

   if (interceptor != null) {
    builder.addInterceptor(interceptor);
   }

   Retrofit retrofit = new Retrofit.Builder()
     .baseUrl(api)
     .client(builder.build())
     .addConverterFactory(GsonConverterFactory.create())
     .build();

   gitHubService = retrofit.create(GitHubService.class);
  }
  return gitHubService;
 }
}
