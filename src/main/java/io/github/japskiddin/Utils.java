package io.github.japskiddin;

import java.io.File;
import java.io.IOException;

/**
 * Utility class
 */
class Utils {
  /**
   * Delete folder with all files
   *
   * @param file Source folder
   * @throws IOException
   */
  public static void deleteDir(File file) throws IOException {
    if (file.isDirectory()) {
      File[] entries = file.listFiles();
      if (entries != null) {
        for (File entry : entries) {
          deleteDir(entry);
        }
      }
    }
    if (!file.delete()) {
      throw new IOException("Failed to delete " + file);
    }
  }
}