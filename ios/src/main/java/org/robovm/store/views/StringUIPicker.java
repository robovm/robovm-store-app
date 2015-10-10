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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewModel;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.store.util.Action;
import org.robovm.store.util.Colors;

@SuppressWarnings("deprecation")
public class StringUIPicker extends UIPickerView {
    private Action<String> selectionListener;

    private List<String> items;
    private int currentIndex;
    private UIActionSheet actionSheet;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
        setModel(new PickerModel(items));
    }

    public int getSelectedIndex() {
        return currentIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (this.currentIndex == selectedIndex) {
            return;
        }
        this.currentIndex = selectedIndex;
        selectRow(currentIndex, 0, true);

        if (selectionListener != null) {
            selectionListener.invoke(getSelectedItem());
        }
    }

    public String getSelectedItem() {
        return items != null && items.size() > currentIndex ? items.get(currentIndex) : "";
    }

    public void setSelectedItem(String item) {
        if (!items.contains(item)) {
            return;
        }
        setSelectedIndex(items.indexOf(item));
    }

    public void showPicker() {
        actionSheet = new UIActionSheet();
        actionSheet.setBackgroundColor(Colors.Clear);

        UIView parentView = UIApplication.getSharedApplication().getKeyWindow().getRootViewController().getView();

        // Creates a transparent grey background who catches the touch actions
        // (and add more style).
        UIView dimBackgroundView = new UIView(parentView.getBounds());
        dimBackgroundView.setBackgroundColor(Colors.Gray.addAlpha(0.5f));

        final float titleBarHeight = 44;
        CGSize actionSheetSize = new CGSize(parentView.getFrame().getWidth(), getFrame().getHeight() + titleBarHeight);
        CGRect actionSheetFrameHidden = new CGRect(0, parentView.getFrame().getHeight(), actionSheetSize.getWidth(),
                actionSheetSize.getHeight());
        CGRect actionSheetFrameDisplayed = new CGRect(0, parentView.getFrame().getHeight()
                - actionSheetSize.getHeight(), actionSheetSize.getWidth(), actionSheetSize.getHeight());

        // Hide the action sheet before we animate it so it comes from the
        // bottom.
        actionSheet.setFrame(actionSheetFrameHidden);

        setFrame(new CGRect(0, 1, actionSheetSize.getWidth(), actionSheetSize.getHeight() - titleBarHeight));

        UIToolbar toolbarPicker = new UIToolbar(new CGRect(0, 0, actionSheet.getFrame().getWidth(), titleBarHeight));
        toolbarPicker.setClipsToBounds(true);
        toolbarPicker.setItems(new NSArray<UIBarButtonItem>(
                new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace),
                new UIBarButtonItem(UIBarButtonSystemItem.Done, (barButtonItem) -> {
                    UIView.animate(.25,
                            () -> actionSheet.setFrame(actionSheetFrameHidden),
                            (finish) -> {
                                dimBackgroundView.removeFromSuperview();
                                actionSheet.removeFromSuperview();
                            });
                })
                ));

        // Creates a blur background using the toolbar trick.
        UIToolbar toolbarBg = new UIToolbar(new CGRect(0, 0, actionSheet.getFrame().getWidth(), actionSheet.getFrame()
                .getHeight()));
        toolbarBg.setClipsToBounds(true);

        actionSheet.addSubviews(toolbarBg, this, toolbarPicker);
        parentView.addSubviews(dimBackgroundView, actionSheet);

        parentView.bringSubviewToFront(actionSheet);

        UIView.animate(.25, () -> actionSheet.setFrame(actionSheetFrameDisplayed));
    }

    public void setSelectionListener(Action<String> listener) {
        this.selectionListener = listener;
    }

    private class PickerModel extends UIPickerViewModel {
        private final List<String> items;

        public PickerModel(List<String> items) {
            this.items = items;
        }

        @Override
        public long getNumberOfComponents(UIPickerView pickerView) {
            return 1;
        }

        @Override
        public long getNumberOfRows(UIPickerView pickerView, long component) {
            return items.size();
        }

        @Override
        public String getRowTitle(UIPickerView pickerView, long row, long component) {
            return items.get((int) row);
        }

        @Override
        public void didSelectRow(UIPickerView pickerView, long row, long component) {
            setSelectedIndex((int) row);
        }
    }
}
