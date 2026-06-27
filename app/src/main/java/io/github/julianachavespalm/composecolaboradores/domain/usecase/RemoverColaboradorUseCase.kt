package io.github.julianachavespalm.composecolaboradores.domain.usecase

import io.github.julianachavespalm.composecolaboradores.domain.repository.ColaboradorRepository

class RemoverColaboradorUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(id: Int) = repository.remover(id)
}
