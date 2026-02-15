package com.client.app.stepDefs;

import org.testng.Assert;
import com.client.app.pages.UserProfilePage;
import appium.webdriver.logging.TestLogger;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

public class UserProfileStepDef {

	private final UserProfilePage userProfilePage = new UserProfilePage();

	//Verify the building the user has currently selected is shown at the top of the overlay
	@Given("User is on user profile page")
	public void user_is_on_profile_page() {
		TestLogger.step("User is on user profile page");
		userProfilePage.selectUserProfile();
	}

	@When("User tap on default location option of personal space")
	public void user_tap_on_default_location_option() {
		TestLogger.step("User tap on default location option of personal space");
		userProfilePage.selectDefaultLocation();
	}

	@Then("Building name is displayed at the top of floor overlay")
	public void user_tap_on_floor_option() {
		TestLogger.step("Building name is displayed at the top of floor overlay");
		userProfilePage.selectFloorOption();
	}

	//Verify the Save button on Default setting screen
	@When("User tap on country option of personal space and choose the country")
	public void user_tap_on_country_option() {
		TestLogger.step("User tap on country option of personal space and choose the country");
		userProfilePage.selectTopCountry();
	}

	@And("User press the save button")
	public void user_press_on_save_button() {
		TestLogger.step("User press the save button");
		userProfilePage.tapOnSave();
	}

	@Then("Changed value should get successfully updated")
	public void validate_saved_value() {
		TestLogger.step("Changed value should get successfully updated");
		userProfilePage.validateSavedValue();
	}

	//Verify the pop up shown on log out button on Default setting screen
	@When("User tap on the logout button")
	public void user_tap_on_logout_button() {
		TestLogger.step("User tap on the logout button");
		userProfilePage.selectLogoutButton();
	}

	@Then("Pop up is shown on tapping logout button {string}")
	public void popup_on_tapping_logout_button(String logoutPopupMsg) {
		TestLogger.step("Pop up is shown on tapping logout button");
		userProfilePage.verifyLogoutPopupShown(logoutPopupMsg);
	}

	// Verify if the user chooses to close the overlay without making a selection, 
	// the previously selected country is not changed
	@When("User tap on country option of personal space and choose the default country")
	public void user_tap_on_default_country_option() {
		TestLogger.step("User tap on country option of personal space and choose the default country");
		userProfilePage.selectDefaultCountry();
	}

	@And("User press the close button")
	public void user_tap_on_close_button() {
		TestLogger.step("User press the close button");
		userProfilePage.selectCloseButton();
	}

	@Then("Previously selected country should not be changed if no selection is made")
	public void verify_country_name_not_changed() {
		TestLogger.step("Previously selected country should not be changed if no selection is made");
		userProfilePage.verifyCountryNameNotChanged();
	}

	//  Verify when the user presses the 'Floor' button, the Floor selection overlay appears
	@When("User tap on floor option")
	public void user_tap_on_floor() {
		TestLogger.step("User tap on floor option");
		userProfilePage.tapOnFloorOption();
	}

	@Then("The floor selection overlay appears")
	public void verify_floor_selection_overlay() {
		TestLogger.step("The floor selection overlay appears");
		userProfilePage.verifyFloorSelectionOverlay();
	}

	//  Verify when the user presses the 'Group' button, the Group selection overlay appears
	@When("User tap on group option")
	public void user_tap_on_group() {
		TestLogger.step("User tap on group option");
		userProfilePage.tapOnGroupOption();
	}

	@Then("The group selection overlay appears")
	public void verify_group_selection_overlay() {
		TestLogger.step("The group selection overlay appears");
		userProfilePage.verifyGroupSelectionOverlay();
	}

	// Verify that selected a WSType exists on the user default floor then 
	// the default floor will be shown selected
	@Given("User is navigated to New Booking Page")
	public void user_Navigate_To_NewBooking() {
		TestLogger.step("User is navigated to New Booking Page");
		userProfilePage.navigateToNewBooking();
	}

	@When("User Tap on Book a Meeting space")
	public void tap_Book_MeetingSpace() {
		TestLogger.step("User Tap on Book a Meeting space");
		userProfilePage.expandBookMeetingSpaceSection();
	}

	@And("Press Continue on New Booking")
	public void press_Contine() {
		TestLogger.step("Press Continue on New Booking");
		userProfilePage.pressContinue();
	}

	@Then("Verify Floor value as {string}")
	public void verify_Floor_Based_On_WSType(String floor) {
		TestLogger.step("Verify Floor value as: " + floor);
		Assert.assertTrue(userProfilePage.verifyFloorOnWSType(floor), 
			"Floor value does not match expected: " + floor);
	}

	//Verify the current selection of floor is highlighted
	@Then("The current selection of floor is highlighted")
	public void verify_floor_highlighted() {
		TestLogger.step("The current selection of floor is highlighted");
		userProfilePage.verifyFloorHighlighted();
	}

	//Verify the current selection of Group is highlighted
	@Then("The current selection of Group is highlighted")
	public void verify_Group_highlighted() {
		TestLogger.step("The current selection of Group is highlighted");
		userProfilePage.verifyGroupHighlighted();
	}

	//Verify if the user chooses to close the overlay without making a selection,
	// the previously selected Group is not changed
	@When("User tap on group option of personal space and choose the default country")
	public void user_tap_on_default_group_option() {
		TestLogger.step("User tap on group option of personal space and choose the default country");
		userProfilePage.selectDefaultGroup();
	}

	@Then("Previously selected group should not be changed if no selection is made")
	public void verify_group_name_not_changed() {
		TestLogger.step("Previously selected group should not be changed if no selection is made");
		userProfilePage.verifyGroupNameNotChanged();
	}
}