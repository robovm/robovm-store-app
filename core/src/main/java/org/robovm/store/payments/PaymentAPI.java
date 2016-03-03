package org.robovm.store.payments;

import com.paypal.api.payments.*;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.robovm.store.model.Basket;
import org.robovm.store.model.User;
import org.robovm.store.util.Countries;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class PaymentAPI {

    private static PaymentAPI instance = new PaymentAPI();
    private CreditCard creditCard;
    private User user;
    private final NumberFormat format;

    private PaymentAPI() {
        this.creditCard = new CreditCard();
//        test card
//        this.creditCard = new CreditCard()
//                .setType("visa")
//                .setNumber("4446283280247004")
//                .setExpireMonth(11)
//                .setExpireYear(2018)
//                .setFirstName("Joe")
//                .setLastName("Shopper");
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        formatSymbols.setDecimalSeparator('.');
        format = new DecimalFormat("#0.00", formatSymbols);
        Countries.getCountries();
    }

    public static PaymentAPI getInstance() {
        return instance;
    }

    private String getAccessToken() throws PayPalRESTException {
        InputStream is = PaymentAPI.class.getResourceAsStream("/sdk_config.properties");
        OAuthTokenCredential tokenCredential = Payment.initConfig(is);
        return tokenCredential.getAccessToken();
    }

    public Payment createPayment(String description,
                                 Basket basket,
                                 User user) throws PayPalRESTException {
        Map<String, String> sdkConfig = new HashMap<>();
        sdkConfig.put("mode", Constants.SANDBOX);


        APIContext apiContext = new APIContext(getAccessToken());
        apiContext.setConfigurationMap(sdkConfig);

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);

        List<FundingInstrument> fundingInstrumentList = new ArrayList<>();
        fundingInstrumentList.add(fundingInstrument);

        Payer payer = new Payer();
        payer.setFundingInstruments(fundingInstrumentList);
        payer.setPaymentMethod("credit_card");

        Transaction transaction = new Transaction();
        transaction.setDescription(description);

        ItemList itemList = new ItemList();
        Amount amount = new Amount("EUR", "0");
        double total = 0;

        ArrayList<Item> items = new ArrayList<>();
        for (org.robovm.store.model.Order order : basket.getOrders()) {
            total += order.getProduct().getPrice();
            items.add(new Item("1", order.toString(), format.format(order.getProduct().getPrice()), amount.getCurrency()));
        }
        itemList.setItems(items);

        // TODO: add validation
        ShippingAddress address = new ShippingAddress();
        address.setCountryCode(user.getCountry());
        address.setPostalCode(user.getZipCode());
        address.setCity(user.getCity());
        address.setLine1(user.getAddress1());
        address.setLine2(user.getAddress2());
        address.setState(user.getState());
        address.setRecipientName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        address.setPhone(user.getPhone() != null && user.getPhone().length() > 0 ? user.getPhone() : null);
        itemList.setShippingAddress(address);

        amount.setTotal(format.format(total + 1.0));
        transaction.setAmount(amount);
        transaction.setItemList(itemList);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        return payment.create(apiContext);
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
