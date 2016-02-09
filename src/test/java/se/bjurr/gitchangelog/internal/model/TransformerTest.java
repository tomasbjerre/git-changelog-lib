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
 private final String messageOnlyOneLineAfterTitle = "INC123 message title\n * The first item\n * INC456 The second item";
 private final String messageOnlyOneLineAfterTitleAndOneEmptyLine = "INC123 message title\n * INC456 The first item\n\n * The second item";
 private final String messageOnlyOneLineAfterTitleAndNoStars = "INC123 message title\nThe first item\nINC456 The second item";
 private final String messageOnlyIncidentInItem = "INC123 message title\n * INC123 \nINC456\n * This is ok";

 @Before
 public void before() {
  noIssues = newArrayList();
  SettingsIssue issue1 = new SettingsIssue("Issue Name", "INC[0-9]*", "http://inc/${PATTERN_GROUP}");
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

 @Test
 public void testThatMessageTitleCanBeTransformedWithIssues() {
  assertThat(transformer.toMessageTitle(false, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(false, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(false, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("INC123 message title");

  assertThat(transformer.toMessageTitle(false, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(false, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(false, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("INC123 message title");
 }

 @Test
 public void testThatMessageTitleCanBeTransformedWithoutIssues() {
  assertThat(transformer.toMessageTitle(true, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo(" message title");
  assertThat(transformer.toMessageTitle(true, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(" message title");
  assertThat(transformer.toMessageTitle(true, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo(" message title");

  assertThat(transformer.toMessageTitle(true, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(true, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo("INC123 message title");
  assertThat(transformer.toMessageTitle(true, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("INC123 message title");
 }

 @Test
 public void testThatMessageBodyCanBeTransformedWithIssues() {
  assertThat(transformer.toMessageBody(false, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo(" * The first item\n * INC456 The second item");
  assertThat(transformer.toMessageBody(false, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(" * INC456 The first item\n * The second item");
  assertThat(transformer.toMessageBody(false, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("The first item\nINC456 The second item");

  assertThat(transformer.toMessageBody(false, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo(" * The first item\n * INC456 The second item");
  assertThat(transformer.toMessageBody(false, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(" * INC456 The first item\n * The second item");
  assertThat(transformer.toMessageBody(false, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("The first item\nINC456 The second item");
 }

 @Test
 public void testThatMessageBodyCanBeTransformedWithoutIssues() {
  assertThat(transformer.toMessageBody(true, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo(" * The first item\n *  The second item");
  assertThat(transformer.toMessageBody(true, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(" *  The first item\n * The second item");
  assertThat(transformer.toMessageBody(true, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("The first item\n The second item");

  assertThat(transformer.toMessageBody(true, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo(" * The first item\n * INC456 The second item");
  assertThat(transformer.toMessageBody(true, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(" * INC456 The first item\n * The second item");
  assertThat(transformer.toMessageBody(true, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo("The first item\nINC456 The second item");
 }

 @Test
 public void testThatMessageItemsCanBeTransformedWithIssues() {
  assertThat(transformer.toMessageItems(false, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));
  assertThat(transformer.toMessageItems(false, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(newArrayList("INC456 The first item", "The second item"));
  assertThat(transformer.toMessageItems(false, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));

  assertThat(transformer.toMessageItems(false, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));
  assertThat(transformer.toMessageItems(false, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(newArrayList("INC456 The first item", "The second item"));
  assertThat(transformer.toMessageItems(false, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));
 }

 @Test
 public void testThatMessageItemsCanBeTransformedWithoutIssues() {
  assertThat(transformer.toMessageItems(true, oneIssue, messageOnlyOneLineAfterTitle))//
    .isEqualTo(newArrayList("The first item", "The second item"));
  assertThat(transformer.toMessageItems(true, oneIssue, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(newArrayList("The first item", "The second item"));
  assertThat(transformer.toMessageItems(true, oneIssue, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo(newArrayList("The first item", "The second item"));

  assertThat(transformer.toMessageItems(true, oneIssue, messageOnlyIncidentInItem))//
    .isEqualTo(newArrayList("This is ok"));

  assertThat(transformer.toMessageItems(true, noIssues, messageOnlyOneLineAfterTitle))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));
  assertThat(transformer.toMessageItems(true, noIssues, messageOnlyOneLineAfterTitleAndOneEmptyLine))//
    .isEqualTo(newArrayList("INC456 The first item", "The second item"));
  assertThat(transformer.toMessageItems(true, noIssues, messageOnlyOneLineAfterTitleAndNoStars))//
    .isEqualTo(newArrayList("The first item", "INC456 The second item"));

  assertThat(transformer.toMessageItems(true, noIssues, messageOnlyIncidentInItem))//
    .isEqualTo(newArrayList("INC123", "INC456", "This is ok"));
 }
}
