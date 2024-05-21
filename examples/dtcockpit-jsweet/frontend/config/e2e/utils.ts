export async function waitForAngular(page) {
  await page.evaluate(async () => {
    // @ts-expect-error
    if (window.getAllAngularTestabilities) {
      // @ts-expect-error
      await Promise.all(window.getAllAngularTestabilities().map(whenStable));

      // @ts-expect-error
      async function whenStable(testability) {
        return new Promise(res => testability.whenStable(res));
      }
    }
  });
}

export async function waitForHydration() {
  await new Promise(res => {
    //TODO: this is a workaround to wait for the hydration with server data to finish
    setTimeout(res, 5000);
  })
}
