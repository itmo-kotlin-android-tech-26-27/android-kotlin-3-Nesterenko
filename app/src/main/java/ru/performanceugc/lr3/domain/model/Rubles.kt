package ru.performanceugc.lr3.domain.model

@JvmInline
value class Rubles(val amount: Int) : Comparable<Rubles> {
    init {
        require(amount > 0) { "The amount in rubles must be positive" }
    }

    override fun compareTo(other: Rubles): Int = amount.compareTo(other.amount)
}
