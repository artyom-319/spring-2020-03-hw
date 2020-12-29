package com.etn319.service.common;

import com.etn319.service.ServiceLayerException;

public class EmptyMandatoryFieldException extends ServiceLayerException {
    public EmptyMandatoryFieldException(String message) {
        super(message);
    }
}
