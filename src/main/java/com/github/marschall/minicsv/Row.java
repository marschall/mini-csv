package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

public final class Row {

  private final Line line;

  private final int lineNumber;

  private final CellSetFactory cellSetFactory;

  Row(Line line, int lineNumber, CellSetFactory cellSetFactory) {
    this.line = line;
    this.lineNumber = lineNumber;
    this.cellSetFactory = cellSetFactory;
  }

  public Line getLine() {
    return this.line;
  }

  public CellSet getCellSet() {
    return this.cellSetFactory.newCellSet(this.line, this.lineNumber);
  }

}
