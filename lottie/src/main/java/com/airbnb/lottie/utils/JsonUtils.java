package com.airbnb.lottie.utils;

import android.graphics.PointF;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
  private JsonUtils() {
  }

  public static List<PointF> jsonToPoints(JsonReader reader, float scale) throws IOException {
    List<PointF> points = new ArrayList<>();

    reader.beginArray();
    while (reader.peek() == JsonToken.BEGIN_ARRAY) {
      reader.beginArray();
      points.add(jsonToPoint(reader, scale));
      reader.endArray();
    }
    reader.endArray();
    return points;
  }

  public static PointF jsonToPoint(JsonReader reader, float scale) throws IOException {
    switch (reader.peek()) {
      case NUMBER: return jsonNumbersToPoint(reader, scale);
      case BEGIN_ARRAY: return jsonArrayToPoint(reader, scale);
      case BEGIN_OBJECT: return jsonObjectToPoint(reader, scale);
      default: throw new IllegalArgumentException("Unknown point starts with " + reader.peek());
    }
  }

  private static PointF jsonNumbersToPoint(JsonReader reader, float scale) throws IOException {
    float x = (float) reader.nextDouble();
    float y = (float) reader.nextDouble();
    while (reader.hasNext()) {
      reader.skipValue();
    }
    return new PointF(x * scale, y * scale);
  }

  private static PointF jsonArrayToPoint(JsonReader reader, float scale) throws IOException {
    float x;
    float y;
    reader.beginArray();
    x = (float) reader.nextDouble();
    y = (float) reader.nextDouble();
    while (reader.peek() != JsonToken.END_ARRAY) {
      reader.skipValue();
    }
    reader.endArray();
    return new PointF(x * scale, y * scale);
  }

  private static PointF jsonObjectToPoint(JsonReader reader, float scale) throws IOException {
    float x = 0f;
    float y = 0f;
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "x":
          x = valueFromObject(reader);
          break;
        case "y":
          y = valueFromObject(reader);
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();
    return new PointF(x * scale, y * scale);
  }

  public static float valueFromObject(JsonReader reader) throws IOException {
    boolean endArray;
    try {
      reader.beginArray();
      endArray = true;
    } catch (IllegalStateException e) {
      // This value is either an array or a double. However, peek will throw if it's a double.
      return (float) reader.nextDouble();
    }
    float value = (float) reader.nextDouble();
    //noinspection ConstantConditions
    if (endArray) {
      while (reader.peek() != JsonToken.END_ARRAY) {
        reader.skipValue();
      }
      reader.endArray();
    }
    return value;
  }

  /** Eventually this should not be used anymore */
  public static JsonReader jsonToReader(Object json) {
    return new JsonReader(new StringReader(json.toString()));
  }
}
