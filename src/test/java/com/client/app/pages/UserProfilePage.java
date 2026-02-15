package com.client.app.pages;

import appium.webdriver.extensions.DriverManager;
import appium.webdriver.extensions.Utility;
import appium.webdriver.logging.TestLogger;
import org.openqa.selenium.By;

public class UserProfilePage extends DriverManager {

	// Locators
	private final By selectUserProfile_xpath = By.xpath("//android.widget.LinearLayout[@content-desc=\"Profile\"]/android.widget.ImageView");
	private final By defaultLocationField_id = By.id("com.condecosoftware.condeco:id/locationDefault");
	private final By defaultLocation_xpath = By.xpath("//android.widget.TextView[@selected='true']");
	private final By locationCloseButton_id = By.id("com.condecosoftware.condeco:id/closeBtn");
	private final By defaultFloor_id = By.id("com.condecosoftware.condeco:id/floorDefault");
	private final By overlayLocationName_id = By.id("com.condecosoftware.condeco:id/subHeader");
	private final By floorCloseButton_id = By.id("com.condecosoftware.condeco:id/closeBtn");
	//private final By backButton_classname = By.className("android.widget.ImageButton");
	private final By defaultCountryField_id = By.id("com.condecosoftware.condeco:id/textCountry");
	private final By topCountryName_id = By.id("android:id/text1");
	private final By saveButton_id = By.id("com.condecosoftware.condeco:id/save");
	private final By logoutButton_id = By.id("com.condecosoftware.condeco:id/logout");
	private final By logoutPopupMsg_id = By.id("com.condecosoftware.condeco:id/core_dlg_message");
	private final By logoutCancelButton_id = By.id("com.condecosoftware.condeco:id/core_dlg_negative_button");
	private final By closeButton_id = By.id("com.condecosoftware.condeco:id/closeBtn");
	private final By defaultCountry_xpath = By.xpath("//android.widget.TextView[@selected='true']");
	private final By defaultFloorSelected_xpath = By.xpath("//android.widget.TextView[@selected='true']");
	private final By defaultGroup_id = By.id("com.condecosoftware.condeco:id/groupDefault");
	private final By defaultGroupSelected_xpath = By.xpath("//android.widget.TextView[@selected='true']");
	private final By newBookingTab_xpath = By.xpath("//android.widget.LinearLayout[@index='2']");
	private final By bookMeetingSpace_id = By.id("com.condecosoftware.condeco:id/viewBookMeetingSpace");
	private final By continueBtnOnNewBooking_id = By.id("com.condecosoftware.condeco:id/button");
	private final By floorField_id = By.id("com.condecosoftware.condeco:id/textFloor");

	// Variables to store state
	private String defaultCountry;
	private String topCountryValue;
	private String defaultLocation;
	private String defaultGroup;

	public void selectUserProfile() {
		try {
			TestLogger.step("Selecting user profile...");
			
			// Handle any notifications/popups before interacting with the profile
			TestLogger.info("Handling any pending notifications before profile selection...");
			Utility.handleAppNotifications();
			
			Utility.waitForElementUntilPresent(selectUserProfile_xpath, 10);
			Utility.clickElement(selectUserProfile_xpath, 10);
			TestLogger.pass("User profile selected");
			
			// Handle notifications again after navigating to profile page
			Thread.sleep(1000);
			Utility.handleAppNotifications();
		} catch (Exception e) {
			TestLogger.error("Error selecting user profile", e);
			throw new RuntimeException("Failed to select user profile", e);
		}
	}

	public String selectDefaultLocation() {
		try {
			TestLogger.step("Selecting default location...");
			
			// Handle any notifications/popups before interacting
			Utility.handleAppNotifications();
			
			Utility.waitForElementUntilPresent(defaultLocationField_id, 10);
			Utility.clickElement(defaultLocationField_id, 10);
			Thread.sleep(1000);
			defaultLocation = Utility.getTextFromid(defaultLocation_xpath, 10);
			TestLogger.pass("Default location: " + defaultLocation);
			Utility.clickElement(locationCloseButton_id, 10);
			return defaultLocation;
		} catch (Exception e) {
			TestLogger.error("Error selecting default location", e);
			throw new RuntimeException("Failed to select default location", e);
		}
	}

