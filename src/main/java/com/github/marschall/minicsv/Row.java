package com.github.marschall.minicsv;

import java.util.Iterator;
import java.util.function.Consumer;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.lineparser.Line;

public final class Row {

  private final Line line;

  private final char delimiter;

  private final int lineNumber;

  Row(Line line, int lineNumber, char delimiter) {
    this.line = line;
    this.lineNumber = lineNumber;
    this.delimiter = delimiter;
  }

  public Line getLine() {
    return this.line;
  }

  public Iterable<Cell> getCells() {
    return new CellIterable(CharSequences.split(this.line.getContent(), this.delimiter));
  }

  public CellSet getCellSet() {
    return new CellSet(this.line, this.lineNumber, this.delimiter);
  }

  static final class CellIterable implements Iterable<Cell> {

    private final Iterable<CharSequence> charSequenceIterable;

    CellIterable(Iterable<CharSequence> charSequenceIterable) {
      this.charSequenceIterable = charSequenceIterable;
    }

    @Override
    public Iterator<Cell> iterator() {
      return new CellIterator(this.charSequenceIterable.iterator());
    }

  }

  static final class CellIterator implements Iterator<Cell> {

    private final Iterator<CharSequence> charSequenceIterator;

    CellIterator(Iterator<CharSequence> charSequenceIterable) {
      this.charSequenceIterator = charSequenceIterable;
    }

    @Override
    public boolean hasNext() {
      return this.charSequenceIterator.hasNext();
    }

    @Override
    public Cell next() {
      return Cell.forSequence(this.charSequenceIterator.next());
    }

    @Override
    public void forEachRemaining(Consumer<? super Cell> action) {
      this.charSequenceIterator.forEachRemaining(charSequence -> action.accept(Cell.forSequence(charSequence)));
    }

  }

}
