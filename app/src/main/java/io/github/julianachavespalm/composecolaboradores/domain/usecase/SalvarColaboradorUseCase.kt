package io.github.julianachavespalm.composecolaboradores.domain.usecase

import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.repository.ColaboradorRepository

class SalvarColaboradorUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(colaborador: Colaborador) = repository.salvar(colaborador)
}
