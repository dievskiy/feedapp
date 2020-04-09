package com.feedapp.app.util

import org.junit.jupiter.api.Test

internal class StringUtilsTest {

    private val stringUtils = StringUtils()
    @Test
    fun checkForCurlyBrackets() {
        var string = "sadasopd na139218048129y38 9123 asdn;i assod naos"
        var resultString = stringUtils.getCorrectRecipeTitle(string)
        assert(string == resultString)

        string = "sadasopd naisod na{ mapod sa}aos"
        resultString = stringUtils.getCorrectRecipeTitle(string)
        assert("sadasopd naisod naaos" == resultString)

        string = "sadasopd nai{}sod naos"
        resultString = stringUtils.getCorrectRecipeTitle(string)
        assert("sadasopd naisod naos" == resultString)
    }


    @Test
    fun checkSiteUtil() {
        val site = "https://google.com"
        val string = stringUtils.getSiteFromUrl(site)
        assert(!string.equals(site))
    }
}