package com.aidsface.faceaids.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Sprites.Mario;
import com.aidsface.faceaids.Sprites.Mario.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;


public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;
    
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private static Integer coins;
    
    Label marioLabel;
    Label emptyLabel;
    Label worldLabel;
    Label timeLabel;
    
    Texture coinTexture;
    static Label coinLabel;
    
    static Label scoreLabel;
    Label levelLabel;
    Label countdownLabel;
    
    public Hud(SpriteBatch sb){
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        score = 0;
        coins = 0;
        worldTimer = 120;
        timeCount = 0;
        
        viewport = new FitViewport(MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        
        Table table1 = new Table();
        table1.top();
        table1.setFillParent(true);
        
        marioLabel = new Label("SCORE", skin, "default-font", Color.WHITE);
        emptyLabel = new Label("COINS", skin, "default-font", Color.WHITE);
        worldLabel = new Label("WORLD", skin, "default-font", Color.WHITE);
        timeLabel = new Label("TIME", skin, "default-font", Color.WHITE);
        
        scoreLabel = new Label(String.format("%06d", score), skin, "default-font", Color.WHITE);
        levelLabel = new Label("1-1", skin, "default-font", Color.WHITE);
        countdownLabel = new Label(String.format("%03d", worldTimer), skin, "default-font", Color.WHITE);
        
        Table table2 = new Table();
        //coinTexture = new Texture(Gdx.files.internal("coin.png"));
        coinLabel = new Label(String.format("%02d", coins), skin, "default-font", Color.WHITE);
        
        //table2.add(new Image(coinTexture));
        table2.add(coinLabel);
        
        table1.add(marioLabel).expandX().padTop(10);
        table1.add(emptyLabel).expandX().padTop(10);
        table1.add(worldLabel).expandX().padTop(10);
        table1.add(timeLabel).expandX().padTop(10);
        table1.row();
        table1.add(scoreLabel).expandX().padTop(4);        
        table1.add(table2).expandX().padTop(4);
        table1.add(levelLabel).expandX().padTop(4);
        table1.add(countdownLabel).expandX().padTop(4);
        
        stage.addActor(table1);       
    }

    public static Integer getScore() {
        return score;
    }
    
    public void update(float dt){
        timeCount += dt;
        if(worldTimer<1) {
            countdownLabel.setText(String.format("%03d", worldTimer));
        } else if(timeCount >=  1){
            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }
    
    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }
    
    public static void addCoins(int value){
        coins += value;
        coinLabel.setText(String.format("%02d", coins));
    }
    
    public Integer getTime() {
        return worldTimer;
    }
    
        
    @Override
    public void dispose() {
        stage.dispose();
    }
}
