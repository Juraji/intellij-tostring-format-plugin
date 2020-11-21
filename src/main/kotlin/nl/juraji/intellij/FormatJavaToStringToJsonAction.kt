package nl.juraji.intellij

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import nl.juraji.intellij.formatter.JavaToStringToJsonFormatter


class FormatJavaToStringToJsonAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selection: SelectionModel = editor.selectionModel
        val doc: Document = editor.document

        val action: () -> Unit =
            if (selection.hasSelection()) runFormatSelection(doc, selection)
            else runFormatDocument(doc)

        WriteCommandAction.runWriteCommandAction(project, action)
    }

    private fun runFormatDocument(
        doc: Document
    ): () -> Unit = JavaToStringToJsonFormatter()
        .format(doc.text)
        .let { { doc.setText(it) } }

    private fun runFormatSelection(
        doc: Document,
        selection: SelectionModel
    ): () -> Unit = selection.selectedText
        ?.let { JavaToStringToJsonFormatter().format(it) }
        ?.let { { doc.replaceString(selection.selectionStart, selection.selectionEnd, it) } }
        ?: {}
}
