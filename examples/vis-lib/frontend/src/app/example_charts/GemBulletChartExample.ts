/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBulletChartExampleComponent, config } from '@src/example_charts/GemBulletChartExampleComponent';

@Component({
    standalone: true,
    imports: [...config.imports],
    selector: config.selector,
    templateUrl: config.templateUrl,
})
export class GemBulletChartExample extends GemBulletChartExampleComponent {
    private chartValue: number;
    private chartValueHash: string;
    range = [0, 6000];
    steps = [0, 1000, 2000, 3000, 4000, 5000];

    public override getBulletchartValue(): number {
        if (!this.persons) {
            return null;
        }
        let sumOfStockPrices: number = 0;
        for (let person of this.persons) {
            for (let stock of person.getStocks()) {
                sumOfStockPrices += stock.getPrice();
            }
        }
        if (JSON.stringify(sumOfStockPrices) === this.chartValueHash && this.chartValue) {
            return this.chartValue;
        }
        this.chartValue = sumOfStockPrices;
        this.chartValueHash = JSON.stringify(this.chartValue);
        return this.chartValue;
    }

}