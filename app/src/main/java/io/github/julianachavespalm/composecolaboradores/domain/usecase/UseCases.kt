package io.github.julianachavespalm.composecolaboradores.domain.usecase

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import kotlinx.coroutines.flow.StateFlow

class GetColaboradoresUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(): StateFlow<List<Colaborador>> = repository.colaboradores
}

class SalvarColaboradorUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(colaborador: Colaborador) = repository.salvar(colaborador)
}

class RemoverColaboradorUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(id: Int) = repository.remover(id)
}
