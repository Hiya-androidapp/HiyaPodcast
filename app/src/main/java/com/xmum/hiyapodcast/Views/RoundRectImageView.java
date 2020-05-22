package com.xmum.hiyapodcast.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;

import java.awt.Canvas;

import javax.swing.text.AttributeSet;


public class RoundRectImageView extends AppCompatImageView{
    private float roundRatio =0.1f;
    private Path path;

    public class RoundRectImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (path == null){
            path = new Path();
            path.addRoundRect(new RectF(0,0,getWidth(),getHeight(),roundRatio*getWidth(),roundRatio*getHeight()));
        }
        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }
}
