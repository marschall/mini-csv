package com.github.marschall.minicsv;


final class EscapedCharSubSequence implements CharSequence {

  private final long escapes;

  private final CharSequence delegate;

  private final int length;

  /**
   * Start index on {@link #delegate}, inclusive.
   */
  private final int start;

  
  /**
   * End index on {@link #delegate}, exclusive.
   */
  private final int end;

  EscapedCharSubSequence(CharSequence delegate, int start, int end, long escapes) {
    this.start = start;
    this.end = end;
    this.escapes = escapes;
    this.delegate = delegate;
    this.length = end - start - Long.bitCount(this.escapes);
  }
  
  EscapedCharSubSequence(CharSequence delegate, int start, int end, long escapes, int length) {
    this.start = start;
    this.end = end;
    this.escapes = escapes;
    this.delegate = delegate;
    this.length = length;
  }

  @Override
  public int length() {
    return this.length;
  }

  // TODO chars

  @Override
  public char charAt(int index) {
    if (index < 0 || index >= this.length) {
      throw new IndexOutOfBoundsException();
    }
    long mask = (1L << (index + 1)) - 1L;
    int offset = Long.bitCount(this.escapes & mask);
    return this.delegate.charAt(this.start + index + offset);
  }

  @Override
  public CharSequence subSequence(int newStart, int end) {
    if (newStart < 0 || end > this.length || newStart >= end) {
      throw new IndexOutOfBoundsException();
    }
    int newLength = end - newStart;
    if (newLength == this.length) {
      return this;
    }
    if (newLength == 0) {
      return "";
    }
    long mask = ((1L << (newLength + 1)) - 1L) << newStart;
    long newEscapes = (this.escapes & mask) >>> newStart;
    return new EscapedCharSubSequence(this.delegate, this.start + newStart, end + Long.bitCount(newEscapes), newEscapes, newLength);
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder(this.length);
    for (int i = 0; i < this.end; i++) {
      if ((this.escapes & (1L << i)) == 0) {
        buffer.append(this.delegate.charAt(this.start + i));
      }
    }
    return buffer.toString();
  }

}
