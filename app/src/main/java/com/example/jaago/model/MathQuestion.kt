package com.example.jaago.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class MathQuestion(
    val operand1: Int,
    val operand2: Int,
    val operator: String,
    val answer: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(operand1)
        parcel.writeInt(operand2)
        parcel.writeString(operator)
        parcel.writeInt(answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MathQuestion> {
        override fun createFromParcel(parcel: Parcel): MathQuestion {
            return MathQuestion(parcel)
        }

        override fun newArray(size: Int): Array<MathQuestion?> {
            return arrayOfNulls(size)
        }

        fun toJsonArray(questions: Array<MathQuestion?>): String {
            val gson = Gson()
            return gson.toJson(questions)
        }

        fun fromJsonArray(jsonString: String?): Array<MathQuestion>? {
            val gson = Gson()
            return gson.fromJson(jsonString, Array<MathQuestion>::class.java)
        }
    }
}

fun generateMathQuestion(difficulty: String): MathQuestion {
    return when (difficulty) {
        "Easy" -> generateEasyQuestion()
        "Medium" -> generateMediumQuestion()
        "Hard" -> generateHardQuestion()
        else -> throw IllegalArgumentException("Invalid difficulty")
    }
}

fun generateEasyQuestion(): MathQuestion {
    val operand1 = (1..10).random()
    val operand2 = (1..10).random()
    val operator = "+"
    val answer = operand1 + operand2
    return MathQuestion(operand1, operand2, operator, answer)
}

fun generateMediumQuestion(): MathQuestion {
    var operand1 = (7..20).random()
    var operand2 = (7..20).random()
    val operator = if ((0..1).random() == 0) "+" else "-"

    // Ensure the larger number is always operand1 for subtraction
    if (operator == "-" && operand2 > operand1) {
        val temp = operand1
        operand1 = operand2
        operand2 = temp
    }

    val answer = if (operator == "+") operand1 + operand2 else operand1 - operand2
    return MathQuestion(operand1, operand2, operator, answer)
}

fun generateHardQuestion(): MathQuestion {
    var operand1 = (10..50).random()
    var operand2 = (10..50).random()
    val operator = when ((0..2).random()) {
        0 -> "+"
        1 -> "-"
        2 -> "*"
        else -> throw IllegalStateException("Unexpected operator")
    }
    if (operator == "*") {
        operand1 = (1..10).random()
        operand2 = (1..10).random()
    }
    val answer = when (operator) {
        "+" -> operand1 + operand2
        "-" -> operand1 - operand2
        "*" -> operand1 * operand2
        else -> throw IllegalStateException("Unexpected operator")
    }

    return MathQuestion(operand1, operand2, operator, answer)
}