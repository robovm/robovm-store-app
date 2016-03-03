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
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.api.GoogleAnalyticsService;
import org.robovm.store.util.Gravatar;
import org.robovm.store.views.CircleDrawable;

public class LoginFragment extends Fragment {
    // TODO: Enter your RoboVM account email address here
    // If you do not have a RoboVM Account please sign up here:
    // https://account.robovm.com/#/register
    private static final String ROBOVM_ACCOUNT_EMAIL = "";

    private Runnable loginSuccessListener;

    private EditText password;
    private Button login;
    private ImageView imageView;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleAnalyticsService.getInstance().reportAnalyticScreen("Login");
        if (ROBOVM_ACCOUNT_EMAIL == null || ROBOVM_ACCOUNT_EMAIL.isEmpty()) {
            return createInstructions(inflater, container, savedInstanceState);
        }
        return createLoginView(inflater, container, savedInstanceState);
    }

    private View createInstructions(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prefill_robovm_account_instructions, null);
        TextView textView = (TextView) view.findViewById(R.id.codeTextView);
        Spanned coloredText = Html.fromHtml(
                "<b><font color='#000080'>static</font></b> String <b><i><font color='#660E7A'>ROBOVM_ACCOUNT_EMAIL</font></i></b> = <b><font color='#008000'>\"...\"</font>;</b>");
        textView.setText(coloredText, TextView.BufferType.SPANNABLE);
        GoogleAnalyticsService.getInstance().reportAnalyticEvent("Errors", "Missing Email", "Email", 1);

        return view;
    }

    private View createLoginView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_screen, null);

        imageView = (ImageView) view.findViewById(R.id.imageView1);
        loadUserImage();

        EditText textView = (EditText) view.findViewById(R.id.email);
        textView.setEnabled(false);
        textView.setText(ROBOVM_ACCOUNT_EMAIL);

        password = (EditText) view.findViewById(R.id.password);
        login = (Button) view.findViewById(R.id.signInBtn);
        login.setOnClickListener((b) -> {
            login(ROBOVM_ACCOUNT_EMAIL, password.getText().toString());
        });

        return view;
    }

    private void loadUserImage() {
        int px = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, getActivity().getResources().getDisplayMetrics());
        Gravatar.getInstance().getImageBytes(ROBOVM_ACCOUNT_EMAIL, px, Gravatar.Rating.PG, (bytes) -> {
            if (bytes != null) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageDrawable(new CircleDrawable(image));
            }
        });
    }

    public void setLoginSuccessListener(Runnable loginSuccessListener) {
        this.loginSuccessListener = loginSuccessListener;
    }

    private void login(String username, String password) {
        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Logging in", true);
        this.login.setEnabled(false);
        this.password.setEnabled(false);

        RoboVMWebService.getInstance().authenticate(username, password, (success) -> {
            if (success && loginSuccessListener != null) {
                loginSuccessListener.run();
            } else {
                Toast.makeText(getActivity(), "Please verify your RoboVM account credentials and try again",
                        Toast.LENGTH_LONG).show();
                GoogleAnalyticsService.getInstance().reportAnalyticEvent("Errors", "Login Failed", "Login", 1);
            }

            this.login.setEnabled(true);
            this.password.setEnabled(true);
            progressDialog.hide();
            progressDialog.dismiss();
        });
    }
}
