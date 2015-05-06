package com.base.lib.engine;

/**
 * Base Types
 */
public enum Type { //todo revision

    OBJECT_2D,
    OBJECT_3D,

    STATIC,
    DYNAMIC,

    DRAW_TEXTURED,
    DRAW_COLOURED,
    DRAW_WIRED,
    DRAW_NONE,

    DOWNLOADER_SYNC,
    DOWNLOADER_ASYNC,

    STORAGE_ASSETS,
    STORAGE_SDCARD,
    STORAGE_INTERNAL,
    STORAGE_RESOURCE,

    VBO_STATIC,
    VBO_DYNAMIC,
    VBO_VERTICES_DYNAMIC,
    VBO_TEXTURES_DYNAMIC,

    OTHER,
}
