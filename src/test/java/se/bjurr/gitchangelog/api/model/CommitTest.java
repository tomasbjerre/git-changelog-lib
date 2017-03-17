package se.bjurr.gitchangelog.api.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CommitTest {

  private final String messageOnlyOneLineAfterTitle =
      " message title\n * The first item\n *  The second item";
  private final String messageOnlyOneLineAfterTitleAndOneEmptyLine =
      " message title\n *  The first item\n\n * The second item";
  private final String messageOnlyOneLineAfterTitleAndNoStars =
      " message title\nThe first item\n The second item";
  private final String messageOnlyIncidentInItem = " message title\n *  \n\n * This is ok";

  @Test
  public void testThatMessageTitleCanBeTransformedWithIssues() {
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitle)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("message title");
  }

  @Test
  public void testThatMessageTitleCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitle)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("message title");
  }

  @Test
  public void testThatMessageBodyCanBeTransformedWithIssues() {
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");

    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");
  }

  @Test
  public void testThatMessageBodyCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");

    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");
  }

  @Test
  public void testThatMessageItemsCanBeTransformedWithIssues() {
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(newArrayList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(newArrayList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo(newArrayList("The first item", "The second item"));
  }

  @Test
  public void testThatMessageItemsCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitle)) //
        .isEqualTo(newArrayList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(newArrayList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo(newArrayList("The first item", "The second item"));

    assertThat(Commit.toMessageItems(messageOnlyIncidentInItem)) //
        .isEqualTo(newArrayList("This is ok"));
  }
}
