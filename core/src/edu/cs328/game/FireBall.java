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

    public FireBall(float x, float y, Facing facing){
        dmg = 3;
        speed = 5f;

        spriteSheet = new Texture(Gdx.files.internal("fireBall.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 8, 8);
        TextureRegion [] frames = new TextureRegion[5];

        for(int i = 0; i < 5; i++){
            frames[i] = tmp[0][i];
        }

        animation = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(frames), PlayMode.LOOP);

        width = height = 8/16f;

        sprite = new Sprite(frames[0]);
        width = height = 8/16f;

        position.set(x - width/2, y - height/2);

        switch(facing){
            case LEFT:
                movement.set(-1, 0);
                break;
            case RIGHT:
                movement.set(1, 0);
                break;
            default:
                break;
        }
    }

    public void update(float delta){
        stateTime += delta;
        sprite.setRegion(animation.getKeyFrame(stateTime, true));
    }
}