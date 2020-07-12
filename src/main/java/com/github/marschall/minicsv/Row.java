package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

public final class Row {

  private final Line line;

  private final char delimiter;

  private final int lineNumber;

  private final char quote;

  Row(Line line, int lineNumber, char delimiter, char quote) {
    this.line = line;
    this.lineNumber = lineNumber;
    this.delimiter = delimiter;
    this.quote = quote;
  }

  public Line getLine() {
    return this.line;
  }

  public CellSet getCellSet() {
    return new CellSet(this.line, this.lineNumber, this.delimiter, this.quote);
  }

}
