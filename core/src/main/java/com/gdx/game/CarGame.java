package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.LinkedList;

public class CarGame extends ApplicationAdapter {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Texture carTexture;

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

    private float roadWidth;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        carTexture = new Texture("car.png");

        carX = Gdx.graphics.getWidth() / 2f - CAR_WIDTH / 2f;
        carY = 100;

        scrollSpeed = 5;

        roadWidth = Gdx.graphics.getWidth() - 2 * (GRASS_WIDTH + BORDER_WIDTH);

        laneLines = new LinkedList<>();
        borders = new LinkedList<>();
        missiles = new LinkedList<>();

        for (int i = 0; i < Gdx.graphics.getHeight() / LANE_LINE_HEIGHT + 1; i++) {
            laneLines.add((float) (i * LANE_LINE_HEIGHT));
            borders.add((float) (i * LANE_LINE_HEIGHT));
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.6f, 0.3f, 1); // Grass green color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(); // Handle car movement and firing

        updateGameObjects();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw grass areas
        shapeRenderer.setColor(0.3f, 0.6f, 0.3f, 1); // Grass green
        shapeRenderer.rect(0, 0, GRASS_WIDTH, Gdx.graphics.getHeight());
        shapeRenderer.rect(Gdx.graphics.getWidth() - GRASS_WIDTH, 0, GRASS_WIDTH, Gdx.graphics.getHeight());

        // Draw road
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1); // Dark gray
        shapeRenderer.rect(GRASS_WIDTH + BORDER_WIDTH, 0, roadWidth, Gdx.graphics.getHeight());

        // Draw borders with alternating red and white colors
        for (int i = 0; i < borders.size(); i++) {
            Float borderY = borders.get(i);
            boolean isRed = i % 2 == 0; // Alternate color based on index

            shapeRenderer.setColor(1f, isRed ? 0f : 1f, isRed ? 0f : 1f, 1); // Red or White

            // Left border
            shapeRenderer.rect(GRASS_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);

            // Right border
            shapeRenderer.rect(Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);
        }

        // Draw lane lines
        shapeRenderer.setColor(1f, 1f, 1f, 1); // White
        for (Float laneLineY : laneLines) {
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2f - LANE_LINE_WIDTH / 2f, laneLineY, LANE_LINE_WIDTH, LANE_LINE_HEIGHT);
        }

        // Draw missiles
        shapeRenderer.setColor(1f, 0f, 0f, 1); // Red
        for (float[] missile : missiles) {
            shapeRenderer.rect(missile[0], missile[1], MISSILE_WIDTH, MISSILE_HEIGHT);
        }

        shapeRenderer.end();

        // Draw car
        spriteBatch.begin();
        spriteBatch.draw(carTexture, carX, carY, CAR_WIDTH, CAR_HEIGHT);
        spriteBatch.end();
    }

    private void handleInput() {
        // Horizontal boundaries
        float leftBoundary = GRASS_WIDTH + BORDER_WIDTH;
        float rightBoundary = Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH - CAR_WIDTH;

        // Vertical boundaries
        float bottomBoundary = 0; // The bottom of the screen
        float topBoundary = Gdx.graphics.getHeight() - CAR_HEIGHT; // The top of the screen minus car height

        // Check for arrow key input and adjust car position
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                carX = Math.max(carX - 20, leftBoundary);
            }
            else{
                carX = Math.max(carX - 10, leftBoundary);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                carX = Math.min(carX + 20, rightBoundary);
            }
            else{
                carX = Math.min(carX + 10, rightBoundary);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                carY = Math.min(carY + 20, topBoundary);
            }
            else{
                carY = Math.min(carY + 10, topBoundary);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                carY = Math.max(carY - 20, bottomBoundary);
            }
            else{
                carY = Math.max(carY - 10, bottomBoundary);
            }
        }

        // Fire missile with Enter key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            missiles.add(new float[]{carX + CAR_WIDTH / 2f - MISSILE_WIDTH / 2f, carY + CAR_HEIGHT});
        }
    }

    private void updateGameObjects() {
        LinkedList<Float> newLaneLines = new LinkedList<>();
        for (Float laneLineY : laneLines) {
            laneLineY -= scrollSpeed;
            if (laneLineY + LANE_LINE_HEIGHT > 0) {
                newLaneLines.add(laneLineY);
            }
        }
        if (laneLines.isEmpty() || laneLines.getLast() < Gdx.graphics.getHeight() - LANE_LINE_HEIGHT) {
            newLaneLines.add((float) Gdx.graphics.getHeight());
        }
        laneLines = newLaneLines;

        LinkedList<Float> newBorders = new LinkedList<>();
        for (Float borderY : borders) {
            borderY -= scrollSpeed;
            if (borderY + LANE_LINE_HEIGHT > 0) {
                newBorders.add(borderY);
            }
        }

        // Ensure new borders are added with alternating colors
        if (borders.isEmpty() || borders.getLast() < Gdx.graphics.getHeight() - LANE_LINE_HEIGHT) {
            newBorders.add((float) Gdx.graphics.getHeight());
        }
        borders = newBorders;

        // Update missiles
        LinkedList<float[]> newMissiles = new LinkedList<>();
        for (float[] missile : missiles) {
            missile[1] += MISSILE_SPEED; // Move missile upward
            if (missile[1] <= Gdx.graphics.getHeight()) {
                newMissiles.add(missile); // Keep missile if it's still on-screen
            }
        }
        missiles = newMissiles;

        // Recalculate road width if window size changes
        roadWidth = Gdx.graphics.getWidth() - 2 * (GRASS_WIDTH + BORDER_WIDTH);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        carTexture.dispose();
    }
}
