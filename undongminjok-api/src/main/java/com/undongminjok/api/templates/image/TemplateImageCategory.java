package com.undongminjok.api.templates.image;

public enum TemplateImageCategory {

  THUMBNAIL("templates/thumbnails"),
  DETAIL("templates/detail");

  private final String dir;

  TemplateImageCategory(String dir) {
    this.dir = dir;
  }

  public String getDir() {
    return dir;
  }
}