package de.dfki.mlt.gnt.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  /** name of model config file in model archive */
  public static final String MODEL_CONFIG_FILE = "model.conf";

  private static final Logger logger = LoggerFactory.getLogger(GlobalConfig.class);
  private static final String DATA_FORMAT_STRING = "_yyyy-MM-dd_HH.mm.ss";

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
        // make model build folder unique by adding a time stamp
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT_STRING);
        instance.setProperty(ConfigKeys.MODEL_BUILD_FOLDER,
            instance.getString(ConfigKeys.MODEL_BUILD_FOLDER)
                + sdf.format(new Timestamp(System.currentTimeMillis())));
      } catch (ConfigurationException e) {
        logger.error(e.getLocalizedMessage(), e);
      }
    }

    return instance;
  }


  /**
   * Convenience method to retrieve the model build folder from config.
   *
   * @return model build folder
   */
  public static Path getModelBuildFolder() {

    return Paths.get(getInstance().getString(ConfigKeys.MODEL_BUILD_FOLDER));
  }


  /**
   * Convenience method to create a new model build folder from config.
   *
   * @return model build folder
   */
  public static Path getNewModelBuildFolder() {

    // make model build folder unique by adding a time stamp
    SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT_STRING);
    String oldModelBuildFolder = getInstance().getString(ConfigKeys.MODEL_BUILD_FOLDER);
    instance.setProperty(ConfigKeys.MODEL_BUILD_FOLDER,
        oldModelBuildFolder.substring(0, oldModelBuildFolder.length() - DATA_FORMAT_STRING.length())
            + sdf.format(new Timestamp(System.currentTimeMillis())));
    return Paths.get(getInstance().getString(ConfigKeys.MODEL_BUILD_FOLDER));
  }


  /**
   * @param key
   *          the config key
   * @return path associated with the given key
   */
  public static Path getPath(String key) {

    return Paths.get(getInstance().getString(key));
  }


  /**
   * @param key
   *          the config key
   * @return path list associated with the given key
   */
  public static List<Path> getPathList(String key) {

    List<Path> pathList = new ArrayList<>();
    List<String> stringList = getInstance().getList(String.class, key, Collections.emptyList());
    for (String oneString : stringList) {
      pathList.add(Paths.get(oneString));
    }
    return pathList;
  }


  /**
   * Convenience method to retrieve string values from config.
   *
   * @param key
   *          the config key
   * @return the key value
   */
  public static String getString(String key) {

    return getInstance().getString(key);
  }


  /**
   * Convenience method to retrieve int values from config.
   *
   * @param key
   *          the config key
   * @return the key value
   */
  public static int getInt(String key) {

    return getInstance().getInt(key);
  }


  /**
   * Convenience method to retrieve boolean values from config.
   *
   * @param key
   *          the config key
   * @return the key value
   */
  public static boolean getBoolean(String key) {

    return getInstance().getBoolean(key);
  }


  /**
   * Convenience method to retrieve double values from config.
   *
   * @param key
   *          the config key
   * @return the key value
   */
  public static double getDouble(String key) {

    return getInstance().getDouble(key);
  }
}
