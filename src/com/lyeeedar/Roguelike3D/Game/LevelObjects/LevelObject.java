/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject.ObjectType;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public abstract class LevelObject extends GameObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4217344798671622221L;
	public static final transient String MONSTER_TYPE = "monster_type";
	public static final transient String LEVEL = "level";
	
	public final AbstractObject ao;
	
	public LevelObject(boolean visible, float x, float y, float z, AbstractObject ao)
	{
		this(ao, new Colour(1.0f, 1.0f, 1.0f, 1.0f), "blank", x, y, z, ao.modelScale, GL20.GL_TRIANGLES, "cube", "1", "1", "1");
		this.visible = visible;
	}

	public LevelObject(AbstractObject ao, Colour colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model) {
		super(colour, texture, x, y, z, scale, primitive_type, model);
		this.ao = ao;
	}

	public static LevelObject checkObject(AbstractObject ao, float x, float y, float z, Level level, MonsterEvolver evolver)
	{
		LevelObject lo = null;
		
		if (ao.type == ObjectType.STATIC)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Static(ao, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Static(ao, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Static(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao);
				
			}
		}
		else if (ao.type == ObjectType.DOOR_UNLOCKED)
		{
			lo = new Door(ao, new Colour(), "tex+", (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, 1.0f, GL20.GL_TRIANGLES, "cube", "1", "1", "1");
		}
		else if (ao.type == ObjectType.FIRE_CAMP)
		{

			lo = new Static(false, x, y, z, ao);
			lo.shortDesc = ao.shortDesc;
			lo.longDesc = ao.longDesc;
			
			ParticleEmitter p = new ParticleEmitter(2.5f, 2, 2.5f, 1.5f, 1.5f, 1.5f, 0.005f, 350);
			p.setTexture("texf", new Vector3(0.0f, 3.5f, 0.0f), 1.6f, new Colour(0.7f, 0.9f, 0.3f, 1.0f), new Colour(1.0f, 0.0f, 0.0f, 1.0f), true, 0.05f, 1.0f, true, true);
			
			level.particleEmitters.add(p);
			lo.addParticleEmitter(p);
		}
		else if (ao.type == ObjectType.FIRE_TORCH)
		{

			lo = new Static(false, x, y, z, ao);
			lo.shortDesc = ao.shortDesc;
			lo.longDesc = ao.longDesc;
			
			ParticleEmitter p = new ParticleEmitter(x-0.3f, y+1.5f, z-0.3f, 1, 1, 1, 0.01f, 50);
			p.setTexture("texf", new Vector3(0.0f, 2.0f, 0.0f), 0.5f, new Colour(0.7f, 0.9f, 0.3f, 1.0f), new Colour(1.0f, 0.0f, 0.0f, 1.0f), true, 0.5f, 1.5f, false, true);
			
			level.particleEmitters.add(p);
			lo.addParticleEmitter(p);
		}
		else if (ao.type == ObjectType.STAIR_UP)
		{
			String texture = ao.texture;
			Colour colour = ao.colour;
			if (ao.modelType.equalsIgnoreCase("model"))
			{
				lo = new Stair(ao, GameData.getCurrentLevelContainer().getUpLevel(), colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
			}
			else if (ao.modelType.equalsIgnoreCase("cube"))
			{
				lo = new Stair(ao, GameData.getCurrentLevelContainer().getUpLevel(), colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
						"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
			}
		}
		else if (ao.type == ObjectType.STAIR_DOWN)
		{
			String texture = ao.texture;
			Colour colour = ao.colour;
			if (ao.modelType.equalsIgnoreCase("model"))
			{
				lo = new Stair(ao, GameData.getCurrentLevelContainer().getDownLevel(), colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
			}
			else if (ao.modelType.equalsIgnoreCase("cube"))
			{
				lo = new Stair(ao, GameData.getCurrentLevelContainer().getDownLevel(), colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
						"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
			}
		}
		else if (ao.type == ObjectType.PLAYER_PLACER)
		{
			lo = new PlayerPlacer(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao);
		}
		else if (ao.type == ObjectType.SPAWNER_0)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 0, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 0, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 0, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_1)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 1, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 1, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 1, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_2)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 2, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 2, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 2, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_3)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 3, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 3, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 3, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_4)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 4, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 4, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 4, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_5)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 5, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 5, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 5, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_6)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 6, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 6, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 6, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_7)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 7, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 7, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 7, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_8)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 8, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 8, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 8, evolver);
			}
		}
		else if (ao.type == ObjectType.SPAWNER_9)
		{
			if (ao.visible)
			{
				String texture = ao.texture;
				Colour colour = ao.colour;
				if (ao.modelType.equalsIgnoreCase("file"))
				{
					lo = new Spawner(ao, 9, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES, "file", ao.modelName);
				}
				else if (ao.modelType.equalsIgnoreCase("cube"))
				{
					lo = new Spawner(ao, 9, evolver, colour, texture, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao.modelScale, GL20.GL_TRIANGLES,
							"cube", ""+ao.modelDimensions[0], ""+ao.modelDimensions[1], ""+ao.modelDimensions[2]);
				}
			}
			else
			{
				lo = new Spawner(false, (ao.x)*GameData.BLOCK_SIZE, 0, (ao.z)*GameData.BLOCK_SIZE, ao, 9, evolver);
			}
		}
		
		return lo;
	}
	
	public void created(){
		
	}
}
