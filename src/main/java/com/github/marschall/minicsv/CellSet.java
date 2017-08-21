package com.github.marschall.minicsv;

import java.math.BigDecimal;

import com.github.marschall.charsequences.CharSequences;

/**
 * A set of cells in a row, can be iterated over much like a
 * {@link java.sql.ResultSet}.
 */
public final class CellSet {

  // TODO quotes
  // TODO BigDecimal pattern
  // TODO default values for empty int and long

  private final Row row;

  private int nextStart;
  private int nextEnd;
  private int columnIndex;

  private boolean start;

  CellSet(Row row) {
    this.row = row;

    this.start = true;
    this.columnIndex = 0;
  }

  private int findEnd() {
    int fromIndex = this.nextStart;
    CharSequence charSequence = this.row.line.getContent();
    int length = charSequence.length();
    if (fromIndex > length) {
      return length;
    }
    int end = CharSequences.indexOf(charSequence, this.row.delimiter, fromIndex);
    if (end == -1) {
      return length;
    } else {
      return end;
    }
  }

  public boolean next() {
    if (this.start) {
      this.nextStart = 0;
      this.nextEnd = this.findEnd();
      this.start = false;
      return true;
    }
    if (this.nextStart ==  -1) {
      return false;
    }

    this.nextStart = this.nextEnd + 1; // skip the delimiter
    CharSequence charSequence = this.row.line.getContent();
    if (this.nextStart > charSequence.length()) {
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
    return this.row.lineNumber;
  }

  public CharSequence getCharSequence() {
    CharSequence charSequence = this.row.line.getContent();
    return charSequence.subSequence(this.nextStart, this.nextEnd);
  }

  public boolean isCellEmpty() {
    return (this.nextEnd - this.nextStart) == 0;
  }

  public int getInt() {
    CharSequence charSequence = this.row.line.getContent();
    return CharSequences.parseInt(charSequence, this.nextStart, this.nextEnd);
  }

  public long getLong() {
    CharSequence charSequence = this.row.line.getContent();
    return CharSequences.parseLong(charSequence, this.nextStart, this.nextEnd);
  }

  public String getString() {
    return this.getCharSequence().toString();
  }

  public BigDecimal getBigDecimal() {
    return new BigDecimal(this.getString());
  }

}
