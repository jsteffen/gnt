package de.dfki.mlt.gnt.config;

/**
 * Defines all configuration keys as constants.
 *
 * @author Jörg Steffen, DFKI
 */
public final class ConfigKeys {

  // GNT global config keys
  public static final String CREATE_LIBLINEAR_INPUT_FILE = "create.liblinear.input.file";
  public static final String EVAL_FOLDER = "eval.folder";
  public static final String DEBUG = "debug";
  public static final String MODEL_BUILD_FOLDER = "model.build.folder";
  public static final String MODEL_OUTPUT_FOLDER = "model.output.folder";

  // common config keys
  public static final String TAGGER_NAME = "tagger.name";

  // model config keys
  public static final String SOLVER_TYPE = "solver.type";
  public static final String C = "c";
  public static final String EPS = "eps";
  public static final String WINDOW_SIZE = "window.size";
  public static final String NUMBER_OF_SENTENCES = "number.of.sentences";
  public static final String DIM = "dim";
  public static final String SUB_SAMPLING_THRESHOLD = "sub.sampling.threshold";
  public static final String WITH_WORD_FEATS = "with.word.feats";
  public static final String WITH_SHAPE_FEATS = "with.shape.feats";
  public static final String WITH_SUFFIX_FEATS = "with.suffix.feats";
  public static final String WITH_CLUSTER_FEATS = "with.cluster.feats";
  public static final String WITH_LABEL_FEATS = "with.label.feats";
  public static final String WORD_SUFFIX_FEATURE_FACTORY_NGRAM =
      "word.suffix.feature.factory.ngram";
  public static final String WORD_SUFFIX_FEATURE_FACTORY_NGRAM_SIZE =
      "word.suffix.feature.factory.ngram.size";

  // corpus config keys
  public static final String WORD_FORM_INDEX = "word.form.index";
  public static final String TAG_INDEX = "tag.index";

  public static final String LABELED_SOURCE_DATA_ENCODING = "labeled.source.data.encoding";
  public static final String TRAINING_LABELED_SOURCE_DATA = "training.labeled.source.data";
  public static final String DEV_LABELED_SOURCE_DATA = "dev.labeled.source.data";
  public static final String TEST_LABELED_SOURCE_DATA = "test.labeled.source.data";
  public static final String TRAINING_LABELED_DATA = "training.labeled.data";
  public static final String DEV_LABELED_DATA = "dev.labeled.data";
  public static final String TEST_LABELED_DATA = "test.labeled.data";
  public static final String TRAINING_UNLABELED_DATA = "training.unlabeled.data";
  public static final String DEV_UNLABELED_DATA = "dev.unlabeled.data";
  public static final String TEST_UNLABELED_DATA = "test.unlabeled.data";
  public static final String CLUSTER_FILE = "cluster.file";


  private ConfigKeys() {

    // private constructor to enforce noninstantiability
  }
}
