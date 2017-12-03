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

public class Knife extends Projectile{
    private TextureRegion upAttack;
    private TextureRegion leftAttack;
    private TextureRegion rightAttack;
    private TextureRegion downAttack;
    private Texture spriteSheet;
    private float stateTime = 0f;

    public Knife(float x, float y, Facing facing){
        dmg = 3;
        speed = 5f;
        this.facing = facing;
        position.set(x,y);

        spriteSheet = new Texture(Gdx.files.internal("knife.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 9, 9);

        upAttack = tmp[0][0];
        rightAttack = tmp[1][0];
        leftAttack = tmp[0][1];
        downAttack = tmp[1][1];

        switch(facing){
            case UP:
                sprite = new Sprite(upAttack);
                movement.set(0, speed);
                break;
            case LEFT:
                sprite = new Sprite(leftAttack);
                movement.set(-speed, 0);
                break;
            case DOWN:
                sprite = new Sprite(downAttack);
                movement.set(0, -speed);
                break;
            case RIGHT:
                sprite = new Sprite(rightAttack);
                movement.set(speed, 0);
                break;
            default:
                break;
        }

        width = height = 9f/16f;

        position.set(x - width/2, y - height/2);
    }
}