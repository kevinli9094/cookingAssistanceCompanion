package com.kevin.cookingassistancecompanion.textAnalyzer

import com.kevin.cookingassistancecompanion.CameraOverlay
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`

class TAndTTextAnalyzerTest {

    private val overlay = mock<CameraOverlay>()

    private val sut = TAndTTextAnalyzer(overlay)


    @Test
    fun `filteredString - filtered price string`() {
        Assert.assertTrue(sut.filteredString("2alsdkfj"))
        Assert.assertTrue(sut.filteredString("$"))
        Assert.assertTrue(sut.filteredString("w $"))
        Assert.assertTrue(sut.filteredString("W$"))
        Assert.assertTrue(sut.filteredString("$"))
        Assert.assertTrue(sut.filteredString("S8"))
        Assert.assertTrue(sut.filteredString("V S4.39"))


        Assert.assertFalse(sut.filteredString("salary"))
    }

    @Test
    fun `filteredString - filtered title string`() {
        Assert.assertTrue(sut.filteredString("deli"))
        Assert.assertTrue(sut.filteredString("produce"))
        Assert.assertTrue(sut.filteredString("pr0duce"))
        Assert.assertTrue(sut.filteredString("MeaT"))
        Assert.assertTrue(sut.filteredString("neaT"))
    }

    @Test
    fun `filteredString - filtered errors string`(){
        Assert.assertTrue(sut.filteredString("z"))
        Assert.assertTrue(sut.filteredString("zh"))
        Assert.assertTrue(sut.filteredString("(A"))
        Assert.assertTrue(sut.filteredString("h"))
        Assert.assertTrue(sut.filteredString("w\\"))
    }

    @Test
    fun `convertString - convert on sale item`(){
        assertThat(sut.convertString("(SALE) item name"), `is`("item name"))
        assertThat(sut.convertString(" ( SALE ) item name"), `is`("item name"))
    }

    @Test
    fun `convertString - do not convert normal item`(){
        assertThat(sut.convertString("item name"), `is`("item name"))
        assertThat(sut.convertString(" another item name"), `is`("another item name"))
    }
}