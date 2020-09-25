package site.neworld.lubricant.bvh

data class AABB(var min: Vec3, var max: Vec3) {
    infix fun union(other: AABB) = AABB(minVec(min, other.min), maxVec(max, other.max))

    infix fun joint(other: AABB) = AABB(maxVec(min, other.min), minVec(max, other.max))

    infix fun move(offset: Vec3) = AABB(min + offset, max + offset)

    fun area(): Float {
        val delta = max - min
        return 2.0f * (delta.x * delta.y + delta.y * delta.z + delta.z * delta.x)
    }

    fun volume(): Float {
        val delta = max - min
        return delta.x * delta.y * delta.z
    }

    fun intersect(other: AABB): Boolean {
        return !(
                this.min.x > other.max.x ||
                this.max.x < other.min.x ||
                this.min.y > other.max.y ||
                this.max.y < other.min.y ||
                this.min.z > other.max.z ||
                this.max.z < other.min.z)
    }


    fun center() = (min + max) / 2.0F
}