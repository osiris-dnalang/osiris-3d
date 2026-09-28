package com.example.osiris.core

class ReplayAdapter {
    private val fixtures = mutableMapOf<String, Map<String, String>>()

    init {
        registerFixture("echo-v1", mapOf("status" to "SUCCESS", "message" to "hello"))
        registerFixture(
            "benchmark-sample-v1",
            mapOf("status" to "SUCCESS", "metric" to "0.9763", "samples" to "1024")
        )
        registerFixture(
            "fixture-energy-test-v1",
            mapOf(
                "status" to "COMPUTATION_SUCCESS",
                "output_tokens" to "42",
                "energy_level" to "420",
                "converged" to "true"
            )
        )
    }

    fun registerFixture(fixtureId: String, payload: Map<String, String>) {
        fixtures[fixtureId] = payload
    }

    fun hasFixture(fixtureId: String): Boolean = fixtures.containsKey(fixtureId)

    fun getRegisteredFixtures(): Map<String, Map<String, String>> = fixtures.toMap()

    fun execute(fixtureId: String): Map<String, String> {
        val payload = fixtures[fixtureId]
            ?: throw ActionNotPermitted("Fixture '$fixtureId' is not registered in ReplayAdapter")
        return payload
    }
}
