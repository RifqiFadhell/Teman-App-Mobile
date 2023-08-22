package id.teman.app.domain.model.wallet.pin

data class ValueCaptionPinWalletScreen(
    val titleBar: String,
    val title: String,
    val subtitle: String,
    val warning: String,
    val buttonTitle: String,
    val useFor: String,
)

fun getValuePinWalletScreen(usingFor: String): ValueCaptionPinWalletScreen {
    var titleBar = ""
    var title = ""
    var subtitle = ""
    var warning = ""
    var buttonTitle = ""
    when(usingFor) {
        "update" -> {
            titleBar = "Teman Kantong"
            title = "Atur PIN Keamanan"
            subtitle = "Nomor PIN diperlukan saat Kamu menggunakan metode pembayaran Teman Kantong."
            warning = "Demi kemanan, hindari angka beruntun atau berulang. Jangan gunakan tanggal lahir kamu ataupun PIN ATM sebagai PIN Teman Kantong."
            buttonTitle = "Set Pin"
        }
        "bill", "pulsa" -> {
            titleBar = "Konfirmasi Pin"
            title = "Autentikasi Pembayaran"
            subtitle = "Masukkan PIN 6 digit nomor untuk memverifikasi bahwa ini benar Kamu."
            warning = "Lupa Pin Anda ?"
            buttonTitle = "Konfirmasi"
        }
    }

    return ValueCaptionPinWalletScreen(
        titleBar, title, subtitle, warning, buttonTitle, usingFor
    )
}
