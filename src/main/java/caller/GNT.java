package caller;

import java.io.IOException;

/**
 * The main calls for training and tagging and testing with GNTagger
 * with arguments;
 * Will be the main class for the self-contained image.
 * @author gune00
 *
 */

/*
 * arguments:
 *
 * -mode train -dataConfig src/main/resources/dataProps/<configFile.xml>
 *   -corpusConfig src/main/resources/corpusProps/<configFile.xml> |
 * -mode test -archiveName resources/models/<archive.zip>
 *   -corpusConfig src/main/resources/corpusProps/<configFile.xml>
 */
public class GNT {

  private String mode = "train"; // or "test"
  private String dataConfig = "";
  private String archiveName = "";
  private String corpusConfig = "";
  private String corpusDir = "";
  private String inEncode = "ISO-8859-1";
  private String outEncode = "UTF-8";


  public GNT() {
  }


  private void errorMessageAndExit() {

    System.err.println(
        "-mode train -dataConfig src/main/resources/dataProps/<configFile.xml> "
            + "-corpusConfig src/main/resources/corpusProps/<configFile.xml>"
            + "\nor ...");
    System.err.println(
        "-mode test -archiveName resources/models/<archive.zip> "
            + "-corpusConfig src/main/resources/corpusProps/<configFile.xml>"
            + "\nor ...");
    System.err.println(
        "-mode test -archiveName resources/models/<archive.zip> "
            + "-corpusDir <folder-with-text-files> -inEncode <encoding> -outEncode <encoding>"
            + "\nor ...");
    System.err.println(this.toString());
    // Exit with error !
    System.exit(1);
  }


  private void initGNTArguments(String[] args) {

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "-mode":
          this.mode = args[i + 1];
          break;
        case "-dataConfig":
          this.dataConfig = args[i + 1];
          break;
        case "-archiveName":
          this.archiveName = args[i + 1];
          break;
        case "-corpusConfig":
          this.corpusConfig = args[i + 1];
          break;
        case "-corpusDir":
          this.corpusDir = args[i + 1];
          break;
        case "-inEncode":
          this.inEncode = args[i + 1];
          break;
        case "-outEncode":
          this.outEncode = args[i + 1];
          break;
        default:
          System.err.println("unknown argument " + args[i]);
      }
    }
  }


  private void setArgValues(String[] args) {

    if ((args.length == 0)) {
      System.err.println("No arguments specified. Run either");
      errorMessageAndExit();
    }
    if ((args.length % 2) != 0) {
      System.err.println("Not all arguments have values! Check!");
      errorMessageAndExit();
    }
    this.initGNTArguments(args);
  }


  @Override
  public String toString() {

    String output = "";

    output += " -mode " + this.mode;
    if (!this.dataConfig.isEmpty()) {
      output += " -dataConfig " + this.dataConfig;
      output += " -corpusConfig " + this.corpusConfig;
    } else if (!this.archiveName.isEmpty()) {
      output += " -archiveName " + this.archiveName;
      if (!this.corpusConfig.isEmpty()) {
        output += " -corpusConfig " + this.corpusConfig;
      } else {
        output += " -corpusDir " + this.corpusDir;
        output += " -inEncode " + this.inEncode;
        output += " -outEncode " + this.outEncode;
      }
    }
    return output;
  }


  private void runGNTrainer() throws IOException {

    System.out.println("Run GNTrainer: ");
    System.out.println(this.toString());
    if (!this.dataConfig.isEmpty()) {
      TrainTagger gntTrainer = new TrainTagger();
      gntTrainer.trainer(this.dataConfig, this.corpusConfig);
    } else {
      System.err.println("Only training GNT with config files is supported!");
    }
  }


  private void runGNTagger() throws IOException {

    System.out.println("Run GNTagger: ");
    System.out.println(this.toString());

    if (!this.archiveName.isEmpty()
        && !this.corpusConfig.isEmpty()
        && this.corpusDir.isEmpty()) {
      RunTagger.runner(this.archiveName, this.corpusConfig);
    } else if (!this.archiveName.isEmpty()
        && !this.corpusDir.isEmpty()
        && this.corpusConfig.isEmpty()) {
      RunTagger.folderRunner(this.archiveName, this.corpusDir, this.inEncode, this.outEncode);
    } else {
      System.err.println("Only running GNT with archive name and config files is supported!");
    }
  }


  public static void main(String[] args) throws IOException {

    GNT newGNT = new GNT();
    newGNT.setArgValues(args);

    if (newGNT.mode.equalsIgnoreCase("train")) {
      newGNT.runGNTrainer();
    } else if (newGNT.mode.equalsIgnoreCase("test")) {
      newGNT.runGNTagger();
    } else {
      System.exit(1);
    }
  }

}
