package de.dfki.mlt.gnt;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import de.dfki.mlt.gnt.caller.GNT;
import de.dfki.mlt.gnt.caller.TrainTagger;
import de.dfki.mlt.gnt.trainer.TrainerInMem;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public final class TestTrainPosTagger {

  private TestTrainPosTagger() {

    // private constructor to enforce noninstantiability
  }


  public static void main(String[] args) throws IOException, ConfigurationException {

    TrainTagger gntTrainer = new TrainTagger();
    TrainerInMem.setDebug(true);
    //    gntTrainer.trainer(
    //        "src/main/resources/EnNerTagger.model.conf",
    //        "src/main/resources/EnNerTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DeNerKonvTagger.model.conf",
    //        "src/main/resources/DeNerKonvTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DePosTagger.model.conf",
    //        "src/main/resources/DePosTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DeNerTagger.model.conf",
    //        "src/main/resources/DeNerTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DeNerKonvTagger.model.conf",
    //        "src/main/resources/DeNerKonvTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DeTweetPosTagger.model.conf",
    //        "src/main/resources/DeTweetPosTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/DeMorphTagger.model.conf",
    //        "src/main/resources/DeMorphTagger.corpus.conf");
//    gntTrainer.trainer(
//        "src/main/resources/EnPosTagger.model.conf",
//        "src/main/resources/EnPosTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/BioNerTagger.model.conf",
    //        "src/main/resources/BioNerTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/EnUniPosTagger.model.conf",
    //        "src/main/resources/EnUniPosTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/EnUniPosTagger.model.conf",
    //        "src/main/resources/EnUniPosTagger.corpus.conf");

    // POS EN
    /*
    GNT.train("Pipeline_EnPosTagger.model.conf", "Pipeline_EnPosTagger.corpus.conf");
    GNT.eval("src/test/resources/model_PIPELINE_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip",
        "Pipeline_EnPosTagger.corpus.conf");
    */

    /*
    // POS DE
    GNT.train("Pipeline_DePosTagger.model.conf", "Pipeline_DePosTagger.corpus.conf");
    GNT.eval("src/test/resources/model_PIPELINE_DEPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip",
        "Pipeline_DePosTagger.corpus.conf");
    */

    /*
    // NER EN
    GNT.train("Pipeline_EnNerTagger.model.conf", "Pipeline_EnNerTagger.corpus.conf");
    GNT.eval("src/test/resources/model_PIPELINE_ENNER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "Pipeline_EnNerTagger.corpus.conf");
    */

    /*
    // NER DE
    GNT.train("Pipeline_DeNerTagger.model.conf", "Pipeline_DeNerTagger.corpus.conf");
    GNT.eval("src/test/resources/model_PIPELINE_DENER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "Pipeline_DeNerTagger.corpus.conf");
    */

    /*
    // NER DE Konvens
    GNT.train("Pipeline_DeNerKonvTagger.model.conf", "Pipeline_DeNerKonvTagger.corpus.conf");
    GNT.eval("src/test/resources/model_PIPELINE_DENERKONV_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "Pipeline_EnNerTagger.corpus.conf");
    */


    // NAMR BIO
//    GNT.train(
//        "src/main/resources/EnNerNamrBioTagger.model.conf",
//        "EnNerNamrBioTagger.corpus.conf");
//    GNT.eval("src/test/resources/model_NAMR-BIO-EN-NER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
//        "EnNerNamrBioTagger.corpus.conf");

 // NAMR ES-NER
    GNT.train(
        "src/main/resources/GNT_NER_ES_CoNLL.model.conf",
        "GNT_NER_ES_CoNLL.corpus.conf");
    GNT.eval("src/test/resources/model_NER_ES_CoNLL_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "GNT_NER_ES_CoNLL.corpus.conf");


    /*
    // NAMR BILOU
    GNT.train(
        "src/main/resources/EnNerNamrBilouTagger.model.conf",
        "EnNerNamrBilouTagger.corpus.conf");
    GNT.eval("src/test/resources/model_NAMR-BILOU-EN-NER_2_0iw-1sent_FTTTT_MCSVM_CS.zip",
        "EnNerNamrBilouTagger.corpus.conf");
    */
  }
}
