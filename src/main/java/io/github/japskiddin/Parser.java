package io.github.japskiddin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
  private final List<String> items = new ArrayList<>();
  private int currentAttr = 0;
  private final List<String> attributes = new ArrayList<>();

  /**
   * Checks parameters and decides what to do next.
   *
   * @param args Entered parameters.
   */
  public void checkArguments(String[] args) {
    List<Option> optsList = new ArrayList<>();
    List<String> doubleOptsList = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      if (args[i].charAt(0) == '-') {
        if (args[i].length() < 2) {
          throw new IllegalArgumentException("Not a valid argument: " + args[i]);
        }
        if (args[i].charAt(1) == '-') {
          if (args[i].length() < 3) {
            throw new IllegalArgumentException("Not a valid argument: " + args[i]);
          }
          // --opt
          doubleOptsList.add(args[i].substring(2));
        } else {
          if (args.length - 1 == i) {
            throw new IllegalArgumentException("Expected arg after: " + args[i]);
          }
          // -opt
          optsList.add(new Option(args[i].substring(1), args[i + 1]));
          i++;
        }
      }
    }

    String src = null, dst = null;

    for (Option option : optsList) {
      switch (option.getFlag()) {
        case "dst" -> dst = option.getOpt();
        case "src" -> src = option.getOpt();
      }
    }

    for (String opt : doubleOptsList) {
      switch (opt) {
        case "version" -> {
          Package p = Main.class.getPackage();
          System.out.println("Version: " + p.getImplementationVersion());
        }
        case "help" -> showHelp();
        case "parse" -> parseToJSON(src, dst, new String[] { "r", "b" });
      }
    }
  }

  private void parseToJSON(String src, String dst, String[] names) {
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(src));
      String line = reader.readLine();
      while (line != null) {
        parseLine(line, names);
        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }

    if (items.size() == 0) return;
    System.out.println(items);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[");
    for (int i = 0; i < items.size(); i++) {
      String json = items.get(i);
      stringBuilder.append(json);
      if (i < items.size() - 1) stringBuilder.append(",");
    }
    stringBuilder.append("]");

    File srcFile = new File(src);
    File dstDir = new File(dst + File.separator + "outputs");
    if (dstDir.exists()) {
      try {
        Utils.deleteDir(dstDir);
      } catch (IOException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        return;
      }
    }

    boolean created = dstDir.mkdirs();
    if (!created) {
      throw new NullPointerException("Can't create output folder.");
    }

    File dstFile = new File(dstDir, "parsed_" + srcFile.getName());
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(dstFile));
      writer.append(stringBuilder);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    System.out.println("Parsing done successfully!");
  }

  private void parseLine(String line, String[] names) {
    if (line.equals("")) return;
    String attrString = String.format("\"%s\":\"%s\"", names[currentAttr], line);
    attributes.add(attrString);
    currentAttr++;
    checkEndAttributes(names);
  }

  private void checkEndAttributes(String[] names) {
    if (currentAttr >= names.length) {
      StringBuilder stringBuilder = new StringBuilder("{");
      for (int i = 0; i < attributes.size(); i++) {
        String attribute = attributes.get(i);
        stringBuilder.append(attribute);
        if (i < attributes.size() - 1) stringBuilder.append(",");
      }
      stringBuilder.append("}");
      items.add(stringBuilder.toString());
      currentAttr = 0;
      attributes.clear();
    }
  }

  /**
   * Shows help information.
   */
  private void showHelp() {
    System.out.println("Usage: [--parse | --help] -src <path> -dst <path>");
    System.out.println("\n--help - Show help information");
    System.out.println("--version - Show library version");
    System.out.println("--parse - Parse file to JSON format");
    System.out.println("-src <path> - Path to source file");
    System.out.println("-dst <path> - Path to folder with output files");
  }
}