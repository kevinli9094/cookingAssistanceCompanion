package com.kevin.cookingassistancecompanion

object ScanningResult {
    enum class ResultType(val str: String) {
        TANDT_CHINESE("T&T Chinese"),
        TANDT_ENGLISH("T&T English");

        companion object {
            fun strToResultType(str: String): ResultType {
                return when (str) {
                    TANDT_CHINESE.str -> TANDT_CHINESE
                    TANDT_ENGLISH.str -> TANDT_ENGLISH
                    else -> throw IllegalStateException("Unrecognized result type")
                }
            }
        }
    }

    var resultType = ResultType.TANDT_CHINESE
    private val mutableSet = LinkedHashSet<String>()

    @Synchronized
    fun addString(key: String) {
        mutableSet.add(key)
    }

    fun getSortedResult(): List<String> {
        return mutableSet.toList()
    }

    fun setResult(list: List<String>) {
        mutableSet.clear()
        mutableSet.addAll(list)
    }
}