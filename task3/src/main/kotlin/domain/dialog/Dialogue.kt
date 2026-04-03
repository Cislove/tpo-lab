package domain.dialog

import domain.Character

data class DialogueTurn(
    val speaker: Character,
    val text: String
)

data class Dialogue(
    val turns: List<DialogueTurn>
) {
    init {
        require(turns.isNotEmpty()) { "Dialogue must contain at least one turn" }
    }
}

