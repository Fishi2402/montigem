import {expect, type Locator, test} from '@playwright/test';
import {waitForAngular, waitForHydration} from "./utils";

// TODO: Adopt to dtcockpit

test.beforeEach(async ({page}) => {
  page.on('console', msg => console.log("[BROWSER]", msg.text()))

  await page.goto('/gui/login/LoginPage');
  await waitForAngular(page)
  await page.locator(".userInput").first().locator("input").first().fill("Alice");
  await page.locator(".userPassword").first().locator("input").first().fill("se");
  await page.locator(".signInButton").first().locator("button").first().click();
  await page.waitForURL("**/gui/exhibits/alldissertations")

  await page.goto('/gui/NotificationSettingsPage');
  await waitForAngular(page)
  await waitForHydration();
})

test('NotificationSettingsPage should contain row title: Name', async ({page}) => {
  const nameList: Locator[] = await page.locator(".valuesRow > .name").all();
  expect(nameList.length).toBeGreaterThan(0);
  for (const name of nameList) {
    await expect(name).toBeVisible();
  }
});

test('NotificationSettingsPage should contain Name and email of Prof', async ({ page }) => {
  await expect(page.getByText(/bob@email.de/)).toBeVisible();
  await expect(page.getByText(/Bob/)).toBeVisible();
});

test('NotificationSettingsPage should have working active checkbox', async ({page}) => {
  const usersList: Locator[] = await page.locator(".valuesRow").all();
  expect(usersList.length).toBeGreaterThan(0);
  for (const user of usersList) {
    const activeCheckbox = user.locator(".active").first().locator("input").first();
    await expect(activeCheckbox).toBeVisible();
    await activeCheckbox.check();
    expect(await activeCheckbox.isChecked()).toBeTruthy();
    await activeCheckbox.uncheck();
    expect(await activeCheckbox.isChecked()).toBeFalsy();
    const otherCheckboxes = await user.locator(".habil, .biology, .math, .computer_science, .physics, .chemistry").all();
    expect(otherCheckboxes.length).toBe(6);
    for (const checkboxWrapper of otherCheckboxes) {
      const checkbox = await checkboxWrapper.locator("input").first();
      await expect(checkbox).toBeDisabled();
    }
  }
});

