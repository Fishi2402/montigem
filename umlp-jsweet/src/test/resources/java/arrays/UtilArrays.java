package arrays;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UtilArrays {
  public String testToString() {
    return Arrays.toString(new Integer[] { 1,2, null, 3 });
  }

  public IntStream testStream() {
    return Arrays.stream(new int[] { 1, 2, 3 });
  }

  public IntStream testStreamRange() {
    return Arrays.stream(new int[] { 1, 2, 3 }, 1, 3);
  }
}
