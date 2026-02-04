package kz.iprody.paymentserviceapp.controller;

import kz.iprody.paymentserviceapp.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Payment payment = new Payment(1L, 99.99);
    private Map<Long, Payment> payments = new HashMap<>();
    public PaymentController() {
        payments.put(1L, payment);
        payments.put(2L, new Payment(2L, 150.50));
        payments.put(3L, new Payment(3L, 200.00));
        payments.put(4L, new Payment(4L, 75.25));
        payments.put(5L, new Payment(5L, 300.99));
    }

    @GetMapping
    public ArrayList<Payment> getPayment() {
        return new ArrayList<>(payments.values());
    }

    @GetMapping("/{userId}")
    public Payment getPayments(@PathVariable Long userId) {
        return payments.get(userId);
    }
}
