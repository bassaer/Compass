package jp.nakayama.compass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.List;

/**
 * Compass App
 * Created by nakayama on 2017/01/19.
 */

public class MainActivity extends Activity implements SensorEventListener{

    private ImageView mCompassImage;
    private Bitmap mCompass;

    private float[] mAccelerometerValues = new float[3];
    private float[] mMagneticValues = new float[3];

    private SensorManager mSensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompassImage = (ImageView) findViewById(R.id.compass_image);

        mCompass = BitmapFactory.decodeResource(getResources(), R.drawable.compass);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

    }

    /**
     * Change Compass direction
     * @param degree direction from the north
     */
    private void changeDirection(float degree) {
        int width = mCompass.getWidth();
        int height = mCompass.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, width/2, height/2);
        Bitmap resultImage = Bitmap.createBitmap(mCompass, 0, 0, width, height, matrix, true);
        mCompassImage.setImageBitmap(resultImage);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagneticValues = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerValues = event.values.clone();
                break;
        }

        if (mMagneticValues != null && mAccelerometerValues != null) {
            float[] rotate = new float[16];
            float[] inclination = new float[16];

            SensorManager.getRotationMatrix(rotate, inclination, mAccelerometerValues, mMagneticValues);

            float[] actualOrientation = new float[3];

            SensorManager.getOrientation(rotate, actualOrientation);

            changeDirection(90 - (float)Math.toDegrees(actualOrientation[0]));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> magneticSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        List<Sensor> accelerometerSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (magneticSensors.size() > 0) {
            Sensor sensor = magneticSensors.get(0);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

        if (accelerometerSensors.size() > 0) {
            Sensor sensor = accelerometerSensors.get(0);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
