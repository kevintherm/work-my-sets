package com.example.workmysets.ui.components

import android.content.Context

data class SearchItem(val name: String, val action: ((context: Context) -> Unit)?)
