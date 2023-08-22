package id.teman.app.domain.usecase.user

import id.teman.app.data.dto.LoginRequest
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.UseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LoginUseCase @Inject constructor(private val repository: UserRepository) :
    UseCase<LoginRequest, Flow<String>> {

    override suspend fun execute(params: LoginRequest): Flow<String> {
        return repository.login(params)
    }
}