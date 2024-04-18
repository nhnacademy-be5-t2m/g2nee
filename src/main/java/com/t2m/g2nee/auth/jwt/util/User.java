package com.t2m.g2nee.auth.jwt.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String username;

    private String password;

    private String role;
}