	public void selectFloorOption() {
		try {
			TestLogger.step("Selecting floor option...");
			Utility.clickElement(defaultFloor_id, 10);
			Thread.sleep(1000);
			String actualLocation = Utility.getTextFromid(overlayLocationName_id, 10);
			TestLogger.debug("Overlay location name: " + actualLocation);
			Utility.clickElement(floorCloseButton_id, 10);
			
			TestLogger.debug("Verifying location match...");
			TestLogger.debug("Default Location: " + defaultLocation);
			TestLogger.debug("Actual Location: " + actualLocation);
			if (!defaultLocation.equals(actualLocation)) {
				throw new AssertionError("Location names do not match: expected [" + defaultLocation + "] but was [" + actualLocation + "]");
			}
			TestLogger.pass("Location names matched: " + defaultLocation + " == " + actualLocation);
			TestLogger.pass("Building name verified at the top of floor overlay");
		} catch (Exception e) {
			TestLogger.error("Error selecting floor option", e);
			throw new RuntimeException("Failed to select floor option", e);
		}
	}

	public String selectTopCountry() {
		try {
			TestLogger.step("Selecting top country...");
			Utility.waitForElementUntilPresent(defaultCountryField_id, 10);
			Utility.clickElement(defaultCountryField_id, 10);
			Thread.sleep(1000);
			topCountryValue = Utility.getTextFromid(topCountryName_id, 10);
			TestLogger.debug("Top country value: " + topCountryValue);
			Utility.clickElement(topCountryName_id, 10);
			return topCountryValue;
		} catch (Exception e) {
			TestLogger.error("Error selecting top country", e);
			throw new RuntimeException("Failed to select top country", e);
		}
	}

	public void tapOnSave() {
		try {
			TestLogger.step("Tapping on Save button...");
			Utility.clickElement(saveButton_id, 10);
			Thread.sleep(2000);
			TestLogger.pass("Save button clicked");
		} catch (Exception e) {
			TestLogger.error("Error tapping save button", e);
			throw new RuntimeException("Failed to tap save button", e);
		}
	}

	public void validateSavedValue() {
		try {
			TestLogger.step("Validating saved value...");
			Utility.waitForElementUntilPresent(selectUserProfile_xpath, 10);
			Utility.clickElement(selectUserProfile_xpath, 10);
			Thread.sleep(1000);
			String updatedValue = Utility.getTextFromid(defaultCountryField_id, 10);
			TestLogger.debug("Updated value: " + updatedValue);
			if (!topCountryValue.equals(updatedValue)) {
				throw new AssertionError("Saved value does not match expected value: expected [" + topCountryValue + "] but was [" + updatedValue + "]");
			}
			TestLogger.pass("Changed value successfully updated");
		} catch (Exception e) {
			TestLogger.error("Error validating saved value", e);
			throw new RuntimeException("Failed to validate saved value", e);
		}
	}

	public void selectLogoutButton() {
		try {
			TestLogger.step("Selecting logout button...");
			Utility.waitForElementUntilPresent(logoutButton_id, 10);
			Utility.clickElement(logoutButton_id, 10);
			Thread.sleep(1000);
			TestLogger.pass("Logout button clicked");
		} catch (Exception e) {
			TestLogger.error("Error selecting logout button", e);
			throw new RuntimeException("Failed to select logout button", e);
		}
	}

	public void verifyLogoutPopupShown(String expectedLogoutPopupMsg) {
		try {
			TestLogger.step("Verifying logout popup message...");
			String actualMsg = Utility.getTextFromid(logoutPopupMsg_id, 10);
			TestLogger.debug("Popup message: " + actualMsg);
			Utility.clickElement(logoutCancelButton_id, 10);
			Thread.sleep(1000);
			if (!expectedLogoutPopupMsg.equals(actualMsg)) {
				throw new AssertionError("Logout popup message does not match: expected [" + expectedLogoutPopupMsg + "] but was [" + actualMsg + "]");
			}
			TestLogger.pass("Logout popup verified successfully");
		} catch (Exception e) {
			TestLogger.error("Error verifying logout popup", e);
			throw new RuntimeException("Failed to verify logout popup", e);
		}
	}

	public String selectDefaultCountry() {
		try {
			TestLogger.step("Selecting default country...");
			Utility.waitForElementUntilPresent(defaultCountryField_id, 10);
			Utility.clickElement(defaultCountryField_id, 10);
			Thread.sleep(1000);
			defaultCountry = Utility.getTextFromid(defaultCountry_xpath, 10);
			TestLogger.pass("Default country: " + defaultCountry);
			return defaultCountry;
		} catch (Exception e) {
			TestLogger.error("Error selecting default country", e);
			throw new RuntimeException("Failed to select default country", e);
		}
	}

