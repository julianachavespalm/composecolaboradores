package io.github.julianachavespalm.composecolaboradores.ui

import android.util.EventLogTags
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciadorColaboradoresScreen(viewModel: ColaboradorViewModel) {
    val listaColaboradores by viewModel.colaboradores.collectAsState()
    var colaboradorParaExcluir by remember { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.limparCampos()
                focusRequester.requestFocus()
            }) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.label_cadastrar))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ColaboradorForm(viewModel, focusRequester)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("lista_colaboradores"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaColaboradores) { colaborador ->
                    ColaboradorCard(
                        colaborador = colaborador,
                        onEdit = {
                            viewModel.editar(colaborador)
                            focusRequester.requestFocus()
                        },
                        onDelete = { colaboradorParaExcluir = colaborador.id }
                    )
                }
            }
        }
    }

    if (colaboradorParaExcluir != null) {
        ConfirmacaoExclusaoDialog(
            onConfirm = {
                colaboradorParaExcluir?.let { viewModel.remover(it) }
                colaboradorParaExcluir = null
            },
            onDismiss = { colaboradorParaExcluir = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaboradorForm(
    viewModel: ColaboradorViewModel,
    focusRequester: FocusRequester
) {
    var menuExpandido by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            text = if (viewModel.colaboradorEmEdicao == null) stringResource(R.string.label_cadastrar)
            else stringResource(R.string.label_editar),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.nome,
            onValueChange = { viewModel.onNomeChange(it) },
            label = { Text(stringResource(R.string.label_nome)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text(stringResource(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.email.isNotBlank() && !viewModel.isEmailValido,
            supportingText = {
                if (viewModel.email.isNotBlank() && !viewModel.isEmailValido) {
                    val mensagem = if (viewModel.email.contains(" ")) {
                        stringResource(R.string.erro_email_espaco)
                    } else {
                        stringResource(R.string.erro_email_invalido)
                    }
                    Text(text = mensagem)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { 
                    focusManager.moveFocus(FocusDirection.Down)
                    menuExpandido = true
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (viewModel.nivelSelecionado == Nivel.NENHUM) "" else viewModel.nivelSelecionado.descricao,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_nivel)) },
                placeholder = { Text(stringResource(R.string.placeholder_nivel)) },
                trailingIcon = {
                    IconButton(onClick = { menuExpandido = !menuExpandido }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.placeholder_nivel)
                        )
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
                        },
                        modifier = Modifier.testTag("menu_item_${nivel.name}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.salvar()
                    focusRequester.requestFocus()
                },
                modifier = if (viewModel.estaEditandoOuPreenchido) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                enabled = viewModel.podeSalvar,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(
                    if (viewModel.colaboradorEmEdicao == null) stringResource(R.string.acao_salvar)
                    else stringResource(R.string.acao_atualizar)
                )
            }

            if (viewModel.estaEditandoOuPreenchido) {
                OutlinedButton(
                    onClick = {
                        viewModel.limparCampos()
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.acao_cancelar))
                }
            }
        }
    }
}

@Composable
fun ColaboradorCard(
    colaborador: Colaborador,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.label_editar), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.acao_excluir), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@Composable
fun ConfirmacaoExclusaoDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.acao_excluir), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.acao_cancelar))
            }
        },
        title = { Text(stringResource(R.string.confirmacao_excluir_titulo)) },
        text = { Text(stringResource(R.string.confirmacao_excluir_mensagem)) }
    )
}
