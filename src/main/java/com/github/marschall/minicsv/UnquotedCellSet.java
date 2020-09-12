package com.github.marschall.minicsv;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.lineparser.Line;

/**
 * Special case in which no quoting and escaping is used. This runs
 * the simplest code path.
 */
final class UnquotedCellSet extends CellSet {

  UnquotedCellSet(Line line, int lineNumber, char delimiter, char quote, char escape) {
    super(line, lineNumber, delimiter, (char) 0, (char) 0);
    if (this.sequenceLength == 0) {
      this.nextStart =  -1;
    } else {
      this.nextStart = 0;
      this.nextEnd = -1; // will be incremented to 0 in #next
    }
  }

  public boolean next() {
    if (this.nextStart ==  -1) {
      return false;
    }

    this.nextStart = this.nextEnd + 1; // skip the delimiter

    if (this.nextStart > this.sequenceLength) {
      // end is reached, was #sequenceLength but got incremented by 1
      this.nextStart = -1;
      return false;
    } else {
      this.nextEnd = this.findEnd();
      this.columnIndex += 1;
      return true;
    }
  }

  private int findEnd() {
    int fromIndex = this.nextStart;
    int end = CharSequences.indexOf(this.charSequence, this.delimiter, fromIndex);
    if (end == -1) {
      return this.sequenceLength;
    } else {
      return end;
    }
  }

}
