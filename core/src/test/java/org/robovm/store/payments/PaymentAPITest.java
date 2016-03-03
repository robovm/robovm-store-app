package org.robovm.store.payments;

import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Before;
import org.junit.Test;
import org.robovm.store.model.*;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentAPITest {

    private Basket basket;
    private User user;

    @Before
    public void before() {
        user = new User();
        user.setFirstName("Max");
        user.setLastName("Mustermann");
        user.setCountry("Austria");
        user.setState("Vienna");
        user.setZipCode("1115");
        user.setCity("Vienna");
        user.setPhone("+4313123123");
        user.setAddress1("Rathausplatz 1");
        user.setAddress2("-");

        basket = new Basket();
        basket.add(new Order(new Product() {

            @Override
            public String getName() {
                return "Test product";
            }

            @Override
            public double getPrice() {
                return 1.0;
            }

            @Override
            public List<ProductColor> getColors() {
                return Collections.singletonList(new ProductColor() {

                    @Override
                    public String getName() {
                        return "green";
                    }
                });
            }

            @Override
            public List<ProductSize> getSizes() {
                return Collections.singletonList(new ProductSize() {
                    @Override
                    public String getName() {
                        return "L";
                    }
                });
            }
        }));
    }

    @Test(expected = PayPalRESTException.class)
    public void testCreatePaymentInvalidCard() throws PayPalRESTException {
        CreditCard creditCard = new CreditCard()
                .setType("visa")
                .setNumber("")
                .setExpireMonth(0)
                .setExpireYear(0)
                .setFirstName("")
                .setLastName("");
        PaymentAPI.getInstance().setCreditCard(creditCard);

        PaymentAPI.getInstance().createPayment("test direct payment with credit card", basket, user);
    }

    @Test
    public void testCreatePaymentVisa() throws PayPalRESTException {
        CreditCard creditCard = new CreditCard()
                .setType("visa")
                .setNumber("4446283280247004")
                .setExpireMonth(11)
                .setExpireYear(2018)
                .setFirstName("Joe")
                .setLastName("Shopper");
        PaymentAPI.getInstance().setCreditCard(creditCard);
        Payment payment = PaymentAPI.getInstance().createPayment("test direct payment with credit card", basket, user);
        assertThat(payment.getState(), is("approved"));
    }

    @Test
    public void testCreatePaymentMastercard() throws PayPalRESTException {
        CreditCard creditCard = new CreditCard()
                .setType("mastercard")
                .setNumber("5500005555555559")
                .setExpireMonth(12)
                .setExpireYear(2018)
                .setFirstName("Batsy")
                .setLastName("Buyer");
        PaymentAPI.getInstance().setCreditCard(creditCard);
        Payment payment = PaymentAPI.getInstance().createPayment("test direct payment with credit card", basket, user);
        assertThat(payment.getState(), is("approved"));
    }
}
