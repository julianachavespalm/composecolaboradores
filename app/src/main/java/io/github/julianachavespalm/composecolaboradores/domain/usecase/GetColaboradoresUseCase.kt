package io.github.julianachavespalm.composecolaboradores.domain.usecase

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.repository.ColaboradorRepository
import kotlinx.coroutines.flow.StateFlow

class GetColaboradoresUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(): StateFlow<List<Colaborador>> = repository.colaboradores
}
