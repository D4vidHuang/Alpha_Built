package com.ecadi.alphabuiltbackend;

import com.ecadi.alphabuiltbackend.model.Memory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

class ShutDownHookTest {


    @Mock
    private Memory memory;

    @InjectMocks
    private ShutDownHook shutDownHook;

    @Test
    public void testDestroy() throws Exception {
        //        projectRepositoryService = Mockito.mock(ProjectRepositoryService.class);
        //        userRepositoryService = Mockito.mock(UserRepositoryService.class);
        //        meshRepositoryService = Mockito.mock(MeshRepositoryService.class);
        //        memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        memory = Mockito.mock(Memory.class);
        shutDownHook = new ShutDownHook(memory);
        shutDownHook.destroy();

        verify(memory).clearAllDatabase();
    }
}