/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models

/**
 * class for validating portion descriptions
 */
class RegexDescriptionChecker {

    fun checkValidity(description: String): String {
        var resultDescription = description

        // get description after "1"
        if(resultDescription.contains("1")){
            val regex = "(?<=1\\s).*".toRegex()
            val matchResult = regex.find(resultDescription) ?: return resultDescription
            val result = matchResult.value
            resultDescription = result
        }

        // get everything until brackets
        if(resultDescription.contains("(")){
            val regex = ".+?(?=\\()".toRegex()
            val matchResult = regex.find(resultDescription) ?: return resultDescription
            val result = matchResult.value
            resultDescription = result
        }

        return resultDescription
    }
}