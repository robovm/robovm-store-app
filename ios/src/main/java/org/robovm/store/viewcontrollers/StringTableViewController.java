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
package org.robovm.store.viewcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.store.util.Action;

public class StringTableViewController extends UITableViewController {
    private static final String CELL_ID = "stringCell";

    private Action<String> selectionListener;
    private final UISearchBar searchBar;
    private List<String> items = new ArrayList<>();
    private List<String> filteredItems = new ArrayList<>();

    public StringTableViewController() {
        searchBar = new UISearchBar();
        searchBar.setDelegate(new UISearchBarDelegateAdapter() {
            @Override
            public void didChange(UISearchBar searchBar, String searchText) {
                filteredItems.clear();
                for (String item : items) {
                    if (StringUtils.containsIgnoreCase(item, searchBar.getText())) {
                        filteredItems.add(item);
                    }
                }
                getTableView().reloadData();
            }
        });
        searchBar.sizeToFit();
        getTableView().setTableHeaderView(searchBar);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        getTableView().reloadData();
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);
        searchBar.becomeFirstResponder();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return filteredItems.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell(CELL_ID);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, CELL_ID);
        }
        cell.getTextLabel().setText(filteredItems.get(indexPath.getRow()));
        return cell;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        String item = filteredItems.get(indexPath.getRow());
        if (selectionListener != null) {
            selectionListener.invoke(item);
        }
        getNavigationController().popViewController(true);
    }

    public void setSelectionListener(Action<String> listener) {
        this.selectionListener = listener;
    }
}
