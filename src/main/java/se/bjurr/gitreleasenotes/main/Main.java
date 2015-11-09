package se.bjurr.gitreleasenotes.main;

import java.io.PrintWriter;

import se.bjurr.gitreleasenotes.api.model.ReleaseNotes;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class Main {
 public static void main(String argv[]) throws Exception {
  MustacheFactory mf = new DefaultMustacheFactory();
  Mustache mustache = mf.compile("template.mustache");
  ReleaseNotes releaseNotes = null;
  mustache.execute(new PrintWriter(System.out), releaseNotes).flush();
 }
}
