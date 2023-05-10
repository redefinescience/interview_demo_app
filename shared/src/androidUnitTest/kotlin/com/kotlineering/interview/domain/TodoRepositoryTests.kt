package com.kotlineering.interview.domain

import com.kotlineering.interview.TestComponents
import com.kotlineering.interview.db.Todos
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.todo.ToDoRepository
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

class TodoRepositoryTests {

    @Test
    fun `Real Test - Basic`() = runBlocking {
        val repo = ToDoRepository(
            api = TestComponents.getTodoApi(),
            db = TestComponents.getDatabase(),
            dev = DeveloperRepository()
        )

        assertTrue {
            repo.refreshTodoList(1) is ServiceState.Done
        }

        expect(9) {
            repo.getTodoList(1).executeAsList().size
        }

        expect(20) {
            repo.getTodoList(1, true).executeAsList().size
        }

        expect(0) {
            repo.db.databaseQueries.getLowestTodoSequence().executeAsOne()
        }

        expect(20) {
            repo.db.databaseQueries.getGreatestTodoId().executeAsOne()
        }

        repo.newTodo(
            Todos(
                id = 0,
                userId = 1,
                title = "this is new",
                completed = 0,
                sequence = 1
            )
        )

        expect(-1) {
            repo.db.databaseQueries.getLowestTodoSequence().executeAsOne()
        }

        expect(21) {
            repo.db.databaseQueries.getGreatestTodoId().executeAsOne()
        }

        expect("this is new") {
            repo.getTodoList(userId = 1).executeAsList()[0].title
        }
    }
}
