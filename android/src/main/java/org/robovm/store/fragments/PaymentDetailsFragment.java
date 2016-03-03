package org.robovm.store.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.robovm.store.R;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.api.ValidationError;
import org.robovm.store.model.Basket;
import org.robovm.store.model.User;
import org.robovm.store.payments.PaymentAPI;

import java.util.Arrays;
import java.util.List;

public class PaymentDetailsFragment extends Fragment {
    private CreditCard creditCard;

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText expireMonthField;
    private EditText expireYearField;
    private EditText creditCardNumberField;
    private RadioButton cardTypeVisaField;
    private RadioButton cardTypeMastercardField;

    private Runnable paymentDetailsEnteredListener;

    private class PaymentResult {
        Payment payment;
        PayPalRESTException error;

        private PaymentResult(Payment payment, PayPalRESTException error) {
            this.payment = payment;
            this.error = error;
        }
    }

    public PaymentDetailsFragment() {
        this.creditCard = PaymentAPI.getInstance().getCreditCard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View shippingDetailsView = inflater.inflate(R.layout.payment_details, container, false);

        Button placeOrder = (Button) shippingDetailsView.findViewById(R.id.placeOrder);

        creditCardNumberField = (EditText) shippingDetailsView.findViewById(R.id.creditcard);
        creditCardNumberField.setText(creditCard.getNumber() == null ? "" : creditCard.getNumber());

        firstNameField = (EditText) shippingDetailsView.findViewById(R.id.firstName);
        firstNameField.setText(creditCard.getFirstName() == null ? "" : creditCard.getFirstName());

        lastNameField = (EditText) shippingDetailsView.findViewById(R.id.lastName);
        lastNameField.setText(creditCard.getLastName() == null ? "" : creditCard.getLastName());

        expireMonthField = (EditText) shippingDetailsView.findViewById(R.id.expireMonth);
        expireMonthField.setText(String.valueOf(creditCard.getExpireMonth()));

        expireYearField = (EditText) shippingDetailsView.findViewById(R.id.expireYear);
        expireYearField.setText(String.valueOf(creditCard.getExpireYear()));

        cardTypeVisaField = (RadioButton) shippingDetailsView.findViewById(R.id.cardTypeVisa);
        cardTypeVisaField.setText("visa");
        cardTypeMastercardField = (RadioButton) shippingDetailsView.findViewById(R.id.cardTypeMastercard);
        cardTypeMastercardField.setText("mastercard");

        List<RadioButton> cardTypes = Arrays.asList(cardTypeVisaField, cardTypeMastercardField);

        for (RadioButton cardType : cardTypes) {
            if (cardType.getText().equals(creditCard.getType())) {
                cardType.setChecked(true);
            } else {
                cardType.setChecked(false);
            }
        }

        placeOrder.setOnClickListener((b) -> placeOrder());

        return shippingDetailsView;
    }

    private void placeOrder() {
        enableControls(false);

        creditCard.setFirstName(firstNameField.getText().toString());
        creditCard.setLastName(lastNameField.getText().toString());
        if (cardTypeVisaField.isChecked()) {
            creditCard.setType(cardTypeVisaField.getText().toString());
        } else if (cardTypeMastercardField.isChecked()) {
            creditCard.setType(cardTypeMastercardField.getText().toString());
        }
        creditCard.setNumber(creditCardNumberField.getText().toString());
        creditCard.setExpireMonth(Integer.parseInt(expireMonthField.getText().toString()));
        creditCard.setExpireYear(Integer.parseInt(expireYearField.getText().toString()));


        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Placing Order", true);

        User user = RoboVMWebService.getInstance().getCurrentUser();
        RoboVMWebService.getInstance().placeOrder(user, (response) -> {

            if (response.isSuccess()) {

                Basket basket = RoboVMWebService.getInstance().getBasket();


                AsyncTask<String, String, PaymentResult> paymentTask = new AsyncTask<String, String, PaymentResult>() {
                    @Override
                    protected PaymentResult doInBackground(String... ignored) {
                        try {
                            Payment payment = PaymentAPI.getInstance().createPayment("Demo", basket, user);
                            return new PaymentResult(payment, null);
                        } catch (PayPalRESTException err) {
                            return new PaymentResult(null, err);
                        }
                    }

                    @Override
                    protected void onPostExecute(PaymentResult result) {
                        progressDialog.hide();
                        progressDialog.dismiss();
                        enableControls(true);
                        if (result.payment != null) {
                            if ("approved".equals(result.payment.getState())) {
                                Toast.makeText(getActivity(), String.format("Your order has been placed! ID:%s", result.payment.getId()), Toast.LENGTH_LONG).show();
                                basket.clear();
                                if (paymentDetailsEnteredListener != null) {
                                    paymentDetailsEnteredListener.run();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Your payment wasn't approved! Please try again later!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error processing payment! Please try again later!", Toast.LENGTH_LONG).show();
                        }
                    }

                };

                paymentTask.execute();

            } else {
                progressDialog.hide();
                progressDialog.dismiss();
                enableControls(true);

                List<ValidationError> errors = response.getErrors();
                String alertMessage = "An unexpected error occurred! Please try again later!";

                if (errors != null) { // We handle only the first error.
                    ValidationError error = errors.get(0);

                    String message = error.getMessage();
                    String field = error.getField();
                    if (field == null) {
                        alertMessage = message;
                    } else {
                        switch (field) {
                            case "firstName":
                                alertMessage = "First name is required";
                                break;
                            case "lastName":
                                alertMessage = "Last name is required";
                                break;
                            case "address1":
                                alertMessage = "Address is required";
                                break;
                            case "city":
                                alertMessage = "City is required";
                                break;
                            case "zipCode":
                                alertMessage = "ZIP code is required";
                                break;
                            case "phone":
                                alertMessage = "Phone number is required";
                                break;
                            case "country":
                                alertMessage = "Country is required";
                                break;
                            default:
                                alertMessage = message;
                                break;
                        }
                    }
                }
                Toast.makeText(getActivity(), alertMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void enableControls(boolean enabled) {
        EditText[] entries = new EditText[]{creditCardNumberField, firstNameField, lastNameField, expireMonthField,
                expireYearField};
        for (EditText entry : entries) {
            entry.setEnabled(enabled);
        }
        cardTypeVisaField.setEnabled(enabled);
    }

    public void setPaymentDetailsEnteredListener(Runnable paymentDetailsEnteredListener) {
        this.paymentDetailsEnteredListener = paymentDetailsEnteredListener;
    }
}
