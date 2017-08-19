package se.bjurr.gitchangelog.internal.integrations.mediawiki;

import org.junit.Before;
import org.junit.Test;

/**
 * This test assumes that MediaWiki is running at: http://localhost:8888/w
 *
 * <p>It can be setup with: https://github.com/pastakhov/compose-mediawiki-ubuntu/
 */
public class MediaWikiClientTest {

  @Before
  public void before() {}

  @Test
  public void testWithUser() throws Exception {
    final MediaWikiClient mediaWikiClient =
        new MediaWikiClient(
            "http://localhost:8889/w",
            "the title with user",
            "some text with user " + Math.random());
    mediaWikiClient //
        .withUser("User@newbot", "3s80m5vam1g8d6vtjpmpv5narcuhev74") //
        .createMediaWikiPage();
  }

  @Test
  public void testWithoutUser() throws Exception {
    final MediaWikiClient mediaWikiClient =
        new MediaWikiClient(
            "http://localhost:8888/w",
            "the title without user",
            "some text without user " + Math.random());
    mediaWikiClient //
        .createMediaWikiPage();
  }
}
