package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;

public class HelpersTest {

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
}
