package io.github.julianachavespalm.composecolaboradores.ui.gerenciador.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.ui.TestTags

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
                modifier = Modifier.testTag(TestTags.BOTAO_EXCLUIR),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.acao_excluir))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(TestTags.BOTAO_CANCELAR)
            ) {
                Text(stringResource(R.string.acao_cancelar))
            }
        },
        title = { Text(stringResource(R.string.confirmacao_excluir_titulo)) },
        text = { Text(stringResource(R.string.confirmacao_excluir_mensagem)) }
    )
}
