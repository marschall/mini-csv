package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

/**
 * Special case where quotes but not escapes can be used.
 */
final class QuotedCellSet extends CellSet {

  private final char quote;
  
  private boolean cellQuoted;

  QuotedCellSet(Line line, int lineNumber, char delimiter, char quote, char escape) {
    super(line, lineNumber, delimiter, quote, (char) 0);
    this.quote = quote;
  }

}
