package se.bjurr.gitreleasenotes.internal.settings;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;

import java.io.File;
import java.util.List;

import com.google.common.io.Files;
import com.google.gson.Gson;

public class Settings {
 private static Gson gson = new Gson();

 private final List<SettingsIssue> issues;

 public Settings() {
  issues = null;
 }

 public List<SettingsIssue> getIssues() {
  return issues;
 }

 public static Settings fromFile(File file) {
  try {
   return gson.fromJson(Files.toString(file, UTF_8), Settings.class);
  } catch (Exception e) {
   throw propagate(e);
  }
 }
}
