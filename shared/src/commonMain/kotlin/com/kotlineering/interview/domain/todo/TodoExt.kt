package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.db.Todos

fun Todos.isCompleted() = this.completed > 0