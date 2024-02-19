package com.cureius.pocket.feature_pot.domain.use_case

import com.cureius.pocket.feature_pot.domain.repository.PotRepository
import kotlinx.coroutines.flow.Flow
import com.cureius.pocket.feature_pot.domain.model.Pot

class GetTemplatePots(
    private val repository: PotRepository
) {
    operator fun invoke(): Flow<List<Pot>> {
        return repository.getTemplatePots()
    }
}