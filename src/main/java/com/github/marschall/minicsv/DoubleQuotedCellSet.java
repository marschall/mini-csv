package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

/**
 * Special case where the quote and escape character are the same.
 */
final class DoubleQuotedCellSet extends CellSet {

  private final char quote;
  
  private boolean cellQuoted;

  DoubleQuotedCellSet(Line line, int lineNumber, char delimiter, char quote, char escape) {
    super(line, lineNumber, delimiter, quote, escape);
    this.quote = quote;
  }

}
