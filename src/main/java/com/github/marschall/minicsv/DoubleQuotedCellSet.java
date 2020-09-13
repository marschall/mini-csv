package com.github.marschall.minicsv;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.lineparser.Line;

/**
 * Special case where the quote and escape character are the same.
 */
final class DoubleQuotedCellSet extends CellSet {

  private static final int MAX_MASK_SIZE = 64;

  private final char quote;

  private int nextCellStart;

  private int nextContentStart;

  private int nextCellEnd;

  DoubleQuotedCellSet(Line line, int lineNumber, char delimiter, char quote, char escape) {
    super(line, lineNumber, delimiter, quote, escape);
    this.quote = quote;
    if (this.sequenceLength == 0) {
      this.nextCellStart =  -1;
    } else {
      this.nextCellStart = 0;
      this.nextCellEnd = -1; // will be incremented to 0 in #next
    }
  }

  public boolean next() {
    if (this.nextCellStart ==  -1) {
      return false;
    }

    this.nextCellStart = this.nextCellEnd + 1; // skip the delimiter
    if (this.nextCellStart > this.sequenceLength) {
      // end is reached, was #sequenceLength but got incremented by 1
      this.nextCellStart = -1;
      return false;
    }
    this.nextContentStart = this.findNextContentStart();

    this.nextCellEnd = this.findEnd();
    this.columnIndex += 1;
    return true;
  }

  private int findNextContentStart() {
    // i = 4, check i = 5 => length 6
    if (this.nextCellStart < this.sequenceLength - 1 && this.charSequence.charAt(this.nextCellStart) == this.quote) {
      return this.nextCellStart + 1;
    } else {
      return this.nextCellStart;
    }
  }

  @Override
  public boolean isCellEmpty() {
    if (this.isCellQuoted()) {
      return (this.nextCellEnd - this.nextContentStart) == 1;
    } else {
      return (this.nextCellEnd - this.nextCellEnd) == 0;
    }
  }
  
  // TODO getString

  @Override
  public CharSequence getCharSequence() {
    if (this.isCellQuoted()) {
      int cellSize = this.nextCellEnd - this.nextContentStart - 1;
      if (cellSize == 0) {
        return this.charSequence.subSequence(this.nextContentStart, this.nextCellEnd - 1);
      } else if (cellSize <= MAX_MASK_SIZE) {
        return this.getShortCharSequence(cellSize);
      } else {
        return this.getLongCharSequence(cellSize);
      }
    } else {
      return this.charSequence.subSequence(this.nextContentStart, this.nextCellEnd);
    }
  }

  private CharSequence getShortCharSequence(int cellSize) {
    long escapeMask = 0L;
    for (int i = 0; i < cellSize; i++) {
      if (this.charSequence.charAt(this.nextContentStart + i) == this.quote) {
        escapeMask |= 1L << i;
        // the next character is by definition not quoted;
        i += 1;
      }
    }
    if (escapeMask != 0) {
      return new EscapedCharSubSequence(this.charSequence, this.nextContentStart, this.nextCellEnd - 1, escapeMask);
    } else {
      return this.charSequence.subSequence(this.nextContentStart, this.nextCellEnd - 1);
    }
  }

  private CharSequence getLongCharSequence(int cellSize) {
    StringBuilder content = new StringBuilder(cellSize);
    for (int i = 0; i < cellSize; i++) {
      char c = this.charSequence.charAt(this.nextContentStart + i);
      if (c == this.quote) {
        // the next character is by definition not quoted;
        content.append(this.charSequence.charAt(this.nextContentStart + i + 1));
        i += 1;
      } else {
        content.append(c);
      }
    }
    return content;
  }

  private boolean isCellQuoted() {
    return this.nextCellStart != this.nextContentStart;
  }

  private int findEnd() {
    if (this.isCellQuoted()) {
      return this.findEndQuoted();
    } else {
      return this.findEndUnquoted();
    }
  }

  private int findEndUnquoted() {
    int fromIndex = this.nextContentStart;
    int end = CharSequences.indexOf(this.charSequence, this.delimiter, fromIndex);
    if (end == -1) {
      return this.sequenceLength;
    } else {
      return end;
    }
  }

  private int findEndQuoted() {
    int fromIndex = this.nextContentStart;
    int end = CharSequences.indexOf(this.charSequence, this.delimiter, fromIndex);
    while (end != -1) {
      if (this.charSequence.charAt(end - 1) == this.quote) {
        return end;
      }
      end = CharSequences.indexOf(this.charSequence, this.delimiter, end + 1);
    }
    // TODO throw if unquoted?
    return this.sequenceLength;
  }

}
