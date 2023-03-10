package selib.conventions;

import selib.BookDescription;

public class InventoryNumberFormatter {
    public static String formatToInventoryNumber(Integer bookNumber, BookDescription bookDescription) {
        return String.format("IN:%s:_:%s", bookNumber, bookDescription.getIsbn());
    }
}
