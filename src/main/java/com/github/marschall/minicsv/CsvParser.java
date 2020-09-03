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

  private final char escape;

  public CsvParser(char delimiter, boolean ignoreFirstLine) {
    this.delimiter = delimiter;
    this.ignoreFirstLine = ignoreFirstLine;
    this.quote = 0;
    this.escape = 0;
  }

  public CsvParser(char delimiter) {
    this(delimiter, false);
  }

  public CsvParser(ParserConfiguration configuration) {
    this.delimiter = configuration.getDelimiter();
    this.escape = configuration.getEscape();
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
    CellSetFactory cellSetFactory = this.newCellSetFactory();
    Consumer<Line> lineCallback;
    if (this.ignoreFirstLine) {
      lineCallback = new IgnoreFirstLineCallback(rowCallback, cellSetFactory);
    } else {
      lineCallback = new AllLinesCallback(rowCallback, cellSetFactory);
    }
    lineParser.forEach(path, charset, lineCallback);
  }
  
  private CellSetFactory newCellSetFactory() {
    if (this.hasQuote()) {
      if (this.hasEscape()) {
        if (this.quote == this.escape) {
          return (line, lineNumber) -> new DoubleQuotedCellSet(line, lineNumber, delimiter, quote, escape);
        } else {
          return (line, lineNumber) -> new QuotedEscapedCellSet(line, lineNumber, delimiter, quote, escape);
        }
      } else {
        return (line, lineNumber) -> new QuotedCellSet(line, lineNumber, delimiter, quote, escape);
      }
    } else {
      return (line, lineNumber) -> new UnquotedCellSet(line, lineNumber, delimiter, quote, escape);
    }
  }

  private boolean hasQuote() {
    return this.quote != 0;
  }

  private boolean hasEscape() {
    return this.escape != 0;
  }


  static final class AllLinesCallback implements Consumer<Line> {

    private int lineNumber;
    private final Consumer<Row> rowCallback;
    private final CellSetFactory cellSetFactory;

    AllLinesCallback(Consumer<Row> rowCallback, CellSetFactory cellSetFactory) {
      this.rowCallback = rowCallback;
      this.cellSetFactory = cellSetFactory;
      this.lineNumber = 0;
    }

    @Override
    public void accept(Line line) {
      Row row = new Row(line, this.lineNumber, this.cellSetFactory);
      this.rowCallback.accept(row);
      this.lineNumber += 1;
    }

  }

  static final class IgnoreFirstLineCallback implements Consumer<Line> {

    private int lineNumber;
    private final Consumer<Row> rowCallback;
    private final CellSetFactory cellSetFactory;

    IgnoreFirstLineCallback(Consumer<Row> rowCallback, CellSetFactory cellSetFactory) {
      this.rowCallback = rowCallback;
      this.cellSetFactory = cellSetFactory;
      this.lineNumber = 0;
    }

    @Override
    public void accept(Line line) {
      if (this.lineNumber == 0) {
        this.lineNumber += 1;
        return;
      }
      Row row = new Row(line, this.lineNumber, this.cellSetFactory);
      this.rowCallback.accept(row);
      this.lineNumber += 1;
    }

  }

}
