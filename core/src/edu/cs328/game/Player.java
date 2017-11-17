package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import java.lang.Math;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

public class Player extends Unit{
	public Player(){
		speed = 3f;
		texture = new Texture(Gdx.files.internal("ground.png"));
		movement = new Vector2();
	}

	public void update(TiledMapTileLayer layer){
        if(Gdx.input.isKeyPressed(Keys.A)){
        	movement.x = -speed;
        }else if(Gdx.input.isKeyPressed(Keys.D)){
        	movement.x = speed;
        }else{
        	movement.x = 0;
        }

        if(Gdx.input.isKeyPressed(Keys.S)){
        	movement.y = -speed;
        }else if(Gdx.input.isKeyPressed(Keys.W)){
        	movement.y = speed;
        }else{
        	movement.y = 0;
        }
	}

	public void recalcMove(TiledMapTileLayer layer, float delta){
		
	}
}