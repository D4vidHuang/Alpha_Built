package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.entity.RestBean;

import com.ecadi.alphabuiltbackend.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.hibernate.validator.constraints.Length;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    private final String usernameRegex = "^[a-zA-Z0-9一-龥]+$";

    @Resource
    AuthService authService;

    @Resource
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Handles user sign up request.
     *
     * @param username The username of the user trying to sign up.
     * @param password The password of the user trying to sign up.
     * @param email The email of the user trying to sign up.
     * @return a {@link RestBean} instance. If the sign up process is successful,
     *         it returns a success message. If the process fails, it returns a failure message with a 400 error code.
     */
    @PostMapping("/sign-up")
    public RestBean<String> signUpUser(
            @Pattern(regexp = usernameRegex) @Length(min = 5, max = 16) @RequestParam("username")String username,
            @Length(min = 6, max = 16) @RequestParam("password")String password,
            @Pattern(regexp = emailRegex) @RequestParam("email")String email)  {
        int statusCode = authService.validateAndRegister(username, encoder.encode(password), email);
        return switch (statusCode) {
            case 0 -> RestBean.failure(400, "用户名或邮箱已存在。\nUsername or email already exists.");
            case -1 -> RestBean.failure(400, "注册失败。\nSign up failed.");
            case 1 -> RestBean.success("注册成功。\nSign up successful.");
            default -> RestBean.failure(400, "未知错误。\nUnknown error.");
        };
    }
}
