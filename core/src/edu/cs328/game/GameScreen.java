package edu.cs328.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen{

 	final DungeonMan game;
 	private TiledMap map;
 	private OrthogonalTiledMapRenderer tiledMapRenderer;
 	private OrthographicCamera camera;
 	private Player player;
 	private int roomWidth = 16;
	private int roomHeight = 12;
	private float stateTime = 0;
	private int birthLimit = 4;
	private int deathLimit = 3;


 	public GameScreen(final DungeonMan game) {
 		this.game = game;
 		makeMap();

 		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

 		player = new Player();
 		player.position = new Vector2(5, 5);

 		camera = new OrthographicCamera();
		camera.setToOrtho(false, roomWidth, roomHeight);
        camera.update();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int x = (int)player.position.x / (roomWidth-1);
        int y = (int)player.position.y / (roomHeight -1);

		Vector3 destPos = new Vector3(x * (roomWidth -1) + (roomWidth/2), y * (roomHeight -1) + (roomHeight/2), 0);

      	camera.position.lerp(destPos, 4 * delta);
      	camera.update();

      	//dungeon.doSimulation(100, 100);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if((camera.position.x != destPos.x || camera.position.y != destPos.y) && stateTime < 1f){
        	stateTime += delta;
        	delta = 0;
        	//game.setScreen(new DungeonScreen(game, new Dungeon(500,100,16,16), this));
        }
        else{
        	stateTime = 0;
        	camera.position.x  = destPos.x;
        	camera.position.y = destPos.y;
        }

		Batch batch = tiledMapRenderer.getBatch();
        batch.begin();
        player.update(delta);
        player.render(batch, delta, map);
        batch.end();
	}

	/* Make the map and place the trees based on the game of life */
	private void makeMap(){
		map = new TiledMap();
 		MapLayers layers = map.getLayers();
 		TiledMapTileLayer layer = new TiledMapTileLayer(50 * roomWidth,50 * roomHeight,16,16);
 		Texture groundTexture = new Texture(Gdx.files.internal("ground.png"));
 		Cell cell = new Cell();
 		TiledMapTileLayer trees = new TiledMapTileLayer(50 * roomWidth,50 * roomHeight,16,16);
 		Texture treeTexture = new Texture(Gdx.files.internal("tree.png"));
 		Cell treeCell = new Cell();
		treeCell.setTile(new StaticTiledMapTile(new TextureRegion(treeTexture)));
		cell.setTile(new StaticTiledMapTile(new TextureRegion(groundTexture)));

		/* Make the background and place trees */
		for(int x = 0; x < 50 * roomWidth; x++){
			for(int y = 0; y < 50 * roomHeight; y++){
				layer.setCell(x,y,cell);
				if(Math.random() < .4)
					trees.setCell(x,y,treeCell);
			}
		}

		layers.add(layer);

		for(int i = 0; i < 3; i++){
			trees = doSimulation(50 * roomWidth, 50 * roomHeight, trees, treeCell);
		}

		trees.setName("walls");
		layers.add(trees);
	}

	/* Play the game of life */
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
	
	@Override
	public void dispose () {
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show(){
	}

	public void resize(int width, int height) {
		// change the stage's viewport when teh screen size is changed
	}
 
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}