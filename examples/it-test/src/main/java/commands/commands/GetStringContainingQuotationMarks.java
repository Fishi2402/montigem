package commands.commands;

import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;
import org.apache.commons.text.StringEscapeUtils;

public class GetStringContainingQuotationMarks extends GetStringContainingQuotationMarksTOP {

  public static final String stringWithQuotationMarks = """
        /**
             * Creates an empty {@code Spliterator}
             *
             * <p>The empty spliterator reports {@link Spliterator#SIZED} and
             * {@link Spliterator#SUBSIZED}.  Calls to
             * {@link java.util.Spliterator#trySplit()} always return {@code null}.
             *
             * @param <T> Type of elements
             * @return An empty spliterator
             */
            @SuppressWarnings("unchecked")
            public static <T> Spliterator<T> emptySpliterator() {
                return (Spliterator<T>) EMPTY_SPLITERATOR;
            }
        """;

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public SimpleResult<String> doAction() {
        return new SimpleResult<String>(this.getId(),
        StringEscapeUtils.escapeJson(stringWithQuotationMarks),
        "java.lang.String");
  }
}
