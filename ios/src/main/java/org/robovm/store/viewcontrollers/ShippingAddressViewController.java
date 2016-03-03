/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.robovm.store.viewcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewModel;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.model.Country;
import org.robovm.store.model.User;
import org.robovm.store.util.Countries;
import org.robovm.store.views.AutoCompleteTextEntry;
import org.robovm.store.views.BottomButtonView;
import org.robovm.store.views.CustomViewCell;
import org.robovm.store.views.StringSelectionCell;
import org.robovm.store.views.TextEntryView;


public class ShippingAddressViewController extends UITableViewController {
    private final User user;

    private Runnable shippingComplete;

    private final TextEntryView firstNameField;
    private TextEntryView lastNameField;
    private TextEntryView address1Field;
    private TextEntryView address2Field;
    private TextEntryView zipCodeField;
    private TextEntryView cityField;
    private AutoCompleteTextEntry stateField;
    private TextEntryView phoneNumberField;
    private AutoCompleteTextEntry countryField;

    private BottomButtonView bottomView;
    private final List<UITableViewCell> cells = new ArrayList<>();

    public ShippingAddressViewController(User user) {
        this.user = user;

        setTitle("Shipping");

        // This hides the back button text when you leave this View Controller
        getNavigationItem().setBackBarButtonItem(new UIBarButtonItem("", UIBarButtonItemStyle.Plain));
        getTableView().setSeparatorStyle(UITableViewCellSeparatorStyle.None);

        cells.add(new CustomViewCell(firstNameField = new TextEntryView("First Name", user.getFirstName())));

        cells.add(new CustomViewCell(lastNameField = new TextEntryView("Last Name", user.getLastName())));

        cells.add(new CustomViewCell(phoneNumberField = new TextEntryView("Phone Number", user.getPhone(),
                UIKeyboardType.PhonePad)));

        cells.add(new CustomViewCell(address1Field = new TextEntryView("Address", user.getAddress1(),
                UITextAutocapitalizationType.Words)));

        cells.add(new CustomViewCell(address2Field = new TextEntryView("Address", user.getAddress2(),
                UITextAutocapitalizationType.Words)));

        cells.add(new CustomViewCell(cityField = new TextEntryView("City", user.getCity(),
                UITextAutocapitalizationType.Words)));

        cells.add(new CustomViewCell(zipCodeField = new TextEntryView("Postal Code", user.getZipCode(),
                UIKeyboardType.NumbersAndPunctuation)));

        String countryName = user.getCountry();
        if (countryName != null) {
            Country c = Countries.getCountryForCode(countryName);
            if (c != null) {
                countryName = c.getName();
            } else {
                countryName = null;
            }
        }
        cells.add(new CustomViewCell(countryField = new AutoCompleteTextEntry(
                "Country", "Select your Country", countryName, this, (value) -> getStates())));

        cells.add(new CustomViewCell(stateField = new AutoCompleteTextEntry(
                "State", "Select your state", user.getState(), this)));

        getCountries();
        getStates();

        UITableView tableView = getTableView();
        tableView.setModel(new ShippingAddressPageModel(cells));
        tableView.setTableFooterView(new UIView(new CGRect(0, 0, 0, BottomButtonView.HEIGHT)));
        tableView.reloadData();

        getView().addSubview(bottomView = new BottomButtonView("Place Order", (button, event) -> placeOrder()));
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Shipping Address");
    }

    public void placeOrder() {
        user.setFirstName(firstNameField.getValue());
        user.setLastName(lastNameField.getValue());
        user.setAddress1(address1Field.getValue());
        user.setAddress2(address2Field.getValue());
        user.setCity(cityField.getValue());
        Country selectedCountry = Countries.getCountryForName(countryField.getValue());
        if (selectedCountry != null) {
            user.setCountry(selectedCountry.getCode());
        }
        user.setPhone(phoneNumberField.getValue());
        user.setState(stateField.getValue());
        user.setZipCode(zipCodeField.getValue());

        if (shippingComplete != null) {
            shippingComplete.run();
        }
    }

    private void getCountries() {
        Country[] countries = Countries.getCountries();
        List<String> items = new ArrayList<>();
        for (Country country : countries) {
            items.add(country.getName());
        }
        countryField.setItems(items);
    }

    private void getStates() {
        Country country = Countries.getCountryForName(countryField.getValue());
        if (country != null) {
            List<String> states = country.getStates();
            stateField.setItems(states);
        }
    }

    public void setShippingCompleteListener(Runnable listener) {
        this.shippingComplete = listener;
    }

    @Override
    public void viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews();

        CGRect bounds = getView().getBounds();
        bounds.setY(bounds.getY() + bounds.getHeight() - BottomButtonView.HEIGHT);
        bounds.setHeight(BottomButtonView.HEIGHT);
        bottomView.setFrame(bounds);
    }

    static class ShippingAddressPageModel extends UITableViewModel {
        private final List<UITableViewCell> cells;

        public ShippingAddressPageModel(List<UITableViewCell> cells) {
            this.cells = cells;
        }

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return cells.size();
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            return cells.get(indexPath.getRow());
        }

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            return cells.get(indexPath.getRow()).getFrame().getHeight();
        }

        @Override
        public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
            if (cells.get(indexPath.getRow()) instanceof StringSelectionCell) {
                ((StringSelectionCell) cells.get(indexPath.getRow())).tap();
            }
            tableView.deselectRow(indexPath, true);
        }
    }
}
