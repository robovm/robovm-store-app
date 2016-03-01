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
package org.robovm.store.api;

import org.robovm.store.util.I18N;

import java.util.List;

public class ValidationError {
    private String field;
    private String message;

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return field != null ? message + ": " + field : message;
    }

    public static String getValidationAlertMessage(List<ValidationError> errors) {
        String alertMessage = I18N.getLocalizedString(I18N.Key.unexpected_error);

        if (errors != null) { // We handle only the first error.
            ValidationError error = errors.get(0);

            String message = error.getMessage();
            String field = error.getField();
            if (field == null) {
                alertMessage = message;
            } else {
                switch (field) {
                    case "firstName":
                        alertMessage = I18N.getLocalizedString(I18N.Key.first_name_required);
                        break;
                    case "lastName":
                        alertMessage = I18N.getLocalizedString(I18N.Key.last_name_required);
                        break;
                    case "address1":
                        alertMessage = I18N.getLocalizedString(I18N.Key.address_required);
                        break;
                    case "city":
                        alertMessage = I18N.getLocalizedString(I18N.Key.city_required);
                        break;
                    case "zipCode":
                        alertMessage = I18N.getLocalizedString(I18N.Key.zip_code_required);
                        break;
                    case "phone":
                        alertMessage = I18N.getLocalizedString(I18N.Key.phone_number_required);
                        break;
                    case "country":
                        alertMessage = I18N.getLocalizedString(I18N.Key.country_required);
                        break;
                    default:
                        alertMessage = message;
                        break;
                }
            }
        }

        return alertMessage;
    }
}
