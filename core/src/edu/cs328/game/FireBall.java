package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class FireBall extends Projectile{
    private Texture spriteSheet;
    private float stateTime = 0f;
    private Animation<TextureRegion> animation;

    public FireBall(){
        dmg = 1;
        speed = 5f;
        sprite = new Sprite();

        spriteSheet = new Texture(Gdx.files.internal("fireBall.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 8, 8);
        TextureRegion [] frames = new TextureRegion[5];

        for(int i = 0; i < 5; i++){
            frames[i] = tmp[0][i];
        }

        animation = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(frames), PlayMode.LOOP);

        sprite.setRegion(frames[0]);
    }

    @Override
    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        sprite.setRegion(animation.getKeyFrame(stateTime, true));
        if(position.x > x + width || position.x < x || position.y > y + height  || position.y < y){
            inAir = false;
        }
    }
}