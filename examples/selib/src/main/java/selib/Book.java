/* (c) https://github.com/MontiCore/monticore */
package selib;

import selib.conventions.InventoryNumberFormatter;

public class Book extends BookTOP {
    @Override
    public String getInventoryNumber() {
        return InventoryNumberFormatter.formatToInventoryNumber(this.getBookNumber(), this.getDescription());
    }
}
