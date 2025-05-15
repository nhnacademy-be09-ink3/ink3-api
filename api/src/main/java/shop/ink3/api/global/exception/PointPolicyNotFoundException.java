package shop.ink3.api.global.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PointPolicyNotFoundException extends NotFoundException {

    public PointPolicyNotFoundException(String message){
        super(message);
    }
}
