package vc.signal;

/**
 * TODO, this should be externalized.
 */
public class BotConfig {
  // ALLOW_PICTURES
  // ALLOW_LINKS
  // LOG_MESSAGES

  private boolean allowPictures;

  private boolean allowLinks;

  public BotConfig allowPictures(boolean allowPictures) {
    this.allowPictures = allowPictures;
    return this;
  }

  public BotConfig allowLinks(boolean allowLinks) {
    this.allowLinks = allowLinks;
    return this;
  }
}
