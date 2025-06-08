package shop.ink3.api.payment.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentType {
    TOSS, KAKAOPAY, NAVERPAY, POINT;

    @JsonCreator
    public static PaymentType from(String str){
        for(PaymentType type : PaymentType.values()){
            if (type.name().equals(str.toUpperCase())) {
                return type;
            }
        }
        //TODO : 어떤 에러를 만들어야하나.
        throw new IllegalArgumentException ();
    }
}