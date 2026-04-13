import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BPlusTreeApiTest : FunSpec({

    context("API basics") {
        test("get on empty returns null") {
            val t = BPlusTree<String>(factor = 3)
            t.get(1) shouldBe null
        }

        test("insert then get returns value") {
            val t = BPlusTree<String>(factor = 3)
            t.insert(10, "a")
            t.get(10) shouldBe "a"
        }

        test("overwrite with same key keeps last value") {
            val t = BPlusTree<String>(factor = 3)
            repeat(100) { i ->
                t.insert(42, "v$i")
            }
            t.get(42) shouldBe "v99"
        }

        test("delete non-existing returns false") {
            val t = BPlusTree<String>(factor = 3)
            t.delete(123) shouldBe false
        }

        test("delete existing returns true and removes") {
            val t = BPlusTree<String>(factor = 3)
            t.insert(1, "x")
            t.delete(1) shouldBe true
            t.get(1) shouldBe null
        }
    }
})

