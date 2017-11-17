package edu.cs328.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.lang.Math;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;



public class Room {
	public int x1,y1,x2,y2, width, height;
	public Vector2 center;
	public Room(int x, int y, int w, int h){
		x1 = x;
		x2 = x + w;
		y1 = y;
		y2 = y + y1;
		width  = w;
		height = h;
		center = new Vector2((int)((x1 + x2)/2), (int)((y1 + y2)/2));
	}

	public boolean intersects(Room room){
		return (x1 <= room.x2 && x2 >= room.x2 && y1 <= room.y1 && y2 >= room.y2);
	}
	
}