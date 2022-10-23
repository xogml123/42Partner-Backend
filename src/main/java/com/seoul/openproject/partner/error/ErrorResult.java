package com.seoul.openproject.partner.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResult {

    private String message;
}
