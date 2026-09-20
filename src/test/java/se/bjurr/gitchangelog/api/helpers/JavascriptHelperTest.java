package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Registering a custom Handlebars helper written in plain JavaScript. */
public class JavascriptHelperTest extends AbstractHandlebarsHelperTest {

  @Test
  public void testThatHelperCanBeSuppliedWithJavascript() throws Exception {
    final String rendered =
        this.baseBuilder //
            .withTemplateContent(
                """
                {{#commits}}
                  First word: {{#firstWord}}{{message}}{{/firstWord}}

                  All of message:

                  {{message}}
                --------------------------------


                {{/commits}}
                """)
            .withHandlebarsHelper(
                "Handlebars.registerHelper(\"firstWord\", function(options) {"
                    + "  return options.fn(this).split(\" \")[0];"
                    + "});")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
              First word: feat:

              All of message:

              feat: dsc

            paragraph first line of two here!
            second line of first paragraph

            second paragraph the only line

            third paragraph first line of two
            and second line of third paragraph.

            first-token-here: value of first token
            second line of first token
            second-token-here: value of second token
            --------------------------------


              First word: fix:

              All of message:

              fix: correct minor typos in code

            see the issue for details

            on typos fixed.

            Reviewed-by: Z
            Refs #133
            --------------------------------


              First word: feat:

              All of message:

              feat: allow provided config object to extend other configs

            BREAKING CHANGE: &#x60;extends&#x60; key in config file is now used for extending other config files
            --------------------------------


              First word: refactor:

              All of message:

              refactor: using only br

            BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
            --------------------------------


              First word: refactor!:

              All of message:

              refactor!: using both ! and br

            BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
            --------------------------------


              First word: refactor!:

              All of message:

              refactor!: doing major stuff
            --------------------------------


              First word: Revert

              All of message:

              Revert &quot;[Gradle Release Plugin] - pre tag commit:  &#x27;1.94&#x27;.&quot;

            This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.
            --------------------------------


              First word: fix(utils:mix):

              All of message:

              fix(utils:mix): the description (fixes ABC-123)
            --------------------------------


              First word: feat(utils):

              All of message:

              feat(utils): more utils
            --------------------------------


              First word: doing

              All of message:

              doing change 2
            --------------------------------


              First word: New

              All of message:

              New version: 1.147.2 [GRADLE SCRIPT]
            --------------------------------


              First word: fix:

              All of message:

              fix: dont require semantic patterns to get highest tag
            --------------------------------


              First word: Updating

              All of message:

              Updating changelog with 1.147.1 [GRADLE SCRIPT]
            --------------------------------


              First word: New

              All of message:

              New version: 1.147.1 [GRADLE SCRIPT]
            --------------------------------


              First word: fix:

              All of message:

              fix: dont use integrations when determining versions
            --------------------------------


            """);
  }
}
