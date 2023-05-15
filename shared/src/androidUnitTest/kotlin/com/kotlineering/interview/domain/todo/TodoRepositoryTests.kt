package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.TestComponents
import com.kotlineering.interview.db.Todos
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

class TodoRepositoryTests {

    private fun `realtest - basic fetch and filter`(): ToDoRepository = runBlocking {
        ToDoRepository(
            api = TestComponents.getTodoApi(),
            db = TestComponents.getDatabase(),
            dev = DeveloperRepository()
        ).also { repo ->
            assertTrue {
                repo.refreshTodoList(userId = 1) is ServiceState.Done
            }

            expect(9) {
                repo.db.databaseQueries.getTodos(userId = 1, 0).executeAsList().size
            }

            expect(20) {
                repo.db.databaseQueries.getTodos(userId = 1, completed = 1).executeAsList().size
            }

            // Validate the queries used by repo utility functions
            expect(0) {
                repo.db.databaseQueries.getLowestTodoSequence().executeAsList().first()
            }

            expect(20) {
                repo.db.databaseQueries.getGreatestTodoId().executeAsList().first()
            }
        }
    }

    private fun `realtest - add and update`(repo: ToDoRepository) = runBlocking {
        expect(
            Todos(
                id = 21, // Expected to have been generated
                userId = 1,
                title = "this is new",
                completed = 0,
                sequence = -1 // expected to have been generated
            )
        ) {
            repo.newTodo(
                Todos(
                    id = 0,
                    userId = 1,
                    title = "this is new",
                    completed = 0,
                    sequence = 1
                )
            )
            repo.db.databaseQueries.getTodos(userId = 1, completed = 1).executeAsList().first()
        }

        expect(
            Todos(
                id = 21,
                userId = 1,
                title = "new is this",
                completed = 1,
                sequence = -1
            )
        ) {
            repo.updateTodo(
                Todos(
                    id = 21,
                    userId = 1,
                    title = "new is this",
                    completed = 1,
                    sequence = -1
                )
            )
            repo.db.databaseQueries.getTodos(userId = 1, completed = 1).executeAsList().first()
        }
    }

    @Test
    fun `realtest - smoke test`() = runBlocking {
        val repo = `realtest - basic fetch and filter`()
        `realtest - add and update`(repo)
    }
}
