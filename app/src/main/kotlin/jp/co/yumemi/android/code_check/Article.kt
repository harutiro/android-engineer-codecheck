package jp.co.yumemi.android.code_check

class Article private constructor(
    val id: String,
    var title: String,
    val description: String
){

    fun changeTitle(title: String) {
        this.title = title
    }
}