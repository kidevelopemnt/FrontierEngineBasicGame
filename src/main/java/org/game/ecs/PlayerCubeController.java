package org.game.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.events.Event;
import frontier.engine.events.OnCollisionEntered;
import frontier.engine.events.OnTriggerEntered;
import frontier.engine.physics.CollisionResult;
import org.game.events.OnPlayerCollision;

import java.util.Map;

public class PlayerCubeController extends Component {
    @Override
    public void initialize() {
        getEngine().getEventBus().subscribe(entity, OnTriggerEntered.class, this::triggerEntered);
    }

    @Override
    public void update(float deltaTime) {
        entity.getTransform().position.x -= entity.getScene().getEngine().getInput().getMouse().getDeltaX() * deltaTime;
        // System.out.println(entity.getTransform().position.x);
    }

    private void triggerEntered(Event event) {
        CollisionResult result = (CollisionResult) event.getContext();
        getEngine().getEventBus().trigger(null, OnPlayerCollision.class, result);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
