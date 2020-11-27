package com.cofitconsulting.cofit.utility.sendNotificationPack;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAL3Mel_o:APA91bGxO3KoH-7K2yJ2YtpSR4Uzl1dxAjIeOnXVEOAcyZSmfaaAi5M8trrYOI3UwmIr2unjwwBLLKR-KAUF5o0Uy4x9EklRYGy6PD6lE9HeMBhNTqGOU4cJj0Hwe0eezGsjUcTuFJkP" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
