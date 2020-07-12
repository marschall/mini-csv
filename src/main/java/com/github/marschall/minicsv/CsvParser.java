package com.github.marschall.minicsv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.marschall.lineparser.Line;
import com.github.marschall.lineparser.LineParser;

public final class CsvParser {

  private final char delimiter;

  private final boolean ignoreFirstLine;

  private final char quote;

  public CsvParser(char delimiter, boolean ignoreFirstLine) {
    this.delimiter = delimiter;
    this.ignoreFirstLine = ignoreFirstLine;
    this.quote = 0;
  }

  public CsvParser(char delimiter) {
    this(delimiter, false);
  }

  public CsvParser(ParserConfiguration configuration) {
    this.delimiter = configuration.getDelimiter();
    this.ignoreFirstLine = configuration.isIgnoreFirstLine();
    this.quote = configuration.getQuote();
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
    Consumer<Line> lineCallback;
    if (this.ignoreFirstLine) {
      lineCallback = new IgnoreFirstLineCallback(rowCallback, this.delimiter, this.quote);
    } else {
      lineCallback = new AllLinesCallback(rowCallback, this.delimiter, this.quote);
    }
    lineParser.forEach(path, charset, lineCallback);
  }

  static final class AllLinesCallback implements Consumer<Line> {

    private int lineNumber;
    private final Consumer<Row> rowCallback;
    private final char delimiter;
    private final char quote;

    AllLinesCallback(Consumer<Row> rowCallback, char delimiter, char quote) {
      this.rowCallback = rowCallback;
      this.delimiter = delimiter;
      this.quote = quote;
      this.lineNumber = 0;
    }

    @Override
    public void accept(Line line) {
      Row row = new Row(line, this.lineNumber, this.delimiter, this.quote);
      this.rowCallback.accept(row);
      this.lineNumber += 1;
    }

  }

  static final class IgnoreFirstLineCallback implements Consumer<Line> {

    private int lineNumber;
    private final Consumer<Row> rowCallback;
    private final char delimiter;
    private final char quote;

    IgnoreFirstLineCallback(Consumer<Row> rowCallback, char delimiter, char quote) {
      this.rowCallback = rowCallback;
      this.delimiter = delimiter;
      this.quote = quote;
      this.lineNumber = 0;
    }

    @Override
    public void accept(Line line) {
      if (this.lineNumber == 0) {
        this.lineNumber += 1;
        return;
      }
      Row row = new Row(line, this.lineNumber, this.delimiter, this.quote);
      this.rowCallback.accept(row);
      this.lineNumber += 1;
    }

  }

}
