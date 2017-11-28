package edu.cs328.game;

import java.lang.Math;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import java.lang.Math;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class Dungeon {
	public TiledMap map;
	private MapLayers layers;
	public Vector2 spawn = new Vector2();
	private float changeToLive = .45f;
	private int birthLimit = 4;
	private int deathLimit = 3;
	private int roomWidth = 16;
	private int roomHeight = 12;

	public Dungeon(int width, int height, int tileWidth, int tileHeight){
		map = new TiledMap();
		layers = map.getLayers();
		
		makeWorld(width, height, tileWidth, tileHeight);		
	}

	private void makeWorld(int width, int height, int tileWidth, int tileHeight){
		TiledMapTileLayer walls = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		int x = 0;
		int y = 0;
		walls = makeRoom(0,0,walls);
		for(int i = 0; i < 50; i++){
			double seed = Math.random();
			if(seed < .25 && x > 0){
				walls = makeRoom(x-(roomWidth-1),y, walls);
				walls.setCell(x, y + roomHeight/2, null);
				walls.setCell(x, y + roomHeight/2 - 1, null);
				x -= (roomWidth-1);
			}else if(seed < .5 && x < width){
				walls = makeRoom(x + (roomWidth-1),y, walls);
				walls.setCell(x + (roomWidth-1), y + roomHeight/2, null);
				walls.setCell(x + (roomWidth-1), y + roomHeight/2 + 1, null);
				x += (roomWidth-1);
			}else if(seed < .75 && y > 0){
				walls = makeRoom(x,y-(roomHeight-1), walls);
				walls.setCell(x + roomWidth/2, y, null);
				walls.setCell(x + roomWidth/2 - 1, y, null);
				y -= (roomHeight-1);
			}else if(y < height){
				walls = makeRoom(x,y + (roomHeight-1), walls);
				walls.setCell(x + roomWidth/2, y + (roomHeight-1), null);
				walls.setCell(x + roomWidth/2 - 1, y + (roomHeight-1), null);
				y += (roomHeight-1);
			}
		}
		walls.setName("walls");
		
		TiledMapTileLayer enemies = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		Texture wallTexture = new Texture(Gdx.files.internal("wall.png"));
		Cell cell = new Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(wallTexture)));
		enemies.setVisible(false);
		enemies.setName("enemies");
		enemies.setCell(x + 4, y + 4, cell);
		layers.add(enemies);
		layers.add(walls);

		spawn.set(roomWidth/2,roomHeight/2);
	}

	private TiledMapTileLayer makeRoom(int startX, int startY, TiledMapTileLayer layer){
		Texture wallTexture = new Texture(Gdx.files.internal("wall.png"));
		Cell cell = new Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(wallTexture)));
		Cell current= new Cell();
		boolean left, right, up, down;
		// See if there is already a wall on each side
		left 	= layer.getCell(startX, startY + 1) == null;
		right 	= layer.getCell(startX + (roomWidth-1), startY + 1) == null;
		up 		= layer.getCell(startX + 1, startY + (roomHeight-1)) == null;
		down 	= layer.getCell(startX + 1, startY) == null;
		if(up){
			for(int x = 0; x < roomWidth; x++){
				layer.setCell(x + startX, startY + (roomHeight-1), cell);
			}
		}
		if(down){
			for(int x = 0; x < roomWidth; x++){
				layer.setCell(x + startX, startY, cell);
			}
		}
		if(left){
			for(int y = 0; y < roomHeight; y++){
				layer.setCell(startX, startY + y, cell);
			}
		}
		if(right){
			for(int y = 0; y < roomHeight; y++){
				layer.setCell(startX + (roomWidth-1), startY + y, cell);
			}
		}
		return layer;
	}
}
