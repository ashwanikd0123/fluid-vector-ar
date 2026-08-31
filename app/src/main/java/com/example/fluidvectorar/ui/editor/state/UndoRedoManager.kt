package com.example.fluidvectorar.ui.editor.state

class UndoRedoManager(val maxSteps: Int = 50) {

    val undoStack = ArrayDeque<CanvasAction>()
    val redoStack = ArrayDeque<CanvasAction>()

    fun addAction(action: CanvasAction) {
        redoStack.clear()

        undoStack.addLast(action)

        if (undoStack.size > maxSteps) {
            undoStack.removeFirst()
        }
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun popUndo(): CanvasAction? {
        if (!canUndo()) return null
        val action = undoStack.removeLast()
        redoStack.addLast(action)
        return action
    }

    fun popRedo(): CanvasAction? {
        if (!canRedo()) return null
        val action = redoStack.removeLast()
        undoStack.addLast(action)
        return action
    }
}