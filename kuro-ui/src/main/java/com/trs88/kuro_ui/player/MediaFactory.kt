package com.trs88.kuro_ui.player

abstract class MediaFactory {
    public fun  createView(type: MediaType):Media{
        return createMedia(type)
    }

    abstract fun createMedia (type: MediaType):Media
}