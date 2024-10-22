package umlp.generator.util;

import com.google.common.collect.Streams;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTTargetElement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Stream;

// CONTAINS FIXES for monticore/monticore#4241
public class UMLPSimpleSymbolTagger extends SimpleSymbolTagger {
  public UMLPSimpleSymbolTagger(@Nonnull Supplier<Iterable<ASTTagUnit>> tagUnitsSupplier) {
    super(tagUnitsSupplier);
  }

  public UMLPSimpleSymbolTagger() {
  }


  @Override
  protected Stream<ASTTargetElement> findTagTargets(@Nonnull ISymbol symbol) {
    String fqn = symbol.getFullName();

    // We return (the stream of) an iterator
    Iterator<ASTTargetElement> iterator = new ProgressiveIteratorFIX<ASTTargetElement, ASTTagUnit> // FIX: USE OR
        (backingTagUnits.get().iterator()) {
      @Override
      protected Collection<? extends ASTTargetElement> doWork(ASTTagUnit tagUnit) {
        // Add the matching ASTTargetElement from #findTagTargetsOfTagUnit to the buffer
        return findTagTargetsOfTagUnit(tagUnit, fqn);
      }

      @Override
      protected void cleanup() {
        // cleanup (remove unloaded TagUnits)
        tagUnitMapping.cleanUp();
      }
    };

    // Return a stream of this iterator
    return Streams.stream(iterator);
  }

  protected abstract static class ProgressiveIteratorFIX<A, B> implements Iterator<A> {
    // With a buffer of target elements
    final LinkedList<A> targetElementBuffer = new LinkedList<>();
    // And an iterator to the backing tag units
    final Iterator<B> backing;

    public ProgressiveIteratorFIX(Iterator<B> backing) {
      this.backing = backing;
    }

    @Override
    public boolean hasNext() {
      // In case the buffer is not empty => return true
      if (!targetElementBuffer.isEmpty()) return true;
      // try to advance the buffer (find targets within the next tagunit)
      tryToAdvance();
      // and return whether the buffer now contains targets
      return !targetElementBuffer.isEmpty();
    }

    @Override
    public A next() {
      return targetElementBuffer.removeFirst();
    }

    /**
     * Advance to the next backing tagunit and add matching targets to the buffer
     */
    void tryToAdvance() {
      if (!backing.hasNext()) {
        this.cleanup();
        return;
      }
      // FIX START
      // Actually do the tagging related logic
      while (backing.hasNext() && targetElementBuffer.isEmpty()) {
        // Repeat the work until the buffer is no longer empty (or the backing iterator consumed)
        targetElementBuffer.addAll(this.doWork(backing.next()));
      }
      if (!backing.hasNext()) {
        this.cleanup();
      }
      // FIX END
    }

    /**
     * Perform some work and add it to a buffer
     *
     * @param next the unit to work on
     * @return the values to be added to a buffer
     */
    @Nonnull
    protected abstract Collection<? extends A> doWork(B next); // FIX: protected

    protected abstract void cleanup(); // FIX: protected
  }
}
