package de.dfki.mlt.gnt.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A singleton wrapper around {@link PropertiesConfiguration} with the global GNT configuration.
 * Configuration is loaded from "gnt.conf" in the classpath.
 *
 * @author Jörg Steffen, DFKI
 */
public final class GlobalConfig {

  private static final Logger logger = LoggerFactory.getLogger(GlobalConfig.class);
  private static PropertiesConfiguration instance;


  private GlobalConfig() {

    // private constructor to enforce noninstantiability
  }


  public static PropertiesConfiguration getInstance() {

    if (null == instance) {
      Parameters params = new Parameters();
      FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
          new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
              .configure(params.properties()
                  .setFileName("gnt.conf")
                  .setEncoding("UTF-8")
                  .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
      try {
        instance = builder.getConfiguration();
      } catch (ConfigurationException e) {
        logger.error(e.getLocalizedMessage(), e);
      }
    }

    return instance;
  }
}
