package com.appsdeveloperblog.estore.usersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.core.PaymentDetails;
import com.appsdeveloperblog.estore.core.User;
import com.appsdeveloperblog.estore.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User findUserPaymentsDetails(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        // This method can be used to handle queries related to user payment details
        // For example, you can fetch user payment details from a database or another service
        // and return the result.
        
        // Example implementation (to be replaced with actual logic):
        // UserPaymentDetails userPaymentDetails = userService.getUserPaymentDetails(query.getUserId());
        // return userPaymentDetails;

        // For now, just a placeholder
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("DIEGO PAULINO")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();
                
        User user = User.builder()
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .firstName("Diego")
                .lastName("Paulino")
                .paymentDetails(paymentDetails)
                .build();

        return user;
    }
}
