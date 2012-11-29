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
package com.lyeeedar.Roguelike3D.Game.Level;

import com.badlogic.gdx.graphics.Color;

public class AbstractObject {
	
	public enum ObjectType {
		DOOR_UNLOCKED,
		DOOR_LOCKED,
		STATIC,
		SPAWNER_0,
		SPAWNER_1,
		SPAWNER_2,
		SPAWNER_3,
		SPAWNER_4,
		SPAWNER_5,
		SPAWNER_6,
		SPAWNER_7,
		SPAWNER_8,
		SPAWNER_9,
		STAIR_UP,
		STAIR_DOWN
	}
	
	public final char character;
	public final ObjectType type;
	public final boolean visible;
	public final String description;
	
	public String modelType;
	public String modelName;
	
	public float[] modelDimensions;
	
	public String texture;

	public Color colour;
	
	public AbstractObject(char character, String type, boolean visible, String description)
	{
		this.character = character;
		this.type = stringToObjectType(type);
		this.visible = visible;
		this.description = description;
	}
	
	float x; float y; float z;
	public AbstractObject(char character, ObjectType type, boolean visible, String description, float x, float y, float z)
	{
		this.character = character;
		this.type = type;
		this.visible = visible;
		this.description = description;
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	private ObjectType stringToObjectType(String type)
	{
		ObjectType otype = null;
		
		if (type.equalsIgnoreCase("STATIC"))
		{
			otype = ObjectType.STATIC;
		}
		else if (type.equalsIgnoreCase("DOOR_UNLOCKED"))
		{
			otype = ObjectType.DOOR_UNLOCKED;
		}
		else if (type.equalsIgnoreCase("DOOR_LOCKED"))
		{
			otype = ObjectType.DOOR_LOCKED;
		}
		else if (type.equalsIgnoreCase("SPAWNER_0"))
		{
			otype = ObjectType.SPAWNER_0;
		}
		else if (type.equalsIgnoreCase("SPAWNER_1"))
		{
			otype = ObjectType.SPAWNER_1;
		}
		else if (type.equalsIgnoreCase("SPAWNER_2"))
		{
			otype = ObjectType.SPAWNER_2;
		}
		else if (type.equalsIgnoreCase("SPAWNER_3"))
		{
			otype = ObjectType.SPAWNER_3;
		}
		else if (type.equalsIgnoreCase("SPAWNER_4"))
		{
			otype = ObjectType.SPAWNER_4;
		}
		else if (type.equalsIgnoreCase("SPAWNER_5"))
		{
			otype = ObjectType.SPAWNER_5;
		}
		else if (type.equalsIgnoreCase("SPAWNER_6"))
		{
			otype = ObjectType.SPAWNER_6;
		}
		else if (type.equalsIgnoreCase("SPAWNER_7"))
		{
			otype = ObjectType.SPAWNER_7;
		}
		else if (type.equalsIgnoreCase("SPAWNER_8"))
		{
			otype = ObjectType.SPAWNER_8;
		}
		else if (type.equalsIgnoreCase("SPAWNER_9"))
		{
			otype = ObjectType.SPAWNER_9;
		}
		else if (type.equalsIgnoreCase("STAIR_UP"))
		{
			otype = ObjectType.STAIR_UP;
		}
		else if (type.equalsIgnoreCase("STAIR_DOWN"))
		{
			otype = ObjectType.STAIR_DOWN;
		}
		
		return otype;
	}
	
	public AbstractObject(char character, ObjectType type, boolean visible, String description)
	{
		this.character = character;
		this.type = type;
		this.visible = visible;
		this.description = description;
	}
	
	public void setModel(String modelType, String modelName, String texture, Color colour, float[] modelDimensions)
	{
		this.modelDimensions = modelDimensions;
		this.modelType = modelType;
		this.modelName = modelName;
		this.texture = texture;
		this.colour = colour;
	}
}
