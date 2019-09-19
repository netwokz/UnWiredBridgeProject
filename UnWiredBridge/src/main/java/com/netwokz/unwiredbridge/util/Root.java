package com.netwokz.unwiredbridge.util;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Steve on 9/9/13.
 */
public class Root {

    private static final String COMMAND_SETPROP = "setprop ";

    public static boolean hasRootPermission() {
        return RootTools.isAccessGiven();
    }

    public static boolean setProp(String property, String value) {
        return runCommand(COMMAND_SETPROP + property + " " + value);
    }

    public static boolean runCommand(String command) {
        try {
            RootTools.getShell(true).add(new CommandCapture(0, command));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isProcessRunning(String process) throws Exception {
        return RootTools.isProcessRunning(process);
    }
}
