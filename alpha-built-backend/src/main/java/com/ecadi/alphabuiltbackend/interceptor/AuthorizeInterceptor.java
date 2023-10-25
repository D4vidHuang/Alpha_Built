package com.ecadi.alphabuiltbackend.interceptor;

import com.ecadi.alphabuiltbackend.domain.account.AccountService;
import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    private AccountService accountService;

    /**
     * Get the account information of the currently logged in user from the SecurityContext object.
     * Subsequently, put this information into the session.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return Always true, because the intention of this method is to put user information into the session.
     * @throws Exception if the handler pre-handle method throws an exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        // Check if the user imported is from org.springframework.security.core.userdetails.User
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        // Find the account information of the currently logged in user.
        AccountInfo accountInfo = accountService.findAccountInfoByUsernameOrEmail(username);
        request.getSession().setAttribute("account_info", accountInfo);
        // Debug section
        System.out.println(authentication);
        return true;
    }
}
