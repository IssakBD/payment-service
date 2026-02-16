package kz.iprody.paymentserviceapp.controller;

import kz.iprody.paymentserviceapp.persistence.entity.Payment;
import kz.iprody.paymentserviceapp.persistence.entity.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping
    public List<Payment> getPayment() {
        return paymentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Payment> getPayments(@PathVariable UUID id) {
        return paymentRepository.findById(id);
    }
}
