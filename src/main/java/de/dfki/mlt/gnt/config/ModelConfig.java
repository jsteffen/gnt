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
  public ModelConfig(FileBasedConfiguration config) {

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


  public static ModelConfig create(String modelConfigFileName)
      throws ConfigurationException {

    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
        new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setFileName(modelConfigFileName)
                .setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    return new ModelConfig(builder.getConfiguration());
  }


  public static ModelConfig create(InputStream in)
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

    return new ModelConfig(config);
  }


  /**
   * @return a name for the model based on its configuration
   */
  public String getModelName() {

    String taggerName = this.getString(ConfigKeys.TAGGER_NAME);
    int windowSize = this.getInt(ConfigKeys.WINDOW_SIZE);
    int numberOfSentences = this.getInt(ConfigKeys.NUMBER_OF_SENTENCES);
    String solverType = this.getString(ConfigKeys.SOLVER_TYPE);
    int dim = this.getInt(ConfigKeys.DIM);
    String wordFeatString = this.getBoolean(ConfigKeys.WITH_WORD_FEATS) ? "T" : "F";
    String shapeFeatString = this.getBoolean(ConfigKeys.WITH_SHAPE_FEATS) ? "T" : "F";
    String suffixFeatString = this.getBoolean(ConfigKeys.WITH_SUFFIX_FEATS) ? "T" : "F";
    String clusterFeatString = this.getBoolean(ConfigKeys.WITH_CLUSTER_FEATS) ? "T" : "F";
    String labelFeatString = this.getBoolean(ConfigKeys.WITH_LABEL_FEATS) ? "T" : "F";
    if (wordFeatString.equals("F")) {
      dim = 0;
    }

    String modelName = "model_" + taggerName + "_" + windowSize + "_" + dim + "iw"
        + numberOfSentences + "sent_" + wordFeatString + shapeFeatString + suffixFeatString
        + clusterFeatString
        + labelFeatString + "_" + solverType;
    return modelName;
  }
}
