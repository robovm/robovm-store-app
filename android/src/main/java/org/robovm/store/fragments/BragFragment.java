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

package org.robovm.store.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.robovm.store.R;
import static org.robovm.store.util.I18N.*;

public class BragFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.brag_screen, null);

        Button bragBtn = (Button) view.findViewById(R.id.bragButton);
        bragBtn.setOnClickListener((v) -> bragOnTwitter());
        bragBtn.setText(getLocalizedString(Key.brag_to_friends));

        TextView orderComplete = (TextView)view.findViewById(R.id.orderComplete);
        orderComplete.setText(getLocalizedString(Key.order_complete));

        TextView orderReceived = (TextView)view.findViewById(R.id.orderReceived);
        orderReceived.setText(getLocalizedString(Key.order_received));

        return view;
    }

    private void bragOnTwitter() {
        String message = "";

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.brag_on)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