	public void selectCloseButton() {
		try {
			TestLogger.step("Clicking close button...");
			Utility.clickElement(closeButton_id, 10);
			Thread.sleep(1000);
			TestLogger.pass("Close button clicked");
		} catch (Exception e) {
			TestLogger.error("Error clicking close button", e);
			throw new RuntimeException("Failed to click close button", e);
		}
	}

	public void verifyCountryNameNotChanged() {
		try {
			TestLogger.step("Verifying country name not changed...");
			Utility.clickElement(defaultCountryField_id, 10);
			Thread.sleep(1000);
			String latestCountry = Utility.getTextFromid(defaultCountry_xpath, 10);
			TestLogger.debug("Latest country: " + latestCountry);
			Utility.clickElement(closeButton_id, 10);
			if (!defaultCountry.equals(latestCountry)) {
				throw new AssertionError("Country selection changed unexpectedly: expected [" + defaultCountry + "] but was [" + latestCountry + "]");
			}
			TestLogger.pass("Previously selected country not changed");
		} catch (Exception e) {
			TestLogger.error("Error verifying country name", e);
			throw new RuntimeException("Failed to verify country name", e);
		}
	}

	public void tapOnFloorOption() {
		try {
			TestLogger.step("Tapping on floor option...");
			Utility.waitForElementUntilPresent(defaultFloor_id, 10);
			Utility.clickElement(defaultFloor_id, 10);
			Thread.sleep(1000);
			TestLogger.pass("Floor option tapped");
		} catch (Exception e) {
			TestLogger.error("Error tapping floor option", e);
			throw new RuntimeException("Failed to tap floor option", e);
		}
	}

	public void verifyFloorSelectionOverlay() {
		try {
			TestLogger.step("Verifying floor selection overlay...");
			boolean floorSelection = Utility.isElementPresent(defaultFloorSelected_xpath, 10);
			if (floorSelection) {
				TestLogger.pass("Floor selection overlay appears");
				Utility.clickElement(closeButton_id, 10);
			} else {
				throw new AssertionError("Floor selection overlay not found");
			}
		} catch (Exception e) {
			TestLogger.error("Error verifying floor selection overlay", e);
			throw new RuntimeException("Failed to verify floor selection overlay", e);
		}
	}

	public void tapOnGroupOption() {
		try {
			TestLogger.step("Tapping on group option...");
			Utility.waitForElementUntilPresent(defaultGroup_id, 10);
			Utility.clickElement(defaultGroup_id, 10);
			Thread.sleep(1000);
			TestLogger.pass("Group option tapped");
		} catch (Exception e) {
			TestLogger.error("Error tapping group option", e);
			throw new RuntimeException("Failed to tap group option", e);
		}
	}

	public void verifyGroupSelectionOverlay() {
		try {
			TestLogger.step("Verifying group selection overlay...");
			boolean groupSelection = Utility.isElementPresent(defaultGroupSelected_xpath, 10);
			if (groupSelection) {
				TestLogger.pass("Group selection overlay appears");
				Utility.clickElement(closeButton_id, 10);
			} else {
				throw new AssertionError("Group selection overlay not found");
			}
		} catch (Exception e) {
			TestLogger.error("Error verifying group selection overlay", e);
			throw new RuntimeException("Failed to verify group selection overlay", e);
		}
	}

	public void navigateToNewBooking() {
		try {
			TestLogger.step("Navigating to new booking...");
			Utility.waitForElementUntilPresent(newBookingTab_xpath, 10);
			Utility.clickElement(newBookingTab_xpath, 10);
			Thread.sleep(2000);
			TestLogger.pass("Navigated to New Booking Page");
		} catch (Exception e) {
			TestLogger.error("Error navigating to new booking", e);
			throw new RuntimeException("Failed to navigate to new booking", e);
		}
	}

	public void expandBookMeetingSpaceSection() {
		try {
			TestLogger.step("Expanding book meeting space section...");
			Utility.waitForElementUntilPresent(bookMeetingSpace_id, 10);
			Utility.clickElement(bookMeetingSpace_id, 10);
			Thread.sleep(1000);
			TestLogger.pass("Book meeting space section expanded");
		} catch (Exception e) {
			TestLogger.error("Error expanding book meeting space", e);
			throw new RuntimeException("Failed to expand book meeting space section", e);
		}
	}

	public void pressContinue() {
		try {
			TestLogger.step("Pressing continue button...");
			Utility.clickElement(continueBtnOnNewBooking_id, 10);
			Thread.sleep(2000);
			TestLogger.pass("Continue button pressed");
		} catch (Exception e) {
			TestLogger.error("Error pressing continue", e);
			throw new RuntimeException("Failed to press continue button", e);
		}
	}

