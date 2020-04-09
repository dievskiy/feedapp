package com.feedapp.app.data.models

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RegexDescriptionCheckerTest {

    @Test
    fun checkValidity() {
        val description = "1 slice (new)"
        val result = RegexDescriptionChecker().checkValidity(description)
        assert(!result.contains("1"))
        assert(!result.contains("(new)"))
    }


}