package se.bjurr.gitchangelog.internal.integrations.github;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.util.Optional;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubServiceFactory {
  static Interceptor interceptor;

  public static void setInterceptor(final Interceptor interceptor) {
    GitHubServiceFactory.interceptor = interceptor;
  }

  public static synchronized GitHubService getGitHubService(
      String api, final Optional<String> token) {
    if (!api.endsWith("/")) {
      api += "/";
    }
    final File cacheDir = new File(".okhttpcache");
    cacheDir.mkdir();
    final Cache cache = new Cache(cacheDir, 1024 * 1024 * 10);

    final OkHttpClient.Builder builder =
        new OkHttpClient.Builder().cache(cache).connectTimeout(10, SECONDS);

    if (token != null && token.isPresent() && !token.get().isEmpty()) {
      builder.addInterceptor(
          chain -> {
            final Request original = chain.request();

            final Request request =
                original
                    .newBuilder() //
                    .addHeader("Authorization", "token " + token.get()) //
                    .method(original.method(), original.body()) //
                    .build();
            return chain.proceed(request);
          });
    }

    if (interceptor != null) {
      builder.addInterceptor(interceptor);
    }

    final Retrofit retrofit =
        new Retrofit.Builder() //
            .baseUrl(api) //
            .client(builder.build()) //
            .addConverterFactory(GsonConverterFactory.create()) //
            .build();

    return retrofit.create(GitHubService.class);
  }
}
