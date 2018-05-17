package com.wireless.ambeent.mozillaprototype.businesslogic;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IRest {


    @FormUrlEncoded
    @POST()
    Call<ResponseBody> messageObject(@Field("technicianId") String technicianId);



}
