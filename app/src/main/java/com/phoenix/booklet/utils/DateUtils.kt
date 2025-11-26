package com.phoenix.booklet.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toHumanReadableDate(): String =
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(this)