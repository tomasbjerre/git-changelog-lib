package se.bjurr.gitchangelog.internal.integrations.github;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GitHubService {
 @GET("issues?state=all&per_page=100")
 Call<List<GitHubIssue>> issues(@Query("page") int page);

 @GET("issues/{number}")
 Call<GitHubIssue> issue(@Path("number") String number);
}
