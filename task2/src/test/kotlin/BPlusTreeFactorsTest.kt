import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BPlusTreeFactorsTest : FunSpec({

    fun smokeScenario(factor: Int) {
        val t = BPlusTree<Int>(factor = factor)

        val keys = (1..200).toList()
        keys.forEach { t.insert(it, it * 10) }

        keys.forEach { t.get(it) shouldBe it * 10 }

        keys.filter { it % 2 == 0 }.forEach { t.delete(it) shouldBe true }

        keys.filter { it % 2 == 0 }.forEach { t.get(it) shouldBe null }
        keys.filter { it % 2 == 1 }.forEach { t.get(it) shouldBe it * 10 }
    }

    test("factor=3 smoke") { smokeScenario(3) }
    test("factor=4 smoke") { smokeScenario(4) }
    test("factor=5 smoke") { smokeScenario(5) }

    test("factor upper bound (7) smoke") { smokeScenario(7) }

    test("invalid factor throws") {
        io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
            BPlusTree<Int>(factor = 2)
        }
        io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
            BPlusTree<Int>(factor = 16)
        }
    }
})

