package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CarSelectionScreen extends com.badlogic.gdx.ScreenAdapter {

    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture enemy1;
    //private Texture back;
    private Texture enemy2;
    private Texture leftCrowdTexture;
    private Texture rightCrowdTexture;// Background texture
    private Texture[] carTextures;
    private int currentCarIndex;
    private BitmapFont font;
    private Music backgroundMusic;

    public CarSelectionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        backgroundTexture = new Texture("stage.png");
        leftCrowdTexture = new Texture("leftcrowd.png");// Load the background texture
        rightCrowdTexture = new Texture("rightcrowd .png");
        enemy1 = new Texture("blue_enemy.png");
        enemy2 = new Texture("yellow_enemy.png");
        //back = new Texture("background.jpg");


        carTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            carTextures[i] = new Texture("car" + (i + 1) + ".png");
        }
        currentCarIndex = 0;

        font = new BitmapFont();
        font.getData().setScale(2.0f);

        Gdx.input.setInputProcessor(stage);

        createButtons();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        backgroundMusic.setLooping(true); // Set the music to loop
        backgroundMusic.setVolume(0.5f); // Adjust volume (0.0 to 1.0)
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Draw the background image first
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the current car texture
        batch.draw(carTextures[currentCarIndex],
            (float) Gdx.graphics.getWidth() / 2 - (float) carTextures[currentCarIndex].getWidth() / 2,
            (float) Gdx.graphics.getHeight() / 2 - (float) carTextures[currentCarIndex].getHeight() / 2);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void createButtons() {
        Table table = new Table();
        table.setFillParent(true);

        TextButton leftArrowButton = new TextButton("<", getButtonStyle());
        leftArrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                currentCarIndex = (currentCarIndex - 1 + carTextures.length) % carTextures.length;
            }
        });

        TextButton rightArrowButton = new TextButton(">", getButtonStyle());
        rightArrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                currentCarIndex = (currentCarIndex + 1) % carTextures.length;
            }
        });

        TextButton playButton = new TextButton("PLAY", getButtonStyle());
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // Assuming you have references to the left and right crowd textures in CarSelectionScreen
                backgroundMusic.stop();
                game.setScreen(new CarGame(carTextures[currentCarIndex], leftCrowdTexture, rightCrowdTexture, enemy1, enemy2));
                // Pass selected texture
            }
        });

        TextButton backButton = new TextButton("BACK", getButtonStyle());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                backgroundMusic.stop();
                game.setScreen(new MainMenuScreen(game)); // Adjust MainMenuScreen as needed
            }
        });

        // Positioning "Play" and "Back" buttons
        table.row().padTop(100); // More space at the top
        table.add(backButton).fillX().uniformX().colspan(3);

        // Positioning arrow buttons
        table.row().padTop(200); // Adjust vertical spacing for the arrows
        table.add(leftArrowButton).padRight(200); // Move left arrow further left
        table.add().expandX(); // Add empty space between arrows
        table.add(rightArrowButton).padLeft(200); // Move right arrow further right

        // Positioning "Play" button
        table.row().padTop(200); // More space below arrows
        table.add(playButton).fillX().uniformX().colspan(3);

        stage.addActor(table);
    }

    private TextButton.TextButtonStyle getButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        return style;
    }

    @Override
    public void hide() {
        batch.dispose();
        backgroundTexture.dispose(); // Dispose of the background texture
        font.dispose();
        for (int i = 0; i < carTextures.length; i++) {
            if (i != currentCarIndex) {
                carTextures[i].dispose(); // Dispose only unused textures
            }
        }
        stage.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose(); // Dispose of music to free resources
        }
    }
}
