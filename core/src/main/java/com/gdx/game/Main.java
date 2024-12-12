package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Array<int[]> snake;
    private int[] foodPosition;
    private int direction;
    private float timer;
    private boolean gameOver;

    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

        snake = new Array<>();
        snake.add(new int[]{WIDTH / 2, HEIGHT / 2});
        spawnFood();
        direction = Input.Keys.RIGHT;
        timer = 0;
        gameOver = false;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.76f, 0.69f, 0.50f, 1); // Light brown for soil background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver) {
            spriteBatch.begin();
            font.draw(spriteBatch, "Game Over", WIDTH / 2f - 60, HEIGHT / 2f + 10);
            spriteBatch.end();
            return;
        }

        handleInput();
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= 0.2f) {
            updateSnake();
            checkCollision();
            timer = 0;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw snake
        for (int i = 0; i < snake.size; i++) {
            int[] segment = snake.get(i);
            shapeRenderer.setColor(0, 1, 0, 1); // Blue for the head
            shapeRenderer.rect(segment[0], segment[1], TILE_SIZE, TILE_SIZE);// Green for the body
            if (i == 0) {
                // Head of the snake
                // Draw eyes
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.circle(segment[0] + 5, segment[1] + 15, 3); // Left eye
                shapeRenderer.circle(segment[0] + 15, segment[1] + 15, 3); // Right eye
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.circle(segment[0] + 5, segment[1] + 15, 1); // Left pupil
                shapeRenderer.circle(segment[0] + 15, segment[1] + 15, 1); // Right pupil
            } else {
                // Body of the snake
                // Draw cross pattern on body
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.line(segment[0], segment[1], segment[0] + TILE_SIZE, segment[1] + TILE_SIZE); // Diagonal line 1
                shapeRenderer.line(segment[0] + TILE_SIZE, segment[1], segment[0], segment[1] + TILE_SIZE); // Diagonal line 2
            }
        }

        // Draw food
        shapeRenderer.setColor(1, 0, 0, 1); // Red for food
        shapeRenderer.rect(foodPosition[0], foodPosition[1], TILE_SIZE, TILE_SIZE);

        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && direction != Input.Keys.DOWN) {
            direction = Input.Keys.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && direction != Input.Keys.UP) {
            direction = Input.Keys.DOWN;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && direction != Input.Keys.RIGHT) {
            direction = Input.Keys.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && direction != Input.Keys.LEFT) {
            direction = Input.Keys.RIGHT;
        }
    }

    private void updateSnake() {
        int[] head = snake.first();
        int[] newHead = new int[]{head[0], head[1]};

        switch (direction) {
            case Input.Keys.UP:
                newHead[1] += TILE_SIZE;
                break;
            case Input.Keys.DOWN:
                newHead[1] -= TILE_SIZE;
                break;
            case Input.Keys.LEFT:
                newHead[0] -= TILE_SIZE;
                break;
            case Input.Keys.RIGHT:
                newHead[0] += TILE_SIZE;
                break;
        }

        snake.insert(0, newHead);
        if (newHead[0] == foodPosition[0] && newHead[1] == foodPosition[1]) {
            spawnFood();
        } else {
            snake.removeIndex(snake.size - 1);
        }
    }

    private void spawnFood() {
        Random random = new Random();
        boolean validPosition = false;

        while (!validPosition) {
            int x = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
            int y = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;

            // Check if the position overlaps with the snake
            validPosition = true; // Assume valid until proven otherwise
            for (int[] segment : snake) {
                if (segment[0] == x && segment[1] == y) {
                    validPosition = false; // Overlaps, so try again
                    break;
                }
            }

            if (validPosition) {
                foodPosition = new int[]{x, y}; // Assign valid position to food
            }
        }
    }


    private void checkCollision() {
        int[] head = snake.first();

        // Check wall collision
        if (head[0] < 0 || head[1] < 0 || head[0] >= WIDTH || head[1] >= HEIGHT) {
            gameOver = true;
        }

        // Check self-collision
        for (int i = 1; i < snake.size; i++) {
            if (head[0] == snake.get(i)[0] && head[1] == snake.get(i)[1]) {
                gameOver = true;
                break;
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
}
