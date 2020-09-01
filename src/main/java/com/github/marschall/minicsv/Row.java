package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

public final class Row {

  private final Line line;

  private final char delimiter;

  private final int lineNumber;

  private final char quote;

  private final char escape;

  Row(Line line, int lineNumber, char delimiter, char quote, char escape) {
    this.line = line;
    this.lineNumber = lineNumber;
    this.delimiter = delimiter;
    this.quote = quote;
    this.escape = escape;
  }

  public Line getLine() {
    return this.line;
  }

  public CellSet getCellSet() {
    return new CellSet(this.line, this.lineNumber, this.delimiter, this.quote, this.escape);
  }

}
