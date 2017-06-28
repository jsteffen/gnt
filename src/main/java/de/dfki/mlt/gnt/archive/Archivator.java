package de.dfki.mlt.gnt.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Archivator {

  private String archiveName;
  private HashMap<String, InputStream> archiveMap;
  private List<String> filesToPack = new ArrayList<String>();
  private ZipFile zip;
  private ZipInputStream zis;


  public Archivator(String archiveName) {

    this.archiveName = archiveName;
    this.archiveMap = new HashMap<String, InputStream>();
  }


  public List<String> getFilesToPack() {

    return this.filesToPack;
  }


  public String getArchiveName() {

    return this.archiveName;
  }


  public void setArchiveName(String archiveName) {

    this.archiveName = archiveName;
  }


  public HashMap<String, InputStream> getArchiveMap() {

    return this.archiveMap;
  }


  /**
   * Creates a stream to the archive name, and adds all files collected in variable filesToPack.
   * @throws IOException
   */
  public void pack() throws IOException {

    OutputStream dest =
        Files.newOutputStream(GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(this.archiveName));
    ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(dest));

    Iterator<String> iter = this.filesToPack.iterator();
    while (iter.hasNext()) {
      String curFile = iter.next();
      curFile = curFile.replaceAll("\\" + System.getProperty("file.separator"), "/");
      zipOut.putNextEntry(new ZipEntry(curFile));
      BufferedInputStream origin = new BufferedInputStream(new FileInputStream(curFile));
      int count = 0;
      byte[] data = new byte[20480];
      while ((count = origin.read(data, 0, 20480)) != -1) {
        zipOut.write(data, 0, count);
      }
      count = 0;
      origin.close();
    }
    zipOut.close();
    dest.close();
  }


  public void deleteSourceFile() {

    Iterator<String> iter = this.filesToPack.iterator();
    while (iter.hasNext()) {
      String curFile = iter.next();
      System.out.println("Delete: " + curFile);
      File realFile = new File(curFile);
      if (realFile.exists()) {
        // Attempt to delete it
        String dir = realFile.getParent();
        boolean success = realFile.delete();
        if (!dir.isEmpty()) {
          new File(dir).delete();
        }

        if (!success) {
          throw new IllegalArgumentException("Delete: deletion failed");
        }
      } else {
        throw new IllegalArgumentException(
            "Delete: no such file or directory: " + curFile);
      }
    }
  }


  public void extract() throws IOException {

    this.zip = new ZipFile(this.archiveName);
    this.zis = new ZipInputStream(new FileInputStream(this.archiveName));
    ZipEntry entry;
    while ((entry = this.zis.getNextEntry()) != null) {
      this.archiveMap.put(Paths.get(entry.getName()).toString(), this.zip.getInputStream(entry));
    }
  }


  public void close() {

    try {
      for (InputStream oneIn : this.archiveMap.values()) {
        oneIn.close();
      }
      this.zis.close();
      this.zip.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String toString() {

    return this.archiveMap.toString();
  }
}
