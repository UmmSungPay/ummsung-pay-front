package com.project.ummsungpay

data class CardData (
    var card_name: String,
    var card_number: String,
    var card_validity: String
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

var fixedBookmark = -1

var fixedCardList: ArrayList<CardData> = arrayListOf(
    CardData("신한S20", "1234 5678 1234 5678", "05/28"),
    CardData("카카오카드", "0000 0000 0000 0000", "12/23"),
    CardData("KB국민", "9999 9999 9999 9999", "09/25")
)
