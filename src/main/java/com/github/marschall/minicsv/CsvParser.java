package com.github.marschall.minicsv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.marschall.lineparser.LineParser;

public final class CsvParser {

  private final char delimiter;
  // TODO implement
  private final boolean ignoreFirstLine;

  public CsvParser(char delimiter, boolean ignoreFirstLine) {
    // TODO quote character
    this.delimiter = delimiter;
    this.ignoreFirstLine = ignoreFirstLine;
  }

  public CsvParser(char delimiter) {
    this(delimiter, false);
  }

  /**
   * Internal iterator over every row in a CSV file.
   *
   * <p>This method is thread safe.</p>
   *
   * @param path the file to parse, has to be on the default file system
   * @param charset the character set of the CSV file
   * @param rowCallback callback executed for every row
   * @throws IOException if an exception happens when reading
   * @see java.nio.file.FileSystems#getDefault()
   * @see com.github.marschall.lineparser.LineParser#forEach(Path, Charset, Consumer)
   */
  public void parse(Path path, Charset charset, Consumer<Row> rowCallback) throws IOException {
    LineParser lineParser = new LineParser();
    LineNumber lineNumber = new LineNumber();
    lineParser.forEach(path, charset, line -> {
      rowCallback.accept(new Row(line, lineNumber.incrementAndGet(), this.delimiter));
    });
  }

  static final class LineNumber {

    private int value;

    LineNumber() {
      this.value = 0;
    }

    int incrementAndGet() {
      int returnValue = this.value;
      this.value += 1;
      return returnValue;
    }

  }

}
