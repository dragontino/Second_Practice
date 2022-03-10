import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.system.measureTimeMillis


class FindPasswordThread(
    private val threadName: String,
    private val hash: String,
    private val startIndex: Int,
    private val count: Int
): Thread() {

    override fun run() {
        val time = measureTimeMillis {
            for (position in startIndex until startIndex + count) {

                if (isFound) break

                val password = StringBuilder("01234")

                for (i in 0..4) {
                    val degree = length - 1 - i
                    password[i] = getLetter(position / (countLetters pow degree) % countLetters)
                }

                if (password.toString().hash() == hash) {
                    isFound = true
                    println("Исходная строка: $password")
                    println("Поток, который нашёл ответ: $threadName")
                    break
                }
            }
        }

        println("$threadName выполнился за $time мс ≈ ${time / 1000} с")
        interrupt()
    }
}


private const val countLetters = 26
private const val length = 5
private val countVariants = countLetters pow length

private var isFound = false


fun main() {
    val input = Scanner(System.`in`)

    println("Введите хэш")
    val hash = input.next()

    println("Введите количество потоков")
    val countThreads = input.nextInt()

    val counts = countVariants fairDiv countThreads

    for (i in 0 until countThreads) {
        val findPasswordThread = FindPasswordThread("Поток №${i + 1}", hash, counts.sumAt(i), counts[i])

        findPasswordThread.start()
    }

    println("Ищем строку...")
}



fun String.hash() = try {
    val md = MessageDigest.getInstance("SHA-256")
    val hash = md.digest(this.toByteArray())

    val sb = StringBuilder()
    hash.forEach {
        sb.append(String.format("%02x", it))
    }

    sb.toString()
} catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
}



// 0 <= positionInAlphabet <= 25
fun getLetter(positionInAlphabet: Int) =
    if (positionInAlphabet in 0..25)
            (positionInAlphabet + 97).toChar()
    else 'a'



infix fun Int.pow(n: Int) =
    toDouble().pow(n).toInt()


infix fun Int.fairDiv(divider: Int): IntArray {
    val answer = IntArray(divider)
    val mod = this % divider

    for (i in answer.indices) {
        answer[i] = this / divider

        if (i < mod)
            answer[i]++
    }

    return answer
}

//сумма элементов массива от начала до index (невключительно)
fun IntArray.sumAt(index: Int): Int {
    var sum = 0

    for (i in 0 until index)
        sum += this[i]

    return sum
}
