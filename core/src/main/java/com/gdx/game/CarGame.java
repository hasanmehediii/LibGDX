package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.LinkedList;

public class CarGame extends com.badlogic.gdx.ScreenAdapter {

    private final Texture carTexture;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private LinkedList<Float> laneLines;
    private LinkedList<Float> borders;
    private LinkedList<float[]> missiles;

    private float carX;
    private float carY;
    private float scrollSpeed;

    private static final int CAR_WIDTH = 100;
    private static final int CAR_HEIGHT = 160;
    private static final int BORDER_WIDTH = 20;
    private static final int GRASS_WIDTH = 200;
    private static final int LANE_LINE_HEIGHT = 50;
    private static final int LANE_LINE_WIDTH = 5;
    private static final int MISSILE_WIDTH = 10;
    private static final int MISSILE_HEIGHT = 40;
    private static final int MISSILE_SPEED = 15;

    public CarGame(Texture carTexture) {
        this.carTexture = carTexture;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        carX = Gdx.graphics.getWidth() / 2f - CAR_WIDTH / 2f;
        carY = 100;

        scrollSpeed = 5;

        laneLines = new LinkedList<>();
        borders = new LinkedList<>();
        missiles = new LinkedList<>();

        for (int i = 0; i < Gdx.graphics.getHeight() / LANE_LINE_HEIGHT + 1; i++) {
            laneLines.add((float) (i * LANE_LINE_HEIGHT));
            borders.add((float) (i * LANE_LINE_HEIGHT));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.6f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        updateGameObjects();

        // Draw using ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Grass
        shapeRenderer.setColor(0.3f, 0.6f, 0.3f, 1);
        shapeRenderer.rect(0, 0, GRASS_WIDTH, Gdx.graphics.getHeight());
        shapeRenderer.rect(Gdx.graphics.getWidth() - GRASS_WIDTH, 0, GRASS_WIDTH, Gdx.graphics.getHeight());

        // Road
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(GRASS_WIDTH + BORDER_WIDTH, 0,
            Gdx.graphics.getWidth() - 2 * (GRASS_WIDTH + BORDER_WIDTH), Gdx.graphics.getHeight());

        // Lane lines and borders
        drawLaneLines();
        drawBorders();

        shapeRenderer.end();

        // Car and missiles
        spriteBatch.begin();
        spriteBatch.draw(carTexture, carX, carY, CAR_WIDTH, CAR_HEIGHT);
        drawMissiles();
        spriteBatch.end();
    }

    private void handleInput() {
        float leftBoundary = GRASS_WIDTH + BORDER_WIDTH;
        float rightBoundary = Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH - CAR_WIDTH;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) carX = Math.max(carX - 10, leftBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) carX = Math.min(carX + 10, rightBoundary);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            missiles.add(new float[]{carX + CAR_WIDTH / 2f - MISSILE_WIDTH / 2f, carY + CAR_HEIGHT});
        }
    }

    private void updateGameObjects() {
        updateLaneLines();
        updateBorders();
        updateMissiles();
    }

    private void drawLaneLines() {
        shapeRenderer.setColor(1, 1, 1, 1); // Set color to white
        for (Float laneLineY : laneLines) {
            // Draw the lane line segment
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2f - LANE_LINE_WIDTH / 2f,
                laneLineY, LANE_LINE_WIDTH, LANE_LINE_HEIGHT);
        }
    }

    private void drawBorders() {
        for (int i = 0; i < borders.size(); i++) {
            float borderY = borders.get(i);
            shapeRenderer.setColor(1, i % 2 == 0 ? 0 : 1, i % 2 == 0 ? 0 : 1, 1);
            shapeRenderer.rect(GRASS_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);
            shapeRenderer.rect(Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);
        }
    }

    private void drawMissiles() {
        for (float[] missile : missiles) {
            spriteBatch.draw(carTexture, missile[0], missile[1], MISSILE_WIDTH, MISSILE_HEIGHT);
        }
    }

    private void updateLaneLines() {
        LinkedList<Float> newLaneLines = new LinkedList<>();
        for (Float laneLineY : laneLines) {
            laneLineY -= scrollSpeed; // Move each lane line down
            if (laneLineY + LANE_LINE_HEIGHT > 0) { // Keep visible lines
                newLaneLines.add(laneLineY);
            }
        }

        // Add new lane lines to ensure consistent spacing
        while (newLaneLines.isEmpty() || newLaneLines.getLast() <= Gdx.graphics.getHeight()) {
            // Add the next line at a position to maintain equal gaps
            float newLineY = (newLaneLines.isEmpty() ? Gdx.graphics.getHeight() : newLaneLines.getLast() + LANE_LINE_HEIGHT * 2);
            newLaneLines.add(newLineY);
        }

        laneLines = newLaneLines;
    }

    private void updateBorders()
    {
        LinkedList<Float> newBorders = new LinkedList<>();
        for (Float borderY : borders) {
            borderY -= scrollSpeed;
            if (borderY > 0) newBorders.add(borderY);
        }
        borders = newBorders;

        // Add new border at the top if needed
        if (borders.isEmpty() || borders.getLast() <= Gdx.graphics.getHeight() - LANE_LINE_HEIGHT) {
            borders.add((float) Gdx.graphics.getHeight());
        }
    }

    private void updateMissiles() {
        LinkedList<float[]> updatedMissiles = new LinkedList<>();
        for (float[] missile : missiles) {
            missile[1] += MISSILE_SPEED;
            if (missile[1] <= Gdx.graphics.getHeight()) updatedMissiles.add(missile);
        }
        missiles = updatedMissiles;
    }

    @Override
    public void hide() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        carTexture.dispose();
    }
}
