package com.feedapp.app.data.exceptions

import java.lang.Exception

class NoInternetConnectionException : Exception() {
    override val message: String?
        get() = "No Internet connection available"
}
