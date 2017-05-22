package de.dfki.mlt.gnt.config;

import java.util.Iterator;

import org.apache.commons.configuration2.PropertiesConfiguration;

/**
 * The corpus configuration.
 *
 * @author Jörg Steffen, DFKI
 */
public class CorpusConfig extends PropertiesConfiguration {

  /**
   * Creates a corpus configuration from the given configuration.
   *
   * @param config
   *          the configuration
   */
  public CorpusConfig(PropertiesConfiguration config) {

    super();
    // no good way to initialize the corpus config using a given config,
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
