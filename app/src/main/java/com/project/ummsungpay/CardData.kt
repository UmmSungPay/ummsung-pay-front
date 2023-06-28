package com.project.ummsungpay

data class CardData (
    val card_name: String,
    val card_number: String,
    val card_validity: String
){
    fun getName(): String? {
        return card_name
    }
    fun getNumber(): String? {
        return card_number
    }
    fun getValidity(): String? {
        return card_validity
    }
}

var bookmark: Int = -1

var cardList: ArrayList<CardData> = arrayListOf(
    CardData("신한S20", "1234 5678 1234 5678", "05/28"),
    CardData("카카오카드", "0000 0000 0000 0000", "12/23"),
    CardData("KB국민", "9999 9999 9999 9999", "09/25")
)