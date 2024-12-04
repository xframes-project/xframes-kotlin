package dev.xframes

import org.koin.java.KoinJavaComponent.inject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ComponentAnn



open class Component(
    val id: Int,
    val type: String,
    initialProps: Map<String, Any?> = emptyMap()
) {
    // Reactive properties for props and state
    private val propsSubject: BehaviorSubject<Map<String, Any?>> = BehaviorSubject.createDefault(initialProps)
    private val stateSubject: BehaviorSubject<Map<String, Any?>> = BehaviorSubject.createDefault(emptyMap())

    private val disposables = CompositeDisposable()

    init {
        // Subscribe to props or state changes and trigger lifecycle methods or re-render
        disposables.add(
            propsSubject
                .distinctUntilChanged()
                .subscribe { onPropsChanged(it) }
        )

        disposables.add(
            stateSubject
                .distinctUntilChanged()
                .subscribe { onStateChanged(it) }
        )
    }

    // Setters for props and state
    fun setProps(newProps: Map<String, Any?>) {
        propsSubject.onNext(newProps)
    }

    fun setState(newState: Map<String, Any?>) {
        stateSubject.onNext(newState)
    }

    // Lifecycle hooks
    open fun componentDidMount() {}
    open fun componentDidUpdate() {}

    // Triggered when props change
    private fun onPropsChanged(newProps: Map<String, Any?>) {
        componentDidUpdate() // Lifecycle hook
        reRender()
    }

    // Triggered when state changes
    private fun onStateChanged(newState: Map<String, Any?>) {
        componentDidUpdate() // Lifecycle hook
        reRender()
    }

    // Render method
    open fun render(): ComponentNode {
        val currentProps = propsSubject.value ?: emptyMap()
        return ComponentNode(id, type, currentProps)
    }

    // Perform the re-render logic
    private fun reRender() {
        render()
        println("Component with id=$id re-rendered.")
    }

    // Clean up subscriptions
    fun dispose() {
        disposables.dispose()
    }
}


class ButtonComponent(id: Int, type: String, props: Map<String, Any?> = emptyMap()) : Component(id, type, props) {
    var label: String = props["label"] as? String ?: ""



    // Override render to handle button specific rendering
    override fun render(): ComponentNode {
        return ComponentNode(
            id = id,
            type = type,
            props = mapOf("label" to label)
        )
    }
}

class CounterComponent(private val counterService: CounterService) {
    suspend fun render() {
        // Listen for state changes
        counterService.state.collect { state ->
            println("Counter: ${state.count}")
        }
    }

    fun increment() {
        counterService.increment() // Update the state
    }
}
