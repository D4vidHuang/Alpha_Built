
package com.ecadi.alphabuiltbackend;


import com.ecadi.alphabuiltbackend.model.Memory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ShutDownHook class implements the DisposableBean interface, which is a part of the Spring framework.
 * This class is used to execute certain actions when the application context is being closed.
 * It's responsible for handling the clean-up operation when the system shuts down.
 */
@Component
public class ShutDownHook implements DisposableBean {

    private Memory memory;

    /**
     * The constructor of the ShutDownHook class.
     *
     * @param memory an instance of the Memory class that
     *      will be used to clear all data when the application context is closed.
     */
    @Autowired
    public ShutDownHook(Memory memory) {
        this.memory = memory;
    }

    /**
     * Overrides the destroy method of the DisposableBean interface.
     * This method is called when the Spring framework's application context is closed.
     * It performs the required shutdown actions - in this case, clearing all data from the database.
     *
     * @throws Exception if an error occurs during database cleanup.
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("Performing shutdown actions...");
        memory.clearAllDatabase();
    }
}
