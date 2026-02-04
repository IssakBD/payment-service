package kz.iprody.paymentserviceapp.model;

public class Payment {
    private Long id;
    private double value;

    public Payment() {
    }

    public Payment(Long id, double value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
