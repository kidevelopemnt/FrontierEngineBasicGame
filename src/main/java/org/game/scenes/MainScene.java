package org.game.scenes;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.events.Event;
import frontier.engine.events.UpdateEvent;
import frontier.engine.scene.Scene;
import org.game.ecs.SlidingBlock;

import java.io.IOException;
import java.util.Random;

public class MainScene {
    private Engine engine;
    private Scene scene;

    private Random random;
    private float groundLength = 25f;
    private float groundWidth = 1f;

    private float spawnDelayMin = 0.5f;
    private float spawnDelayMax = 2.0f;
    private float spawnDelay;
    private float timeSinceLastSpawn = 0f;

    public MainScene(Engine engine) {
        this.engine = engine;
        scene = engine.createScene("Main Scene");
        random = new Random();

        scene.getCamera().getEntity().getTransform().position.set(0, 1f, groundLength / 2f - 1f);
        scene.getCamera().getEntity().getTransform().rotation.set(0, 180, 0);
        // scene.getCamera().getEntity().addComponent(CameraController.class);

        Entity ground = new Entity("Ground", scene);
        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);
        try {
            meshRenderer.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ground.getTransform().position.set(0, 0, groundLength);
        ground.getTransform().scale.set(groundWidth, 0.1, groundLength);
        scene.addEntity(ground);

        spawnBlock();

        spawnDelay = random.nextFloat(spawnDelayMin, spawnDelayMax);
        engine.getEventBus().subscribe(null, UpdateEvent.class, this::update);
    }

    private void update(Event event) {
        float deltaTime = (float) event.getContext();

        timeSinceLastSpawn += deltaTime;

        if (timeSinceLastSpawn >= spawnDelay) {
            spawnBlock();
            timeSinceLastSpawn = 0f; // Reset the timer
            spawnDelay = random.nextFloat(spawnDelayMin, spawnDelayMax); // Randomize spawn time
        }
    }

    private void spawnBlock() {
        Entity block = new Entity("Block", scene);
        MeshRenderer mr = block.addComponent(MeshRenderer.class);
        try {
            mr.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        block.getTransform().scale.set(0.25f, 0.25f, 0.25f);
        block.getTransform().position.set(random.nextFloat(-groundWidth/2, groundWidth/2), 0.5f, groundLength * 1.5f);
        SlidingBlock sliding = block.addComponent(SlidingBlock.class);
        sliding.setBounds(scene.getCamera().getEntity().getTransform().position.z);

        scene.addEntity(block);
    }

    public Scene getScene() {
        return scene;
    }
}
