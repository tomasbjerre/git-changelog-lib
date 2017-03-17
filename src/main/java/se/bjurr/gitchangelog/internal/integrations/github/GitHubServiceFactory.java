package se.bjurr.gitchangelog.internal.integrations.github;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.base.Optional;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubServiceFactory {

  private static GitHubService gitHubService = null;

  public static void reset() {
    gitHubService = null;
  }

  public static GitHubService getGitHubService(String api, final Optional<String> token) {
    return getGitHubService(api, token, null);
  }

  public static synchronized GitHubService getGitHubService(
      String api, final Optional<String> token, final Interceptor interceptor) {
    if (!api.endsWith("/")) {
      api += "/";
    }
    if (gitHubService == null) {
      File cacheDir = new File(".okhttpcache");
      cacheDir.mkdir();
      Cache cache = new Cache(cacheDir, 1024 * 1024 * 10);

      OkHttpClient.Builder builder =
          new OkHttpClient.Builder().cache(cache).connectTimeout(10, SECONDS);

      if (token != null && token.isPresent() && !token.get().isEmpty()) {
        builder.addInterceptor(
            new Interceptor() {
              @Override
              public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request =
                    original
                        .newBuilder() //
                        .addHeader("Authorization", "token " + token.get()) //
                        .method(original.method(), original.body()) //
                        .build();
                return chain.proceed(request);
              }
            });
      }

      if (interceptor != null) {
        builder.addInterceptor(interceptor);
      }

      Retrofit retrofit =
          new Retrofit.Builder() //
              .baseUrl(api) //
              .client(builder.build()) //
              .addConverterFactory(GsonConverterFactory.create()) //
              .build();

      gitHubService = retrofit.create(GitHubService.class);
    }
    return gitHubService;
  }
}
