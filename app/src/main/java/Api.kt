import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @POST("/POST/api/command")
    @FormUrlEncoded
    fun postControl(@Body command:Command): Call<Command>
}