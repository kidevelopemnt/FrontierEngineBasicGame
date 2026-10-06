package org.game;

import frontier.engine.Engine;
import frontier.engine.game.IGame;
import org.game.scenes.MainScene;

public class Game implements IGame {

    @Override
    public void initialize(Engine engine) {
        MainScene mainScene = new MainScene(engine);
        engine.setActiveScene(mainScene.getScene());
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void shutdown() {
        System.out.println("Shutdown");
    }
}
