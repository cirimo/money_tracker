package dev.cirimo.trosko

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/**
 * The real clock of the device. Unlike `Clock.systemDefaultZone()`, which remembers the time
 * zone it was created in, this one asks for the zone every time, so "today" stays right for a
 * process that lives through a flight or a change of the system setting.
 */
class DeviceClock : Clock() {
    override fun instant(): Instant = Instant.now()

    override fun getZone(): ZoneId = ZoneId.systemDefault()

    override fun withZone(zone: ZoneId): Clock = system(zone)
}
