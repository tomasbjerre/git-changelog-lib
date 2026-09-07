package se.bjurr.gitchangelog.internal.integrations.github;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubServiceFactory {
  /**
   * Where the HTTP responses from GitHub are cached. It used to be ".okhttpcache" relative to the
   * working directory, which meant that generating a changelog littered the repository it was
   * generated for.
   */
  public static final String CACHE_DIR_PROPERTY = "se.bjurr.gitchangelog.okhttp.cache.dir";

  private static final int CACHE_SIZE_BYTES = 1024 * 1024 * 10;
  private static final long CONNECT_TIMEOUT_SECONDS = 10;
  private static final long READ_TIMEOUT_SECONDS = 30;

  static Interceptor interceptor; // NOPMD

  /** Shared, so that the connection pool and the response cache are reused between requests. */
  private static OkHttpClient sharedClient;

  public static void setInterceptor(final Interceptor interceptor) {
    GitHubServiceFactory.interceptor = interceptor;
  }

  public static synchronized GitHubService getGitHubService(
      String api, final Optional<String> token) {
    if (!api.endsWith("/")) {
      api += "/";
    }

    final OkHttpClient.Builder builder = getSharedClient().newBuilder();

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

  private static synchronized OkHttpClient getSharedClient() {
    if (sharedClient == null) {
      sharedClient =
          new OkHttpClient.Builder()
              .cache(new Cache(getCacheDir(), CACHE_SIZE_BYTES))
              .connectTimeout(CONNECT_TIMEOUT_SECONDS, SECONDS)
              .readTimeout(READ_TIMEOUT_SECONDS, SECONDS)
              .build();
    }
    return sharedClient;
  }

  private static File getCacheDir() {
    final String configured = System.getProperty(CACHE_DIR_PROPERTY);
    final File cacheDir =
        configured != null
            ? new File(configured)
            : Paths.get(System.getProperty("java.io.tmpdir"), "git-changelog-lib-okhttpcache")
                .toFile();
    if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
      throw new IllegalStateException("Cannot create cache directory " + cacheDir);
    }
    return cacheDir;
  }
}
