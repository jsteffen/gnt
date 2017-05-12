package archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Archivator {

  private String archiveName;
  private HashMap<String,InputStream> archiveMap;
  private List<String> filesToPack = new ArrayList<String>();

  //Setters and getters
  public List<String> getFilesToPack() {
    return filesToPack;
  }
  public void setFilesToPack(List<String> filesToPack) {
    this.filesToPack = filesToPack;
  }
  public String getArchiveName() {
    return archiveName;
  }
  public void setArchiveName(String archiveName) {
    this.archiveName = archiveName;
  }
  public HashMap<String, InputStream> getArchiveMap() {
    return archiveMap;
  }
  public void setArchiveMap(HashMap<String, InputStream> archiveMap) {
    this.archiveMap = archiveMap;
  }

  // Creator
  public Archivator(String archiveName){
    this.archiveName = archiveName;
    this.archiveMap = new HashMap<String,InputStream>();
  }

  // Methods

  /**
   * Creates a stream to the archive name, and adds all files collected in variable
   * filesToPack
   * @throws IOException
   */
  public void pack() throws IOException {
    FileOutputStream dest = new FileOutputStream(archiveName);
    ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(dest));

    Iterator<String> iter = filesToPack.iterator();
    while (iter.hasNext()) {
      String curFile = iter.next();
      curFile = curFile.replaceAll("\\"+System.getProperty("file.separator"), "/");
      zip.putNextEntry(new ZipEntry(curFile));
      BufferedInputStream origin = new BufferedInputStream(new FileInputStream(curFile));
      int count = 0;  
      byte data[] = new byte[20480];
      while((count = origin.read(data,0,20480)) != -1) {
        zip.write(data,0,count);
      }
      count = 0;
      origin.close();
     }
     zip.close();
    System.out.println("Delete source files:");
    this.deleteSourceFile();
  }

  public void deleteSourceFile(){
    Iterator<String> iter = filesToPack.iterator();
    while (iter.hasNext()) {
      String curFile = iter.next();
      System.out.println("Delete: " + curFile);
      File realFile = new File(curFile);
      if (realFile.exists()){
        // Attempt to delete it
        String dir = realFile.getParent();
        boolean success = realFile.delete();
        if (!dir.isEmpty()){new File(dir).delete();}

        if (!success)
          throw new IllegalArgumentException("Delete: deletion failed");
      }
      else
      {
        throw new IllegalArgumentException(
            "Delete: no such file or directory: " + curFile);
      }
    }
  }

  public void extract() throws IOException {
    ZipFile zip = new ZipFile(this.archiveName);
    ZipInputStream zis = new ZipInputStream(new FileInputStream(this.archiveName));
    ZipEntry entry;
    while ((entry = zis.getNextEntry()) != null) {
      archiveMap.put(entry.getName(), zip.getInputStream(entry));
    }
  }

  public String toString() {
    return archiveMap.toString();
  }
}
