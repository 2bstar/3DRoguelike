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
package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModel;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public abstract class Equipment_HAND extends Equippable{

	private static final long serialVersionUID = -1462740444282967851L;
	public enum WeaponType {
		MELEE,
		RANGED
	}
	
	public static WeaponType convertStringtoWepType(String type)
	{
		for (WeaponType w : WeaponType.values())
		{
			if (type.equalsIgnoreCase(""+w)) return w;
		}
		
		return null;
	}

	public static Equipment_HAND getWeapon(String typeString, String visualType, String styleString, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, float weight, boolean two_handed, int range, float scale, Level level) {
		
		WeaponType type = convertStringtoWepType(typeString);
		
		return getWeapon(type, visualType, styleString, strength, ele_dam, dam_dam, attack_speed, weight, two_handed, range, scale, level);
	}
	
	public static Equipment_HAND getWeapon(WeaponType type, String visualType, String styleString, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, float weight, boolean two_handed, int range, float scale, Level level) {
		
		Equipment_HAND weapon = null;
		RiggedModel model = null;

		if (visualType.equalsIgnoreCase("sword"))
		{
			model = RiggedModel.getSword(level, range, scale);
		}
		else if (visualType.equalsIgnoreCase("torch"))
		{
			model = RiggedModel.getTorch(level);
		}
		
		if (type == WeaponType.MELEE)
		{
			weapon = new MeleeWeapon(MeleeWeapon.convertWeaponStyle(styleString),  
					weight, strength, ele_dam, dam_dam,
					attack_speed, two_handed, range, model);
		}
		else if (type == WeaponType.RANGED)
		{
			weapon = new RangedWeapon(RangedWeapon.convertWeaponStyle(styleString),  
					weight, strength, ele_dam, dam_dam,
					attack_speed, two_handed, range, model);
		}
		
		return weapon;
	}
	
	public final boolean two_handed;
	public final float attack_speed;
	public final int strength;
	public final HashMap<Element, Integer> ele_dam;
	public final HashMap<Damage_Type, Integer> dam_dam;
	public final int range;
	
	private transient GameActor holder;
	/**
	 * @return the holder
	 */
	public GameActor getHolder() {
		return holder;
	}
	/**
	 * 0 = none
	 * 1 = left
	 * 2 = right
	 */
	public int equippedSide;
	
	public transient float useCD = 0;
	
	public final RiggedModel model;
	
	public Equipment_HAND(float WEIGHT, int strength, 
			HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, boolean two_handed, int range, RiggedModel model) {
		super(WEIGHT, Item_Type.WEAPON);
		
		this.model = model;
		this.two_handed = two_handed;
		this.strength = strength;
		this.ele_dam = ele_dam;
		this.dam_dam = dam_dam;
		this.attack_speed = attack_speed;
		this.range = range;
	}
	
	public void damage(GameActor ga)
	{
		System.out.println("HIT on "+ga.UID);
		ga.damage(strength, ele_dam, dam_dam);
	}
	
	public void unequip()
	{
		System.out.println("Unequipping");
		holder = null;
		equippedSide = 0;
		unequipped();
	}
	protected abstract void unequipped();
	
	public void equip(GameActor actor, int side)
	{
		System.out.println("Equipping");
		
		model.create();
		
		if (side == 1)
		{
			actor.L_HAND = this;
			if (two_handed) {
				actor.R_HAND.unequip();
				actor.R_HAND = null;
			}
			model.rootNode.position.setToTranslation(-actor.radius, 0, -2);
		}
		else if (side == 2)
		{
			actor.R_HAND = this;
			if (two_handed) {
				actor.L_HAND.unequip();
				actor.L_HAND = null;
			}
			model.rootNode.position.setToTranslation(actor.radius, 0, -2);
		}
		else
		{
			System.err.println("Invalid equip side: "+side);
		}
		
		holder = actor;
		equippedSide = side;
		
		model.equip(actor, side);	
		equipped(actor, side);
		
	}
	protected abstract void equipped(GameActor actor, int side);
	
	public void held()
	{
		model.held();
		if (holder == null)
		{
			System.err.println("Holder null!");
		}
		
		if (useCD > 0) return;
		useCD = attack_speed;
	}
	
	public void released()
	{
		model.released();
	}
	
	Matrix4 tmp = new Matrix4();
	public void render(Renderer renderer, ArrayList<ParticleEmitter> emitters, Camera cam)
	{
		model.render(renderer, emitters, cam);
	}
	protected abstract void drawed(Camera cam);
	
	public void update(float delta, Camera cam)
	{
		model.update(delta, cam);
		model.composeMatrixes(tmp.set(holder.getTransform()).mul(holder.getRotationMatrix()));
		
		useCD -= delta;
		updated(delta);
	}
	protected abstract void updated(float delta);
	
	public void create()
	{
		model.create();
	}
	
	public void fixReferences(GameActor actor)
	{
		holder = actor;
		
		model.fixReferences();
		
		fixReferencesSuper();
	}
	
	protected abstract void fixReferencesSuper();

	public void dispose()
	{
		model.dispose();
	}
}
