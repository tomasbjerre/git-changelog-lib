package se.bjurr.gitchangelog.internal.integrations.github;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {
  @GET("issues?state=all&per_page=100")
  Call<List<GitHubIssue>> issues(@Query("page") int page);
}
