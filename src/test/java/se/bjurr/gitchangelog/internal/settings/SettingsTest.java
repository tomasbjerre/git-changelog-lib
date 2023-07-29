package se.bjurr.gitchangelog.internal.settings;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author Réda Housni Alaoui
 */
public class SettingsTest {

  @Test
  public void nullFromRevisionCanBeSet() {
    Settings settings = new Settings();
    settings.setFromRevision(null);
    assertThat(settings.getFromRevision()).isEmpty();
  }

  @Test
  public void nullToRevisionCanBeSet() {
    Settings settings = new Settings();
    settings.setToRevision(null);
    assertThat(settings.getToRevision()).isEmpty();
  }
}
