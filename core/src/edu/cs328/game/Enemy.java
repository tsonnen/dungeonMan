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

public class Enemy extends Unit{
	private int awareness;
	public Enemy(){
		speed = .1f;
		texture = new Texture(Gdx.files.internal("devil.png"));
		movement = new Vector2();
		awareness = 0;
	}

	public void setAwareness(int awareness){
		this.awareness = awareness;
	}

	public void update(float x, float y, TiledMapTileLayer layer){
		int dist = (int)Math.sqrt((double)(Math.pow((x - this.position.x),2) + Math.pow((y - position.y),2)));
		if(dist <= awareness){
			movement.x = x - position.x;
			movement.y = y - position.y;
		}
		else{
			movement.x = (float)(Math.random() * 10f);
			movement.y = (float)(Math.random() * 10f);
			if(Math.random() < .5){
				movement.x = movement.x * -1;
			}
			if(Math.random() < .5){
				movement.y = movement.y * -1;
			}
			System.out.print(movement.x);
		}
	}
}