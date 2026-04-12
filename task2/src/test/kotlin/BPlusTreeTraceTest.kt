import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

class BPlusTreeTraceTest : FunSpec({

    fun List<String>.semantic(): List<String> = this.map { it.substringAfter('|') }

    test("insert triggers leaf split for factor=3 and logs key steps") {
        val t = BPlusTree<Int>(factor = 3)
        t.clearTrace()

        t.insert(1, 1)
        t.insert(2, 2)
        t.insert(3, 3)

        val sem = t.traceSnapshot().semantic()

        sem.shouldContainInOrder(
            "insert|start|1",
            "find_leaf|start|1",
            "find_leaf|done|1|leafKeys=[]",
            "insert|placed|1|leafKeys=[1]",

            "insert|start|2",
            "find_leaf|start|2",
            "find_leaf|done|2|leafKeys=[1]",
            "insert|placed|2|leafKeys=[1, 2]",

            "insert|start|3",
            "find_leaf|start|3",
            "find_leaf|done|3|leafKeys=[1, 2]",
            "insert|placed|3|leafKeys=[1, 2, 3]",
            "insert|split_leaf_required|3|leafKeys=[1, 2, 3]",
            "split_leaf|start|-|keys=[1, 2, 3]",
            "split_leaf|done|-|left=[1, 2];right=[3]"
        )

        sem.any { it.startsWith("insert_parent|new_root") } shouldBe true
    }

    test("delete can cause rebalance and logs it (factor=3)") {
        val t = BPlusTree<Int>(factor = 3)
        (1..6).forEach { t.insert(it, it) }
        t.clearTrace()

        t.delete(1) shouldBe true
        t.delete(2) shouldBe true

        val sem = t.traceSnapshot().semantic()

        sem.any { it.startsWith("delete|rebalance_leaf_required") } shouldBe true
        sem.any {
            it.startsWith("rebalance_leaf|borrow_left") ||
                it.startsWith("rebalance_leaf|borrow_right") ||
                it.startsWith("rebalance_leaf|merge_into_left") ||
                it.startsWith("rebalance_leaf|merge_right_into_leaf")
        } shouldBe true
    }

    test("get logs hit/miss and leaf keys") {
        val t = BPlusTree<Int>(factor = 3)
        t.insert(10, 100)
        t.insert(20, 200)
        t.clearTrace()

        t.get(10) shouldBe 100
        t.get(15) shouldBe null

        val sem = t.traceSnapshot().semantic()

        sem.shouldContainInOrder(
            "get|start|10",
            "find_leaf|start|10",
            "find_leaf|done|10|leafKeys=[10, 20]",
            "get|hit|10|leafKeys=[10, 20]",

            "get|start|15",
            "find_leaf|start|15",
            "find_leaf|done|15|leafKeys=[10, 20]",
            "get|miss|15|leafKeys=[10, 20]"
        )
    }
})

