/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Materials.GlowAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.CircularTrail;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.Particle;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class Player extends GameActor {
	
	public GameObject lookedAtObject;

	public final Vector3 offsetPos = new Vector3();
	public final Vector3 offsetRot = new Vector3();
	
	public Player(String model, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(model, colour, texture, x, y, z, scale);
		visible = false;
		description = "This is you. Wave to yourself you!";
		WEIGHT = 1;
	}
	
	float cooldown = 0;

	float move = 0;
	float xR = 0;
	float yR = 0;

	float headBob = 0;
	
	boolean jumpCD = false;
	@Override
	public void update(float delta) {	
		
		headBob += delta*15;
		
		cooldown -= delta;
		
		move = delta * 10;
		
		velocity.y -= GameData.gravity*move*WEIGHT;

		if (grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) left_right(move);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) left_right(-move);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) forward_backward(move*2);
			else headBob = 0;
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) forward_backward(-move/2);

			if ((grounded) && (Gdx.input.isKeyPressed(Keys.SPACE) && !jumpCD)) 
			{
				jumpCD = true;
				grounded = false;
				velocity.y += 0.4;
			}
			else if (!Gdx.input.isKeyPressed(Keys.SPACE))
			{
				jumpCD = false;
			}
		}
		else
		{
			headBob = 0;
		}
		offsetPos.y = (float) Math.sin(headBob)/5;
		
		if (Gdx.input.isKeyPressed(Keys.B) && cooldown < 0)
		{			
			MotionTrail trail = new CircularTrail(new Vector3(0, -4, 0), new Vector3(180, -180, 0), new Vector3(0, 0, 180), 10, 15, this, 60, 0.5f);
			motionTrails.add(trail);
			
			cooldown = 1;
		}
		
		applyMovement();

		xR = (float)Gdx.input.getDeltaX()*xrotate*move;
		yR = (float)Gdx.input.getDeltaY()*yrotate*move;
		
		if (xR < -5.0f) xR = -5.0f;
		else if (xR > 5.0f) xR = 5.0f;
		
		if (yR < -3.0f) yR = -3.0f;
		else if (yR > 3.0f) yR = 3.0f;
		
		Yrotate(yR);

		Xrotate(xR);
		
	}
	
	

}
