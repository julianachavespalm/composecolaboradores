package io.github.julianachavespalm.composecolaboradores.domain.repository

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import kotlinx.coroutines.flow.StateFlow

interface ColaboradorRepository {
    val colaboradores: StateFlow<List<Colaborador>>
    fun salvar(colaborador: Colaborador)
    fun remover(id: Int)
    fun isEmailValido(email: String): Boolean
}
