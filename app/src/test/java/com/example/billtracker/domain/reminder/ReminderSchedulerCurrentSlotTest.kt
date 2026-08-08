package com.example.billtracker.domain.reminder

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Calendar

class ReminderSchedulerCurrentSlotTest {


    @Test
    fun `currentSlot when time now more than fifteen minute`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,20)
        val expected = null

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }

    @Test
    fun `currentSlot when time now is fifteen minute`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,15)
        val expected = null

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }

    @Test
    fun `currentSlot when time now less than fifteen minute and less than 9am`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,7)
        val expected = null

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        Assertions.assertEquals(expected, actual)

    }

    @Test
    fun `currentSlot when time now less than fifteen minute and is 9am`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,9)
        val expected = "09:00"

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }

    @Test
    fun `currentSlot when time now less than fifteen minute and than than is 9am and less than 13`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,11)
        val expected = null

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }

    @Test
    fun `currentSlot when time now less than fifteen minute and is 1pm`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,13)
        val expected = "13:00"

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }


    @Test
    fun `currentSlot when time now less than fifteen minute and than than is 1pm and less than 8pm`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,17)
        val expected = null

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }


    @Test
    fun `currentSlot when time now less than fifteen minute and than than is 8pm`(){
        //Arrange
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE,9)
        now.set(Calendar.HOUR_OF_DAY,20)
        val expected = "20:00"

        // Act
        val actual =  ReminderScheduler.currentSlot(now)


        // Assert
        assertEquals(expected, actual)

    }
}