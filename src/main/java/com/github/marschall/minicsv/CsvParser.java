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
    LineCallback lineCallback = new LineCallback(this.ignoreFirstLine, rowCallback, this.delimiter);
    lineParser.forEach(path, charset, lineCallback);
  }

  static final class LineCallback implements Consumer<Line> {

    private int lineNumber;
    private final Consumer<Row> rowCallback;
    private final boolean ignoreFirstLine;
    private final char delimiter;

    LineCallback(boolean ignoreFirstLine, Consumer<Row> rowCallback, char delimiter) {
      this.ignoreFirstLine = ignoreFirstLine;
      this.rowCallback = rowCallback;
      this.delimiter = delimiter;
      this.lineNumber = 0;
    }

    @Override
    public void accept(Line line) {
      this.lineNumber += 1;
      if (this.ignoreFirstLine && (this.lineNumber == 1)) {
        return;
      }
      Row row = new Row(line, this.lineNumber, this.delimiter);
      this.rowCallback.accept(row);
    }

  }

}
