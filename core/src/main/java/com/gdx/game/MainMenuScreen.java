package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends com.badlogic.gdx.ScreenAdapter {

    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Music backgroundMusic; // Add a Music object

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Load the background image
        backgroundTexture = new Texture("back.jpeg");

        // Get the default font with increased size
        font = getDefaultFontWithIncreasedSize();

        // Create a ShapeRenderer for custom drawing
        shapeRenderer = new ShapeRenderer();

        // Set up the stage input listener
        Gdx.input.setInputProcessor(stage);

        // Create and position the buttons
        createButtons();

        // Load and configure the background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        backgroundMusic.setLooping(true); // Set the music to loop
        backgroundMusic.setVolume(0.5f); // Adjust volume (0.0 to 1.0)
        backgroundMusic.play(); // Start the music
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw the UI elements (buttons)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private BitmapFont getDefaultFontWithIncreasedSize() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(4.0f);
        return font;
    }

    private TextButton.TextButtonStyle getButtonStyle(BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        return style;
    }

    private void createButtons() {
        Table table = new Table();
        table.top().center();
        table.setFillParent(true);

        TextButton startButton = new TextButton("START", getButtonStyle(font));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop(); // Stop music when transitioning to another screen
                game.setScreen(new CarSelectionScreen(game));
            }
        });

        TextButton exitButton = new TextButton("EXIT", getButtonStyle(font));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop(); // Stop music when exiting the game
                Gdx.app.exit();
            }
        });

        table.row().pad(20, 0, 20, 0);
        table.add(startButton).fillX().uniformX();
        table.row().pad(20, 0, 20, 0);
        table.add(exitButton).fillX().uniformX();

        stage.addActor(table);
    }

    @Override
    public void hide() {
        batch.dispose();
        backgroundTexture.dispose();
        font.dispose();
        shapeRenderer.dispose();
        stage.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.dispose(); // Dispose of music to free resources
        }
    }
}
