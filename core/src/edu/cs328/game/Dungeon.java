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

	public Dungeon(int width, int height, int tileWidth, int tileHeight){
		map = new TiledMap();
		layers = map.getLayers();
		
		makeWorld(width, height, tileWidth, tileHeight);		
	}

	private void makeWorld(int width, int height, int tileWidth, int tileHeight){
		Texture wallTexture = new Texture(Gdx.files.internal("wall.png"));
		TiledMapTileLayer walls = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		Cell cell = new Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(wallTexture)));
		for(int x = 0; x < width; x++){
			walls.setCell(x, 0, cell);
			walls.setCell(x, height - 1, cell);
			for(int y = 0; y < height - 1; y++){
				walls.setCell(0, y, cell);
				walls.setCell(width, y, cell);
				if(Math.random() < changeToLive){
					walls.setCell(x, y, cell);
				}
			}
		}
		for(int i = 0; i < 4; i++){
			walls = doSimulation(width, height, walls, cell);
		}
		walls.setName("walls");
		layers.add(walls);
		placeTreasure(width, height, walls);
		getSpawn(width, height, walls);
	}

	public TiledMapTileLayer doSimulation(int width, int height, TiledMapTileLayer layer, Cell cell){
		TiledMapTileLayer newLayer = new TiledMapTileLayer(width, height, 16, 16);
		for(int x = 0; x<width; x++){
        	for(int y = 0; y < height; y++){
            	int nbs = countAliveNeighbours(x, y, width, height, layer);
            	//The new value is based on our simulation rules
            	//First, if a cell is alive but has too few neighbours, kill it.
            	Cell neighbour = layer.getCell(x,y);
            	if(neighbour != null){
                	if(nbs > deathLimit){
                    	newLayer.setCell(x, y, cell);
                	}
            	} //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
            	else{
                	if(nbs > birthLimit){
                    	newLayer.setCell(x, y, cell);
                	}
            	}
        	}
    	}
        return newLayer;
	}

	public int countAliveNeighbours(int x, int y, int width, int height, TiledMapTileLayer countLayer){
    	int count = 0;
    	for(int i=-1; i<2; i++){
        	for(int j=-1; j<2; j++){
            	int neighbour_x = x+i;
            	int neighbour_y = y+j;
            	Cell neighbour = countLayer.getCell(neighbour_x, neighbour_y);
            	//If we're looking at the middle point
            	if(i == 0 && j == 0){
                	//Do nothing, we don't want to add ourselves in!
            	}
            	//In case the index we're looking at it off the edge of the map
            	else if(neighbour_x < 1 || neighbour_y < 1 || neighbour_x >= width - 1 || neighbour_y >= height - 1){
                	count = count + 1;
            	}
            	//Otherwise, a normal check of the neighbour
            	else if(neighbour != null){
                	count = count + 1;
            	}
        	}
    	}
    	return count;
	}

	private void placeTreasure(int width, int height, TiledMapTileLayer wallLayer){
		Texture treasureTexture = new Texture(Gdx.files.internal("treasure.png"));
		TiledMapTileLayer treasure = new TiledMapTileLayer(width, height, 16, 16);
		Cell cell = new Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(treasureTexture)));

		int treasureHiddenLimit = 5;
    	for (int x=0; x < width; x++){
        	for (int y=0; y < height; y++){
        		Cell neighbour = wallLayer.getCell(x,y);
            	if(neighbour == null){
                	int nbs = countAliveNeighbours(x, y, width, height, wallLayer);
                	if(nbs >= treasureHiddenLimit){
                    	treasure.setCell(x, y, cell);
                	}
            	}
        	}
    	}
    	treasure.setName("treasure");
    	layers.add(treasure);
	}

	public boolean hitTreasure(int x, int y, Rectangle hit){
		TiledMapTileLayer layer = (TiledMapTileLayer)layers.get("treasure");
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 3; j++){
				Rectangle tile = new Rectangle(x + i, y + j,1,1);
				Cell cell = layer.getCell(x + i, y + j);
				if(tile.overlaps(hit) && cell != null){
					layer.setCell(x + i, y + j,null);
					return true;
				}
			}
		}
		return false;
	}

	private void getSpawn(int width, int height, TiledMapTileLayer layer){
		int x = width/2;
		int y = height/2;
		Cell spawnLoc = layer.getCell(x, y);
		int mod = 1;
		while(spawnLoc != null){
			if(Math.random() < .5){
				mod = -1;
			}
			if(Math.random() < .5){
				x += mod;
			}else{
				y += mod;
			}
			spawnLoc = layer.getCell(x, y);
		}
		spawn.set(x,y);
	}
}
