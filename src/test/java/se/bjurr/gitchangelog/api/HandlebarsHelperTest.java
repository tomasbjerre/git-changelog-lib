package se.bjurr.gitchangelog.api;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.test.ApprovalsWrapper;

public class HandlebarsHelperTest {
  private GitChangelogApi baseBuilder;

  @Before
  public void before() {
    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withFromRepo(".") //
            .withFromCommit("1edc0d7") //
            .withToCommit("e78a62f");
  }

  @Test
  public void testThatHelperCanBeSuppliedWithJavascript() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder //
            .withTemplatePath("templatetest/testThatHelperCanBeSuppliedWithJavascript.mustache")
            .withHandlebarsHelper(
                "Handlebars.registerHelper(\"firstWord\", function(options) {"
                    + "  return options.fn(this).split(\" \")[0];"
                    + "});");

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatBuiltInHelperMethodsCanBeUsed() throws Exception {
	    final GitChangelogApi given =
	            this.baseBuilder //
	            .withFromCommit("ed95e6a") //
	            .withToCommit("d346292") //
	                .withTemplatePath("templatetest/testThatBuiltInHelperMethodsCanBeUsed.mustache");

	        ApprovalsWrapper.verify(given);
  }
}
