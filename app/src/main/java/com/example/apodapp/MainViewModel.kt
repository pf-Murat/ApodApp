package com.example.apodapp

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun generateImageList(): List<String> {
        val list = mutableListOf<String>()
        repeat(10){ counter ->
            list.add("Sample $counter")
        }
        return list.toList()
    }
}