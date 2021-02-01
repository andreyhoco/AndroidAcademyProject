package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigurationResponse(
	@SerialName("images")
	val imageConfigurations: ImageConfigurations,
)

@Serializable
data class ImageConfigurations(
	@SerialName("poster_sizes")
	val posterSizes: List<String>,
	@SerialName("secure_base_url")
	val secureBaseUrl: String,
	@SerialName("backdrop_sizes")
	val backdropSizes: List<String>,
	@SerialName("base_url")
	val baseImageLoadUrl: String,
	@SerialName("profile_sizes")
	val profileSizes: List<String>
)

