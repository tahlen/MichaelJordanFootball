package com.aidsface.faceaids.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Scenes.Hud;
import com.aidsface.faceaids.Sprites.Enemy;
import com.aidsface.faceaids.Sprites.Items.Item;
import com.aidsface.faceaids.Sprites.Items.ItemDef;
import com.aidsface.faceaids.Sprites.Items.Mushroom;
import com.aidsface.faceaids.Sprites.Mario;
import com.aidsface.faceaids.Tools.B2WorldCreator;
import com.aidsface.faceaids.Tools.WorldContactListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen{
    private MyGdxGame game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;
    
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    
    private World world;
    private Box2DDebugRenderer b2dr; 
    private B2WorldCreator creator;
    
    private Mario player;
    
    private Music music;
    
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
            
    public PlayScreen(MyGdxGame game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        
        hud = new Hud(game.batch);
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        
        gamecam = new OrthographicCamera();        
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGdxGame.PPM);
        
        world = new World(new Vector2(0, -10), true);
        creator = new B2WorldCreator(this);
        player = new Mario(this);
        world.setContactListener(new WorldContactListener());
        
        music = MyGdxGame.manager.get("audio/music/theme1.wav", Music.class);
        music.setLooping(true);
        music.setVolume(0.2f);
        music.play();
        
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }
    
    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }
    
    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
           
    }
    
    public TextureAtlas getAtlas() {
        return atlas;
    }
    
    @Override
    public void show() {
    }
    
    public void handleInput(float dt) {
        if(player.currentState != Mario.State.DEAD) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)
            		&& player.currentState != Mario.State.JUMPING
            		&& player.currentState != Mario.State.FALLING)
                player.jump();
            if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            		&& player.currentState != Mario.State.FALLING)
                player.sit();
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) 
            		&& player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT) 
            		&& player.b2body.getLinearVelocity().x >= -2) //  && player.b2body.getPosition().x>max-1.5&&player.b2body.getPosition().x<=max låser
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }
    
    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();
        
        world.step(1/60f, 6, 2);
        
        player.update(dt);
        for(Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / MyGdxGame.PPM) {
                enemy.b2body.setActive(true);
            }
        }
        
        for(Item item : items) 
            item.update(dt);
        
        hud.update(dt);        
        
        MapProperties mapProperties = map.getProperties();

        int mapWidth = mapProperties.get("width", Integer.class);
        
        if(player.currentState != Mario.State.DEAD) {
            if (player.b2body.getPosition().x >= (0.01f + (gamePort.getWorldWidth() / 2)) && player.b2body.getPosition().x <= mapWidth - (gamePort.getWorldWidth() / 2)) {
                gamecam.position.x = player.b2body.getPosition().x;
            }
        }
        
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        renderer.render();
        
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        for(Item item : items)
            item.draw(game.batch);
        game.batch.end();
        
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        //hud.stage.draw();
        
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }
    
    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD 
        		&& player.getStateTimer() > 3) {
            music.stop();
            return true;
        } else if(hud.getTime() <= 0 && player.currentState != Mario.State.DEAD) {
        	if(player.isBig()) 
        		player.redefineMario();
            music.stop();
            player.setDead();
            player.die();
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }
    
    public TiledMap getMap() {
        return map;
    }
    
    public World getWorld() {
        return world;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        hud.dispose();
    }
}
