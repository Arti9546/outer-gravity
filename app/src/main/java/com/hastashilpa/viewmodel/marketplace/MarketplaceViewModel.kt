package com.hastashilpa.viewmodel.marketplace

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastashilpa.model.Product
import com.hastashilpa.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {
    private val repository = MarketplaceRepository()

    private val _trendingProducts = MutableStateFlow<List<Product>>(emptyList())
    val trendingProducts = _trendingProducts.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess = _uploadSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        fetchTrendingProducts()
    }

    fun fetchTrendingProducts() {
        viewModelScope.launch {
            _trendingProducts.value = repository.getTrendingProducts()
        }
    }

    fun uploadProduct(name: String, price: String, category: String, description: String, imageUri: Uri?) {
        val priceDouble = price.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            _isUploading.value = true
            _errorMessage.value = null
            try {
                repository.uploadProduct(name, priceDouble, category, description, imageUri)
                _uploadSuccess.value = true
                fetchTrendingProducts()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Upload failed"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun resetUploadStatus() {
        _uploadSuccess.value = false
        _errorMessage.value = null
    }
}
