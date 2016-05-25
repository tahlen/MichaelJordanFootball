/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aidsface.faceaids.Sprites;

import com.aidsface.faceaids.MyGdxGame;
import com.aidsface.faceaids.Scenes.Hud;
import com.aidsface.faceaids.Screens.PlayScreen;
import com.aidsface.faceaids.Sprites.Items.ItemDef;
import com.aidsface.faceaids.Sprites.Items.Mushroom;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author xyCz
 */
public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MyGdxGame.COIN_BIT);
    }
    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN)
            MyGdxGame.manager.get("audio/sounds/bump.wav", Sound.class).play(0.1f);
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MyGdxGame.PPM), Mushroom.class));
                MyGdxGame.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play(0.1f);
            }
            else {
                Hud.addCoins(1);
                MyGdxGame.manager.get("audio/sounds/coin.wav", Sound.class).play(0.1f);
            }
            getCell().setTile(tileSet.getTile(BLANK_COIN));
        }
    }
}
