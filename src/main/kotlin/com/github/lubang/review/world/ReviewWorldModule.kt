package com.github.lubang.review.world

import com.github.lubang.review.world.domain.entities.streamline.StreamlineRepository
import com.github.lubang.review.world.port.adapters.AkkaReviewWorldApplication
import com.github.lubang.review.world.port.adapters.persistence.AkkaStreamlineRepository
import com.google.inject.AbstractModule

class ReviewWorldModule : AbstractModule() {
    override fun configure() {
        super.configure()

        bind(ReviewWorldApplication::class.java).to(AkkaReviewWorldApplication::class.java)
        bind(StreamlineRepository::class.java).to(AkkaStreamlineRepository::class.java)
    }
}