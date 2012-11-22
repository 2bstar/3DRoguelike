package com.lyeeedar.Roguelike3D.Graphics.Materials;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;

public class GlowAttribute extends MaterialAttribute {

	static final public String glow = "u_glow";
	
	float glowAmount;
	
	protected GlowAttribute() {
		// TODO Auto-generated constructor stub
	}

	public GlowAttribute(float glowAmount, String name) {
		super(name);
		this.glowAmount = glowAmount;
	}

	@Override
	public void bind(ShaderProgram program) {
		program.setUniformf(name, glowAmount);

	}

	@Override
	public MaterialAttribute copy() {
		return new GlowAttribute(glowAmount, name);
	}
	
	private final static Pool<GlowAttribute> pool = new Pool<GlowAttribute>() {
		@Override
		protected GlowAttribute newObject () {
			return new GlowAttribute();
		}
	};

	@Override
	public MaterialAttribute pooledCopy() {
		GlowAttribute attr = pool.obtain();
		attr.set(this);
		return attr;
	}

	@Override
	public void free() {
		if (isPooled) pool.free(this);

	}

	@Override
	public void set(MaterialAttribute attr) {
		GlowAttribute glowAttr = (GlowAttribute)attr;
		name = glowAttr.name;
		glowAmount = glowAttr.glowAmount;

	}

}
