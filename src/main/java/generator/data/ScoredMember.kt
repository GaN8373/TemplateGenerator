package generator.data

class ScoredMember<T> : Comparable<ScoredMember<Any>> {

    var score: Long? = null
    var member: T? = null

    constructor(score: Long, member: T) {
        this.score = score
        this.member = member
    }

    constructor(member: T) : this(System.currentTimeMillis(), member)
    @Deprecated(message = "Framework internal use only", level = DeprecationLevel.ERROR)
    constructor()

    override fun compareTo(other: ScoredMember<Any>): Int {
        return score?.compareTo(other.score ?: 0) ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this.member === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoredMember<*>

        return this.member.toString() == other.member.toString()
    }

    override fun toString(): String {
        return member.toString()
    }

    override fun hashCode(): Int {
        return member.hashCode()
    }
}