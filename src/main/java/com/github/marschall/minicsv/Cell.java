package com.github.marschall.minicsv;

import java.math.BigDecimal;

import com.github.marschall.charsequences.CharSequences;

public final class Cell {

  private static final Cell EMPTY = new Cell("");

  private final CharSequence charSequence;

  // TODO index
  // TODO quotes
  // TODO default values

  private Cell(CharSequence charSequence) {
    this.charSequence = charSequence;
  }

  static Cell forSequence(CharSequence charSequence) {
    if (charSequence.length() == 0) {
      return EMPTY;
    } else {
      return new Cell(charSequence);
    }
  }

  public CharSequence getCharSequence() {
    return this.charSequence;
  }

  public boolean isEmpty() {
    return this.charSequence.length() == 0;
  }

  public int getAsInt() {
    return CharSequences.parseInt(this.charSequence);
  }

  public long getAsLong() {
    return CharSequences.parseLong(this.charSequence);
  }

  public BigDecimal getAsBigDecimal() {
    return new BigDecimal(this.charSequence.toString());
  }

  public String getAsString() {
    return this.charSequence.toString();
  }

}
