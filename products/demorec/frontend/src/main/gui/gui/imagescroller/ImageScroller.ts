/* Generated from Java with JSweet 3.2.1-UMLP-SNAPSHOT - http://www.jsweet.org */
import { StringHelper } from '@umlp/commonj2ts';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { ImageScrollerComponent, config } from 'gui/imagescroller/ImageScrollerComponent';
import { OnDestroy } from '@angular/core';
import { GemListUtil } from '@umlp/commonj2ts';
import ManyKeysMap from 'many-keys-map';import { GemIdentifiable } from '@umlp/commonj2ts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: [...config.styleUrls]
})
export class ImageScroller extends ImageScrollerComponent implements OnDestroy {

  public allImages : string[] = [];
  public currentPageIndex : number = 0;
  public PAGE_SIZE = 10;

  public ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  public override setImagesApiUrl(url :string) {
    if(url) {
      url = this.getCorrectUrl(url);
      this.fetchImages(url);
    }
    super.setImagesApiUrl(url);
  }

  /**
   * Fetches the image list from imagesApiUrl and stores it in allImages.
   * Falls back silently if imagesApiUrl is not set.
   * @param {string} imagesApiUrl
   * @private
   */
  fetchImages(imagesApiUrl: string) {
    console.warn(imagesApiUrl)
    if (imagesApiUrl) {
      fetch(imagesApiUrl).then((r: any) => r.json()).then((data: string[]) => {
        this.allImages = data;
        this.currentPageIndex = 0;
      });
    }
  }

  public getCurrentPageImages() : string[]{
    const start = this.currentPageIndex * this.PAGE_SIZE;
    const end = Math.min(start + this.PAGE_SIZE, this.allImages.length);
    if (start >= this.allImages.length) {
      return [];
    }
    return this.allImages.slice(start, end);
  }

  public isFirstPage() : boolean {
    return this.currentPageIndex == 0;
  }

  public isLastPage() : boolean {
    return this.currentPageIndex * this.PAGE_SIZE >= this.allImages.length - 1;
  }

  public getTotalPages() : number {
    if (!this.allImages || this.allImages.length == 0) {
      return 0;
    }
    return Math.floor((this.allImages.length + this.PAGE_SIZE - 1) / this.PAGE_SIZE);
  }

  public getDisplayPage() : number {
    return this.currentPageIndex + 1;
  }

  public onPrevPage() : void {
    if (this.currentPageIndex > 0) {
      this.currentPageIndex--;
    }
  }

  public onNextPage() : void  {
    if (!this.isLastPage()) {
      this.currentPageIndex++;
    }
  }

  public onSelectImage(imageName : string) : void {
    this.fireImageSelected(imageName);
  }

  public getFullImageUrl(imageName : string) : string {
    return this.getBaseScanUrl() + imageName + ".png";
  }

  public getBaseScanUrl() :string {
    return window.location.origin+"/scans/"
  }

  /**
   * Fires the imageSelected event with the given URL.
   * imageSelected is the Event<String> input and is expected to be a callback function.
   * @param {string} imageUrl
   */
  public fireImageSelected(imageUrl: string) {
    (target => (typeof target === 'function') ? target(imageUrl) : (<any>target).accept(imageUrl))(this.getImageSelected());
  }

  public getCorrectUrl(givenUrl: string) :string {
    let imgUrl = givenUrl;
    if(!imgUrl.startsWith("http")) {
      imgUrl = window.location.origin+imgUrl;
    }
    return imgUrl;
  }
}
