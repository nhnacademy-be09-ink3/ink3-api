package shop.ink3.api.payment.paymentUtil.parser;

import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.entity.Payment;

public interface PaymentParser {
    /****
 * Parses a payment response from a JSON string using the provided payment confirmation request.
 *
 * @param paymentConfirmRequest the payment confirmation request containing relevant payment details
 * @param json the JSON string representing the payment response
 * @return a Payment object parsed from the JSON response and request data
 */
Payment paymentResponseParser(PaymentConfirmRequest paymentConfirmRequest, String json);
}
