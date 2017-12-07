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

public class MagicBall extends Projectile{
    private Texture spriteSheet;
    private float stateTime = 0f;
    private Animation<TextureRegion> animation;

    public MagicBall(float x, float y, Facing facing){
        this.facing = facing;
        dmg = 3;
        speed = 5f;
        blockAble = false;

        spriteSheet = new Texture(Gdx.files.internal("magicBall.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 8, 8);
        TextureRegion [] frames = new TextureRegion[8];

        for(int i = 0; i < 8; i++){
            frames[i] = tmp[0][i];
        }

        animation = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(frames), PlayMode.LOOP);

        width = height = 8/16f;

        sprite = new Sprite(frames[0]);
        width = height = 8/16f;

        position.set(x - width/2, y - height/2);

        switch(this.facing){
            case LEFT:
                movement.set(-1, 0);
                break;
            case RIGHT:
                movement.set(1, 0);
                break;
            case UP:
                movement.set(0, 1);
                break;
            case DOWN:
                movement.set(0, -1);
                break;
            default:
                break;
        }
    }

    @Override
    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        sprite.setRegion(animation.getKeyFrame(stateTime, true));
        if(position.x + this.width > x + width || position.x < x || position.y + this.height > y + height  || position.y < y){
            atWall = true;
        }
    }
}