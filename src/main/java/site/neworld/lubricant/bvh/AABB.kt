package site.neworld.lubricant.bvh

data class AABB(var min: Vec3, var max: Vec3) {
    infix fun union(other: AABB) = AABB(minVec(min, other.min), maxVec(max, other.max))

    infix fun joint(other: AABB) = AABB(maxVec(min, other.min), minVec(max, other.max))

    infix fun move(offset: Vec3) = AABB(min + offset, max + offset)

    fun surfaceArea(): Float {
        val delta = max - min
        return 2.0f * (delta.x * delta.y + delta.y * delta.z + delta.z * delta.x)
    }
}