	public boolean verifyFloorOnWSType(String expectedFloor) {
		try {
			TestLogger.step("Verifying floor on WSType...");
			TestLogger.debug("Expected floor: " + expectedFloor);
			Utility.waitForElementUntilPresent(floorField_id, 10);
			String actualFloorValue = Utility.getTextFromid(floorField_id, 10);
			TestLogger.debug("Actual floor: " + actualFloorValue);
			
			if (actualFloorValue.equals(expectedFloor)) {
				TestLogger.pass("Floor value matches expected value");
				return true;
			} else {
				TestLogger.fail("Floor value does not match");
				return false;
			}
		} catch (Exception e) {
			TestLogger.error("Error verifying floor on WSType", e);
			throw new RuntimeException("Failed to verify floor on WSType", e);
		}
	}

	public void verifyFloorHighlighted() {
		try {
			TestLogger.step("Verifying floor is highlighted...");
			String selectedFloor = Utility.getTextFromid(defaultFloorSelected_xpath, 10);
			TestLogger.debug("Selected Floor is: " + selectedFloor);
			
			boolean floorFound = false;
			for (int i = 0; i <= 20; i++) {
				try {
					By floorListItem = By.xpath("//android.widget.LinearLayout[@clickable='true'][@index=" + i + "]//child::*");
					if (Utility.isElementPresent(floorListItem, 2)) {
						String floorList = Utility.getTextFromid(floorListItem, 2);
						TestLogger.debug("Floor Name is: " + floorList);
						
						if (selectedFloor.equals(floorList)) {
							TestLogger.pass("Selected floor found in the list and is highlighted");
							floorFound = true;
							break;
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
			
			Utility.clickElement(closeButton_id, 10);
			
			if (!floorFound) {
				throw new AssertionError("Selected floor not found in the list");
			}
		} catch (Exception e) {
			TestLogger.error("Error verifying floor highlighted", e);
			throw new RuntimeException("Failed to verify floor highlighted", e);
		}
	}

	public void verifyGroupHighlighted() {
		try {
			TestLogger.step("Verifying group is highlighted...");
			String selectedGroup = Utility.getTextFromid(defaultGroupSelected_xpath, 10);
			TestLogger.debug("Selected Group is: " + selectedGroup);
			
			boolean groupFound = false;
			for (int i = 0; i <= 20; i++) {
				try {
					By groupListItem = By.xpath("//android.widget.LinearLayout[@clickable='true'][@index=" + i + "]//child::*");
					if (Utility.isElementPresent(groupListItem, 2)) {
						String groupList = Utility.getTextFromid(groupListItem, 2);
						TestLogger.debug("Group Name is: " + groupList);
						
						if (selectedGroup.equals(groupList)) {
							TestLogger.pass("Selected group found in the list and is highlighted");
							groupFound = true;
							break;
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
			
			Utility.clickElement(closeButton_id, 10);
			
			if (!groupFound) {
				throw new AssertionError("Selected group not found in the list");
			}
		} catch (Exception e) {
			TestLogger.error("Error verifying group highlighted", e);
			throw new RuntimeException("Failed to verify group highlighted", e);
		}
	}

	public String selectDefaultGroup() {
		try {
			TestLogger.step("Selecting default group...");
			Utility.waitForElementUntilPresent(defaultGroup_id, 10);
			Utility.clickElement(defaultGroup_id, 10);
			Thread.sleep(1000);
			defaultGroup = Utility.getTextFromid(defaultGroupSelected_xpath, 10);
			TestLogger.pass("Default group: " + defaultGroup);
			return defaultGroup;
		} catch (Exception e) {
			TestLogger.error("Error selecting default group", e);
			throw new RuntimeException("Failed to select default group", e);
		}
	}

	public void verifyGroupNameNotChanged() {
		try {
			TestLogger.step("Verifying group name not changed...");
			Utility.clickElement(defaultGroup_id, 10);
			Thread.sleep(1000);
			String latestGroup = Utility.getTextFromid(defaultGroupSelected_xpath, 10);
			TestLogger.debug("Latest group: " + latestGroup);
			Utility.clickElement(closeButton_id, 10);
			if (!defaultGroup.equals(latestGroup)) {
				throw new AssertionError("Group selection changed unexpectedly: expected [" + defaultGroup + "] but was [" + latestGroup + "]");
			}
			TestLogger.pass("Previously selected group not changed");
		} catch (Exception e) {
			TestLogger.error("Error verifying group name", e);
			throw new RuntimeException("Failed to verify group name", e);
		}
	}
}
