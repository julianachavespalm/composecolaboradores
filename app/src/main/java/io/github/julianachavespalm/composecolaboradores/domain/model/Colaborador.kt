package io.github.julianachavespalm.composecolaboradores.domain.model

enum class Nivel(val descricao: String) {
    ADMINISTRATIVO("Administrativo"),
    FINANCEIRO("Financeiro"),
    GERENCIA("Gerência"),
    SUPORTE("Suporte")
}

data class Colaborador(
    val id: Int,
    val nome: String,
    val email: String,
    val nivel: Nivel
)
