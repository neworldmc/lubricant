package site.neworld.lubricant.bvh

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate")
data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vec3): Vec3 = Vec3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vec3): Vec3 = Vec3(x - other.x, y - other.y, z - other.z)

    operator fun unaryPlus(): Vec3 = Vec3(x, y, z)

    operator fun unaryMinus(): Vec3 = Vec3(x, y, z)

    operator fun times(scale: Float): Vec3 = Vec3(x * scale, y * scale, z * scale)

    operator fun div(scale: Float): Vec3 = Vec3(x / scale, y / scale, z / scale)

    infix fun dot(r: Vec3): Float = x * r.x + y * r.y + z * r.z

    infix fun cross(r: Vec3): Vec3 = Vec3(y * r.z - z * r.y, z * r.x - x * r.z, x * r.y - y * r.x)

    fun lengthSquared() = this dot this

    fun length() = sqrt(lengthSquared())
}

fun minVec(l: Vec3, r: Vec3) = Vec3(min(l.x, r.x), min(l.y, r.y), min(l.z, r.z))

fun maxVec(l: Vec3, r: Vec3) = Vec3(max(l.x, r.x), max(l.y, r.y), max(l.z, r.z))