import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgChartsModule } from 'ng2-charts';
import { GemSunburstChart } from './gem-sunburst-chart.component';
import { gemInstanceOfSDNshort, gemSdnShortToSDN } from './gem-sunburst-chart.types';
import { GemChartLegend } from "../gemchartlegend/gem-chart-legend.component";
import { GemSDNshort, GemSunburstData } from './gem-sunburst-chart-types/gem-sunburst-chart-types.types-gen';

describe('GemSunburstChartComponent', () => {
	let component: GemSunburstChart;
	let fixture: ComponentFixture<GemSunburstChart>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NgChartsModule],
			declarations: [
				GemSunburstChart,
				GemChartLegend
			],
			providers: [
			]
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GemSunburstChart);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	// Testing sunburst-chart.types
	it('testing gemInstanceOfSDNshort', () => {
		// Should be true with SDNshort objects
		expect(gemInstanceOfSDNshort(realTestData1Short)).toBeTruthy();
		expect(gemInstanceOfSDNshort(realTestData2Short)).toBeTruthy();
		expect(gemInstanceOfSDNshort(realTestData3Short)).toBeTruthy();
		// Should be false with SunburstDataNode objects
		expect(gemInstanceOfSDNshort(realTestData1Long)).toBeFalsy();
		expect(gemInstanceOfSDNshort(realTestData2Long)).toBeFalsy();
		expect(gemInstanceOfSDNshort(realTestData3Long)).toBeFalsy();
	});

	it('testing gemSdnShortToSDN', () => {
		// Should equal the corresponding long SunburstDataNode objects
		expect(gemSdnShortToSDN(realTestData1Short)).toEqual(realTestData1Long);
		expect(gemSdnShortToSDN(realTestData2Short)).toEqual(realTestData2Long);
		expect(gemSdnShortToSDN(realTestData3Short)).toEqual(realTestData3Long);
		// Should be not equal the other long SunburstDataNode objects
		expect(gemSdnShortToSDN(realTestData1Short)).not.toEqual(realTestData2Long);
		expect(gemSdnShortToSDN(realTestData1Short)).not.toEqual(realTestData3Long);
		expect(gemSdnShortToSDN(realTestData2Short)).not.toEqual(realTestData1Long);
		expect(gemSdnShortToSDN(realTestData2Short)).not.toEqual(realTestData3Long);
		expect(gemSdnShortToSDN(realTestData3Short)).not.toEqual(realTestData1Long);
		expect(gemSdnShortToSDN(realTestData3Short)).not.toEqual(realTestData2Long);
	});

	// Testing sunburst-chart.components
	it('testing check', () => {
		component.ignoreError = true;
		// Should be true for realData
		component.hasData = true;
		component.check(realTestData1Long);
		expect(component.hasData).toBeTruthy();
		component.hasData = true;
		component.check(realTestData2Long);
		expect(component.hasData).toBeTruthy();
		component.hasData = true;
		component.check(realTestData3Long);
		expect(component.hasData).toBeTruthy();
		// Should be false for fakeData
		component.hasData = true;
		component.ignoreError = false;
		component.check(fakeTestData2Long);
		expect(component.hasData).toBeFalsy();
	});

	it('testing getDepth', () => {
		// Should equal the depth of the trees
		expect(component.getDepth(realTestData1Long)).toEqual(2);
		expect(component.getDepth(realTestData2Long)).toEqual(2);
		expect(component.getDepth(realTestData3Long)).toEqual(2);
	});

	it('testing updateData, dataNodesToChart, makeLegend', () => {
		// After updateData there has to be data
		component.legendColors = null;
		component.newData = realTestData1Long;
		component.updateData();
		setTimeout(() => {
			expect(component.legendColors).not.toBeNull();

			//
			component.legendColors = null;
			component.newData = realTestData2Long;
			component.updateData();
			setTimeout(() => {
				expect(component.legendColors).not.toBeNull();
				//
				component.legendColors = null;
				component.newData = realTestData3Long;
				component.updateData();
				setTimeout(() => {
					expect(component.legendColors).not.toBeNull();
				}, 10)
			}, 10)
		}, 10)
	});

	let realTestData1Long: GemSunburstData = {
		entry: { label: "Anzahl der internationalen FachanfaengerInnen (1. Fachsemester) nach Nationalitaet und Geschlecht", value: 10588, },
		subEntries: [
			{
				entry: { label: "International", value: 3162, },
				subEntries: [
					{
						entry: { label: "Männlich", value: 2224, },
						subEntries: [],
					},
					{
						entry: { label: "Weiblich", value: 938, },
						subEntries: [],
					},
				],
			},
			{
				entry: { label: "Deutsch", value: 7426, },
				subEntries: [
					{
						entry: { label: "Männlich", value: 4746, },
						subEntries: [],
					},
					{
						entry: { label: "Weiblich", value: 2680, },
						subEntries: [],
					},
				],
			},
		],
	};


	let realTestData1Short: GemSDNshort = {
		c: ["Anzahl der internationalen FachanfaengerInnen (1. Fachsemester) nach Nationalitaet und Geschlecht", 10588],
		sN: [
			{
				c: ["International", 3162],
				sN: [
					{
						c: ["Männlich", 2224],
					},
					{
						c: ["Weiblich", 938],
					},
				],
			},
			{
				c: ["Deutsch", 7426],
				sN: [
					{
						c: ["Männlich", 4746],
					},
					{
						c: ["Weiblich", 2680],
					},
				],
			}
		],
	};

	let realTestData2Long: GemSunburstData = {
		entry: { label: "Studentenzahl RWTH, Quelle: Zahlenspiegel 2020 der RWTH", value: 47173, },
		subEntries: [
			{
				entry: { label: "Ingenieurwissenschaften", value: 26739, },
				subEntries: [
					{
						entry: { label: "Bachelor", value: 15722, },
						subEntries: [],
					},
					{
						entry: { label: "Master", value: 8842, },
						subEntries: [],
					},
					{
						entry: { label: "Lehramt", value: 171, },
						subEntries: [],
					},
					{
						entry: { label: "Sonstige", value: 248, },
						subEntries: [],
					},
					{
						entry: { label: "Promotion", value: 1756, },
						subEntries: [],
					},
				],
			},
			{
				entry: { label: "Mathematik, Informatik und Naturwissenschaften", value: 11574, },
				subEntries: [
					{
						entry: { label: "Bachelor", value: 6163, },
						subEntries: [],
					},
					{
						entry: { label: "Master", value: 3571, },
						subEntries: [],
					},
					{
						entry: { label: "Lehramt", value: 556, },
						subEntries: [],
					},
					{
						entry: { label: "Sonstige", value: 56, },
						subEntries: [],
					},
					{
						entry: { label: "Promotion", value: 1228, },
						subEntries: [],
					},
				],
			},
			{
				entry: { label: "Geistes-, Gesellschafts- und Wirtschaftswissenschaften", value: 5805, },
				subEntries: [
					{
						entry: { label: "Bachelor", value: 2525, },
						subEntries: [],
					},
					{
						entry: { label: "Master", value: 1764, },
						subEntries: [],
					},
					{
						entry: { label: "Lehramt", value: 1256, },
						subEntries: [],
					},
					{
						entry: { label: "Sonstige", value: 22, },
						subEntries: [],
					},
					{
						entry: { label: "Promotion", value: 238, },
						subEntries: [],
					},
				],
			},
			{
				entry: { label: "Medizin", value: 3055, },
				subEntries: [
					{
						entry: { label: "Bachelor", value: 2511, },
						subEntries: [],
					},
					{
						entry: { label: "Master", value: 222, },
						subEntries: [],
					},
					{
						entry: { label: "Lehramt", value: 0, },
						subEntries: [],
					},
					{
						entry: { label: "Sonstige", value: 20, },
						subEntries: [],
					},
					{
						entry: { label: "Promotion", value: 302, },
						subEntries: [],
					},
				],
			},
		],
	};


	let realTestData2Short: GemSDNshort = {
		c: ["Studentenzahl RWTH, Quelle: Zahlenspiegel 2020 der RWTH", 47173],
		sN: [
			{
				c: ["Ingenieurwissenschaften", 26739],
				sN: [
					{
						c: ["Bachelor", 15722],
					},
					{
						c: ["Master", 8842],
					},
					{
						c: ["Lehramt", 171],
					},
					{
						c: ["Sonstige", 248],
					},
					{
						c: ["Promotion", 1756],
					},
				],
			},
			{
				c: ["Mathematik, Informatik und Naturwissenschaften", 11574],
				sN: [
					{
						c: ["Bachelor", 6163],
					},
					{
						c: ["Master", 3571],
					},
					{
						c: ["Lehramt", 556],
					},
					{
						c: ["Sonstige", 56],
					},
					{
						c: ["Promotion", 1228],
					},
				],
			},
			{
				c: ["Geistes-, Gesellschafts- und Wirtschaftswissenschaften", 5805],
				sN: [
					{
						c: ["Bachelor", 2525],
					},
					{
						c: ["Master", 1764],
					},
					{
						c: ["Lehramt", 1256],
					},
					{
						c: ["Sonstige", 22],
					},
					{
						c: ["Promotion", 238],
					},
				],
			},
			{
				c: ["Medizin", 3055],
				sN: [
					{
						c: ["Bachelor", 2511],
					},
					{
						c: ["Master", 222],
					},
					{
						c: ["Lehramt", 0],
					},
					{
						c: ["Sonstige", 20],
					},
					{
						c: ["Promotion", 302],
					},
				],
			},
		],
	};


	let realTestData3Long: GemSunburstData = {
		entry: { label: "Verteilung der Studierenden und Fachanfänger auf die Fakultäten im WS 2020/21, Quelle: Zahlenspiegel 2020 der RWTH", value: 47173, },
		subEntries: [
			{
				entry: { label: "Fak. 1", value: 10005, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 2158, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 2", value: 1740, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 493, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 3", value: 4722, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 1158, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 4", value: 13002, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 2807, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 5", value: 4187, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 871, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 6", value: 4657, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 1011, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 7", value: 4007, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 1125, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 8", value: 1798, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 460, },
						subEntries: [],
					},
				]
			},
			{
				entry: { label: "Fak. 10", value: 3055, },
				subEntries: [
					{
						entry: { label: "Fachanfänger", value: 505, },
						subEntries: [],
					},
				]
			}
		]
	};


	let realTestData3Short: GemSDNshort = {
		c: ['Verteilung der Studierenden und Fachanfänger auf die Fakultäten im WS 2020/21, Quelle: Zahlenspiegel 2020 der RWTH',
			47173],
		sN: [
			{
				c: ["Fak. 1", 10005], sN: [
					{ c: ["Fachanfänger", 2158] },
				]
			},
			{
				c: ["Fak. 2", 1740], sN: [
					{ c: ["Fachanfänger", 493] },
				]
			},
			{
				c: ["Fak. 3", 4722], sN: [
					{ c: ["Fachanfänger", 1158] },
				]
			},
			{
				c: ["Fak. 4", 13002], sN: [
					{ c: ["Fachanfänger", 2807] },
				]
			},
			{
				c: ["Fak. 5", 4187], sN: [
					{ c: ["Fachanfänger", 871] },
				]
			},
			{
				c: ["Fak. 6", 4657], sN: [
					{ c: ["Fachanfänger", 1011] },
				]
			},
			{
				c: ["Fak. 7", 4007], sN: [
					{ c: ["Fachanfänger", 1125] },
				]
			},
			{
				c: ["Fak. 8", 1798], sN: [
					{ c: ["Fachanfänger", 460] },
				]
			},
			{
				c: ["Fak. 10", 3055], sN: [
					{ c: ["Fachanfänger", 505] },
				]
			},
		]
	};



	let fakeTestData1Short: GemSDNshort = {
		c: ['Verteilung der Studierenden und Fachanfänger auf die Fakultäten im WS 2020/21, Quelle: Zahlenspiegel 2020 der RWTH',
			47174],
		sN: [
			{
				c: ["Fak. 1", 10005], sN: [
					{ c: ["Fachanfänger", 2158] },
				]
			},
			{
				c: ["Fak. 2", 1740], sN: [
					{ c: ["Fachanfänger", 493] },
				]
			},
			{
				c: ["Fak. 3", 4722], sN: [
					{ c: ["Fachanfänger", 1158] },
				]
			},
			{
				c: ["Fak. 4", 13002], sN: [
					{ c: ["Fachanfänger", 2807] },
				]
			},
			{
				c: ["Fak. 5", 4187], sN: [
					{ c: ["Fachanfänger", 871] },
				]
			},
			{
				c: ["Fak. 6", 4657], sN: [
					{ c: ["Fachanfänger", 1011] },
				]
			},
			{
				c: ["Fak. 7", 4007], sN: [
					{ c: ["Fachanfänger", 1125] },
				]
			},
			{
				c: ["Fak. 8", 1798], sN: [
					{ c: ["Fachanfänger", 460] },
				]
			},
			{
				c: ["Fak. 10", 3055], sN: [
					{ c: ["Fachanfänger", 505] },
				]
			},
		]
	};


	let fakeTestData2Long: GemSunburstData = {
		entry: { label: "Fake", value: 10589, },
		subEntries: [
			{
				entry: { label: "International", value: 3162, },
				subEntries: [
					{
						entry: { label: "Männlich", value: 2224, },
					},
					{
						entry: { label: "Weiblich", value: 938, },
					},
				],
			},
			{
				entry: { label: "Deutsch", value: 7426, },
				subEntries: [
					{
						entry: { label: "Männlich", value: 4746, },
					},
					{
						entry: { label: "Weiblich", value: 2680, },
					},
				],
			}
		],
	};
});