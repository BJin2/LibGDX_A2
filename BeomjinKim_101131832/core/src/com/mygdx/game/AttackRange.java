package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class AttackRange extends ActorBeta
{
	boolean enabled;
	float life;
	float damage;
	float timer;
	Character target;
	Sound hit;

	AttackRange(float x, float y, float _width, float _height, float lifetime, float _damage, Character _target)
	{
		enabled = true;
		life = lifetime;
		damage = _damage;
		timer = 0;
		target = _target;

		hit = Gdx.audio.newSound(Gdx.files.internal("Audio/hit_0.wav"));
		setSize(_width, _height);
		setPosition(x, y);
		setBoundaryRectangle();
	}

	@Override
	public void act(float dt)
	{
		super.act(dt);
		timer += dt;

		if(timer >= life)
		{
			enabled = false;
			remove();
			return;
		}

		if(enabled)
		{
			if(this.overlaps(target))
			{
				this.preventOverlap(target);
				enabled = false;
				target.TakeDamage(damage);
				hit.play();
				remove();
			}
		}
	}
}
