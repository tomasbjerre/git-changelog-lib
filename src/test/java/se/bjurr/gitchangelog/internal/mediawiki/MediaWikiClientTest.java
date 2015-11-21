package se.bjurr.gitchangelog.internal.mediawiki;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class MediaWikiClientTest {

 private final Map<String, String> mockedResponses = newHashMap();

 public class MockedMediaWikiClient extends MediaWikiClient {
  public MockedMediaWikiClient(String url, String title, String text) {
   super(url, title, text);
  }

  @Override
  String getResponse(HttpURLConnection conn) throws Exception {
   String key = conn.getURL().getPath() + "?" + conn.getURL().getQuery();
   if (mockedResponses.containsKey(key)) {
    return mockedResponses.get(key);
   } else {
    throw new RuntimeException("Could not find mock for \"" + key + "\" available mocks are:\n"
      + on("\n").join(mockedResponses.keySet()));
   }
  }

  @Override
  HttpURLConnection openConnection(URL addr) throws Exception {
   return new HttpURLConnection(addr) {
    @Override
    public Map<String, List<String>> getHeaderFields() {
     Map<String, List<String>> map = newHashMap();
     map.put("Set-Cookie", newArrayList("thesetcookie"));
     return map;
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public boolean usingProxy() {
     return false;
    }

    @Override
    public void disconnect() {
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
     return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
      }
     };
    }
   };
  }
 }

 private MockedMediaWikiClient mockedMediaWikiClient;

 @Before
 public void before() {
  mockedMediaWikiClient = new MockedMediaWikiClient("http://url", "the title", "some text");
 }

 @Test
 public void testWithUser() {
  this.mockedResponses
    .put(
      "/api.php?action=login&lgname=username&lgpassword=password&format=json",
      "{\"login\":{\"result\":\"NeedToken\", \n \"token\":\n\"9262cbea833c2f486c46de85d2a1ce1c\" \n,\"cookieprefix\":\"my_wiki\",\"sessionid\":\"54150481260436601c815fca1f5151b9\"}}");
  this.mockedResponses
    .put(
      "/api.php?action=login&lgname=username&lgpassword=password&format=json&lgtoken=9262cbea833c2f486c46de85d2a1ce1c",
      "{\"login\":{\"result\":\"Success\",\"lguserid\":1,\"lgusername\":\"Tomas\",\n \"lgtoken\":\n\"9bf282b3513874d03253a171676f6edc\" ,\"cookieprefix\":\"my_wiki\",\"sessionid\":\"54150481260436601c815fca1f5151b9\"}}");
  this.mockedResponses
    .put(
      "/api.php?action=query&prop=info%7Crevisions&intoken=edit&rvprop=timestamp&titles=the+title&format=json",
      "{\"query\":{\"pages\":{\"3\":{\"pageid\":3,\"ns\":0,\"title\":\"Tomas Title\",\"touched\":\"2015-11-21T07:38:57Z\",\"lastrevid\":46,\"counter\":35,\"length\":5993,\"starttimestamp\":\"2015-11-21T07:39:42Z\",\n \"edittoken\"\n: \"a7ac6516e756ebd932579ead6fe3a878+\\\" ,\"revisions\":[{\"timestamp\":\"2015-11-21T07:18:24Z\"}]}}}}");
  this.mockedResponses.put("/api.php?null",
    "{\"edit\":{\"result\":\"Success\",\"pageid\":3,\"title\":\"Tomas Title\",\"nochange\":\"\"}}");

  this.mockedMediaWikiClient //
    .withUser("username", "password") //
    .createMediaWikiPage();
 }

 @Test
 public void testWithoutUser() {
  this.mockedResponses.put("/api.php?null",
    "{\"edit\":{\"result\":\"Success\",\"pageid\":3,\"title\":\"Tomas Title\",\"nochange\":\"\"}}");

  this.mockedMediaWikiClient //
    .createMediaWikiPage();
 }

}
