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

public class DungeonScreen implements Screen{

    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private Dungeon dungeon;
    private Player player;
    private Array<Enemy> enemies = new Array<Enemy>();
    private Array<Item> items = new Array<Item>();
    private Vector2 position;
    private MiniMap miniMap;
    private float stateTime = 0;
    final DungeonMan game;
    private int roomWidth = 16;
    private int roomHeight = 12;
    private GameScreen gameScreen;
    private Lancer boss;
    private ShapeRenderer shapeRenderer;

    public DungeonScreen(final DungeonMan game, Dungeon dungeon, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.dungeon = dungeon;
        map = dungeon.map;
        shapeRenderer = new ShapeRenderer();
        miniMap = new MiniMap(map);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
        player = new Player();
        player.position = new Vector2(dungeon.spawn.x, dungeon.spawn.y);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, roomWidth, roomHeight);
        camera.update();
    }
    

    @Override
    public void render (float delta) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");

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
        shapeRenderer.setProjectionMatrix(camera.combined);

       if((camera.position.x != destPos.x || camera.position.y != destPos.y) && stateTime < 1f){

            /* If we just started the screen transition,
            * place enemies 
            */
            if(stateTime <= 0f){
                TiledMapTileLayer enemyLayer = (TiledMapTileLayer)map.getLayers().get("enemies");
                TiledMapTileLayer bossLayer = (TiledMapTileLayer)map.getLayers().get("boss");
                enemies.clear();
                items.clear();
                for(int i = (int)destPos.x - roomWidth/2; i < (int)destPos.x + roomWidth/2; i++){
                    for(int j = (int)destPos.y - roomHeight/2; j < (int)destPos.y + roomHeight/2; j++){
                        Cell enemyCell = enemyLayer.getCell(i, j);
                        if(enemyCell != null){
                            String enemyType = enemyCell.getTile().getProperties().get("type", String.class);
                            
                            if(enemyType.equals("lancer"))
                                enemies.add(new Lancer(i,j));
                            else
                                enemies.add(new Whelp(i,j));
                        }
                        enemyCell = bossLayer.getCell(i, j);
                        if(enemyCell != null){
                            boss = new Lancer(i - 1, j - 1);
                            boss.width = boss.height = 2;
                            boss.hp = 30;
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
            if(player.bounds.overlaps(item.bounds) && player.hp < player.maxHp){
                player.hp++;
                items.removeValue(item, true);
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
                player.takeHit(1);
                if(player.hp < 0){
                     game.setScreen(new LoseScreen(game));
                }
            }
            if(enemy.hp <= 0){
                if(Math.random() < .5){
                    items.add(new Item(enemy.position.x, enemy.position.y));
                }
                enemies.removeValue(enemy, true);
            }
        }

        if(boss != null){
            boss.update(delta, x * (roomWidth - 1), y * (roomHeight - 1), roomWidth - 1, roomHeight - 1);
            boss.render(batch, delta, map);
            if(player.projectile != null && boss.bounds.overlaps(player.projectile.bounds)){
                boss.takeHit(player.projectile.dmg);
                player.projectile = null;
            }
            if(player.state == Unit.State.ATTACK){
                if(boss.bounds.overlaps(player.getAttackBox())){
                    boss.takeHit(player.attackDmg);
                    player.stateTime = .15f;
                }
            }
            else if(boss.bounds.overlaps(player.bounds) && player.state == Unit.State.WALKING){
                player.takeHit(1);
            }
            if(boss.hp <= 0){
                boss = null;
                game.setScreen(new WinScreen(game));
            }
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

        batch.end();

        /* Minimap */
        shapeRenderer.begin(ShapeType.Filled);

        shapeRenderer.setColor(.75f, .75f, .75f, 1);
        float miniMapX = camera.position.x - roomWidth/2; 
        float miniMapY = camera.position.y + roomWidth/2 - 3;
        shapeRenderer.rect(miniMapX, miniMapY, 1, 1);
        shapeRenderer.setColor(1f, 1f, 1f, 1);
        shapeRenderer.rect((miniMapX/(roomWidth-1))/20 + miniMapX, (miniMapY/(roomHeight-1))/20 + miniMapY, 1/20f, 1/20f);
        shapeRenderer.setColor(1f, 0f, 0f, 1);
        shapeRenderer.rect(((dungeon.bossLoc.x - (roomWidth/2))/(roomWidth-1))/20 + miniMapX, ((dungeon.bossLoc.y - ((roomHeight -  1)/2))/(roomHeight-1))/20 + miniMapY, 1/20f, 1/20f);
        
        shapeRenderer.end();
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