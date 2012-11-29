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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.CollisionBox;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public class Level {
	
	Tile[][] levelArray;
	
	HashMap<Character, String> descriptions = new HashMap<Character, String>();
	HashMap<Character, Color> colours = new HashMap<Character, Color>();
	ArrayList<Character> opaques = new ArrayList<Character>();
	ArrayList<Character> solids = new ArrayList<Character>();
	
	public ArrayList<GameActor> actors = new ArrayList<GameActor>();
	public ArrayList<VisibleItem> items = new ArrayList<VisibleItem>();
	public ArrayList<Spell> spells = new ArrayList<Spell>();
	public ArrayList<LevelObject> levelObjects = new ArrayList<LevelObject>();
	
	public ArrayList<DungeonRoom> rooms;
	
	public int width;
	public int height;
	
	public Level(int width, int height, GeneratorType gtype, BiomeReader biome)
	{
		this.width = width;
		this.height = height;
		
		solids.add('#');
		solids.add(' ');
		
		opaques.add('#');
		opaques.add(' ');
		
		colours.put('#', biome.getWallColour());
		colours.put('.', biome.getFloorColour());
		colours.put(' ', biome.getWallColour());
		
		MapGenerator generator = new MapGenerator(width, height, solids, opaques, colours, gtype, biome);
		levelArray = generator.getLevel();
		rooms = generator.getRooms();
	}
	
	int fillRoomIndex = 0;
	public boolean fillRoom(RoomReader rReader)
	{
		if (fillRoomIndex == rooms.size())
		{
			return true;
		}
		
		DungeonRoom room = rooms.get(fillRoomIndex);
		AbstractRoom aroom = rReader.getRoom(room.roomtype, room.width, room.height);
		
		if (aroom == null) {
			fillRoomIndex++;
			System.out.println("Failed to place "+room.roomtype);
			return false;
		}
		System.out.println("Placed "+room.roomtype);
		
		for (int i = 0; i < aroom.width; i++)
		{
			for (int j = 0; j < aroom.height; j++)
			{
				if (aroom.contents[i][j] == '#')
				{
					levelArray[room.x+i][room.y+j].character = '#';
					levelArray[room.x+i][room.y+j].height = levelArray[room.x+i][room.y+j].roof;
				}
				else
				{
					levelArray[room.x+i][room.y+j].character = '.';
					levelArray[room.x+i][room.y+j].height = levelArray[room.x+i][room.y+j].floor;
					
					AbstractObject ao = aroom.objects.get(aroom.contents[i][j]);
					
					if (ao == null) continue;
					
					System.out.println(aroom.contents[i][j]);
					
					if (ao.visible)
					{
						String texture = ao.texture;
						Color colour = ao.colour;
						if (ao.modelType.equalsIgnoreCase("model"))
						{
							LevelObject lo = new LevelObject(ao.modelName, colour, texture, (room.x+1)*10, 0, (room.y+j)*10, ao);
							levelObjects.add(lo);
						}
						else if (ao.modelType.equalsIgnoreCase("cube"))
						{
							Mesh mesh = Shapes.genCuboid(ao.modelDimensions[0], ao.modelDimensions[1], ao.modelDimensions[2]);
							LevelObject lo = new LevelObject(mesh, colour, texture, (room.x+i)*10, 0, (room.y+j)*10, ao);
							levelObjects.add(lo);
						}
					}
					else
					{
						LevelObject lo = new LevelObject(false, (room.x+1)*10, 0, (room.y+j)*10, ao);
						levelObjects.add(lo);
					}
				}
				
			}
		}
		
		
		fillRoomIndex++;
		return false;
	}
	
	public Tile getTile(float x, float z)
	{
		int ix = (int)(x+0.5f);
		int iz = (int)(z+0.5f);
		
		return getLevelArray()[ix][iz];
	}
	
	public boolean checkCollision(CollisionBox box, String UID)
	{
		if (checkBoxToLevelCollision(box)) return true;
		
		return checkEntities(box, UID) != null;
	}
	
	public boolean checkBoxToLevelCollision(CollisionBox box)
	{
		if (checkLevelCollision(box.position.x, box.position.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y+box.dimensions.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y+box.dimensions.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y+box.dimensions.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y+box.dimensions.y, box.position.z+box.dimensions.z)) return true;
		
		return false;
	}
	
	public boolean checkLevelCollision(float x, float y, float z)
	{					
		int ix = (int)(x+3)/10;
		int iz = (int)(z+3)/10;
		
		if (ix < 0 || ix > width) return true;
		if (iz < 0 || iz > height) return true;

		Tile t = null;
		
		t = getLevelArray()[ix][iz];
		if (y < t.floor || y > t.roof) return true;
		
		return checkSolid(ix, iz);
	}
	
	public boolean checkSolid(int x, int z)
	{
		Tile t = null;
		
		t = getLevelArray()[x][z];
		
		if (t.character == ' ') return true;

		for (Character c : solids)
		{
			if (t.character == c)
			{
				return true;
			}
		}
		return false;
	}
	
	public GameActor checkEntities(CollisionBox box, String UID)
	{
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) continue;
			
			if (box.intersect(ga.getCollisionBox()))
			{
				return ga;
			}
		}
		
		return null;
	}
	
	public void removeItem(String UID)
	{
		int i = 0;
		for (VisibleItem ga : items)
		{
			if (ga.UID.equals(UID)) 
			{
				if (ga.boundLight!= null) GameData.lightManager.removeLight(ga.boundLight.UID);
				items.remove(i);
				return;
			}
			i++;
		}
		System.err.println("Failed to remove item!");
	}
	
	public void removeActor(String UID)
	{
		int i = 0;
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) 
			{
				if (ga.boundLight!= null) GameData.lightManager.removeLight(ga.boundLight.UID);
				actors.remove(i);
				return;
			}
			i++;
		}
		System.err.println("Failed to remove actor!");
	}

	public void addItem(VisibleItem item)
	{
		items.add(item);
	}
	
	public void addActor(GameActor actor)
	{
		actors.add(actor);
	}
	
	public void addSpell(Spell spell)
	{
		spells.add(spell);
	}
	
	public void addLvlObject(LevelObject lo)
	{
		levelObjects.add(lo);
	}
	
	public boolean checkOpaque(int x, int z)
	{
		if (x < 0 || x > getLevelArray()[0].length-1) return true;
		if (z < 0 || z > getLevelArray().length-1) return true;
		
		boolean opaque = false;
		
		for (Character c : solids)
		{
			if (getLevelArray()[(int)x][(int)z].character == c)
			{
				opaque = true;
				break;
			}
		}
		
		return opaque;
	}

	public Tile[][] getLevelArray() {
		return levelArray;
	}

	public void setLevelArray(Tile[][] levelArray) {
		this.levelArray = levelArray;
	}

	public HashMap<Character, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(HashMap<Character, String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public void dispose()
	{
		for (GameActor ga : actors)
		{
			ga.dispose();
		}
		for (VisibleItem vi : items)
		{
			vi.dispose();
		}
	}

	public HashMap<Character, Color> getColours() {
		return colours;
	}

	public void setColours(HashMap<Character, Color> colours) {
		this.colours = colours;
	}

	public ArrayList<Character> getOpaques() {
		return opaques;
	}

	public void setOpaques(ArrayList<Character> opaques) {
		this.opaques = opaques;
	}

	public ArrayList<Character> getSolids() {
		return solids;
	}

	public void setSolids(ArrayList<Character> solids) {
		this.solids = solids;
	}
}

