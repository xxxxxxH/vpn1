package win.enlarge.zoovpn

import okhttp3.ResponseBody
import retrofit2.http.*
import win.enlarge.zoovpn.pojo.ResultPojo

interface AppService {

    @POST("config")
    suspend fun getConfig(): ResponseBody?

    @POST
    suspend fun uploadFbData(
        @Url url: String,
        @Body body: Map<String,String>,
    ): ResultPojo?
}