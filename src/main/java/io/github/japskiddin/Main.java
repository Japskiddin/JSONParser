package io.github.japskiddin;

public class Main {
  // --parse -src <path> -dst <path>
  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("No parameters found! Print --help for more information.");
    }

    Parser parser = new Parser();
    parser.checkArguments(args);
  }
}