package id.teman.app.domain.usecase.user

import id.teman.app.data.dto.RegisterRequest
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.UseCase
import id.teman.app.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository,
    private val json: Json,
    private val preference: Preference
) : UseCase<RegisterRequest, Flow<Unit>> {

    override suspend fun execute(params: RegisterRequest): Flow<Unit> {
        return flow {
            repository.register(params)
                .catch { exception ->
                    throw exception
                }.collect {
                    val rawJson = json.encodeToString(it.second)
                    preference.setBearerToken(it.first)
                    preference.setUserInfo(rawJson)
                    emit(Unit)
                }
        }
    }
}