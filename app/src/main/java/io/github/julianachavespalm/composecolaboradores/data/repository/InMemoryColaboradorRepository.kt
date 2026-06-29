package io.github.julianachavespalm.composecolaboradores.data.repository

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.domain.repository.ColaboradorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryColaboradorRepository : ColaboradorRepository {
    companion object {
        private val _colaboradores = MutableStateFlow<List<Colaborador>>(emptyList())
        private var proximoId = 1
    }

    override val colaboradores: StateFlow<List<Colaborador>> = _colaboradores.asStateFlow()
    
    override fun salvar(colaborador: Colaborador) {
        if (colaborador.nome.isBlank() || 
            !isEmailValido(colaborador.email) || 
            colaborador.nivel == Nivel.NENHUM) {
            return
        }

        val listaAtual = _colaboradores.value.toMutableList()
        val jaExiste = listaAtual.any { 
            it.id != colaborador.id &&
            it.nome.equals(colaborador.nome, ignoreCase = true) && 
            it.email.equals(colaborador.email, ignoreCase = true) && 
            it.nivel == colaborador.nivel 
        }
        if (jaExiste) return

        if (colaborador.id == 0) {
            listaAtual.add(colaborador.copy(id = proximoId++))
        } else {
            val index = listaAtual.indexOfFirst { it.id == colaborador.id }
            if (index != -1) {
                listaAtual[index] = colaborador
            }
        }
        _colaboradores.value = listaAtual
    }

    override fun isEmailValido(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    override fun remover(id: Int) {
        _colaboradores.value = _colaboradores.value.filter { it.id != id }
    }
}
