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
    range = [0, 100];
    steps = [0, 13, 20, 36, 56];

    public override getBulletchartValue(): number {
        if (!this.persons) {
            return null;
        }
        let sumOfAges = 0;
        for (let person of this.persons) {
            let currentYear = new Date().getFullYear();
            let age = currentYear - Number(person?.getBirthday()?.toDateString().substring(11, 15));
            sumOfAges += age;
        }
        let averageYear = sumOfAges / this.persons.size();
        if (JSON.stringify(averageYear) === this.chartValueHash && this.chartValue) {
            return this.chartValue;
        }
        this.chartValue = averageYear;
        this.chartValueHash = JSON.stringify(this.chartValue);
        return this.chartValue;
    }

}