package com.example.tests.repository

import com.example.tests.model.SearchResponse

interface RepositoryContract {

    suspend fun searchGithubAsync(query: String): SearchResponse
}