import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemDualRangeInput } from './gem-dual-range-input.component';

describe('GemDualRangeInput', () => {
  let component: GemDualRangeInput;
  let fixture: ComponentFixture<GemDualRangeInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemDualRangeInput ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GemDualRangeInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
