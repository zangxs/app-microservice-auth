package com.brayanspv.auth.mocks;

public class JSONMockConstants {


    public static String SIGNUP_REQUEST = """
            {
              "username":"brayanpv",
              "email":"brayanstivenp578@gmail.com",
              "password":"password123"
            }
            """;

    public static String LOGIN_REQUEST = """
            {
              "username": "daniel",
              "password": "123tafur"
            }
            
            """;

    public static String FORGOT_PASSWORD_REQUEST = """
            {
              "email": "user@example.com"
            }
            """;

    public static String VERIFY_CODE_REQUEST = """
            {
              "email": "user@example.com",
              "code": "123456"
            }
            """;

    public static String RESET_PASSWORD_REQUEST = """
            {
              "email": "user@example.com",
              "code": "123456",
              "newPassword": "newPassword123"
            }
            """;
}
