package com.kevin.cookingassistancecompanion.textAnalyzer

import com.kevin.cookingassistancecompanion.CameraOverlay
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test

class TAndTTextAnalyzerTest {

    private val overlay = mock<CameraOverlay>()

    private val sut = TAndTTextAnalyzer(overlay)


    @Test
    fun `filteredString - filtered correct string`() {
        Assert.assertTrue(sut.filteredString("2alsdkfj"))
        Assert.assertTrue(sut.filteredString("$"))
        Assert.assertTrue(sut.filteredString("w $"))
        Assert.assertTrue(sut.filteredString("W$"))
    }
}