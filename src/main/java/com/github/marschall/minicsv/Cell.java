package com.github.marschall.minicsv;

import com.github.marschall.charsequences.CharSequences;

public final class Cell {

  private final CharSequence charSequence;

  // TODO index

  Cell(CharSequence charSequence) {
    this.charSequence = charSequence;
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

  public String getAsString() {
    return this.charSequence.toString();
  }

}
