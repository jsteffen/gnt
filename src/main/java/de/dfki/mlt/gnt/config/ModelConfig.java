package de.dfki.mlt.gnt.config;

import java.util.Iterator;

import org.apache.commons.configuration2.PropertiesConfiguration;

/**
 * The model configuration.
 *
 * @author Jörg Steffen, DFKI
 */
public class ModelConfig extends PropertiesConfiguration {

  /**
   * Creates a model configuration from the given configuration.
   *
   * @param config
   *          the configuration
   */
  public ModelConfig(PropertiesConfiguration config) {

    super();
    // no good way to initialize the model config using a given config,
    // so we just copy the properties
    Iterator<String> keyIt = config.getKeys();
    while (keyIt.hasNext()) {
      String oneKey = keyIt.next();
      Object oneValue = config.getProperty(oneKey);
      System.out.println(oneKey + ": " + oneValue);
      this.setProperty(oneKey, oneValue);
    }
  }
}
