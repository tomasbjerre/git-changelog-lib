package se.bjurr.gitchangelog.internal.integrations.mediawiki;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

public class MediaWikiClientTest {

 private MediaWikiClient mediaWikiClient;
 private RestClientMock mockedMediaWikiClient;

 @Before
 public void before() {
  mediaWikiClient = new MediaWikiClient("http://url", "the title", "some text");
  mockedMediaWikiClient = new RestClientMock();
  RestClient.mock(mockedMediaWikiClient);
 }

 @Test
 public void testWithUser() throws Exception {
  mockedMediaWikiClient
    .addMockedResponse(
      "/api.php?action=login&lgname=username&lgpassword=password&format=json",
      "{\"login\":{\"result\":\"NeedToken\", \n \"token\":\n\"9262cbea833c2f486c46de85d2a1ce1c\" \n,\"cookieprefix\":\"my_wiki\",\"sessionid\":\"54150481260436601c815fca1f5151b9\"}}");
  mockedMediaWikiClient
    .addMockedResponse(
      "/api.php?action=login&lgname=username&lgpassword=password&format=json&lgtoken=9262cbea833c2f486c46de85d2a1ce1c",
      "{\"login\":{\"result\":\"Success\",\"lguserid\":1,\"lgusername\":\"Tomas\",\n \"lgtoken\":\n\"9bf282b3513874d03253a171676f6edc\" ,\"cookieprefix\":\"my_wiki\",\"sessionid\":\"54150481260436601c815fca1f5151b9\"}}");
  mockedMediaWikiClient
    .addMockedResponse(
      "/api.php?action=query&prop=info%7Crevisions&intoken=edit&rvprop=timestamp&titles=the+title&format=json",
      "{\"query\":{\"pages\":{\"3\":{\"pageid\":3,\"ns\":0,\"title\":\"Tomas Title\",\"touched\":\"2015-11-21T07:38:57Z\",\"lastrevid\":46,\"counter\":35,\"length\":5993,\"starttimestamp\":\"2015-11-21T07:39:42Z\",\n \"edittoken\"\n: \"a7ac6516e756ebd932579ead6fe3a878+\\\\\" ,\"revisions\":[{\"timestamp\":\"2015-11-21T07:18:24Z\"}]}}}}");
  mockedMediaWikiClient.addMockedResponse("/api.php?null",
    "{\"edit\":{\"result\":\"Success\",\"pageid\":3,\"title\":\"Tomas Title\",\"nochange\":\"\"}}");

  this.mediaWikiClient //
    .withUser("username", "password") //
    .createMediaWikiPage();
 }

 @Test
 public void testWithoutUser() throws Exception {
  mockedMediaWikiClient.addMockedResponse("/api.php?null",
    "{\"edit\":{\"result\":\"Success\",\"pageid\":3,\"title\":\"Tomas Title\",\"nochange\":\"\"}}");

  this.mediaWikiClient //
    .createMediaWikiPage();
 }

}
