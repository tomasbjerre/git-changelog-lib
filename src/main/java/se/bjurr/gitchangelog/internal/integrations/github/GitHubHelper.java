package se.bjurr.gitchangelog.internal.integrations.github;

import static com.google.common.base.Splitter.on;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Response;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitHubHelper {

  private static Pattern PAGE_PATTERN = Pattern.compile("page=([0-9]+)>");
  private final GitHubService service;

  public GitHubHelper(final GitHubService service) {
    this.service = service;
  }

  public Optional<GitHubIssue> getIssueFromAll(String issue)
      throws GitChangelogIntegrationException {
    if (issue.startsWith("#")) {
      issue = issue.substring(1);
    }

    int page = 1;
    while (page > 0) {
      final Call<List<GitHubIssue>> call = this.service.issues(page);
      page = -1;

      try {
        final Response<List<GitHubIssue>> response = call.execute();

        if (!response.isSuccessful()) {
          throw new GitChangelogIntegrationException(
              "Request:"
                  + response.raw().request().toString()
                  + "\nError:\n"
                  + response.errorBody().string());
        }

        // Pagination
        if (response.headers().get("Link") != null) {
          final String link = response.headers().get("Link");
          String parsedPage = null;
          PART:
          for (final String part : on(',').split(link)) {
            for (final String piece : on(';').split(part)) {
              if ("rel=\"next\"".equals(piece.trim()) && parsedPage != null) {
                // Previous piece pointed to next
                page = Integer.parseInt(parsedPage);
                break PART;
              } else if (piece.contains("&page=")) {
                final Matcher match = PAGE_PATTERN.matcher(piece);
                if (match.find()) {
                  parsedPage = match.group(1);
                }
              }
            }
          }
        }

        for (final GitHubIssue gitHubIssue : response.body()) {
          if (issue.equals(gitHubIssue.getNumber())) {
            return of(gitHubIssue);
          }
        }

      } catch (final IOException e) {
        throw new GitChangelogIntegrationException(issue, e);
      }
    }
    return empty();
  }
}
