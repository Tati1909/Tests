package com.example.tests.repository

import com.example.tests.model.SearchResponse

class GitHubRepository(private val gitHubService: GitHubService) : RepositoryContract {

    override suspend fun searchGithubAsync(query: String): SearchResponse {
        return gitHubService.searchGithubAsync(query).await()
    }
}