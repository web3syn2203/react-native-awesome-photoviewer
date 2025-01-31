
package com.reactnativeawesomephotoviewer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.view.WindowManager;
import android.os.Build;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.imagehelper.ImageSource;

import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnDismissListener;
import com.stfalcon.imageviewer.listeners.OnImageChangeListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import android.util.Log;

public class MerryPhotoView extends View {

    private MerryPhotoOverlay overlayView;
    protected StfalconImageViewer.Builder builder;
    private ThemedReactContext mThemedReactContext;

    public MerryPhotoView(ThemedReactContext themedReactContext) {
        super(themedReactContext);
        mThemedReactContext = themedReactContext;
    }

    public MerryPhotoData[] getData() {
        return data;
    }

    public MerryPhotoView setData(MerryPhotoData[] data) {
        this.data = data;
        return this;
    }

    protected MerryPhotoData[] data;

    public String getShareText() {
        return shareText;
    }

    public MerryPhotoView setShareText(String shareText) {
        this.shareText = shareText;
        return this;
    }

    protected String shareText;


    protected int initial;

    public int getInitial() {
        return initial;
    }

    public MerryPhotoView setInitial(int initial) {
        this.initial = initial;
        return this;
    }

    public boolean isHideStatusBar() {
        return hideStatusBar;
    }

    public MerryPhotoView setHideStatusBar(boolean hideStatusBar) {
        this.hideStatusBar = hideStatusBar;
        return this;
    }

    protected boolean hideStatusBar;


    public boolean isHideShareButton() {
        return hideShareButton;
    }

    public MerryPhotoView setHideShareButton(boolean hideShareButton) {
        this.hideShareButton = hideShareButton;
        return this;
    }

    protected boolean hideShareButton;

    protected boolean hideTitle;

    public boolean isHideTitle() {
        return hideTitle;
    }

    public MerryPhotoView setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
        return this;
    }

    protected boolean hideCloseButton;

    public boolean isHideCloseButton() {
        return hideCloseButton;
    }

    public MerryPhotoView setHideCloseButton(boolean hideCloseButton) {
        this.hideCloseButton = hideCloseButton;
        return this;
    }


    protected void init() {
        if(builder != null){
            return;
        }
        final Context context = getContext();
        overlayView = new MerryPhotoOverlay(mThemedReactContext);
        StfalconImageViewer.Builder<MerryPhotoData> builder = new StfalconImageViewer.Builder<>(context, getData(), (imgViewer, photo) ->
        Glide.with(mThemedReactContext)
        .load(photo.source.getString("uri"))
        .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.i("test", "onLoadFailed="+e);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.i("test", "onResourceReady!");
                overlayView.hiddenLoading();
                return false;
            }
        })
        .into(imgViewer));

        builder.withStartPosition(200);
        builder.withDismissListener(getDismissListener());
        builder.withImageChangeListener(getImageChangeListener());
        builder.withStartPosition(getInitial());
        builder.withHiddenStatusBar(isHideStatusBar());
        builder.withOverlayView(overlayView);
        StfalconImageViewer imageViewer = builder.show();
        overlayView.setImageViewer(imageViewer);
        setOverlayViewValue(getInitial());
    }

    private ImageRequestBuilder getLocalImage() {
        final Context context = getContext();

        ImageSource imageSource = new ImageSource(context, "cat-2575694_1920");

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(imageSource.getUri());
        return imageRequestBuilder;
    }

    private OnImageChangeListener getImageChangeListener() {
        return new OnImageChangeListener() {
            @Override
            public void onImageChange(int position) {
                setOverlayViewValue(position);
                final MerryPhotoData merryPhotoData = getData()[position];
                WritableMap writableMap = Arguments.createMap();
                writableMap.putString("title", merryPhotoData.title);
                writableMap.putString("summary", merryPhotoData.summary);
                writableMap.putInt("summaryColor", merryPhotoData.summaryColor);
                writableMap.putInt("titleColor", merryPhotoData.titleColor);
                writableMap.putMap("source", Utils.toWritableMap(merryPhotoData.source));

                // onChange event from js side
                WritableMap map = Arguments.createMap();
                map.putMap("photo", writableMap);
                map.putInt("index", position);

                onNavigateToPhoto(map);

            }
        };
    }

    private void setOverlayViewValue(int position) {
      final MerryPhotoData merryPhotoData = getData()[position];
      String url = merryPhotoData.source.getString("uri");
      overlayView.setShareContext(url);

      overlayView.setDescription(merryPhotoData.summary);
      overlayView.setTitleText(merryPhotoData.title);

      int summaryColor = Color.WHITE;
      int titleColor = Color.WHITE;
      if (getShareText() != null) {
          overlayView.setShareText(getShareText());
      }

      overlayView.setHideShareButton(isHideShareButton());
      overlayView.setHideCloseButton(isHideCloseButton());
      overlayView.setHideTitle(isHideTitle());

      overlayView.setPagerText((position + 1) + " / " + getData().length);
      if (merryPhotoData.titleColor != 0) {

          titleColor = merryPhotoData.titleColor;
      }
      overlayView.setTitleTextColor(titleColor);
      if (merryPhotoData.summaryColor != 0) {
          summaryColor = merryPhotoData.summaryColor;
      }
      overlayView.setDescriptionTextColor(summaryColor);
    }

    /**
     * on dismiss
     */
    protected void onDialogDismiss() {
        final Context context = getContext();
        builder = null;
        overlayView = null;
        if (context instanceof ReactContext) {
            ((ReactContext) context).getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onDismiss", null);
        }
    }

    /**
     * on photo change
     */
    protected void onNavigateToPhoto(WritableMap map) {
        final Context context = getContext();
        if (context instanceof ReactContext) {
            ((ReactContext) context).getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onChange", map);
        }
    }

    private OnDismissListener getDismissListener() {
        return new OnDismissListener() {
            @Override
            public void onDismiss() {
                onDialogDismiss();
            }
        };
    }
}
