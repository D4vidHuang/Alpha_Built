package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.domain.account.Account;
import com.ecadi.alphabuiltbackend.domain.account.AccountService;
import com.ecadi.alphabuiltbackend.domain.account.ProjectIdAndUserIdPair;
import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.model.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @InjectMocks
    private AccountController controller;

    @Mock
    private AccountService accountService;

    @Mock
    private Memory memory;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void me_success() {
        AccountInfo info = new AccountInfo(1, "jck", "abc@gmail.com");
        RestBean restBean = controller.me(info);
        assertEquals(restBean, RestBean.success(info));
    }

    @Test
    public void getNextUserId_success() {
        when(accountService.getNextUserId()).thenReturn(1);

        RestBean restBean = controller.getNextUserId();
        assertEquals(restBean, RestBean.success(1));
    }

    @Test
    public void createNewProject_success() {
        when(memory.getNextProjectId()).thenReturn(1);
        ReflectionTestUtils.setField(controller, "memory", memory);

        AccountInfo info = new AccountInfo(10, "jck", "abc@gmail.com");

        RestBean restBean = controller.createNewProject(info);
        assertEquals(RestBean.success(1), restBean);
    }

    @Test
    public void createNewProject_fail() {
        AccountInfo info = new AccountInfo(10, "jck", "abc@gmail.com");

        RestBean restBean = controller.createNewProject(info);
        assertEquals(RestBean.failure(400), restBean);
    }

    @Test
    public void getUserProjects_fail() {
        AccountInfo info = new AccountInfo(10, "jck", "abc@gmail.com");
        Account account = new Account("Jin", "1234", "abc@gmail.com");
        var x = account.getUserRolesInProjects();
        when(accountService.getAccountById(10)).thenReturn(account);
        RestBean restBean = controller.getUserProjects(info);
        assertEquals(RestBean.success(account.getUserRolesInProjects()), restBean);
    }

    @Test
    public void addUserToProject_noSuchUser() {
        RestBean result = controller.addUserToProject("anon", 30);
        assertEquals(RestBean.failure(400, "该用户不存在。\n The user does not exist."), result);

    }

    @Test
    public void addUserToProject_duplicateUser() {
        Account account = new Account("anon", "123412", "abc@gmail.com");
        account.setUserRolesInProjects(List.of(new ProjectIdAndUserIdPair(30, 1)));
        when(accountService.findAccountByUsernameOrEmail("anon")).thenReturn(account);
        RestBean result = controller.addUserToProject("anon", 30);
        assertEquals(RestBean.failure(400, "该用户已经在该项目中。\n The user is already in this project."), result);
    }

    @Test
    public void addUserToProject_success() {
        Account account = new Account("anon", "123412", "abc@gmail.com");
        account.setUserRolesInProjects(List.of(new ProjectIdAndUserIdPair(31, 1)));
        when(accountService.findAccountByUsernameOrEmail("anon")).thenReturn(account);
        RestBean result = controller.addUserToProject("anon", 30);
        assertEquals(RestBean.success("添加用户成功。\n User added successful."), result);
        verify(accountService, times(1)).updateAccountProjects(eq(0), any());
    }

    @Test
    public void addUserToProject_exception() {
        Account account = new Account("anon", "123412", "abc@gmail.com");
        account.setUserRolesInProjects(List.of(new ProjectIdAndUserIdPair(31, 1)));
        when(accountService.findAccountByUsernameOrEmail("anon")).thenReturn(account);
        doThrow(NoSuchElementException.class).when(accountService).updateAccountProjects(anyInt(), any());

        RestBean result = controller.addUserToProject("anon", 30);
        assertEquals(RestBean.failure(400, "添加用户失败。\n Failed to add user."), result);
    }

}
