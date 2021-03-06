package de.dfki.mlt.gnt.caller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.mlt.gnt.tagger.GNTagger;

/**
 * The main calls for training, evaluating and tagging with GNT.
 * <p>
 * Will be the main class for the self-contained image.
 *
 * @author Günter Neumann, DFKI
 */
public final class GNT {

  private static final Logger logger = LoggerFactory.getLogger(GNT.class);

  private static final String IN_ENCODE_DEFAULT = "ISO-8859-1";
  private static final String OUT_ENCODE_DEFAULT = "UTF-8";


  private GNT() {

    // private constructor to enforce noninstantiability
  }


  /**
   * Trains a tagger model from an annotated corpus.
   *
   * @param modelConfigName
   *          model configuration file name
   * @param corpusConfigName
   *          corpus configuration file name
   */
  public static void train(String modelConfigName, String corpusConfigName) {

    try {
      TrainTagger gntTrainer = new TrainTagger();
      gntTrainer.trainer(modelConfigName, corpusConfigName);
    } catch (IOException | ConfigurationException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
  }


  /**
   * Evaluates a tagger model against an annotated corpus.
   *
   * @param modelArchiveName
   *          model archive, to be loaded from file system or classpath
   * @param corpusConfigName
   *          corpus configuration file name
   */
  public static void eval(String modelArchiveName, String corpusConfigName) {

    try {
      GNTagger tagger = new GNTagger(modelArchiveName);
      tagger.eval(corpusConfigName);
    } catch (IOException | ConfigurationException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
  }


  /**
   * Tags all files in a folder using a tagger model.
   *
   * @param modelArchiveName
   *         modelArchive, to be loaded from file system or classpath
   * @param inputFolderName
   *         input folder name of files to tag
   * @param inputEncodingName
   *         input files encoding name
   * @param outputEncodingName
   *         output files encoding name
   */
  public static void tag(
      String modelArchiveName, String inputFolderName,
      String inputEncodingName, String outputEncodingName) {

    try {
      GNTagger tagger = new GNTagger(modelArchiveName);
      tagger.tagFolder(inputFolderName, inputEncodingName, outputEncodingName);
    } catch (IOException | ConfigurationException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
  }


  /*
  private static PropertiesConfiguration loadConfig(String configName)
      throws ConfigurationException {
  
    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
        new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setFileName(configName)
                .setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    return builder.getConfiguration();
  }
  */


  /**
   * Main method to run GNT.
   *
   * GNT can
   * <p><ul>
   * <li> train a tagger model from an annotated corpus
   * <li> evaluate a tagger model against an annotated corpus
   * <li> tag files using a tagger model
   * </ul><p>
   * GNT options for train mode:
   * <pre>
   * {@code
   * -train                 run in train mode
   * -modelConfig <file>    model config file
   * -corpusConfig <file>   corpus config file
   * }</pre>
   *
   * GNT options for eval mode:
   * <pre>
   * {@code
   * -eval                  run in evaluation mode
   * -model <file>          model, to be loaded from file system or classpath
   * -corpusConfig <file>   corpus config file
   * }</pre>
   *
   * GNT options for tag mode:
   * <pre>
   * {@code
   * -tag                    run in tag mode
   * -model <file>           model, to be loaded from file system or classpath
   * -input <folder>         input folder
   * -inEncode <encoding>    input encoding, optional, default: ISO-8859-1
   * -outEncode <encoding>   output encoding, optional, default: UTF-8
   * }</pre>
   *
   * @param args
   *          input options as described above
   */
  public static void main(String[] args) {

    List<Options> optionsList = new ArrayList<>();
    optionsList.add(createTrainOptions());
    optionsList.add(createEvalOptions());
    optionsList.add(createTagOptions());

    CommandLine cmd = parseArguments(args, optionsList);
    if (null == cmd) {
      return;
    }

    String mode = cmd.getOptions()[0].getOpt();
    switch (mode) {
      case "train":
        train(
            cmd.getOptionValue("modelConfig"),
            cmd.getOptionValue("corpusConfig"));
        break;
      case "eval":
        eval(
            cmd.getOptionValue("model"),
            cmd.getOptionValue("corpusConfig"));
        break;
      case "tag":
        tag(
            cmd.getOptionValue("model"),
            cmd.getOptionValue("input"),
            cmd.getOptionValue("inEncode", IN_ENCODE_DEFAULT),
            cmd.getOptionValue("outEncode", OUT_ENCODE_DEFAULT));
        break;
      default:
        logger.error(String.format("unkown mode '%s'", mode));
        return;
    }
  }


  private static Options createTrainOptions() {

    Options trainOptions = new Options();

    Option modeOption = new Option("train", false, "run in train mode");
    modeOption.setRequired(true);
    trainOptions.addOption(modeOption);

    Option modelConfigOption = new Option("modelConfig", true, "model config file");
    modelConfigOption.setRequired(true);
    modelConfigOption.setArgName("file");
    trainOptions.addOption(modelConfigOption);

    Option corpusConfigOption = new Option("corpusConfig", true, "corpus config file");
    corpusConfigOption.setRequired(true);
    corpusConfigOption.setArgName("file");
    trainOptions.addOption(corpusConfigOption);

    return trainOptions;
  }


  private static Options createEvalOptions() {

    Options evalOptions = new Options();

    Option modeOption = new Option("eval", false, "run in evaluation mode");
    modeOption.setRequired(true);
    evalOptions.addOption(modeOption);

    Option modelOption = new Option(
        "model", true, "model, to be loaded from classpath or file system");
    modelOption.setRequired(true);
    modelOption.setArgName("file");
    evalOptions.addOption(modelOption);

    Option corpusConfigOption = new Option("corpusConfig", true, "corpus config file");
    corpusConfigOption.setRequired(true);
    corpusConfigOption.setArgName("file");
    evalOptions.addOption(corpusConfigOption);

    return evalOptions;
  }


  private static Options createTagOptions() {

    Options tagOptions = new Options();

    Option modeOption = new Option("tag", false, "run in tag mode");
    modeOption.setRequired(true);
    tagOptions.addOption(modeOption);

    Option modelConfigOption = new Option(
        "model", true, "model, to be loaded from classpath or file system");
    modelConfigOption.setRequired(true);
    modelConfigOption.setArgName("file");
    tagOptions.addOption(modelConfigOption);

    Option inputOption = new Option("input", true, "input folder");
    inputOption.setRequired(true);
    inputOption.setArgName("folder");
    tagOptions.addOption(inputOption);

    Option inputEncodingOption = new Option(
        "inEncode", true,
        String.format("input encoding (optional, default: %s)", IN_ENCODE_DEFAULT));
    inputEncodingOption.setRequired(false);
    inputEncodingOption.setArgName("encoding");
    tagOptions.addOption(inputEncodingOption);

    Option outputEncodingOption = new Option("outEncode", true,
        String.format("output encoding (optional, default: %s)", OUT_ENCODE_DEFAULT));
    outputEncodingOption.setRequired(false);
    outputEncodingOption.setArgName("encoding");
    tagOptions.addOption(outputEncodingOption);

    return tagOptions;
  }


  private static CommandLine parseArguments(String[] args, List<Options> optionsList) {

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;

    for (Options oneOptions : optionsList) {
      try {
        cmd = parser.parse(oneOptions, args);
        // arguments successfully parsed
        break;
      } catch (ParseException e) {
        // only log if parse exception is NOT related to mode
        if (args.length > 0 && !e.getMessage().contains(args[0])) {
          logger.error(e.getLocalizedMessage());
          break;
        }
      }
    }

    if (null == cmd) {
      System.out.format(
          "GNT can%n"
              + "- train a tagger model from an annotated corpus%n"
              + "- evaluate a tagger model against an annotated corpus%n"
              + "- tag files using a tagger model%n%n");
      HelpFormatter formatter = new HelpFormatter();
      formatter.setOptionComparator(null);
      for (Options oneOptions : optionsList) {
        formatter.setSyntaxPrefix(
            String.format("GNT options for %s mode", oneOptions.getRequiredOptions().get(0)));
        formatter.printHelp(":", oneOptions);
        System.out.println();
      }
    }

    return cmd;
  }
}
