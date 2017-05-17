package de.dfki.mlt.gnt;

import java.io.IOException;

import de.dfki.mlt.gnt.caller.TrainTagger;
import de.dfki.mlt.gnt.trainer.TrainerInMem;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class TestTrainPosTagger {

  public static void main(String[] args) throws IOException {
    TrainTagger gntTrainer = new TrainTagger();
    TrainerInMem.setDebug(true);
//    gntTrainer.trainer(
//        "src/main/resources/dataProps/EnNerTagger.xml",
//        "src/main/resources/corpusProps/EnNerTagger.xml");
    gntTrainer.trainer(
        "src/main/resources/dataProps/DeNerKonvTagger.xml",
        "src/main/resources/corpusProps/DeNerKonvTagger.xml");
//            gntTrainer.trainer(
//                "src/main/resources/dataProps/DePosTagger.xml",
//                "src/main/resources/corpusProps/DePosTagger.xml");
//            gntTrainer.trainer(
//                "src/main/resources/dataProps/DeNerTagger.xml",
//                "src/main/resources/corpusProps/DeNerTagger.xml");
//            gntTrainer.trainer(
//                "src/main/resources/dataProps/DeNerKonvTagger.xml",
//                "src/main/resources/corpusProps/DeNerKonvTagger.xml");
    //        gntTrainer.trainer(
    //            "src/main/resources/dataProps/DeTweetPosTagger.xml",
    //            "src/main/resources/corpusProps/DeTweetPosTagger.xml");
    //        gntTrainer.trainer(
    //            "src/main/resources/dataProps/DeMorphTagger.xml",
    //            "src/main/resources/corpusProps/DeMorphTagger.xml");
//            gntTrainer.trainer(
//                "src/main/resources/dataProps/EnPosTagger.xml",
//                "src/main/resources/corpusProps/EnPosTagger.xml");
    //        gntTrainer.trainer(
    //            "src/main/resources/dataProps/BioNerTagger.xml",
    //            "src/main/resources/corpusProps/BioNerTagger.xml");
    //    gntTrainer.trainer(
    //        "src/main/resources/dataProps/EnUniPosTagger.xml",
    //        "src/main/resources/corpusProps/EnUniPosTagger.xml");
    //    gntTrainer.trainer(
    //        "src/main/resources/dataProps/EnUniPosTagger.xml",
    //        "src/main/resources/corpusProps/EnUniPosTagger.xml");
  }
}
