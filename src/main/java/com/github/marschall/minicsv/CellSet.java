package com.github.marschall.minicsv;

import java.math.BigDecimal;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.lineparser.Line;

/**
 * A set of cells in a row, can be iterated over much like a
 * {@link java.sql.ResultSet}.
 */
public final class CellSet {

  // TODO quotes
  // TODO BigDecimal pattern
  // TODO default values for empty int and long

  private final CharSequence charSequence;

  private final char delimiter;

  private int nextStart;
  private int nextEnd;
  private int columnIndex;

  private final int lineNumber;
  private boolean start;

  CellSet(Line line, int lineNumber, char delimiter) {
    this.lineNumber = lineNumber;
    this.charSequence = line.getContent();
    this.delimiter = delimiter;

    this.start = true;
    this.columnIndex = -1; // first time scanning will increment to 0
  }

  private int findEnd() {
    int fromIndex = this.nextStart;
    int length = this.charSequence.length();
    if (fromIndex > length) {
      return length;
    }
    int end = CharSequences.indexOf(this.charSequence, this.delimiter, fromIndex);
    if (end == -1) {
      return length;
    } else {
      return end;
    }
  }

  public boolean next() {
    if (this.nextStart ==  -1) {
      return false;
    }

    // keep code paths as similar as possible in order to reduce method
    // size after inlining
    if (this.start) {
      this.nextStart = 0;
      this.start = false;
    } else {
      this.nextStart = this.nextEnd + 1; // skip the delimiter
    }

    if (this.nextStart > this.charSequence.length()) {
      // end is reached
      this.nextStart = -1;
      return false;
    } else {
      this.nextEnd = this.findEnd();
      this.columnIndex += 1;
      return true;
    }

  }

  public int getColumnIndex() {
    return this.columnIndex;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public CharSequence getCharSequence() {
    return this.charSequence.subSequence(this.nextStart, this.nextEnd);
  }

  public boolean isCellEmpty() {
    return (this.nextEnd - this.nextStart) == 0;
  }

  public int getInt() {
    return CharSequences.parseInt(this.charSequence, this.nextStart, this.nextEnd);
  }

  public long getLong() {
    return CharSequences.parseLong(this.charSequence, this.nextStart, this.nextEnd);
  }

  public String getString() {
    return this.getCharSequence().toString();
  }

  public BigDecimal getBigDecimal() {
    return new BigDecimal(this.getString());
  }

}
