import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemRangeInput } from '@monticore/montigem-input';

describe('GemRangeInput', () => {
  let component: GemRangeInput;
  let fixture: ComponentFixture<GemRangeInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemRangeInput ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GemRangeInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
