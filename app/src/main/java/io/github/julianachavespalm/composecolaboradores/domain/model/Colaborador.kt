package io.github.julianachavespalm.composecolaboradores.domain.model

import androidx.annotation.StringRes
import io.github.julianachavespalm.composecolaboradores.R

enum class Nivel(@StringRes val descricao: Int) {
    NENHUM(R.string.placeholder_nivel),
    ADMINISTRATIVO(R.string.nivel_administrativo),
    FINANCEIRO(R.string.nivel_financeiro),
    GERENCIA(R.string.nivel_gerencia),
    SUPORTE(R.string.nivel_suporte)
}


data class Colaborador(
    val id: Int,
    val nome: String,
    val email: String,
    val nivel: Nivel
)