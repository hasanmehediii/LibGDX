/*package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CarSelectionScreen extends com.badlogic.gdx.ScreenAdapter {

    private Stage stage;
    private SpriteBatch batch;
    private Texture[] carTextures;
    private int currentCarIndex;
    private BitmapFont font;

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Load car textures
        carTextures = new Texture[5];
        for (int i = 0; i < 5; i++) {
            carTextures[i] = new Texture("car" + (i + 1) + ".png");
        }
        currentCarIndex = 0;  // Start with the first car

        // Load font
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        // Set up the stage input listener
        Gdx.input.setInputProcessor(stage);

        // Create and position the buttons
        createButtons();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the car image
        batch.begin();
        batch.draw(carTextures[currentCarIndex], (float) Gdx.graphics.getWidth() / 2 - (float) carTextures[currentCarIndex].getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
        batch.end();

        // Draw the UI elements (buttons)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    // Create buttons for car selection and play
    private void createButtons() {
        Table table = new Table();
        table.top().center();
        table.setFillParent(true);

        // Create left arrow button to go to previous car
        TextButton leftArrowButton = new TextButton("<", Game.getButtonStyle(font));
        leftArrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentCarIndex = (currentCarIndex - 1 + carTextures.length) % carTextures.length;  // Go to previous car
            }
        });

        // Create right arrow button to go to next car
        TextButton rightArrowButton = new TextButton(">", Game.getButtonStyle(font));
        rightArrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentCarIndex = (currentCarIndex + 1) % carTextures.length;  // Go to next car
            }
        });

        // Create Play button to start the game
        TextButton playButton = new TextButton("PLAY", Game.getButtonStyle(font));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen());  // Replace with actual game screen
            }
        });

        // Add the buttons to the table
        table.row().pad(20, 0, 20, 0);
        table.add(leftArrowButton).padRight(50);
        table.add(rightArrowButton).padLeft(50);
        table.row().pad(20, 0, 20, 0);
        table.add(playButton).fillX().uniformX();

        // Add the table to the stage
        stage.addActor(table);
    }

    @Override
    public void hide() {
        // Dispose of assets when the screen is hidden
        batch.dispose();
        font.dispose();
        for (Texture carTexture : carTextures) {
            carTexture.dispose();
        }
        stage.dispose();
    }
}
*/
