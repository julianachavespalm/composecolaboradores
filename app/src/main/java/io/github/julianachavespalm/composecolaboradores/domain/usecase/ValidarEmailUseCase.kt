package io.github.julianachavespalm.composecolaboradores.domain.usecase

import io.github.julianachavespalm.composecolaboradores.domain.repository.ColaboradorRepository

class ValidarEmailUseCase(private val repository: ColaboradorRepository) {
    operator fun invoke(email: String): Boolean = repository.isEmailValido(email)
}
