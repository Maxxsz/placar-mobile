package data

data class GameResult(
    val result: String,
    val duracao: String,
    val heartCount: String,
    val dataHora: String,
    val latitude: Double?,
    val longitude: Double?
)