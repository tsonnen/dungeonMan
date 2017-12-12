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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;

public class GameScreen implements Screen{

    final DungeonMan game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    public Player player;
    private int roomWidth = 16;
    private int roomHeight = 12;
    private float stateTime = 0;
    private int birthLimit = 4;
    private int deathLimit = 3;
    private MapLayers layers;
    private Array<Enemy> enemies = new Array<Enemy>();
    private Array<Item> items = new Array<Item>();
    private Array<Rectangle> dungeonEntrances = new Array<Rectangle>();
    private Sound collect;
    private Vector2 spawnLoc = new Vector2();
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();

    public GameScreen(final DungeonMan game) {
        this.game = game;
        if(this.game.music != null)
            this.game.music.dispose();

        collect = Gdx.audio.newSound(Gdx.files.internal("collect.mp3"));
        map = new TiledMap();
        layers = map.getLayers();
        makeMap();

        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

        player = new Player();

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
        int x = 5;
        int y = 5;
        Cell cell = layer.getCell(x,y);
        while(cell != null){
            x++;
            y++;
            cell = layer.getCell(x,y);
        }



        player.position = new Vector2(x, y);
        Vector3 destPos = new Vector3((int)player.position.x / (roomWidth - 1) * (roomWidth - 1) + (roomWidth/2), (int)player.position.y / (roomHeight - 1) * (roomHeight - 1) + (roomHeight/2), 0);

        TiledMapTileLayer dungeonLayer = (TiledMapTileLayer)map.getLayers().get("entrance");
        for(int i = (int)destPos.x - roomWidth/2; i < (int)destPos.x + roomWidth/2; i++){
            for(int j = (int)destPos.y - roomHeight/2; j < (int)destPos.y + roomHeight/2; j++){
                Cell entranceCell = dungeonLayer.getCell(i, j);
                if(entranceCell != null){
                    dungeonEntrances.add(new Rectangle(i,j,1,1));
                }
            }
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, roomWidth, roomHeight);
        camera.position.x = destPos.x;
        camera.position.y = destPos.y;
        camera.update();
    }

    @Override
    public void render (float delta) {
        if(game.gameState == DungeonMan.State.PLAYING){
            playRender(delta);
        }else{
            pauseRender();
        }
        
    }
    
