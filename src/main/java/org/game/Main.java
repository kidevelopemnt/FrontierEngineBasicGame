package org.game;

import frontier.engine.application.Application;
import frontier.engine.application.ApplicationConfiguration;
import frontier.engine.core.Logger;

import java.nio.file.Paths;


public class Main {
    static void main() {
        ApplicationConfiguration appConfig = new ApplicationConfiguration();
        appConfig.logMode = Logger.LogMode.CONSOLE;
        appConfig.logLevel = Logger.LogLevel.ERROR;

        Application application = new Application(Paths.get("").toAbsolutePath(), appConfig, new Game());
        application.run();
    }
}
