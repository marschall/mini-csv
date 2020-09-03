package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

final class QuotedEscapedCellSet extends CellSet {

  private final char quote;
  private final char escape;
  
  private boolean cellQuoted;

  QuotedEscapedCellSet(Line line, int lineNumber, char delimiter, char quote, char escape) {
    super(line, lineNumber, delimiter, quote, escape);
    this.quote = quote;
    this.escape = escape;
  }

}
