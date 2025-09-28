package se.bjurr.gitchangelog.internal.settings;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author Réda Housni Alaoui
 */
public class SettingsTest {

  @Test
  public void nullFromRevisionCanBeSet() {
    final Settings settings = new Settings();
    settings.setFromRevision(null);
    assertThat(settings.getFromRevision()).isEmpty();
  }

  @Test
  public void nullToRevisionCanBeSet() {
    final Settings settings = new Settings();
    settings.setToRevision(null);
    assertThat(settings.getToRevision()).isEmpty();
  }

  @Test
  public void canBeSerializedAndDeserialized() {
    final Settings settings =
        Settings.fromFile(Settings.class.getResource("/settings/git-changelog-test-settings.json"));
    final String settingsSerialized = settings.toJson();
    final Settings settingsDeserialized = Settings.fromJson(settingsSerialized);
    assertThat(settingsDeserialized).isEqualTo(settings);
  }

  @Test
  public void canBeCopied() {
    final Settings settings =
        Settings.fromFile(Settings.class.getResource("/settings/git-changelog-test-settings.json"));
    final Settings settingsCopy = settings.copy();
    assertThat(settingsCopy).isEqualTo(settings);
  }
}
