package com.hastashilpa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastashilpa.model.Blueprint
import com.hastashilpa.repository.BlueprintRepository
import com.hastashilpa.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlueprintViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BlueprintRepository(application)
    private val geminiService = GeminiService()

    private val _blueprints = MutableStateFlow<List<Blueprint>>(emptyList())
    val blueprints = _blueprints.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _filteredBlueprints = MutableStateFlow<List<Blueprint>>(emptyList())
    val filteredBlueprints = _filteredBlueprints.asStateFlow()

    private val _aiSuggestions = MutableStateFlow("Fetching design ideas...")
    val aiSuggestions = _aiSuggestions.asStateFlow()

    init {
        fetchBlueprints()
        fetchAiTrends()
    }

    private fun fetchAiTrends() {
        viewModelScope.launch {
            _aiSuggestions.value = geminiService.getTrendingDesignSuggestions()
        }
    }

    fun onSearchQueryChange(query: String) {
        if (query.isBlank()) {
            _filteredBlueprints.value = _blueprints.value
        } else {
            _filteredBlueprints.value = _blueprints.value.filter {
                it.name.contains(query, ignoreCase = true) || 
                it.category.contains(query, ignoreCase = true)
            }
        }
    }

    private fun fetchBlueprints() {
        viewModelScope.launch {
            _isLoading.value = true
            val remoteBlueprints = repository.getBlueprints()
            if (remoteBlueprints.isNotEmpty()) {
                _blueprints.value = remoteBlueprints
            } else {
                loadMockBlueprints()
            }
            _filteredBlueprints.value = _blueprints.value
            _isLoading.value = false
        }
    }

    private fun loadMockBlueprints() {
        _blueprints.value = listOf(
            Blueprint(
                id = "1",
                name = "Bamboo Laptop Stand",
                category = "Office",
                dimensions = "30cm x 15cm x 10cm",
                materials = listOf("2x 30cm Bamboo poles", "4x 10cm Bamboo poles", "Eco-glue"),
                instructions = listOf("Cut the poles to size", "Sand the edges smooth", "Assemble the base frame", "Attach the top surface"),
                pdfUrl = "https://www.adobe.com/support/products/enterprise/knowledgecenter/pdfs/stats.pdf"
            ),
            Blueprint(
                id = "2",
                name = "Cane Lamp Shade",
                category = "Home Decor",
                dimensions = "20cm diameter",
                materials = listOf("Cane strips (various sizes)", "Metal wire frame", "Bulb holder set"),
                instructions = listOf("Soak cane strips in water", "Weave onto the metal frame", "Tighten the ends carefully", "Dry and varnish for protection"),
                pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
            )
        )
    }

    fun downloadBlueprint(blueprint: Blueprint) {
        repository.downloadBlueprintPdf(blueprint.name, blueprint.pdfUrl)
    }
}
