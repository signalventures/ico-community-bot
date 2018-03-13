package vc.signal.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Application default properties, that can be overridden in the database.
 */
public enum ApplicationConfig {
  INSTANCE;

  private Properties props;

  ApplicationConfig() {
    this.props = new Properties();
    String propFileName = "application.properties";
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
      this.props.load(inputStream);
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to load plugin config properties file.", ioe);
    }
  }

  public String getProperty(String key) {
    return this.props.getProperty(key);
  }

  public List<String> getPropertyAsList(String key) {
    String propertyValue = getProperty(key);
    if (StringUtils.isNotEmpty(propertyValue)) {
      return Arrays.asList(propertyValue.split("\\s*,\\s*"));
    }
    return null;
  }
}
