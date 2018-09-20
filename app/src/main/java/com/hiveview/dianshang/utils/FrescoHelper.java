package com.hiveview.dianshang.utils;

import android.net.Uri;
import android.support.annotation.DrawableRes;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by carter on 7/9/17.
 */

public class FrescoHelper {


    /**
     * uri对应的图片在指定宽高在simpleDraweeView上显示
     *
     * @param simpleDraweeView
     * @param uri
     * @return
     */
    public static void setImage(SimpleDraweeView simpleDraweeView, Uri uri) {

        ImageRequest request = ImageRequest.fromUri(uri);
        setImage(simpleDraweeView, request);
    }


    /**
     * uri对应的图片在指定宽高在simpleDraweeView上显示，options主要是用来设置大小，Fresco会找到最适应的图片大小
     *
     * @param simpleDraweeView
     * @param uri
     * @param options
     */
    public static void setImage(SimpleDraweeView simpleDraweeView, Uri uri, ResizeOptions options) {

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(options)
                .setAutoRotateEnabled(true)
                .build();
        setImage(simpleDraweeView, request);
    }


    /**
     * 将ImageRequest请求返回的图片显示在simpleDraweeView上
     *
     * @param simpleDraweeView
     * @param request
     */
    public static void setImage(SimpleDraweeView simpleDraweeView, ImageRequest request) {

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
        simpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
    }


    /**
     * 给simpleDraweeView设置res下的资源图片
     *
     * @param simpleDraweeView
     * @param resId            图片对应的resId
     */
    public static void setImageResource(SimpleDraweeView simpleDraweeView, @DrawableRes int resId) {

        ImageRequestBuilder imageRequest = ImageRequestBuilder.newBuilderWithResourceId(resId);
        simpleDraweeView.setImageURI(imageRequest.getSourceUri());
    }


    /**
     * 将uri对应的图片在zoomableDraweeView上显示
     * @param zoomableDraweeView
     * @param uri
     *//*
    public static void setImage(ZoomableDraweeView zoomableDraweeView, Uri uri) {

        zoomableDraweeView.setController(
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .build());
        Resources resources = zoomableDraweeView.getResources();
        GenericDraweeHierarchy hierarchy =
                new GenericDraweeHierarchyBuilder(resources)
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .setPlaceholderImage(resources.getDrawable(R.color.black))
                        .setProgressBarImage(resources.getDrawable(R.drawable.loading_drawable))
                        .build();
        zoomableDraweeView.setBackgroundColor(resources.getColor(R.color.black));
        zoomableDraweeView.setHierarchy(hierarchy);
    }



    *//**
     * uri对应的图片在zoomableDraweeView上显示,部分手机在显示本地图片时必须指定大小
     * @param zoomableDraweeView
     * @param uri
     * @param options
     *//*
    public static void setImage(ZoomableDraweeView zoomableDraweeView, Uri uri, ResizeOptions options) {

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(options)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(zoomableDraweeView.getController())
                .build();
        zoomableDraweeView.setController(controller);
        Resources resources = zoomableDraweeView.getResources();
        GenericDraweeHierarchy hierarchy =
                new GenericDraweeHierarchyBuilder(resources)
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .setPlaceholderImage(resources.getDrawable(R.color.black))
                        .setProgressBarImage(resources.getDrawable(R.drawable.loading_drawable))
                        .build();
        zoomableDraweeView.setBackgroundColor(resources.getColor(R.color.black));
        zoomableDraweeView.setHierarchy(hierarchy);
    }*/


    /**
     * 给photo的图片以半径为radius的圆角显示
     *
     * @param photoView
     * @param radius
     */
    public static void setCornersRadius(SimpleDraweeView photoView, int radius) {

        GenericDraweeHierarchy hierarchy = photoView.getHierarchy();
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        if (roundingParams == null) {

            roundingParams = new RoundingParams();
        }
        roundingParams.setCornersRadius(radius);
        hierarchy.setRoundingParams(roundingParams);
    }

}
