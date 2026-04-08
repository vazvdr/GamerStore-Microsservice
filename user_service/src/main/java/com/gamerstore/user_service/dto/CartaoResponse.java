package com.gamerstore.user_service.dto;

public class CartaoResponse {

    private Long id;
    private String brand;
    private String last4;
    private Integer expMonth;
    private Integer expYear;
    private String stripePaymentMethodId;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getLast4() {
        return last4;
    }
    public void setLast4(String last4) {
        this.last4 = last4;
    }
    public Integer getExpMonth() {
        return expMonth;
    }
    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }
    public Integer getExpYear() {
        return expYear;
    }
    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }
    public String getStripePaymentMethodId(){
        return stripePaymentMethodId;
    }
    public void setStripePaymentMethodId(String stripePaymentMethodId){
        this.stripePaymentMethodId = stripePaymentMethodId;
    }    
}