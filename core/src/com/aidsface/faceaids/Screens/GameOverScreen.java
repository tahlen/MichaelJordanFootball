package com.aidsface.faceaids.Screens;

import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Scenes.Hud;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen implements Screen{
    private Viewport viewport;
    private Stage stage;
    
    private Game game;
    
    public GameOverScreen(Game game){
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.game = game;
        viewport = new FitViewport(MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MyGdxGame) game).batch);
        
        
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        
        Label gameOverLabel = new Label("GAME OVER", skin, "default-font", Color.WHITE);
        Label scoreLabel = new Label(String.format("YOU SCORED %d POINTS", Hud.getScore()), skin, "default-font", Color.WHITE);
        Label playAgainLabel = new Label("PRESS ANY KEY TO RESTART", skin, "default-font", Color.WHITE);
        
        table.add(gameOverLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX().padTop(16f);
        table.row();
        table.add(playAgainLabel).expandX().padTop(16f);
        
        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            // game.setScreen(new PlayScreen((MyGdxGame) game));
        	game.setScreen(new MenuScreen((MyGdxGame) game));
            dispose();
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    
    
}
