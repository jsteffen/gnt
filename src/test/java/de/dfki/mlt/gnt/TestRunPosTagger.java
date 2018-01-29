package de.dfki.mlt.gnt;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import de.dfki.mlt.gnt.tagger.GNTagger;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public final class TestRunPosTagger {

  private TestRunPosTagger() {

    // private constructor to enforce noninstantiability
  }


  public static void main(String[] args) throws IOException, ConfigurationException {

    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_ENUNIPOS_2_0iw-1sent_FTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/EnUniPosTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_ENNER_2_0iw-1sent_FTTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/EnNerTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_DENERKONV_2_0iw-1sent_FTTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/DeNerKonvTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_BIONER_2_0iw-1sent_FFTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/BioNerTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_DETWEETPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/DeTweetPosTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_DEMORPH_2_0iw-1sent_FTTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/DeMorphTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_DENER_2_0iw-1sent_FTTTT_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/DeNerTagger.corpus.conf");
    GNTagger tagger = new GNTagger(
        "src/test/resources/model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip");
    tagger.eval("src/main/resources/EnPosTagger.corpus.conf");
    //    GNTagger tagger = new GNTagger(
    //        "resources/models/model_DEPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip");
    //    tagger.eval("src/main/resources/DePosTagger.corpus.conf");
  }
}
