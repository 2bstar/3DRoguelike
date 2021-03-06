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
package com.lyeeedar.Roguelike3D.Graphics.Materials;

import java.io.Serializable;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public abstract class MaterialAttribute implements Serializable {

	private static final long serialVersionUID = 7055324739119426496L;
	protected static final String FLAG = "Flag";
	public String name;
	protected final boolean isPooled;

	protected MaterialAttribute () {
		isPooled = true;
	}

	public MaterialAttribute (String name) {
		this.name = name;
		isPooled = false;
	}

	public abstract void bind (ShaderProgram program, LightManager lights);

	public abstract MaterialAttribute copy ();

	public abstract MaterialAttribute pooledCopy ();

	public abstract void free ();

	public abstract void set (MaterialAttribute attr);

	public String getShaderFlag () {
		return name + FLAG;
	}
	
	public abstract void create();
	
	public abstract void dispose();
}
