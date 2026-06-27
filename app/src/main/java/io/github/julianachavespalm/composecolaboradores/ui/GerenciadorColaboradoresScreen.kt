package io.github.julianachavespalm.composecolaboradores.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
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
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("lista_colaboradores"),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            if (viewModel.colaboradorEmEdicao == null) R.string.label_cadastrar 
                            else R.string.label_editar
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    ColaboradorForm(
                        viewModel = viewModel,
                        focusRequester = focusRequester
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

            if (listaColaboradores.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum colaborador cadastrado",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(listaColaboradores, key = { it.id }) { colaborador ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
            
            item {
                Spacer(modifier = Modifier.imePadding())
                Spacer(modifier = Modifier.navigationBarsPadding())
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

@Composable
fun ColaboradorForm(
    viewModel: ColaboradorViewModel,
    focusRequester: FocusRequester
) {
    var menuExpandido by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        FormField(
            value = viewModel.nome,
            onValueChange = { viewModel.onNomeChange(it) },
            label = stringResource(R.string.label_nome),
            icon = Icons.Default.Person,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FormField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = stringResource(R.string.label_email),
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = viewModel.email.isNotBlank() && !viewModel.isEmailValido,
            supportingText = if (viewModel.email.isNotBlank() && !viewModel.isEmailValido) {
                if (viewModel.email.contains(" ")) stringResource(R.string.erro_email_espaco)
                else stringResource(R.string.erro_email_invalido)
            } else null,
            onImeAction = { menuExpandido = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (viewModel.nivelSelecionado == Nivel.NENHUM) "" else viewModel.nivelSelecionado.descricao,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_nivel)) },
                placeholder = { Text(stringResource(R.string.placeholder_nivel)) },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { menuExpandido = !menuExpandido }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            DropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false },
                modifier = Modifier.fillMaxWidth(0.85f)
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

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    viewModel.limparCampos()
                    focusManager.clearFocus()
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                enabled = viewModel.estaEditandoOuPreenchido
            ) {
                Text(stringResource(R.string.acao_cancelar))
            }

            Button(
                onClick = {
                    viewModel.salvar()
                    focusManager.clearFocus()
                },
                modifier = Modifier.weight(1f),
                enabled = viewModel.podeSalvar,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    if (viewModel.colaboradorEmEdicao == null) stringResource(R.string.acao_salvar)
                    else stringResource(R.string.acao_atualizar)
                )
            }
        }
    }
}

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() },
            onSearch = { onImeAction() },
            onSend = { onImeAction() },
            onGo = { onImeAction() }
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun ColaboradorCard(
    colaborador: Colaborador,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { 
                Text(
                    colaborador.nome, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ) 
            },
            supportingContent = {
                Column {
                    Text(
                        text = colaborador.email, 
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SuggestionChip(
                        onClick = { },
                        label = { Text(colaborador.nivel.descricao) },
                        icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        border = null,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit, 
                            contentDescription = stringResource(R.string.label_editar),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete, 
                            contentDescription = stringResource(R.string.acao_excluir),
                            tint = MaterialTheme.colorScheme.error
                        )
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
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.acao_excluir))
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
