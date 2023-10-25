package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.domain.account.ProjectIdAndUserIdPair;
import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.model.Memory;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @InjectMocks
    private ProjectController controller;

    @Mock
    private Memory memory;

    @Test
    void saveProject_noPrivilege() {
        AccountInfo accountInfo = new AccountInfo(10, "anon", "abc@gmail.com");

        RestBean result = controller.saveProject(accountInfo, 30);
        assertEquals(RestBean.failure(400, "您没有权限访问该项目。\n You don't have access to this project."), result);
    }

    @Test
    void saveProject_success() {
        AccountInfo accountInfo = new AccountInfo(10, "anon", "abc@gmail.com");
        accountInfo.setUserRolesInProjects(List.of(new ProjectIdAndUserIdPair(30, 1)));

        ReflectionTestUtils.setField(controller, "memory", memory);

        RestBean result = controller.saveProject(accountInfo, 30);
        assertEquals(RestBean.success("保存Project - " + 30 + "成功。\n Project - "
                + 30 + " saved successful."), result);
        verify(memory, times(1)).snapshotProject(30);
    }

    @Test
    void saveProject_exception() {
        AccountInfo accountInfo = new AccountInfo(10, "anon", "abc@gmail.com");
        accountInfo.setUserRolesInProjects(List.of(new ProjectIdAndUserIdPair(30, 1)));

        ReflectionTestUtils.setField(controller, "memory", memory);
        doThrow(RuntimeException.class).when(memory).snapshotProject(30);

        RestBean result = controller.saveProject(accountInfo, 30);
        assertEquals(RestBean.failure(400, "保存Project - " + 30 + "失败。\n Project - "
                + 30 + " failed to save."), result);
        verify(memory, times(1)).snapshotProject(30);
    }


    @Test
    void removeUserFromProject_noSuchUser() {
        Project project = new Project();
        when(memory.getProjectHandler(20)).thenReturn(project);
        ReflectionTestUtils.setField(controller, "memory", memory);

        RestBean result = controller.removeUserFromProject(10, 20);
        assertEquals(RestBean.failure(400, "该用户不在该项目中。\n The user is not in this project."), result);
    }

    @Test
    void removeUserFromProject_success() {
        User user = new User(10, 20, mock(ChannelHandlerContext.class));
        user.setId(10L);
        Project project = new Project();
        project.addActiveUser(user);
        when(memory.getProjectHandler(20)).thenReturn(project);
        ReflectionTestUtils.setField(controller, "memory", memory);

        RestBean result = controller.removeUserFromProject(10, 20);
        assertEquals(RestBean.success(), result);
    }

    @Test
    void removeUserFromProject_exception() {
        User user = new User(10, 20, mock(ChannelHandlerContext.class));
        user.setId(10L);
        Project project = new Project();
        project.addActiveUser(user);
        Project spy = spy(project);
        when(memory.getProjectHandler(20)).thenReturn(spy);

        doThrow(RuntimeException.class).when(spy).removeActiveUser(10);
        ReflectionTestUtils.setField(controller, "memory", memory);

        RestBean result = controller.removeUserFromProject(10, 20);
        assertEquals(RestBean.failure(400), result);
    }
}