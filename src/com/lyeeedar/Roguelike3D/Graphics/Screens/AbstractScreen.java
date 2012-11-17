package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.awt.Font;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ArrayMap;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
 

public abstract class AbstractScreen implements Screen{
	
	int screen_width;
	int screen_height;

	protected final Roguelike3DGame game;

	protected final SpriteBatch spriteBatch;
	protected BitmapFont font;
	protected final Stage stage;

	protected PrototypeRendererGL20 protoRenderer;
	
	PerspectiveCamera cam;
	
	FPSLogger fps = new FPSLogger();

	public AbstractScreen(Roguelike3DGame game)
	{
		this.game = game;
		
		font = new BitmapFont(Gdx.files.internal("data/skins/default.fnt"), false);
		spriteBatch = new SpriteBatch();
		stage = new Stage(0, 0, true, spriteBatch);
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);

		draw(delta);

		update(delta);

		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		stage.draw();
		
        fps.log();
		
	}

	@Override
	public void resize(int width, int height) {
		screen_width = width;
		screen_height = height;

		float aspectRatio = (float) width / (float) height;
        //cam = new PerspectiveCamera(90, 2f * aspectRatio, 2f);
        cam = new PerspectiveCamera(90, width, height);
        cam.near = 0.01f;
        cam.far = 200;
        protoRenderer.cam = cam;
		
		stage.setViewport( width, height, true );
	}

	@Override
	public void dispose() {
		protoRenderer.dispose();

		spriteBatch.dispose();
		font.dispose();
		stage.dispose();

	}
	
	@Override
	public void show()
	{
		
	}
	
	public abstract void create();
	public abstract void draw(float delta);
	public abstract void update(float delta);

}
