package site.neworld.lubricant.bvh

import java.util.*

open class Node<T>(var value: T) {
    var parent: Node<T>? = null
    var left: Node<T>? = null
    var right: Node<T>? = null

    constructor(n: Node<T>) {
        value = n.value
        parent = n.parent
        left = n.left
        right = n.right
    }

    fun setHierarchies(P: Node<T>?, L: Node<T>?, R: Node<T>?) {
        parent = P
        left = L
        right = R
    }

    override fun toString() = value.toString()
}

class BoundingVolumeNode(v: AABB) : Node<AABB>(v) {
    fun intersect(another: BoundingVolumeNode) = value.intersect(another.value)
}

class BoundingVolumeHierarchies {
    var root: BoundingVolumeNode? = null
    private val dictionary = HashSet<BoundingVolumeNode>(32)

    internal class Candidate(var v: BoundingVolumeNode? = null)

    fun add(node: BoundingVolumeNode) {
        if (dictionary.contains(node)) return
        dictionary.add(node)
        if (root == null) root = node else {
            val score = -1f
            val candidate = Candidate()
            insertHierarchy(root!!, node, score, 0f, candidate)
            if (candidate.v != null) {
                val newNode = BoundingVolumeNode(node.value joint candidate.v!!.value)
                if (candidate.v!!.parent == null) { //现节点是根节点
                    root = newNode
                } else {
                    if (candidate.v === candidate.v!!.parent!!.left)
                        candidate.v!!.parent!!.left = newNode
                    else
                        candidate.v!!.parent!!.right = newNode
                }
                newNode.parent = candidate.v!!.parent
                newNode.left = candidate.v
                newNode.right = node
                candidate.v!!.parent = newNode
                node.parent = newNode
                var cur = newNode
                while (cur.parent != null) {
                    cur.parent!!.value = cur.parent!!.value joint cur.value
                    cur = cur.parent as BoundingVolumeNode
                }
            }
        }
    }

    private fun insertHierarchy(current: BoundingVolumeNode, target: BoundingVolumeNode, bestCost: Float, inheritedCost: Float, candidate: Candidate) {
        val isLeaf = current.left == null // 必定是二叉树判断一边就行
        val newVol = current.value joint target.value
        val curCost = inheritedCost + newVol.volume()
        val nextCost = inheritedCost + newVol.volume() - current.value.volume()
        if (curCost <= bestCost || bestCost < 0) {
            candidate.v = current
            if (!isLeaf) {
                insertHierarchy(current.left as BoundingVolumeNode, target, curCost, nextCost, candidate)
                insertHierarchy(current.right as BoundingVolumeNode, target, curCost, nextCost, candidate)
            }
        }
    }

    fun remove(node: BoundingVolumeNode) {
        if (!dictionary.contains(node)) return
        dictionary.remove(node)
        if (node === root) root = null else {
            val onLeft = node.parent!!.left === node
            val brother: BoundingVolumeNode?
            brother = if (onLeft) node.parent!!.right as BoundingVolumeNode? else node.parent!!.left as BoundingVolumeNode?
            if (node.parent === root) {
                root = brother
                brother!!.parent = null
            } else {
                val onRootLeft = brother!!.parent!!.parent!!.left === brother!!.parent
                if (onRootLeft) brother!!.parent!!.parent!!.left = brother else brother!!.parent!!.parent!!.right = brother
                brother.parent = brother.parent!!.parent
            }
            var cur = brother
            while (cur!!.parent != null) {
                cur.parent!!.value = cur.parent!!.left!!.value joint cur.parent!!.right!!.value
                cur = cur.parent as BoundingVolumeNode?
            }
        }
    }

    fun intersect(volume: AABB): List<BoundingVolumeNode> {
        return if (root != null) {
            val list = ArrayList<BoundingVolumeNode>()
            val queue = LinkedList<BoundingVolumeNode>()
            queue.add(root!!)
            while (queue.size > 0) {
                val node = queue.removeFirst()
                val isLeaf = node.left == null
                val intersect = node.value.intersect(volume)
                if (intersect && isLeaf) {
                    list.add(node)
                } else if (intersect) {
                    queue.add(node.left as BoundingVolumeNode)
                    queue.add(node.right as BoundingVolumeNode)
                }
            }
            list
        } else emptyList()
    }
}