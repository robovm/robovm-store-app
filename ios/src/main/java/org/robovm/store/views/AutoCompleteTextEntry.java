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
 * 
 */
package org.robovm.store.views;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.store.viewcontrollers.StringTableViewController;

public class AutoCompleteTextEntry extends TextEntryView {
    private final StringTableViewController controller;
    private String title;
    private List<String> items = new ArrayList<>();
    private UIViewController presenterView;

    public AutoCompleteTextEntry() {
        controller = new StringTableViewController();
        controller.setSelectionListener((item) -> {
            setValue(item);
        });
    }

    @Override
    public void didBeginEditing(UITextField textField) {
        super.didBeginEditing(textField);
        search();
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    private void search() {
        if (items.size() == 0) {
            return;
        }
        textField.resignFirstResponder();
        controller.setTitle(title);
        controller.setItems(items);

        if (presenterView != null && presenterView.getNavigationController().getTopViewController() != controller) {
            presenterView.getNavigationController().pushViewController(controller, true);
        }
    }

    public UIViewController getPresenterView() {
        return presenterView;
    }

    public void setPresenterView(UIViewController presenterView) {
        this.presenterView = presenterView;
    }
}
