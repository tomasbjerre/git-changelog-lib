package se.bjurr.gitchangelog.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("message title");
  }

  @Test
  public void testThatMessageTitleCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo("message title");
    assertThat(Commit.toMessageTitle(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("message title");
  }

  @Test
  public void testThatMessageBodyCanBeTransformedWithIssues() {
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");

    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");
  }

  @Test
  public void testThatMessageBodyCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");

    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(" * The first item\n *  The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(" *  The first item\n * The second item");
    assertThat(Commit.toMessageBody(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo("The first item\n The second item");
  }

  @Test
  public void testThatMessageItemsCanBeTransformedWithIssues() {
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));
  }

  @Test
  public void testThatMessageItemsCanBeTransformedWithoutIssues() {
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitle)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitleAndOneEmptyLine)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));
    assertThat(Commit.toMessageItems(this.messageOnlyOneLineAfterTitleAndNoStars)) //
        .isEqualTo(Arrays.asList("The first item", "The second item"));

    assertThat(Commit.toMessageItems(this.messageOnlyIncidentInItem)) //
        .isEqualTo(Arrays.asList("This is ok"));
  }
}
