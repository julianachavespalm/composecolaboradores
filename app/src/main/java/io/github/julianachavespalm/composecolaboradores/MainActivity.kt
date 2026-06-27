package io.github.julianachavespalm.composecolaboradores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.julianachavespalm.composecolaboradores.data.repository.InMemoryColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.ValidarEmailUseCase
import io.github.julianachavespalm.composecolaboradores.ui.gerenciador.ColaboradorViewModel
import io.github.julianachavespalm.composecolaboradores.ui.gerenciador.GerenciadorColaboradoresScreen
import io.github.julianachavespalm.composecolaboradores.ui.theme.ComposeColaboradoresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val repository = InMemoryColaboradorRepository()
        val viewModel = ColaboradorViewModel(
            GetColaboradoresUseCase(repository),
            SalvarColaboradorUseCase(repository),
            RemoverColaboradorUseCase(repository),
            ValidarEmailUseCase(repository)
        )

        setContent {
            ComposeColaboradoresTheme {
                GerenciadorColaboradoresScreen(viewModel)
            }
        }
    }
}
