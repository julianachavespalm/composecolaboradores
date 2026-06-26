package io.github.julianachavespalm.composecolaboradores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.ValidarEmailUseCase
import io.github.julianachavespalm.composecolaboradores.ui.ColaboradorViewModel
import io.github.julianachavespalm.composecolaboradores.ui.theme.ComposeColaboradoresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val repository = ColaboradorRepository()
        val viewModel = ColaboradorViewModel(
            GetColaboradoresUseCase(repository),
            SalvarColaboradorUseCase(repository),
            RemoverColaboradorUseCase(repository),
            ValidarEmailUseCase(repository)
        )

        setContent {
            ComposeColaboradoresTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        GerenciadorColaboradoresScreen(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciadorColaboradoresScreen(viewModel: ColaboradorViewModel) {
    val listaColaboradores by viewModel.colaboradores.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }

    var colaboradorParaExcluir by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.limparCampos() }) {
                Icon(Icons.Default.Edit, contentDescription = "Novo Cadastro")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = if (viewModel.colaboradorEmEdicao == null) "Cadastrar Colaborador" else "Editar Colaborador",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = viewModel.nome,
                onValueChange = { viewModel.onNomeChange(it) },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.email.isNotBlank() && !viewModel.isEmailValido
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (viewModel.nivelSelecionado == Nivel.NENHUM) "" else viewModel.nivelSelecionado.descricao,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nível") },
                    placeholder = { Text("Selecione um nível") },
                    trailingIcon = {
                        IconButton(onClick = { menuExpandido = !menuExpandido }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = menuExpandido, 
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Nivel.entries.filter { it != Nivel.NENHUM }.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel.descricao) },
                            onClick = {
                                viewModel.onNivelChange(nivel)
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.salvar() },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.podeSalvar,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(if (viewModel.colaboradorEmEdicao == null) "Salvar Colaborador" else "Atualizar Dados")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaColaboradores) { colaborador ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        ListItem(
                            headlineContent = { Text(colaborador.nome, style = MaterialTheme.typography.titleMedium) },
                            supportingContent = {
                                Column {
                                    Text(colaborador.email)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    AssistChip(
                                        onClick = { },
                                        label = { Text(colaborador.nivel.descricao) }
                                    )
                                }
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { viewModel.editar(colaborador) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { colaboradorParaExcluir = colaborador.id }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (colaboradorParaExcluir != null) {
        AlertDialog(
            onDismissRequest = { colaboradorParaExcluir = null },
            confirmButton = {
                TextButton(onClick = {
                    colaboradorParaExcluir?.let { viewModel.remover(it) }
                    colaboradorParaExcluir = null
                }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { colaboradorParaExcluir = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja remover este colaborador? Esta ação não pode ser desfeita.") }
        )
    }
}
