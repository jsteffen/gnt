package de.dfki.mlt.gnt.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
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
  private List<Path> filesToPack = new ArrayList<>();


  public Archivator(String archiveName) {

    this.archiveName = archiveName;
  }


  public String getArchiveName() {

    return this.archiveName;
  }


  public void setArchiveName(String archiveName) {

    this.archiveName = archiveName;
  }


  public List<Path> getFilesToPack() {

    return this.filesToPack;
  }


  public InputStream getInputStream(String entry)
      throws IOException {

    InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.archiveName);
    // if can't be loaded from classpath, try to load it from the file system
    if (null == in) {
      in = Files.newInputStream(Paths.get(this.archiveName));
    }
    ZipInputStream zin = new ZipInputStream(in);
    for (ZipEntry e; (e = zin.getNextEntry()) != null;) {
      if (e.getName().equals(entry)) {
        return zin;
      }
    }
    throw new IOException(String.format("\"%s\" not found in archive", entry));
  }


  /**
   * Creates a stream to the archive name, and adds all files collected in variable filesToPack.
   * @throws IOException
   */
  public void pack() throws IOException {

    OutputStream dest =
        Files.newOutputStream(GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(this.archiveName));
    ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(dest));

    for (Path onePath : this.filesToPack) {
      String zipEntry = GlobalConfig.getModelBuildFolder().relativize(onePath).normalize().toString();
      zipEntry = zipEntry.replaceAll("\\" + System.getProperty("file.separator"), "/");
      zipOut.putNextEntry(new ZipEntry(zipEntry));
      BufferedInputStream origin = new BufferedInputStream(Files.newInputStream(onePath));
      int count = 0;
      byte[] data = new byte[20480];
      while ((count = origin.read(data, 0, 20480)) != -1) {
        zipOut.write(data, 0, count);
      }
      count = 0;
      origin.close();
    }
    zipOut.close();
  }
}
