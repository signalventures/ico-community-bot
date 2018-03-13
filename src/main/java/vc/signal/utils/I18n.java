package vc.signal.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internationalization service to translate messages to multiple languages.
 */
public class I18n {

  public static String getMessage() {
    ResourceBundle.getBundle("../../resources/basic", new Locale("en", "EN"));
    return null;
  }

  /**
   */
  public static String t(String s, Object... args) {
    // TODO: i18n...
    return String.format(s, args);
  }

}
