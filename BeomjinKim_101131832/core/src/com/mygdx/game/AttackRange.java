package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class AttackRange extends ActorBeta
{
	boolean enabled;
	float life;
	float damage;
	float timer;
	Character[] targets;
	Sound hit;

	AttackRange(float x, float y, float _width, float _height, float lifetime, float _damage, Character _target)
	{
		targets = new Character[1];
		enabled = true;
		life = lifetime;
		damage = _damage;
		timer = 0;
		targets[0] = _target;

		hit = Gdx.audio.newSound(Gdx.files.internal("Audio/hit_0.wav"));
		setSize(_width, _height);
		setPosition(x, y);
		setBoundaryRectangle();
	}

	AttackRange(float x, float y, float _width, float _height, float lifetime, float _damage, Character[] _targets)
	{
		enabled = true;
		life = lifetime;
		damage = _damage;
		timer = 0;
		targets = _targets;

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

		if(enabled)
		{
			if(timer >= life)
			{
				enabled = false;
			}
			for(int i = 0; i < targets.length; i++)
			{
				if(this.overlaps(targets[i]))
				{
					this.preventOverlap(targets[i]);
					enabled = false;
					targets[i].TakeDamage(damage);
					hit.play();
				}
			}
		}
		else
		{
			remove();
		}
	}
}
