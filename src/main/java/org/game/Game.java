package org.game;

import frontier.engine.Engine;
import frontier.engine.events.Event;
import frontier.engine.game.IGame;
import org.game.events.OnPlayerCollision;
import org.game.scenes.MainScene;

public class Game implements IGame {
    private Engine engine;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;

        // TODO: Punish player for staying still
        // TODO: Better graphics! Materials/models/textures, as engine allows

        MainScene mainScene = new MainScene(engine);
        engine.setActiveScene(mainScene.getScene());
        engine.getInput().getMouse().setCursorLocked(true);

        engine.getEventBus().subscribe(null, OnPlayerCollision.class, this::endGame);
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void shutdown() {

    }

    private void endGame(Event event) {
        engine.shutdown();
    }
}
