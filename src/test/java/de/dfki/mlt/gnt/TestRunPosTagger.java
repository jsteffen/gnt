package de.dfki.mlt.gnt;

import java.io.IOException;

import de.dfki.mlt.gnt.caller.RunTagger;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class TestRunPosTagger {

  public static void main(String[] args) throws IOException {

//    RunTagger.runner(
//        "resources/models/model_ENUNIPOS_2_0iw-1sent_FTTT_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/EnUniPosTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_ENNER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/EnNerTagger.xml");
    RunTagger.runner(
        "resources/models/model_DENERKONV_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "src/main/resources/corpusProps/DeNerKonvTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_BIONER_2_0iw-1sent_FFTTT_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/BioNerTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_DETWEETPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/DeTweetPosTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_DEMORPH_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/DeMorphTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_DENER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/DeNerTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/EnPosTagger.xml");
//    RunTagger.runner(
//        "resources/models/model_DEPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip",
//        "src/main/resources/corpusProps/DePosTagger.xml");
  }
}
