package id.teman.app.domain.usecase.user

import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.UseCaseWithoutParams
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RequestOtpUseCase @Inject constructor(private val repository: UserRepository) :
    UseCaseWithoutParams<Flow<Int>> {
    override suspend fun execute(): Flow<Int> {
        return repository.sendOtp()
    }
}