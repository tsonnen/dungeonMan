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

        layers.add(walls);

        placeEnemy(50 * roomWidth, 50 * roomHeight, walls);

        spawn.set(roomWidth/2,roomHeight/2);
    }

    private TiledMapTileLayer makeRoom(int startX, int startY, TiledMapTileLayer layer){
        Texture wallTexture = new Texture(Gdx.files.internal("wall.png"));
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(new TextureRegion(wallTexture)));
        Cell current= new Cell();
        boolean left, right, up, down;
        // See if there is already a wall on each side
        left    = layer.getCell(startX, startY + 1) == null;
        right   = layer.getCell(startX + (roomWidth-1), startY + 1) == null;
        up      = layer.getCell(startX + 1, startY + (roomHeight-1)) == null;
        down    = layer.getCell(startX + 1, startY) == null;
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

    private void placeEnemy(int width, int height, TiledMapTileLayer wallLayer){
        Texture texture = new Texture(Gdx.files.internal("treasure.png"));
        TiledMapTileLayer enemies = new TiledMapTileLayer(width, height, 16, 16);
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));


        /* Make the background and place trees */
        for(int x = 0; x < 50 * roomWidth; x++){
            for(int y = 0; y < 50 * roomHeight; y++){
                if(wallLayer.getCell(x,y) == null){
                    if(Math.random() < .4)
                        enemies.setCell(x,y,cell);
                }
            }
        }

        for(int i = 0; i < 3; i++){
            TiledMapTileLayer newLayer = new TiledMapTileLayer(width, height, 16, 16);
            for(int x = 0; x<width; x++){
                for(int y = 0; y < height; y++){
                    if(wallLayer.getCell(x,y) == null){
                        int nbs = countAliveNeighbours(x, y, width, height, enemies) +  countAliveNeighbours(x, y, width, height, wallLayer);
                        //The new value is based on our simulation rules
                        //First, if a cell is alive but has too few neighbours, kill it.
                        Cell neighbour = enemies.getCell(x,y);
                        if(neighbour != null){
                            if(nbs > 6){
                                newLayer.setCell(x, y, cell);
                            }
                        } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
                        else{
                            if(nbs > 7){
                                newLayer.setCell(x, y, cell);
                            }
                        }
                    }
                }
            }
            enemies = newLayer;
        }

        enemies.setVisible(false);
        enemies.setName("enemies");
        layers.add(enemies);
    }

    /* Count the number of alive neighbors */
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
}
