package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.animation.Interpolator;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.utils.Utils;

import java.io.IOException;

public class PathKeyframe extends Keyframe<PointF> {
  @Nullable private Path path;

  private PathKeyframe(LottieComposition composition, @Nullable PointF startValue,
      @Nullable PointF endValue, @Nullable Interpolator interpolator,
      float startFrame, @Nullable Float endFrame) {
    super(composition, startValue, endValue, interpolator, startFrame, endFrame);

    // This must use equals(float, float) because PointF didn't have an equals(PathF) method
    // until KitKat...
    boolean equals = endValue != null && startValue != null &&
        startValue.equals(endValue.x, endValue.y);
    //noinspection ConstantConditions
    if (endValue != null && !equals) {
      path = Utils.createPath(startValue, endValue, pathCp1, pathCp2);
    }
  }

  public static class Factory {
    private Factory() {
    }

    public static PathKeyframe newInstance(JsonReader reader, LottieComposition composition,
        AnimatableValue.Factory<PointF> valueFactory) throws IOException {
      // TODO (json): figure out if the animated parameter is correct
      Keyframe<PointF> keyframe = Keyframe.Factory.newInstance(
          reader, composition, composition.getDpScale(), valueFactory,
          reader.peek() == JsonToken.BEGIN_OBJECT);


      return new PathKeyframe(composition, keyframe.startValue,
          keyframe.endValue, keyframe.interpolator, keyframe.startFrame, keyframe.endFrame);
    }
  }

  /** This will be null if the startValue and endValue are the same. */
  @Nullable Path getPath() {
    return path;
  }
}
