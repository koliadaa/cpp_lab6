package udpWork.task3_multicast.gui.messenger;

import udpWork.task3_multicast.gui.interfaces.UITasks;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EDTInvocationHandler implements InvocationHandler {

    private Object invocationResult;
    private final UITasks tasks;

    public EDTInvocationHandler(UITasks tasks) {
        this.tasks = tasks;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (SwingUtilities.isEventDispatchThread()) {
            invocationResult = method.invoke(tasks, args);
        } else {
            Runnable shell = () -> {
                try {
                    invocationResult = method.invoke(tasks, args);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
            SwingUtilities.invokeAndWait(shell);
        }
        return invocationResult;
    }

}
