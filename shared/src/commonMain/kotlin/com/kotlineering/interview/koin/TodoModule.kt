package com.kotlineering.interview.koin

import com.kotlineering.interview.domain.todo.ToDoApi
import com.kotlineering.interview.domain.todo.ToDoRepository
import com.kotlineering.interview.domain.todo.ToDoService
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun todoModule() = module {

    single(named("todo-api-domain")) {
        "https://jsonplaceholder.typicode.com/"
    }

    single {
        ToDoApi(
            client = get(),
            domain = get(named("todo-api-domain"))
        )
    }

    single {
        ToDoRepository(
            api = get(),
            db = get(),
            dev = get()
        )
    }

    single {
        ToDoService(
            get(),
            get()
        )
    }
}

