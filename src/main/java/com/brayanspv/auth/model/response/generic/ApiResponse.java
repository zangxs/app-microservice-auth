package com.brayanspv.auth.model.response.generic;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class ApiResponse implements Serializable {
    private Long dateTime;
    private int code;
    private Object data;

}
