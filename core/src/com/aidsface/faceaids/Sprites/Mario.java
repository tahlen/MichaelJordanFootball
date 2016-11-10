package com.aidsface.faceaids.Sprites;

import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Screens.PlayScreen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, SHRINKING, DEAD };
    public State currentState;
    public State previousState;
    
    public World world;
    public Body b2body;
    
    private TextureRegion marioStand;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    
    private Animation marioRun;
    private Animation bigMarioRun;
    private Animation growMario;
    private Animation shrinkMario;
    
    private float stateTimer;
    
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean runShrinkAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    
    private PlayScreen screen;
    
    public Mario(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        
        Array<TextureRegion> frames = new Array<TextureRegion>();
        
        for(int i = 1; i < 4; i++) 
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 18));
        marioRun = new Animation(0.1f, frames);
        
        frames.clear();
        
        for(int i = 1; i < 4; i++) 
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);
        
        frames.clear();
        
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);
        
        frames.clear();
        
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        shrinkMario = new Animation(0.2f, frames);
        
        frames.clear();
        
        
        
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 18);       
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        
        
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 18);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        
        
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 18);
        
        
        defineMario();
        
        
        setBounds(0, 0, 16 / MyGdxGame.PPM, 16 / MyGdxGame.PPM);
        setRegion(marioStand);
    }
    
    public void update(float dt) {
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MyGdxGame.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        
        if (b2body.getPosition().y < 0.2 && !marioIsDead) {
            marioIsDead = true;
            die();
        }
        setRegion(getFrame(dt));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();
    }
    
    public TextureRegion getFrame (float dt) {
        currentState = getState();
        
        TextureRegion region;
        switch(currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }   
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case SHRINKING:
                region = shrinkMario.getKeyFrame(stateTimer);
                if(shrinkMario.isAnimationFinished(stateTimer)) {
                    runShrinkAnimation = false;
                    marioIsBig = false;
                    timeToRedefineMario = true;
                }
                break;
            case FALLING:
            case STANDING:

            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }
        
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }
    
    public State getState() {
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(runShrinkAnimation)
            return State.SHRINKING;
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }
    
    public void grow(){
        if( !isBig() ) {
            timeToDefineBigMario = true;
            marioIsBig = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MyGdxGame.manager.get("audio/sounds/powerup.wav", Sound.class).play(0.1f);
        }
    }
    
    public void redefineMario(){
        setBounds(getX(), getY(), getWidth(), getHeight() / 2);
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGdxGame.PPM);
        fdef.filter.categoryBits = MyGdxGame.MARIO_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT | 
                MyGdxGame.COIN_BIT | 
                MyGdxGame.BRICK_BIT |
                MyGdxGame.ENEMY_BIT |
                MyGdxGame.OBJECT_BIT |
                MyGdxGame.ENEMY_HEAD_BIT|
                MyGdxGame.ITEM_BIT;
        
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM), new Vector2(2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM));
        fdef.filter.categoryBits = MyGdxGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        
        b2body.createFixture(fdef).setUserData(this);
        
        timeToRedefineMario = false;
    }
    
    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MyGdxGame.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGdxGame.PPM);
        fdef.filter.categoryBits = MyGdxGame.MARIO_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT |
                MyGdxGame.COIN_BIT |
                MyGdxGame.BRICK_BIT |
                MyGdxGame.ENEMY_BIT |
                MyGdxGame.OBJECT_BIT |
                MyGdxGame.ENEMY_HEAD_BIT |
                MyGdxGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MyGdxGame.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM), new Vector2(2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM));
        fdef.filter.categoryBits = MyGdxGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
        runGrowAnimation = true;
    }
    
    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(288 / MyGdxGame.PPM, 32 / MyGdxGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGdxGame.PPM);
        fdef.filter.categoryBits = MyGdxGame.MARIO_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT | 
                MyGdxGame.COIN_BIT | 
                MyGdxGame.BRICK_BIT |
                MyGdxGame.ENEMY_BIT |
                MyGdxGame.OBJECT_BIT |
                MyGdxGame.ENEMY_HEAD_BIT|
                MyGdxGame.ITEM_BIT;
        
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM), new Vector2(2 / MyGdxGame.PPM, 6 / MyGdxGame.PPM));
        fdef.filter.categoryBits = MyGdxGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        
        b2body.createFixture(fdef).setUserData(this);
    }   
    
    public void die() {
            MyGdxGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play(0.1f);
            Filter filter = new Filter();
            filter.maskBits = MyGdxGame.NOTHING_BIT;
            filter.categoryBits = MyGdxGame.NOTHING_BIT;
            for(Fixture fixture : b2body.getFixtureList())
                fixture.setFilterData(filter);
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }
    
    public boolean isDead() {
        return marioIsDead;
    }
    
    public float getStateTimer() {
        return stateTimer;
    }
    
    public boolean isBig() {
        return marioIsBig;   
    }
    
    public void setDead(){
        marioIsDead=true;
    }
    
    public void hit(){ // fix invicible
        boolean invincible = false;
        if(marioIsBig&&!runShrinkAnimation){
            runShrinkAnimation = true;
            MyGdxGame.manager.get("audio/sounds/powerdown.wav", Sound.class).play(0.1f);
        } else if (runShrinkAnimation) { 
            invincible = true; 
        } else if(!invincible) {
            setDead();
            die();
        }
        invincible = false;
    }
    
    public void jump() {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING; 
    }
    
    public void sit() {
            currentState = State.JUMPING; 
    }
    
    public void draw() {
        
    }
}
