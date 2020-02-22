package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	private OrthographicCamera camera;
	private SpriteBatch batch; // ддя отрисовки 2d изображений, в нашем случае для Texture

    private Rectangle bucket;
    private Array<Rectangle> rainDrops;
	private long lastDropItem;



	private void spawnRainDrop(){
		Rectangle rainDrop = new Rectangle();
		rainDrop.x = MathUtils.random(0, 800 - 64);
		rainDrop.y = 480;
		rainDrop.width = 64;
		rainDrop.height = 64;
		rainDrops.add(rainDrop);
		lastDropItem = TimeUtils.nanoTime();

	}


	@Override
	public void create() {
		super.create();

		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        rainDrops = new Array<Rectangle>();
        spawnRainDrop();

	}

    @Override
    public void render() {
	    Gdx.gl.glClearColor(0, 0, 0.2f, 1 );
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);


		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop: rainDrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// передвижение корзины по экрану
		if (Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos); // чтобы клик по экрану ракитывался в пределах viewport`a (ширины и высоты экрана)
			bucket.x = touchPos.x - 64 / 2;
		}

			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				bucket.x -= 200 * Gdx.graphics.getDeltaTime();

			}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}

			if(bucket.x < 0) {
				bucket.x = 0;

			}
			if(bucket.x >800 - 64){
				bucket.x = 800- 64;
			}

			if(TimeUtils.nanoTime() - lastDropItem > 1000000000) {
				spawnRainDrop();
		}

		for(Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext();) {
			Rectangle raindrop_loop = iter.next();
			raindrop_loop.y -= 200 * Gdx.graphics.getDeltaTime();

			if(raindrop_loop.y + 64 < 0) {
				iter.remove();
			}
			if (raindrop_loop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}

	}

	@Override
	public void dispose() {
		super.dispose();
	// он удаляет ресурсы созданные Lipgdx
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}



