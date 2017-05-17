package de.dfki.mlt.gnt.corpus;

import java.util.Comparator;
import java.util.Map;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
final class ValueComparator implements Comparator<String> {

  private Map<String, Integer> map;


  ValueComparator(Map<String, Integer> base) {
    this.map = base;
  }


  @Override
  public int compare(String a, String b) {

    if (this.map.get(a) >= this.map.get(b)) {
      return -1;
    } else {
      return 1;
    } // returning 0 would merge keys
  }
}
