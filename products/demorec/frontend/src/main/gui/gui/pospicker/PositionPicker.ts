import { PositionPickerComponent, config } from 'gui/pospicker/PositionPickerComponent';
import { Component, Output, EventEmitter, inject, NgZone, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface PickResponse {
  x: number;
  y: number;
  z: number;
}

interface LocateResponse {
  col: number;
  row: number;
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

  @ViewChild('pickerImg') private pickerImgEl!: ElementRef<HTMLImageElement>;

  constructor() {
    super();
  }

  @Output()
  public positionSelected: EventEmitter<string> = new EventEmitter<string>();

  public dotX: number | null = null;
  public dotY: number | null = null;
  public dotLocated: boolean = false;
  public locateError: string | null = null;

  public override setImageUrl(url: string) {
    super.setImageUrl(url);
    if (this.getX() != null && this.getY() != null && this.getZ() != null) {
      this.locateDot(this.getCorrectUrl(), this.getX(), this.getY(), this.getZ());
    }
  }

  private locateDot(scanUrl: string, x: number, y: number, z: number): void {
    // Derive locate URL: /scans/<dataset>/<frame>.png -> /locate/<dataset>/<frame>
    const locateUrl = scanUrl
      .replace('/scans/', '/locate/')
      .replace(/\.png$/, '');
    console.error("Locate Dot")
    this.http.get<LocateResponse>(`${locateUrl}?x=${x}&y=${y}&z=${z}`).subscribe({
      next: (pixel) => {
        const imgEl = this.pickerImgEl?.nativeElement;
        if (imgEl && imgEl.naturalWidth > 0 && imgEl.naturalHeight > 0) {
          this.dotX = pixel.col * imgEl.clientWidth / imgEl.naturalWidth;
          this.dotY = pixel.row * imgEl.clientHeight / imgEl.naturalHeight;
        } else {
          this.dotX = pixel.col;
          this.dotY = pixel.row;
        }
        this.dotLocated = true;
        this.locateError = null;
        console.log("Received scan pixel coordinates: ", pixel);
        console.log("Mapped to display coordinates: ", this.dotX, this.dotY);
      },
      error: (err) => {
        const msg = err?.error?.error;
        this.dotX = null;
        this.dotY = null;
        this.locateError = msg ?? 'Could not locate coordinates on this scan.';
        console.error('PositionPicker: locate request failed', err);
      }
    });
  }

  public onImageClick(event: MouseEvent, imgEl: HTMLImageElement): void {
    const rect = imgEl.getBoundingClientRect();
    const displayX = event.clientX - rect.left;
    const displayY = event.clientY - rect.top;

    const pixelX = Math.round(displayX * imgEl.naturalWidth / imgEl.clientWidth);
    const pixelY = Math.round(displayY * imgEl.naturalHeight / imgEl.clientHeight);

    // Derive pick URL from scan URL: /scans/<dataset>/<frame>.png -> /pick/<dataset>/<frame>
    let imgUrl = this.getCorrectUrl();
    const pickUrl = imgUrl
      .replace('/scans/', '/pick/')
      .replace(/\.png$/, '');

    this.http.get<PickResponse>(`${pickUrl}?x=${pixelX}&y=${pixelY}`).subscribe({
      next: (coords) => {
        console.warn("Received a position")
        this.dotX = displayX;
        this.dotY = displayY;
        this.dotLocated = false;
        console.warn("Picked the pixel:", displayX, displayY)
        this.xChange.emit(coords.x)
        this.yChange.emit(coords.y)
        this.zChange.emit(coords.z)
      },
      error: (err) => {
        console.error('PositionPicker: pick request failed', err);
      }
    });
  }

  public getCorrectUrl() :string {
    let imgUrl = this.imageUrl;
    if(!imgUrl.startsWith("http")) {
      imgUrl = window.location.origin+this.imageUrl;
    }
    return imgUrl;
  }
}
