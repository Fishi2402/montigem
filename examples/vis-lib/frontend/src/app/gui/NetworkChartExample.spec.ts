/* (c) https://github.com/MontiCore/monticore */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AddressBuilder } from '@src/domain/AddressBuilder';
import { DomainMock } from '@src/domain/DomainMock';
import { Person } from '@src/domain/Person';
import { PersonBuilder } from '@src/domain/PersonBuilder';
import { NetworkData, NetworkEdge, NetworkNode } from '@umlp/charts';
import { NetworkChartExample } from '@src/gui/NetworkChartExample';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('NetworkChartExample', () => {

  let fixture: ComponentFixture<NetworkChartExample>;
  let comp: NetworkChartExample;

  let person1: PersonBuilder;
  let address1: AddressBuilder;

  let person2: PersonBuilder;
  let address2: AddressBuilder;

  let person3: PersonBuilder;
  let address3: AddressBuilder;

  let person4: PersonBuilder;
  let address4: AddressBuilder;

  let person5: PersonBuilder;
  let address5: AddressBuilder;

  /**
   * Friends tree:
   *
   * p1 -> p2
   * p1 -> p3 -> p4
   * p5
   */
  beforeAll(() => {
    DomainMock.init();

    person1 = new PersonBuilder(11);
    address1 = new AddressBuilder(12);

    person2 = new PersonBuilder(21);
    address2 = new AddressBuilder(22);

    person3 = new PersonBuilder(31);
    address3 = new AddressBuilder(32);

    person4 = new PersonBuilder(41);
    address4 = new AddressBuilder(42);

    person5 = new PersonBuilder(51);
    address5 = new AddressBuilder(52);

    address5
      .house(3321)
      .street("-HHH- Way")
      .build();

    person5
      .name("undefined")
      .address(address5)
      .birthday(new Date())
      .build();

    address4
      .house(33)
      .street("null")
      .build();

    person4
      .name("C3-PO")
      .address(address4)
      .birthday(new Date(0))
      .build();

    address3
      .house(1)
      .street("New Friend alley")
      .build();

    person3
      .name("Anna")
      .address(address3)
      .birthday(new Date(1999, 1, 2))
      .friends(new Set([person4]))
      .build();

    address2
      .house(200)
      .street("Friend1 Str.")
      .build();

    person2
      .name("Bruno Mergez")
      .address(address2)
      .birthday(new Date(2022, 10, 3))
      .build();

    address1
      .house(12)
      .street("Test Str.")
      .build();

    person1
      .name("Alfred")
      .address(address1)
      .birthday(new Date(2023, 11, 22))
      .friends(new Set([person2, person3]))
      .build();
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        NetworkChartExample
      ],
      imports: [
        RouterTestingModule
      ],
      schemas: [
        NO_ERRORS_SCHEMA
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkChartExample);
    comp = fixture.componentInstance;
    fixture.detectChanges();
  });

  it(`should create the network example component`, () => {
    expect(comp).toBeTruthy();
  });

  it(`fillFriendsEdgesRecursively should return correct edges for simplest case 1`, () => {
    let data: NetworkEdge[];
    // @ts-expect-error
    data = comp.fillFriendsEdgesRecursively(person5, new Set());
    let expectation: NetworkEdge[] = [];

    expect(data).toEqual(expectation);
  });

  it(`fillFriendsEdgesRecursively should return correct edges for simplest case 2`, () => {
    let data: NetworkEdge[];
    // @ts-expect-error
    data = comp.fillFriendsEdgesRecursively(person1, new Set());
    let expectation: NetworkEdge[] = [
      { from: person1.getName(), to: person2.getName() },
      { from: person1.getName(), to: person3.getName() },
      { from: person3.getName(), to: person4.getName() }
    ];

    expect(data).toEqual(expectation);
  });

  it(`fillFriendsEdgesRecursively should return correct edges for complex case`, () => {
    let data: NetworkEdge[];
    // @ts-expect-error
    data = comp.fillFriendsEdgesRecursively(person1, new Set([person3, person4]));
    let expectation: NetworkEdge[] = [
      { from: person1.getName(), to: person2.getName() },
      { from: person1.getName(), to: person3.getName() }
    ];

    expect(data).toEqual(expectation);
  });

  it(`getGemNetworkGraphData should always return null if person is unset`, () => {
    expect(comp.p).toBeFalsy();

    let data: NetworkData | null;

    data = comp.getPersonGraphData();
    expect(data).toBeNull();

    data = comp.getPersonGraphData();
    expect(data).toBeNull();
  });

  it(`getGemNetworkGraphData should return graph for one person if person is set`, () => {
    comp.p = person1;

    let data: NetworkData | null;

    data = comp.getPersonGraphData();

    const nodes: NetworkNode[] = [
      { id: person1.getName(), label: person1.getName(), group: person1.constructor.name, mass: 50, color: "yellow" },
      { id: person2.getName(), label: person2.getName(), group: person2.constructor.name, mass: 50, color: "yellow" },
      { id: person3.getName(), label: person3.getName(), group: person3.constructor.name, mass: 50, color: "yellow" },
      { id: person4.getName(), label: person4.getName(), group: person4.constructor.name, mass: 50, color: "yellow" }
    ];
    const allPersons: Set<Person> = new Set();
    // @ts-expect-error
    const edges: NetworkEdge[] = comp.fillFriendsEdgesRecursively(comp.p, allPersons);
    const newNetworkData = { nodes: nodes, edges: edges };

    expect(data).toEqual(newNetworkData)
  });

  it(`getGemNetworkGraphData should return the same object if person is set and unchanged`, () => {
    comp.p = person1;

    let data1: NetworkData | null;
    data1 = comp.getPersonGraphData();

    let data2: NetworkData | null;
    data2 = comp.getPersonGraphData();

    expect(data1).toBe(data2);
  });

});
