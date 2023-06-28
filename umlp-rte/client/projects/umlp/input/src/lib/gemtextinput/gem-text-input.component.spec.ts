import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemTextInput } from './gem-text-input.component';

describe('GemTextInput', () => {
  let component: GemTextInput;
  let fixture: ComponentFixture<GemTextInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemTextInput ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemTextInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
