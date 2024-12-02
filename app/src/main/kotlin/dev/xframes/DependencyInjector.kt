package dev.xframes

import kotlin.reflect.KMutableProperty

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject

class DependencyInjector(private val componentService: ComponentService) {
    fun injectDependencies(component: Any) {
        component::class.members.filterIsInstance<KMutableProperty<*>>().forEach { property ->
            if (property.annotations.any { it is Inject }) {
                if (property.returnType.classifier == ComponentService::class) {
                    property.setter.call(component, componentService)
                } else {
                    throw IllegalArgumentException("Unsupported type for @Inject property: ${property.returnType}")
                }
            }
        }
    }
}