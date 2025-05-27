package com.LingTH.fridge.Notification

import java.time.LocalTime
import java.time.Duration



class AlertTimeManager(
    private val alertsPerDay: Int,
    private val startHour: Int = 9,
    private val endHour: Int = 21
) {
    private val timeSlots: List<LocalTime> = run {
        val start = LocalTime.of(startHour, 0)
        val end = LocalTime.of(endHour, 0)
        val interval = Duration.between(start, end).dividedBy(alertsPerDay.toLong())
        List(alertsPerDay) { i -> start.plus(interval.multipliedBy(i.toLong())) }
    }

    fun isWithinAlertWindow(marginMinutes: Long = 5): Boolean {
        val now = LocalTime.now()
        val margin = Duration.ofMinutes(marginMinutes)
        return timeSlots.any { Duration.between(it, now).abs() <= margin }
    }

    fun getNextAlertTime(): LocalTime? =
        timeSlots.firstOrNull { it.isAfter(LocalTime.now()) }

    fun debugTimeSlots(): List<String> =
        timeSlots.map { it.toString() }
}




