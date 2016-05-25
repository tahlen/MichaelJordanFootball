package com.aidsface.faceaids.Screens;

import com.aidsface.faceaids.MyGdxGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {
    private final Viewport viewport;
    private final Stage stage;
    
    TextButton button1;
    TextButton button2;
    Skin skin;
    TextButtonStyle textButtonStyle;
    TextureAtlas buttonAtlas;
    
    private final SpriteBatch batch;
    private final Texture Background;
    private final Sprite sprite;
    
    private Music music;
    
    int select = 0;
    int max = 2;
    Texture cursor = new Texture(Gdx.files.internal("cursor.png"));
    final MyGdxGame game;
    
	public MenuScreen(MyGdxGame g){
            game = g;
            batch = new SpriteBatch();
            viewport = new FitViewport(MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT, new OrthographicCamera());
            stage = new Stage(viewport, ((MyGdxGame) g).batch);
            
            Gdx.input.setInputProcessor(stage);
            
            skin = new Skin(Gdx.files.internal("uiskin.json"));    

            /*buttonAtlas = new TextureAtlas("buttons.pack");  
            
            skin.addRegions(buttonAtlas);
            
            textButtonStyle = new TextButtonStyle();
            textButtonStyle.font = font;
            textButtonStyle.up = skin.getDrawable("newUp");
            textButtonStyle.down = skin.getDrawable("newDown");
            textButtonStyle.checked = skin.getDrawable("newChk");
            
            
            button1 = new TextButton("Button1", textButtonStyle);
            button2 = new TextButton("Button2", textButtonStyle);*/
            
            Background = new Texture(Gdx.files.internal("titlebg.png"));
            sprite = new Sprite(Background);
            sprite.setSize(640, 480);
            
                Table table1 = new Table();
                Label newGame = new Label("New Game", skin, "default");
                Label loadGame = new Label("Load Game", skin, "default");
                Label settings = new Label("Settings", skin, "default");
                table1.center();
                table1.bottom();
                table1.setFillParent(true);
                table1.add(newGame).expandX();
                table1.row();
                table1.add(loadGame).expandX();
                table1.row();
                table1.add(settings).expandX().padBottom(40f);
               
                
                Table table2 = new Table();
                Label copyright = new Label("Copyright 2016 Conatus Entertainment", skin, "default");
                table2.bottom();
                table2.setFillParent(true);
                table2.add(copyright).expandX().padBottom(4f);
                
                stage.addActor(table1);
                stage.addActor(table2);
                
            music = MyGdxGame.manager.get("BGM/prelude.mp3", Music.class);
            music.setLooping(true);
            music.setVolume(0.4f);
            music.play();
	}

	public void render (float delta) {
                handleInput(delta);
                Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);    
                batch.begin();
                sprite.draw(batch);
                if(select==0){batch.draw(cursor, 224, 208);}
                if(select==1){batch.draw(cursor, 224, 160);}
                if(select==2){batch.draw(cursor, 224, 112);}
                batch.end();  
                stage.draw();
	}        
        
        public void handleInput(float dt) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                MyGdxGame.manager.get("audio/sounds/select3.wav", Sound.class).play(0.1f);
                if(select<max)select++;
                else if(select>=max)select=0;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                MyGdxGame.manager.get("audio/sounds/select3.wav", Sound.class).play(0.1f);
                if(select>0)select--;
                else if(select==0)select=max;
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)&&select==0) {
                music.stop();
                MyGdxGame.manager.get("audio/sounds/menuenter.wav", Sound.class).play(0.1f);
                dispose();
                game.setScreen(new PlayScreen(game));
            }
        }

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void dispose () {
            batch.dispose();
            Background.dispose();
            stage.dispose();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}