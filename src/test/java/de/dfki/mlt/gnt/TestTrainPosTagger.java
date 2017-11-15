package de.dfki.mlt.gnt;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import de.dfki.mlt.gnt.caller.TrainTagger;
import de.dfki.mlt.gnt.trainer.TrainerInMem;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class TestTrainPosTagger {

  public static void main(String[] args) throws IOException, ConfigurationException {
    TrainTagger gntTrainer = new TrainTagger();
    TrainerInMem.setDebug(true);
//    gntTrainer.trainer(
//        "src/main/resources/EnNerTagger.model.conf",
//        "src/main/resources/EnNerTagger.corpus.conf");
//    gntTrainer.trainer(
//        "src/main/resources/DeNerKonvTagger.model.conf",
//        "src/main/resources/DeNerKonvTagger.corpus.conf");
//            gntTrainer.trainer(
//                "src/main/resources/DePosTagger.model.conf",
//                "src/main/resources/DePosTagger.corpus.conf");
//            gntTrainer.trainer(
//                "src/main/resources/DeNerTagger.model.conf",
//                "src/main/resources/DeNerTagger.corpus.conf");
//            gntTrainer.trainer(
//                "src/main/resources/DeNerKonvTagger.model.conf",
//                "src/main/resources/DeNerKonvTagger.corpus.conf");
    //        gntTrainer.trainer(
    //            "src/main/resources/DeTweetPosTagger.model.conf",
    //            "src/main/resources/DeTweetPosTagger.corpus.conf");
    //        gntTrainer.trainer(
    //            "src/main/resources/DeMorphTagger.model.conf",
    //            "src/main/resources/DeMorphTagger.corpus.conf");
            gntTrainer.trainer(
                "src/main/resources/EnPosTagger.model.conf",
                "src/main/resources/EnPosTagger.corpus.conf");
    //        gntTrainer.trainer(
    //            "src/main/resources/BioNerTagger.model.conf",
    //            "src/main/resources/BioNerTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/EnUniPosTagger.model.conf",
    //        "src/main/resources/EnUniPosTagger.corpus.conf");
    //    gntTrainer.trainer(
    //        "src/main/resources/EnUniPosTagger.model.conf",
    //        "src/main/resources/EnUniPosTagger.corpus.conf");
  }
}
