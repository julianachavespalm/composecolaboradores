package io.github.julianachavespalm.composecolaboradores.data.repository

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColaboradorRepository {
    private val _colaboradores = MutableStateFlow<List<Colaborador>>(emptyList())
    val colaboradores: StateFlow<List<Colaborador>> = _colaboradores.asStateFlow()
    
    private var proximoId = 1

    fun salvar(colaborador: Colaborador) {
        // Validação na camada de serviço (SRV)
        if (colaborador.nome.isBlank() || 
            !isEmailValido(colaborador.email) || 
            colaborador.nivel == Nivel.NENHUM) {
            return
        }

        val listaAtual = _colaboradores.value.toMutableList()
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

    fun isEmailValido(email: String): Boolean {
        if (email.count { it == '@' } != 1) return false
        
        val antesDoAt = email.substringBefore("@")
        val depoisDoAt = email.substringAfter("@")
        
        if (antesDoAt.isEmpty()) return false
        
        if (!depoisDoAt.contains(".")) return false
        
        val dominio = depoisDoAt.substringBefore(".")
        val extensao = depoisDoAt.substringAfterLast(".")

        return dominio.length >= 2 && extensao.length >= 2
    }

    fun remover(id: Int) {
        _colaboradores.value = _colaboradores.value.filter { it.id != id }
    }
}
