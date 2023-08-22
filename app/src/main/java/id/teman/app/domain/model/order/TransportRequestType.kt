package id.teman.app.domain.model.order

//requesting = customer pesan
//accepted = mitra menyetujui
//rejected = mitra menolak
//onroute = mitra sudah menjemput customer dan mulai menuju ke lokasi
//finished = ketika sudah sampai tujuan
enum class TransportRequestType(val value: String) {
    REQUESTING("requesting"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    ONROUTE("onroute"),
    ARRIVED("arrived"),
    CANCELED("canceled"),
    FINISHED("finished");

    companion object {
        fun from(value: String?) = when (value) {
            REQUESTING.value -> REQUESTING
            ACCEPTED.value -> ACCEPTED
            REJECTED.value -> REJECTED
            ONROUTE.value -> ONROUTE
            ARRIVED.value -> ARRIVED
            else -> FINISHED
        }
    }
}