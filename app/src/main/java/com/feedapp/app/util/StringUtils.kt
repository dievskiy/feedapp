package com.feedapp.app.util

class StringUtils {

    fun getCorrectRecipeTitle(string: String): String {
        val result: String
        // remove curly brackets and space at start
        val regexBracketsAndSpaceStart = "(?=\\{)(.*?)(?<=\\})|(^ +)".toRegex()
        // remove extra space
        val regexExtraSpace = "\\s\\s+".toRegex()
        result = string.replace(regexBracketsAndSpaceStart, "").replace(regexExtraSpace, " ")
        return result
    }

    fun getSiteFromUrl(sourceUrl: String): String {
        // remove curly brackets
        val regex =
            "(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]".toRegex()
        // remove extra space
        return regex.find(sourceUrl)?.value ?: sourceUrl
    }

    fun checkTeaspoonWriting(unit: String): String {
        // check correct writing of "tablespoon(s)" and "teaspoon(s)"
        return unit
            .replace("((tablespoons)|(tablespoon)|(Tbsp)|(TBSP))".toRegex(), "tbsp")
            .replace("((teaspoons)|(teaspoon))".toRegex(), "tsp")
    }

}