    private void playRender(float delta){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");

        /* Target camera location */
        int x = (int)player.position.x / (roomWidth - 1);
        int y = (int)player.position.y / (roomHeight - 1);

        Vector3 destPos = new Vector3(x * (roomWidth - 1) + (roomWidth/2), y * (roomHeight - 1) + (roomHeight/2), 0);

        camera.position.lerp(destPos, 4 * delta);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if((camera.position.x != destPos.x || camera.position.y != destPos.y) && stateTime < 1f){

            /* If we just started the screen transition,
            * place enemies 
            */
            if(stateTime <= 0f){
                TiledMapTileLayer enemyLayer = (TiledMapTileLayer)map.getLayers().get("enemies");
                TiledMapTileLayer dungeonLayer = (TiledMapTileLayer)map.getLayers().get("entrance");
                enemies.clear();
                dungeonEntrances.clear();
                items.clear();
                for(int i = (int)destPos.x - roomWidth/2; i < (int)destPos.x + roomWidth/2; i++){
                    for(int j = (int)destPos.y - roomHeight/2; j < (int)destPos.y + roomHeight/2; j++){
                        Cell enemyCell = enemyLayer.getCell(i, j);
                        Cell entranceCell = dungeonLayer.getCell(i, j);
                        if(enemyCell != null){
                            String enemyType = enemyCell.getTile().getProperties().get("type", String.class);
                            
                            if(enemyType.equals("lancer"))
                                enemies.add(new Lancer(i,j));
                            else if(enemyType.equals("whelp"))
                                enemies.add(new Whelp(i,j));
                             else if(enemyType.equals("kultist"))
                                enemies.add(new Kultist(i,j));
                        }
                        if(entranceCell != null){
                            dungeonEntrances.add(new Rectangle(i,j,1,1));
                        }
                    }
                }

            }
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

        /* Draw items and entrances before (under) Player and enemies */
        for(Item item : items){
            item.render(batch);
            boolean collected = false;
            if(player.bounds.overlaps(item.bounds)){
                if(item instanceof Heart && player.hp < player.maxHp){
                    player.hp++;
                    items.removeValue(item, true);
                    collected = true;
                }else if(item instanceof CollectableKnife){
                    player.numKnife++;
                    items.removeValue(item, true);
                    collected = true;
                }
                if(collected)
                    collect.play();
            }
        }
        
        for(Rectangle entrance : dungeonEntrances){
            if(entrance.overlaps(player.bounds)){
                game.setScreen(new DungeonScreen(game, new Dungeon(20 * roomWidth,20 * roomHeight,16,16), this));
                ((TiledMapTileLayer)map.getLayers().get("entrance")).setCell((int)entrance.x, (int)entrance.y, null); // Remove an entrance once it is used
                dungeonEntrances.removeValue(entrance, true);
            }
        }

        player.update(delta, x * (roomWidth - 1), y * (roomHeight - 1), roomWidth - 1, roomHeight - 1);
        player.render(batch, delta, map);

        /* Draw enemies 'over' player */
        for(Enemy enemy : enemies){
            enemy.update(delta, x * (roomWidth - 1), y * (roomHeight - 1), roomWidth - 1, roomHeight - 1);
            enemy.render(batch, delta, map);
            if(player.projectile != null && enemy.bounds.overlaps(player.projectile.bounds)){
                enemy.takeHit(player.projectile.dmg);
                player.projectile = null;
            }

            if(enemy.projectile != null && player.bounds.overlaps(enemy.projectile.bounds)){
                if(enemy.projectile.blockAble)
                    player.takeHit(enemy.projectile.dmg, enemy.projectile.facing);
                else
                    player.takeHit(enemy.projectile.dmg);
                enemy.projectile = null;
            }

            if(player.state == Unit.State.ATTACK){
                if(enemy.bounds.overlaps(player.getAttackBox())){
                    enemy.takeHit(player.attackDmg);
                    player.stateTime = .15f;
                }
            }
            else if(enemy.bounds.overlaps(player.bounds) && player.state == Unit.State.WALKING){
                player.takeHit(enemy.attackDmg);
            }
            if(enemy.hp <= 0){
                double seed = Math.random();
                if(seed < .45){
                    items.add(new Heart(enemy.position.x, enemy.position.y));
                }else if(seed < .9){
                    items.add(new CollectableKnife(enemy.position.x, enemy.position.y));
                }
                /* Delete enemy once they are killed */
                ((TiledMapTileLayer)map.getLayers().get("enemies")).setCell((int)enemy.id.x, (int)enemy.id.y, null);
                enemies.removeValue(enemy, true);
            }
        }

        if(player.hp <= 0){
            player.Die();
            game.setScreen(new LoseScreen(game, this));
        }

        /* Draw hearts over everything */
        Texture hearts = new Texture(Gdx.files.internal("hearts.png"));
        for(int i = 0; i < player.maxHp; i++){
            if(i < player.hp){
                batch.draw(new TextureRegion(hearts, 0, 0, 8, 8), camera.position.x + ((roomWidth)/2 - .5f) - i * .6f, camera.position.y + ((roomHeight)/2 - .5f), .5f, .5f);
            }
            else{
                batch.draw(new TextureRegion(hearts, 0, 8, 8, 8), camera.position.x + ((roomWidth)/2 - .5f) - i * .6f, camera.position.y + ((roomHeight)/2 - .5f), .5f, .5f); 
            }
        }
        /* Draw hearts over everything */
        Texture knife = new Texture(Gdx.files.internal("knife.png"));
        for(int i = 0; i < player.numKnife; i++){
            batch.draw(new TextureRegion(knife, 0, 0, 8, 8), camera.position.x + ((roomWidth)/2 - .5f) - i * .3f, camera.position.y + ((roomHeight)/2 - 1f), .5f, .5f);
        }

        batch.end();
    }

    private void pauseRender(){

    }

    /* Make the map and place the trees based on the game of life */
    private void makeMap(){
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

        placeEnemy(50 * roomWidth, 50 * roomHeight, trees);
        placeEntrance(50 * roomWidth, 50 * roomHeight, trees);
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

    /* Generate enemies using the game of life */
    private void placeEnemy(int width, int height, TiledMapTileLayer wallLayer){
        Texture texture = new Texture(Gdx.files.internal("treasure.png"));
        TiledMapTileLayer enemies = new TiledMapTileLayer(width, height, 16, 16);

        Cell lancerCell = new Cell();
        lancerCell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));
        lancerCell.getTile().getProperties().put("type", "lancer");

        Cell whelpCell = new Cell();
        whelpCell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));
        whelpCell.getTile().getProperties().put("type", "whelp");

        Cell kultistCell = new Cell();
        kultistCell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));
        kultistCell.getTile().getProperties().put("type", "kultist");


        /* Make the background and place trees */
        for(int x = 0; x < 50 * roomWidth; x++){
            for(int y = 0; y < 50 * roomHeight; y++){
                if(wallLayer.getCell(x,y) == null){
                    if(Math.random() < .05){
                        double seed = Math.random();
                        if(seed < .33){
                            enemies.setCell(x,y,lancerCell);
                        }
                        else if(seed < .66){
                            enemies.setCell(x,y,whelpCell);
                        }
                        else{
                            enemies.setCell(x,y, kultistCell);
                        }
                    }
                }
            }
        }

        enemies.setVisible(false);
        enemies.setName("enemies");
        layers.add(enemies);
    }


    private void placeEntrance(int width, int height, TiledMapTileLayer wallLayer){
        Texture texture = new Texture(Gdx.files.internal("entrance.png"));
        TiledMapTileLayer entrance = new TiledMapTileLayer(width, height, 16, 16);
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));

        int entranceHiddenLimit = 5;
        for (int x=0; x < width; x++){
            for (int y=0; y < height; y++){
                Cell neighbour = wallLayer.getCell(x,y);
                if(neighbour == null){
                    int nbs = countAliveNeighbours(x, y, 50 * roomWidth, 50 * roomHeight, wallLayer);
                    if(nbs >= entranceHiddenLimit){
                        entrance.setCell(x, y, cell);
                    }
                }
            }
        }
        entrance.setName("entrance");
        layers.add(entrance);
    }
    
    public void dispose () {
        for(Enemy enemy : enemies){
            enemy.dispose();
        }
        map.dispose();
        player.dispose();
        collect.dispose();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void show(){
        this.game.music.dispose();
        this.game.music = Gdx.audio.newMusic(Gdx.files.internal("overWorldMusic.mp3"));
        this.game.music.setLooping(true);
        this.game.music.play();
        inputMultiplexer.addProcessor(player);
        inputMultiplexer.addProcessor(game);
        Gdx.input.setInputProcessor(inputMultiplexer);
        player.hp = player.maxHp;
        player.numKnife = 10;
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
        int x = 5;
        int y = 5;
        Cell cell = layer.getCell(x,y);
        while(cell != null){
            x++;
            y++;
            cell = layer.getCell(x,y);
        }

        player.position = new Vector2(x, y);
        Vector3 destPos = new Vector3((int)player.position.x / (roomWidth - 1) * (roomWidth - 1) + (roomWidth/2), (int)player.position.y / (roomHeight - 1) * (roomHeight - 1) + (roomHeight/2), 0);
        player.movement.set(0,0);
        player.facing = Unit.Facing.UP;

        game.dungeonsCleared++;
        if(game.dungeonsCleared >= 5){
            game.setScreen(new StoryScreen(game, this));
        }
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

    public Player getPlayer(){
        return player;
    }
}