package edu.cs328.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    final DungeonMan game;
    private Stage stage;

    public MainMenuScreen(final DungeonMan game) {
        this.game = game;
        if(this.game.music != null)
            this.game.music.dispose();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show(){
        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);        
        // temporary until we have asset manager in
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));        
        //create buttons
        TextButton newGame  = new TextButton("New Game", skin);
        TextButton help     = new TextButton("Help", skin);
        TextButton exit     = new TextButton("Exit", skin);        
        //add buttons to table
        table.add(newGame).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(help).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();
        
        // create button listeners
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();             
            }
        });
        help.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HelpScreen(game));       
            }
        });
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               game.setScreen(new GameScreen(game));
               /* For testing dungeons without having to find them */
               //game.setScreen(new DungeonScreen(game, new Dungeon(500,100,16,16), new GameScreen(game)));       
            }
        });
        
    }
 
    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
 
    @Override
    public void resize(int width, int height) {
        // change the stage's viewport when teh screen size is changed
        stage.getViewport().update(width, height, true);
    }
 
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void dispose() {
        // dispose of assets when not needed anymore
        stage.dispose();
    }
}