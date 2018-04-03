package com.example.kaushiknsanji.novalines.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Custom {@link AppCompatImageView} class that extends {@link #setFrame(int, int, int, int)}
 * to resize/scale the image/drawable to 70 percent zoom in portrait mode
 * and to 100 percent zoom in landscape mode. The images are cropped and centered in the View Frame.
 * As with 70 percent zoom, images cropped will have black bars. This is known as Window Boxing,
 * hence the name.
 *
 * @author Kaushik N Sanji
 */
public class WindowBoxedImageView extends AppCompatImageView {

    //Constant for the Picture Zoom level in portrait mode
    private final static float PORTRAIT_ZOOM_LEVEL = 0.70f;

    public WindowBoxedImageView(Context context) {
        //Propagating the call to super
        super(context);
    }

    public WindowBoxedImageView(Context context, AttributeSet attrs) {
        //Propagating the call to super
        super(context, attrs);
    }

    public WindowBoxedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        //Propagating the call to super
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method extended to resize/scale the image/drawable to 70 percent zoom in portrait mode
     * and to 100 percent zoom in landscape mode (with occurrence of black bars in 70 percent zoom)
     *
     * @param frameLeft   is the Integer value of the position of the Left edge of the image frame
     * @param frameTop    is the Integer value of the position of the Top edge of the image frame
     * @param frameRight  is the Integer value of the position of the Right edge of the image frame
     * @param frameBottom is the Integer value of the position of the Bottom edge of the image frame
     * @return Boolean value. True if the new size and position are different than the previous ones
     */
    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {
        //Retrieving the current Drawable
        Drawable imageDrawable = getDrawable();

        if (imageDrawable != null) {
            //If Drawable is present

            //Retrieving the current screen orientation
            int screenOrientation = getResources().getConfiguration().orientation;

            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                //When the device is in portrait mode

                //Calculating the dimensions of the Frame
                float frameWidth = frameRight - frameLeft;
                float frameHeight = frameBottom - frameTop;

                //Retrieving the dimensions of the Drawable
                float imageDrawableWidth = (float) imageDrawable.getIntrinsicWidth();
                float imageDrawableHeight = (float) imageDrawable.getIntrinsicHeight();

                //Retrieving the current Image Matrix
                Matrix imageMatrix = getImageMatrix();

                //Declaring a float to calculate the scale
                float scale;

                //Calculating the scale based on the image dimensions retaining its aspect ratio
                if (imageDrawableWidth * frameHeight > frameWidth * imageDrawableHeight) {
                    scale = (frameHeight / imageDrawableHeight) * PORTRAIT_ZOOM_LEVEL;
                } else {
                    scale = (frameWidth / imageDrawableWidth) * PORTRAIT_ZOOM_LEVEL;
                }

                //Centering the Image in the View Frame based on the scaled dimensions
                float dx = (frameWidth - imageDrawableWidth * scale) * 0.5f;
                float dy = (frameHeight - imageDrawableHeight * scale) * 0.5f;

                //Applying a new Image Matrix with the scale calculated
                imageMatrix.setScale(scale, scale);
                //Translating the scaled image to the center of the frame
                imageMatrix.postTranslate(Math.round(dx), Math.round(dy));

                //Set scale type to Matrix
                setScaleType(ScaleType.MATRIX);

                //Pass the modified Matrix as the Image Matrix to be used
                setImageMatrix(imageMatrix);

            } else {

                //Set scale type to Center Crop
                setScaleType(ScaleType.CENTER_CROP);

            }

        }

        //Calling to super and returning its value
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }

    /**
     * Sets a Bitmap as the content of this ImageView.
     *
     * @param bm The bitmap to set
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        //Applying a black background for the black bar appearance
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.black));
        //Propagating the call to super class
        super.setImageBitmap(bm);
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param resId the resource identifier of the drawable
     */
    @Override
    public void setImageResource(int resId) {
        //Applying a black background for the black bar appearance
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.black));
        //Propagating the call to super class
        super.setImageResource(resId);
    }

}