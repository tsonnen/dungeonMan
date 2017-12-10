package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import java.lang.Math;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Exit extends Unit{
    public Projectile projectile = null;
    public Vector2 id = new Vector2();

    public Exit(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        Texture texture = new Texture(Gdx.files.internal("exit.png"));
        sprite = new Sprite(texture);
        movement = new Vector2();
        height = width = 1f;
        bounds = new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void dispose(){
        if(hitSound != null)
            hitSound.dispose();
        if(projectile != null)
            projectile.dispose();
    }
}