import { PositionPickerComponent, config } from 'gui/pospicker/PositionPickerComponent';
import { Component, Output, EventEmitter, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface PickResponse {
  x: number;
  y: number;
  z: number;
}

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: './PositionPicker.html',
  styles: config.styles,
  styleUrls: ['./PositionPicker.scss']
})
export class PositionPicker extends PositionPickerComponent {

  private http = inject(HttpClient);

  constructor() {
    super();
  }

  @Output()
  public positionSelected: EventEmitter<string> = new EventEmitter<string>();

  public onImageClick(event: MouseEvent, imgEl: HTMLImageElement): void {
    const rect = imgEl.getBoundingClientRect();
    const displayX = event.clientX - rect.left;
    const displayY = event.clientY - rect.top;

    const pixelX = Math.round(displayX * imgEl.naturalWidth / imgEl.clientWidth);
    const pixelY = Math.round(displayY * imgEl.naturalHeight / imgEl.clientHeight);

    // Derive pick URL from image URL: /images/<dataset>/<frame>.png -> /pick/<dataset>/<frame>
    const pickUrl = this.imageUrl
      .replace('/images/', '/pick/')
      .replace(/\.png$/, '');

    this.http.get<PickResponse>(`${pickUrl}?x=${pixelX}&y=${pixelY}`).subscribe({
      next: (coords) => {
        console.warn("Received a positionhow ")
        this.positionSelected.emit(`${coords.x},${coords.y},${coords.z}`);
      },
      error: (err) => {
        console.error('PositionPicker: pick request failed', err);
      }
    });
  }
}
