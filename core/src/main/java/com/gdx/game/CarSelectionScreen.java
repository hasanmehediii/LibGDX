package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
    private Texture[] carTextures;
    private int currentCarIndex;
    private BitmapFont font;

    public CarSelectionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        carTextures = new Texture[5];
        for (int i = 0; i < 5; i++) {
            carTextures[i] = new Texture("car" + (i + 1) + ".png");
        }
        currentCarIndex = 0;

        font = new BitmapFont();
        font.getData().setScale(2.0f);

        Gdx.input.setInputProcessor(stage);

        createButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(carTextures[currentCarIndex],
            (float) Gdx.graphics.getWidth() / 2 - (float) carTextures[currentCarIndex].getWidth() / 2,
            (float) Gdx.graphics.getHeight() / 2 - (float) carTextures[currentCarIndex].getHeight() / 2);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void createButtons() {
        Table table = new Table();
        table.top().center();
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
                game.setScreen(new CarGame(carTextures[currentCarIndex])); // Pass selected texture
            }
        });

        TextButton backButton = new TextButton("BACK", getButtonStyle());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game)); // Adjust MainMenuScreen as needed
            }
        });

        table.row().pad(20, 0, 20, 0);
        table.add(backButton).fillX().uniformX();

        table.row().pad(20, 0, 20, 0);
        table.add(leftArrowButton).padRight(50);
        table.add(rightArrowButton).padLeft(50);

        table.row().pad(20, 0, 20, 0);
        table.add(playButton).fillX().uniformX();

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
        font.dispose();
        for (int i = 0; i < carTextures.length; i++) {
            if (i != currentCarIndex) {
                carTextures[i].dispose(); // Dispose only unused textures
            }
        }
        stage.dispose();
    }

}
