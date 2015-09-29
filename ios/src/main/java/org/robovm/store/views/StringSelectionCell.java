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

import java.util.List;

import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Action;
import org.robovm.store.util.Colors;

public class StringSelectionCell extends UITableViewCell {
    private static final String REUSE_ID = "SelectionCell";

    private Action<String> selectionListener;

    private final UIView pickerView;
    private final StringUIPicker picker;

    public StringSelectionCell(UIView pickerView) {
        super(UITableViewCellStyle.Value1, REUSE_ID);
        this.pickerView = pickerView;

        setSelectionStyle(UITableViewCellSelectionStyle.None);
        getTextLabel().setTextColor(Colors.Purple);
        setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);

        picker = new StringUIPicker();
        picker.setSelectionListener((selection) -> {
            getDetailTextLabel().setText(selection);
            if (selectionListener != null) {
                selectionListener.invoke(selection);
            }
        });
    }

    public List<String> getItems() {
        return picker.getItems();
    }

    public void setItems(List<String> items) {
        picker.setItems(items);

        switch (items.size()) {
        case 1:
            getDetailTextLabel().setTextColor(Colors.Gray);
//            setUserInteractionEnabled(false);
            setAccessoryType(UITableViewCellAccessoryType.None);
            break;
        default:
            getDetailTextLabel().setTextColor(Colors.Black);
//            setUserInteractionEnabled(true);
            setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
            break;
        }
    }

    public String getSelectedItem() {
        return picker.getSelectedItem();
    }

    public void setSelectedItem(String item) {
        picker.setSelectedItem(item);
        setDetailText(item);
    }

    public int getSelectedIndex() {
        return picker.getSelectedIndex();
    }

    public void setSelectedIndex(int index) {
        picker.setSelectedIndex(index);
    }

    public String getText() {
        return getTextLabel().getText();
    }

    public void setText(String text) {
        getTextLabel().setText(text);
    }

    public String getDetailText() {
        return getDetailTextLabel().getText();
    }

    public void setDetailText(String text) {
        getDetailTextLabel().setText(text);
    }

    public UIView getPickerView() {
        return pickerView;
    }

    public void tap() {
        // Don't show the picker when we don't have options.
        if (getItems().size() == 1) {
            return;
        }

        picker.setSelectedIndex(getItems().indexOf(getDetailText()));
        picker.showPicker();
    }
}
