/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aidsface.faceaids.Sprites;

import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Scenes.Hud;
import com.aidsface.faceaids.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;

/**
 *
 * @author xyCz
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MyGdxGame.BRICK_BIT);
    }
    
    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MyGdxGame.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MyGdxGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play(0.1f);
        } else
            MyGdxGame.manager.get("audio/sounds/bump.wav", Sound.class).play(0.1f);
    }
}
