package com.trs88.kuro_ui.player

abstract class Media(val name: String,val url: String, val localPatch: String, val mediaType: MediaType) {
    abstract fun play()

    abstract fun stop()
}