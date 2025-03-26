package generator.data

class ScoredMember : Comparable<ScoredMember> {

    var score: Long? = null
    var member: String? = null

    constructor(score: Long, member: String) {
        this.score = score
        this.member = member
    }

    constructor(member: String) : this(System.currentTimeMillis(), member)
    @Deprecated(message = "Framework internal use only", level = DeprecationLevel.ERROR)
    constructor()

    override fun compareTo(other: ScoredMember): Int {
        return score?.compareTo(other.score ?: 0) ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this.member === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoredMember

        return this.member.toString() == other.member.toString()
    }

    override fun toString(): String {
        return member.toString()
    }

    override fun hashCode(): Int {
        return member.hashCode()
    }
}