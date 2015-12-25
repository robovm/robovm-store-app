package org.robovm.store.util;

import java.util.ResourceBundle;

public class I18N {
    public enum Key {
        checkout, brag_twitter, done, order_complete, order_received, processing_order, try_again, tweet_text, error, order_placed, unexpected_error, first_name_required, last_name_required, address_required, city_required, zip_code_required, phone_number_required, country_required, ok, processing_title, could_not_log_in, verify_credentials, logging_in, log_in_title, please_wait, placing_order, add_to_basket, size, color, free, products_list_title, shipping_title, shipping_information, first_name, last_name, phone_number, address, city, postal_code, country, state, select_country, select_state, place_order, your_basket_title, street_adress_1, basket_empty, go_add_something, brag_to_friends, explanation_text, street_adress_2, selected
    }

    public static String getLocalizedString(Key key) {
        ResourceBundle stringsBundle = ResourceBundle.getBundle("StringsBundle");
        if (stringsBundle.containsKey(key.name())) {
            return stringsBundle.getString(key.name());
        }
        return key.name();
    }
}
