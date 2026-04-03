package domain.logic

sealed interface Fact {
    data object HasMatches : Fact
    data object Smokes : Fact
    data object DrinkAlcohol : Fact
    data object HasMoney : Fact
    data object HasSex : Fact

    data class Labeled(val label: Label) : Fact
}

enum class Label {
    NOT_GAY,
    GAY
}

