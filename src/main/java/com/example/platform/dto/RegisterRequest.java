package com.example.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//represents the data coming from the user
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(min = 3, max = 50)
    private String displayName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    private String confirmPassword;

    private String profilePictureUrl; // todo: implement multipart to string url when designing profile pic functionality
    //later we will add custom validator to check if password==confirmPassword
}
