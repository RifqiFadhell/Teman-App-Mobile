package id.teman.app.domain.usecase

interface UseCase<P, R> {
    suspend fun execute(params: P): R
}

interface UseCaseWithoutParams<R> {
    suspend fun execute(): R
}