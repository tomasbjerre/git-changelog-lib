package se.bjurr.gitchangelog.internal.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class TransformerTest {

 private List<SettingsIssue> noIssues;
 private List<SettingsIssue> oneIssue;
 private Transformer transformer;
 private final String message = "INC123 message title\n\n * INC456 The first item\n * The second item";

 @Before
 public void before() {
  noIssues = newArrayList();
  SettingsIssue issue1 = new SettingsIssue("Issue Name", "INC[0-9]*", "http://inc/${PATTERN_GROUP}", null);
  oneIssue = newArrayList(issue1);
  transformer = new Transformer(new Settings());
 }

 @Test
 public void testThatMessageCanBeTransformedWithIssues() {
  assertThat(transformer.toMessage(false, oneIssue, message))//
    .isEqualTo(message);
  assertThat(transformer.toMessage(false, noIssues, message))//
    .isEqualTo(message);
 }

 @Test
 public void testThatMessageCanBeTransformedWithoutIssues() {
  assertThat(transformer.toMessage(true, oneIssue, message))//
    .isEqualTo(" message title\n\n *  The first item\n * The second item");
  assertThat(transformer.toMessage(true, noIssues, message))//
    .isEqualTo(message);
 }
}
