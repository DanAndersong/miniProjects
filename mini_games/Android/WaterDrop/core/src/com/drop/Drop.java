package com.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture dropImage;
	private Texture bucketImage;
	private Texture background;
	private Sound dropSound;
	private Music rainMusic;
	private Vector3 touchPos;
	private Rectangle bucket;
	private Sprite backgroundSprite;
	private Array<Rectangle> rainDrops;
	private long lastDropTime;

	private void spawnRainDrop () {
		Rectangle rainDrop = new Rectangle();
		rainDrop.x = MathUtils.random(0, 800 - 64);
		rainDrop.y = 480;
		rainDrop.width = 64;
		rainDrop.height = 64;
		rainDrops.add(rainDrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		bucket = new Rectangle();
		camera = new OrthographicCamera();
		touchPos = new Vector3();
		rainDrops = new Array<>();
		dropImage = new Texture("droplet.png");
		background = new Texture("background.jpg");
		bucketImage = new Texture("bucket.png");
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));

		camera.setToOrtho(false, 800, 480);
		rainMusic.setLooping(true);
		rainMusic.play();
		spawnRainDrop();
		backgroundSprite = new Sprite(background);
		backgroundSprite.setSize(800, 480);
		backgroundSprite.setPosition(0,0f);

		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		backgroundSprite.draw(batch);
		batch.draw(bucketImage, bucket.x, bucket.y);

		for (Rectangle rainDrop : rainDrops) {
			batch.draw(dropImage, rainDrop.x, rainDrop.y);
		}
		batch.end();

		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = (int) (touchPos.x - 64 / 2);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 400 * Gdx.graphics.getDeltaTime();
		}
		if (bucket.x < -60) {
			bucket.x = 800;
		}
		if (bucket.x > 800) {
			bucket.x = -60;
		}
		if (TimeUtils.nanoTime() - lastDropTime > 500_000_000) {
			spawnRainDrop();
		}
		Iterator<Rectangle> iterator = rainDrops.iterator();

		while (iterator.hasNext()) {
			Rectangle rainDrop = iterator.next();
			rainDrop.y -= 200 * Gdx.graphics.getDeltaTime();

			if (rainDrop.y + 60 < 0) {
				iterator.remove();
			}
			if (rainDrop.overlaps(bucket)) {
				dropSound.play();
				iterator.remove();
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		dropImage.dispose();
		bucketImage.dispose();
		dropImage.dispose();
		dropSound.dispose();
		batch.dispose();
		background.dispose();
	}

	@Override
	public void pause() {
		super.pause();

	}
}
