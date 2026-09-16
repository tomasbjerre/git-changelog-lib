package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.model.Tag;

public class HelpersTest {

  private static Tag tag(final String name, final long tagTimeLong) {
    return new Tag(
        name,
        "",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Long.toString(tagTimeLong),
        tagTimeLong);
  }

  private String render(final String template, final Object context) throws Exception {
    final Handlebars handlebars = new Handlebars();
    for (final Entry<String, Helper<?>> helper : Helpers.getAll().entrySet()) {
      handlebars.registerHelper(helper.getKey(), helper.getValue());
    }
    final Template compiled = handlebars.compileInline(template);
    return compiled.apply(Context.newContext(context));
  }

  @Test
  public void testSubStringWithPositiveIndices() throws Exception {
    assertThat(this.render("{{subString . 0 3}}", "abcdef")).isEqualTo("abc");
    assertThat(this.render("{{subString . 3}}", "abcdef")).isEqualTo("def");
  }

  @Test
  public void testSubStringWithNegativeEndIndexDropsLastCharacter() throws Exception {
    assertThat(this.render("{{subString . 0 -1}}", "[some value]")).isEqualTo("[some value");
  }

  @Test
  public void testSubStringWithNegativeStartAndEndIndicesStripsBrackets() throws Exception {
    assertThat(this.render("{{subString . 1 -1}}", "[some value]")).isEqualTo("some value");
  }

  @Test
  public void testSubStringWithNegativeStartIndexOnly() throws Exception {
    assertThat(this.render("{{subString . -3}}", "abcdef")).isEqualTo("def");
  }

  @Test
  public void testSubStringWithNegativeIndexBeyondStringLengthClampsToStart() throws Exception {
    assertThat(this.render("{{subString . -100}}", "abc")).isEqualTo("abc");
  }

  @Test
  public void testPreviousAndNextTagNameResolveByTagTimeRegardlessOfListOrder() throws Exception {
    final Tag v1 = tag("v1.0.0", 100L);
    final Tag v2 = tag("v2.0.0", 200L);
    final Tag v3 = tag("v3.0.0", 300L);
    // Rendered newest-first, as the real changelog does.
    final List<Tag> tags = java.util.Arrays.asList(v3, v2, v1);
    final Map<String, Object> context = new HashMap<>();
    context.put("tags", tags);

    final String template =
        "{{#tags}}{{name}}:prev={{previousTagName .}}:next={{nextTagName .}} {{/tags}}";

    assertThat(this.render(template, context))
        .isEqualTo(
            "v3.0.0:prev=v2.0.0:next= "
                + "v2.0.0:prev=v1.0.0:next=v3.0.0 "
                + "v1.0.0:prev=:next=v2.0.0 ");
  }

  @Test
  public void testPreviousAndNextTagAreUsableAsArgumentsToOtherHelpers() throws Exception {
    final Tag v1 = tag("v1.0.0", 100L);
    final Tag v2 = tag("v2.0.0", 200L);
    final List<Tag> tags = java.util.Arrays.asList(v2, v1);
    final Map<String, Object> context = new HashMap<>();
    context.put("tags", tags);

    final String template =
        "{{#tags}}{{#ifEquals name \"v2.0.0\"}}{{tagDate (previousTag .)}}{{/ifEquals}}{{/tags}}";

    assertThat(this.render(template, context)).isEqualTo(this.render("{{tagDate .}}", v1));
  }
}
