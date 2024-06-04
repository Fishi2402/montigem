import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Overlays } from './GemDialog';
import { SimpleChange } from '@angular/core';

/*
describe('Overlays', () => {
  let component: Overlays;
  let fixture: ComponentFixture<Overlays>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Overlays ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Overlays);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create overlay component', () => {
    expect(component).toBeTruthy();
  });

  it('should match expected value | enableBackdrop', () => {
    expect(component.enableBackdrop).toBeFalse();
  });

  it('should match expected value | positionStrategy', () => {
    expect(component.positionStrategy).toEqual("global");
  });

  it('should match expected value | originX', () => {
    expect(component.originX).toEqual("center");
  });

  it('should match expected value | originY', () => {
    expect(component.originY).toEqual("center");
  });

  it('should match expected value | overlayX', () => {
    expect(component.overlayX).toEqual("center");
  });

  it('should match expected value | overlayY', () => {
    expect(component.overlayY).toEqual("center");
  });

  it('should react on input changes | ngOnChanges()', () => {
    component.isOpen = false;

    component.ngOnChanges({name1: new SimpleChange(null, component.isOpen, true)});

    expect(component.isOpen).toBeFalse();

    component.isOpen = true;

    component.ngOnChanges({name1: new SimpleChange(false, component.isOpen, false)});

    expect(component.isOpen).toBeTrue();
  });

  it('should open an overlay correctly (default values) | openOverlay()', () => {
    expect(component.config).toBeUndefined();
    expect(component.templatePortal).toBeUndefined();
    expect(component.overlayRef).toBeUndefined();
    
    component.openOverlay();

    expect(component.positionStrategy).toEqual("global");
    expect(component.config).toBeDefined();
    expect(component.config.positionStrategy).toBeDefined();
    expect(component.config.hasBackdrop).toBeFalse();
    expect(component.templatePortal).toBeDefined();
    expect(component.overlayRef).toBeDefined();
  });

  it('should open an overlay correctly (enableBackdrop) | openOverlay()', () => {
    expect(component.config).toBeUndefined();
    expect(component.templatePortal).toBeUndefined();
    expect(component.overlayRef).toBeUndefined();
    
    component.enableBackdrop = true;

    component.openOverlay();

    expect(component.positionStrategy).toEqual("global");
    expect(component.config).toBeDefined();
    expect(component.config.positionStrategy).toBeDefined();
    expect(component.config.hasBackdrop).toBeTrue();
    expect(component.templatePortal).toBeDefined();
    expect(component.overlayRef).toBeDefined();
  });

  it('should open an overlay correctly (positionStrategy fallback) | openOverlay()', () => {
    expect(component.config).toBeUndefined();
    expect(component.templatePortal).toBeUndefined();
    expect(component.overlayRef).toBeUndefined();
    
    component.positionStrategy = "flexible";

    component.openOverlay();

    expect(component.positionStrategy).toEqual("global");
    expect(component.config).toBeDefined();
    expect(component.config.positionStrategy).toBeDefined();
    expect(component.config.hasBackdrop).toBeFalse();
    expect(component.templatePortal).toBeDefined();
    expect(component.overlayRef).toBeDefined();
  });
});
/**/