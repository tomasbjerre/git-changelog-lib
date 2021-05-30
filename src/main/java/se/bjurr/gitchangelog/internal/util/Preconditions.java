package se.bjurr.gitchangelog.internal.util;

public class Preconditions {

  public static void checkState(final boolean b, final String string) {
    if (!b) {
      throw new IllegalStateException(string);
    }
  }

  public static void checkArgument(final boolean b, final String string) {
    if (!b) {
      throw new IllegalStateException(string);
    }
  }

  public static boolean isNullOrEmpty(final String it) {
    return it == null || it.isEmpty();
  }

  public static String nullToEmpty(final String it) {
    if (it == null) {
      return "";
    }
    return it;
  }

  public static <T> T checkNotNull(final T it, final String string) {
    if (it == null) {
      throw new IllegalStateException(string);
    }
    return it;
  }

  public static String emptyToNull(final String from) {
    if (from != null && !from.trim().isEmpty()) {
      return from;
    }
    return null;
  }

  public static <T> T firstNonNull(final T t1, final T t2) {
    if (t1 == null) {
      return t2;
    }
    return t1;
  }
}
