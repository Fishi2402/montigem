package arrays;

import java.util.Arrays;

public class UtilArraysUnsupported {
  public void test() {
    // None of these operations/signatures are supported by umlp-jsweet
    // and should generate transpiler errors

    Arrays.binarySearch(new int[] { 1,2,3 }, 2);

    Arrays.compare(new int[] { 1,2,3 }, new int[] { 4, 5, 6 });

    Arrays.compareUnsigned(new int[] { 1,2,3 }, new int[] { 4, 5, 6 });

    // overload with Clazz not supported
    Arrays.copyOf(new Integer[] { 1,2,3 }, 1, Long[].class);

    // overload with Clazz not supported
    Arrays.copyOfRange(new Integer[] { 1,2,3 }, 1, 3, Long[].class);

    Arrays.deepEquals(new Integer[] { 1,2,3 }, new Integer[] { 1,2,3 });

    Arrays.deepHashCode(new Integer[] { 1,2,3 });

    Arrays.deepToString(new Integer[] { 1,2,3 });

    Arrays.equals(new int[] { 1,2,3 }, new int[] { 1,2,3 });

    Arrays.hashCode(new int[] { 1,2,3 });

    Arrays.parallelSort(new int[] { 1,2,3 });

    // overload with Comparator not supported
    Arrays.sort(new Integer[] { 1,2,3 }, (a, b) -> a - b);

    Arrays.spliterator(new int[] { 1,2,3 });
  }
}
