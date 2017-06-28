package de.dfki.mlt.gnt.config;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

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
  public CorpusConfig(FileBasedConfiguration config) {

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


  public static CorpusConfig create(String corpusConfigFileName)
      throws ConfigurationException {

    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
        new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setFileName(corpusConfigFileName)
                .setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    return new CorpusConfig(builder.getConfiguration());
  }


  public static CorpusConfig create(InputStream in)
      throws ConfigurationException {

    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
        new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));

    FileBasedConfiguration config = builder.getConfiguration();
    FileHandler fileHandler = new FileHandler(config);
    fileHandler.load(in);

    return new CorpusConfig(config);
  }
}
