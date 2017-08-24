package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

public final class Row {

  private final Line line;

  private final char delimiter;

  private final int lineNumber;

  Row(Line line, int lineNumber, char delimiter) {
    this.line = line;
    this.lineNumber = lineNumber;
    this.delimiter = delimiter;
  }

  public Line getLine() {
    return this.line;
  }

  public CellSet getCellSet() {
    return new CellSet(this.line, this.lineNumber, this.delimiter);
  }

}
