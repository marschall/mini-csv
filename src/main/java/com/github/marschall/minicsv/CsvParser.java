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

  public void parse(Path path, Charset cs, Consumer<Row> rowCallback) throws IOException {
    LineParser lineParser = new LineParser();
    LineNumber lineNumber = new LineNumber();
    lineParser.forEach(path, cs, line -> {
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
