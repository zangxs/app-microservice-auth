package com.brayanspv.auth.model.response.generic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ApiError implements Serializable {
    ArrayList<String> errors;

}
