import kotlin.math.ceil


interface Tree<V> {
    fun get(key: Int): V?
    fun insert(key: Int, value: V)
    fun delete(key: Int): Boolean
}

class BPlusTree<V>(
    private val factor: Int = 3
) : Tree<V> {
    init {
        require(factor in 3..7) { "factor must be between 3..7" }
    }

    private var root: Node<V> = LeafNode()
    private val maxKeys = factor - 1
    private val minChildren = ceil(factor / 2.0).toInt()
    private val minKeysLeaf = maxKeys / 2
    private val operationTrace: MutableList<String> = mutableListOf()
    private var traceSeq: Int = 0

    fun traceSnapshot(): List<String> = operationTrace.toList()

    fun clearTrace() {
        operationTrace.clear()
        traceSeq = 0
    }

    override fun get(key: Int): V? {
        trace("get", "start", key)
        val leaf = findLeaf(key)
        val index = leaf.keys.binarySearch(key)
        return if (index >= 0) {
            trace("get", "hit", key, "leafKeys=${leaf.keys}")
            leaf.values[index]
        } else {
            trace("get", "miss", key, "leafKeys=${leaf.keys}")
            null
        }
    }

    override fun insert(key: Int, value: V) {
        trace("insert", "start", key)
        val leaf = findLeaf(key)
        val pos = leaf.keys.binarySearch(key)

        if (pos >= 0) {
            leaf.values[pos] = value
            trace("insert", "update", key, "leafKeys=${leaf.keys}")
            return
        }

        val insertAt = -pos - 1
        leaf.keys.add(insertAt, key)
        leaf.values.add(insertAt, value)
        trace("insert", "placed", key, "leafKeys=${leaf.keys}")

        if (leaf.keys.size > maxKeys) {
            trace("insert", "split_leaf_required", key, "leafKeys=${leaf.keys}")
            splitLeaf(leaf)
        }
    }

    override fun delete(key: Int): Boolean {
        trace("delete", "start", key)
        val leaf = findLeaf(key)
        val pos = leaf.keys.binarySearch(key)
        if (pos < 0) {
            trace("delete", "not_found", key, "leafKeys=${leaf.keys}")
            return false
        }

        leaf.keys.removeAt(pos)
        leaf.values.removeAt(pos)
        trace("delete", "removed", key, "leafKeys=${leaf.keys}")

        if (leaf === root) {
            trace("delete", "root_leaf_done", key, "leafKeys=${leaf.keys}")
            return true
        }

        if (leaf.keys.size >= minKeysLeaf) {
            trace("delete", "no_rebalance_needed", key, "leafKeys=${leaf.keys}")
            refreshKeysUpwards(leaf.parent)
            return true
        }

        trace("delete", "rebalance_leaf_required", key, "leafKeys=${leaf.keys}")
        rebalanceLeafAfterDelete(leaf)
        return true
    }

    private fun splitLeaf(leaf: LeafNode<V>) {
        trace("split_leaf", "start", details = "keys=${leaf.keys}")
        val splitIndex = leaf.keys.size - minKeysLeaf
        val right = LeafNode<V>()

        right.keys.addAll(leaf.keys.subList(splitIndex, leaf.keys.size))
        right.values.addAll(leaf.values.subList(splitIndex, leaf.values.size))

        leaf.keys.subList(splitIndex, leaf.keys.size).clear()
        leaf.values.subList(splitIndex, leaf.values.size).clear()

        right.next = leaf.next
        right.prev = leaf
        leaf.next?.prev = right
        leaf.next = right

        trace("split_leaf", "done", details = "left=${leaf.keys};right=${right.keys}")

        insertIntoParent(leaf, right)
    }

    private fun splitBranch(node: BranchNode<V>) {
        trace("split_branch", "start", details = "keys=${node.keys};children=${node.children.size}")
        val splitChildIndex = node.children.size / 2

        val right = BranchNode<V>()
        right.children.addAll(node.children.subList(splitChildIndex, node.children.size))
        right.children.forEach { it.parent = right }

        node.children.subList(splitChildIndex, node.children.size).clear()

        rebuildKeys(node)
        rebuildKeys(right)

        trace("split_branch", "done", details = "left=${node.keys};right=${right.keys}")

        insertIntoParent(node, right)
    }

    private fun insertIntoParent(left: Node<V>, right: Node<V>) {
        val parent = left.parent

        if (parent == null) {
            val newRoot = BranchNode<V>()
            newRoot.children.add(left)
            newRoot.children.add(right)
            left.parent = newRoot
            right.parent = newRoot
            rebuildKeys(newRoot)
            root = newRoot
            trace("insert_parent", "new_root", details = "keys=${newRoot.keys};children=${newRoot.children.size}")
            return
        }

        val leftIndex = parent.children.indexOf(left)
        require(leftIndex >= 0) { "Parent does not contain left child" }

        parent.children.add(leftIndex + 1, right)
        right.parent = parent
        rebuildKeys(parent)
        trace("insert_parent", "insert_child", details = "parentKeys=${parent.keys};children=${parent.children.size}")

        if (parent.children.size > factor) {
            trace("insert_parent", "split_branch_required", details = "parentKeys=${parent.keys};children=${parent.children.size}")
            splitBranch(parent)
        }
    }

    private fun rebalanceLeafAfterDelete(leaf: LeafNode<V>) {
        val parent = leaf.parent ?: return
        val index = parent.children.indexOf(leaf)

        val left = if (index > 0) parent.children[index - 1] as LeafNode<V> else null
        val right = if (index < parent.children.lastIndex) parent.children[index + 1] as LeafNode<V> else null

        if (left != null && left.keys.size > minKeysLeaf) {
            trace("rebalance_leaf", "borrow_left", details = "leaf=${leaf.keys};left=${left.keys}")
            leaf.keys.add(0, left.keys.removeAt(left.keys.lastIndex))
            leaf.values.add(0, left.values.removeAt(left.values.lastIndex))
            refreshKeysUpwards(parent)
            return
        }

        if (right != null && right.keys.size > minKeysLeaf) {
            trace("rebalance_leaf", "borrow_right", details = "leaf=${leaf.keys};right=${right.keys}")
            leaf.keys.add(right.keys.removeAt(0))
            leaf.values.add(right.values.removeAt(0))
            refreshKeysUpwards(parent)
            return
        }

        if (left != null) {
            trace("rebalance_leaf", "merge_into_left", details = "leaf=${leaf.keys};left=${left.keys}")
            left.keys.addAll(leaf.keys)
            left.values.addAll(leaf.values)
            left.next = leaf.next
            leaf.next?.prev = left

            parent.children.removeAt(index)
            rebuildKeys(parent)
            rebalanceBranchAfterDelete(parent)
            return
        }

        if (right != null) {
            trace("rebalance_leaf", "merge_right_into_leaf", details = "leaf=${leaf.keys};right=${right.keys}")
            leaf.keys.addAll(right.keys)
            leaf.values.addAll(right.values)
            leaf.next = right.next
            right.next?.prev = leaf

            parent.children.removeAt(index + 1)
            rebuildKeys(parent)
            rebalanceBranchAfterDelete(parent)
        }
    }

    private fun rebalanceBranchAfterDelete(node: BranchNode<V>) {
        if (node === root) {
            if (node.children.size == 1) {
                root = node.children.first()
                root.parent = null
                trace("rebalance_branch", "root_shrink", details = "newRootType=${root::class.simpleName}")
            } else {
                rebuildKeys(node)
                trace("rebalance_branch", "root_rebuild", details = "keys=${node.keys};children=${node.children.size}")
            }
            return
        }

        if (node.children.size >= minChildren) {
            rebuildKeys(node)
            trace("rebalance_branch", "enough_children", details = "keys=${node.keys};children=${node.children.size}")
            refreshKeysUpwards(node.parent)
            return
        }

        val parent = node.parent ?: return
        val index = parent.children.indexOf(node)

        val left = if (index > 0) parent.children[index - 1] as BranchNode<V> else null
        val right = if (index < parent.children.lastIndex) parent.children[index + 1] as BranchNode<V> else null

        if (left != null && left.children.size > minChildren) {
            trace("rebalance_branch", "borrow_left", details = "nodeChildren=${node.children.size};leftChildren=${left.children.size}")
            val moved = left.children.removeAt(left.children.lastIndex)
            moved.parent = node
            node.children.add(0, moved)

            rebuildKeys(left)
            rebuildKeys(node)
            refreshKeysUpwards(parent)
            return
        }

        if (right != null && right.children.size > minChildren) {
            trace("rebalance_branch", "borrow_right", details = "nodeChildren=${node.children.size};rightChildren=${right.children.size}")
            val moved = right.children.removeAt(0)
            moved.parent = node
            node.children.add(moved)

            rebuildKeys(right)
            rebuildKeys(node)
            refreshKeysUpwards(parent)
            return
        }

        if (left != null) {
            trace("rebalance_branch", "merge_into_left", details = "nodeChildren=${node.children.size};leftChildren=${left.children.size}")
            node.children.forEach { it.parent = left }
            left.children.addAll(node.children)

            parent.children.removeAt(index)
            rebuildKeys(left)
            rebuildKeys(parent)
            rebalanceBranchAfterDelete(parent)
            return
        }

        if (right != null) {
            trace("rebalance_branch", "merge_right_into_node", details = "nodeChildren=${node.children.size};rightChildren=${right.children.size}")
            right.children.forEach { it.parent = node }
            node.children.addAll(right.children)

            parent.children.removeAt(index + 1)
            rebuildKeys(node)
            rebuildKeys(parent)
            rebalanceBranchAfterDelete(parent)
        }
    }

    private fun refreshKeysUpwards(start: BranchNode<V>?) {
        var current = start
        while (current != null) {
            rebuildKeys(current)
            trace("refresh", "branch_keys_rebuilt", details = "keys=${current.keys};children=${current.children.size}")
            current = current.parent
        }
    }

    private fun rebuildKeys(node: BranchNode<V>) {
        node.keys.clear()
        for (i in 1 until node.children.size) {
            node.keys.add(firstKey(node.children[i]))
        }
    }

    private fun firstKey(node: Node<V>): Int {
        return when (node) {
            is LeafNode<V> -> node.keys.firstOrNull()
                ?: error("Leaf node has no keys (unexpected empty leaf)")
            is BranchNode<V> -> firstKey(node.children.first())
        }
    }

    private fun findLeaf(key: Int): LeafNode<V> {
        trace("find_leaf", "start", key)
        var node = root
        while (node is BranchNode<V>) {
            val childIndex = upperBound(node.keys, key)
            trace("find_leaf", "step", key, "branchKeys=${node.keys};childIndex=$childIndex")
            node = node.children[childIndex]
        }
        trace("find_leaf", "done", key, "leafKeys=${node.keys}")
        return node as LeafNode<V>
    }

    private fun trace(op: String, stage: String, key: Int? = null, details: String = "") {
        traceSeq += 1
        val keyPart = key?.toString() ?: "-"
        val suffix = if (details.isEmpty()) "" else "|$details"
        operationTrace.add("$traceSeq|$op|$stage|$keyPart$suffix")
    }

    private fun upperBound(keys: List<Int>, key: Int): Int {
        var l = 0
        var r = keys.size
        while (l < r) {
            val m = (l + r) ushr 1
            if (keys[m] <= key) l = m + 1 else r = m
        }
        return l
    }
}

private sealed class Node<V> {
    abstract val keys: MutableList<Int>
    abstract var parent: BranchNode<V>?
}

private class BranchNode<V>(
    override val keys: MutableList<Int> = mutableListOf(),
    val children: MutableList<Node<V>> = mutableListOf()
) : Node<V>() {
    override var parent: BranchNode<V>? = null
}

private class LeafNode<V>(
    override val keys: MutableList<Int> = mutableListOf(),
    val values: MutableList<V> = mutableListOf()
) : Node<V>() {
    override var parent: BranchNode<V>? = null
    var next: LeafNode<V>? = null
    var prev: LeafNode<V>? = null
}