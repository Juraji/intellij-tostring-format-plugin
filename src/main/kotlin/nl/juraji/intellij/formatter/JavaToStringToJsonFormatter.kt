package nl.juraji.intellij.formatter

import java.util.*

class JavaToStringToJsonFormatter {

    fun format(input: String): String {
        val text: String = input.trim().let {
            if (!(input.startsWith(LEX_OBJ_START) || input.startsWith(LEX_LIST_START))) "{$it}"
            else it
        }

        val tokenizer = StringTokenizer(text, LEX_ALL, true)
        val output = StringBuilder()
        var lexContext = CurrentLexContext()

        while (tokenizer.hasMoreTokens()) {
            val token: String = tokenizer.nextToken().trim()
            if (token.isBlank()) continue

            lexContext = updateLexContextBefore(lexContext, token)
            output.run(renderToken(token, lexContext, tokenizer))
            lexContext = updateLexContextAfter(lexContext, token)
        }

        return output.toString()
    }

    private fun renderToken(
        token: String,
        ctx: CurrentLexContext,
        tokenizer: StringTokenizer,
    ): StringBuilder.() -> Unit = {
        val indent: String = REPL_INDENT.repeat(ctx.level)

        when (token) {
            LEX_OBJ_START -> append(REPL_OBJ_START, REPL_NEWLINE, indent)
            LEX_DATC_START -> append(REPL_ASSIGNMENT, REPL_OBJ_START, REPL_NEWLINE, indent)
            LEX_OBJ_END, LEX_DATC_END -> append(REPL_NEWLINE, indent, REPL_OBJ_END)
            LEX_LIST_START -> append(REPL_LIST_START, REPL_NEWLINE, indent)
            LEX_LIST_END -> append(REPL_NEWLINE, indent, REPL_LIST_END)
            LEX_SEPARATOR -> append(REPL_SEPARATOR, REPL_NEWLINE, indent)
            LEX_ASSIGNMENT -> {
                // When encountered assignment in list or directly after assignment, this should never happen,
                // treating it as part of the previous assign by appending current token and pulling in next token, then continue normally
                val isImproperAssignment = ctx.lexContext == LexContext.LIST
                        || (ctx.lexContext == LexContext.OBJ && ctx.lastLex == LEX_ASSIGNMENT)

                if (isImproperAssignment) {
                    replace(length - 1, length, LEX_ASSIGNMENT)
                    append(tokenizer.nextToken().trim(), REPL_TXT_BOUNDARY)
                } else append(REPL_ASSIGNMENT)
            }
            else -> append(REPL_TXT_BOUNDARY, token, REPL_TXT_BOUNDARY)
        }
    }

    private fun updateLexContextBefore(
        ctx: CurrentLexContext,
        token: String,
    ): CurrentLexContext = when (token) {
        LEX_OBJ_START, LEX_DATC_START -> ctx.copy(
            lexContext = LexContext.OBJ,
            level = ctx.level + 1
        )
        LEX_LIST_START -> ctx.copy(
            lexContext = LexContext.LIST,
            level = ctx.level + 1
        )
        LEX_OBJ_END, LEX_DATC_END, LEX_LIST_END -> ctx.copy(
            lexContext = LexContext.START,
            level = ctx.level - 1
        )
        else -> ctx
    }

    private fun updateLexContextAfter(
        lexContext: CurrentLexContext,
        token: String,
    ): CurrentLexContext =
        if (token in LEX_ALL) lexContext.copy(lastLex = token)
        else lexContext

    companion object {
        private const val LEX_OBJ_START = "{"
        private const val LEX_OBJ_END = "}"
        private const val LEX_DATC_START = "("
        private const val LEX_DATC_END = ")"
        private const val LEX_LIST_START = "["
        private const val LEX_LIST_END = "]"
        private const val LEX_SEPARATOR = ","
        private const val LEX_ASSIGNMENT = "="
        private const val LEX_ALL = "{}()[],="

        private const val REPL_OBJ_START = "{"
        private const val REPL_OBJ_END = "}"
        private const val REPL_LIST_START = "["
        private const val REPL_LIST_END = "]"
        private const val REPL_SEPARATOR = ","
        private const val REPL_ASSIGNMENT = ": "
        private const val REPL_TXT_BOUNDARY = "\""
        private const val REPL_INDENT = "  "
        private const val REPL_NEWLINE = "\n"
    }

    private data class CurrentLexContext(
        val lexContext: LexContext = LexContext.START,
        val lastLex: String = "",
        val level: Int = 0,
    )

    private enum class LexContext {
        START, OBJ, LIST
    }
}
