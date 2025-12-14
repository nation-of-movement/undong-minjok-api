package com.undongminjok.api.global.storage;

public enum ImageCategory {
  PROFILE("profiles"),
  WORKOUT("workouts"),
  THUMBNAIL("templates/thumbnails"),
  DETAIL("templates/detail");

  private final String dir;

  ImageCategory(String dir) {
    this.dir = dir;
  }

  public String getDir() {
    return dir;
  }
}
