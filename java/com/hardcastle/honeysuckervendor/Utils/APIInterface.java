package com.hardcastle.honeysuckervendor.Utils;

import com.hardcastle.honeysuckervendor.Model.AssignedUserRequest;
import com.hardcastle.honeysuckervendor.Model.AvailableDriverDetails;
import com.hardcastle.honeysuckervendor.Model.DriverProfile;
import com.hardcastle.honeysuckervendor.Model.HistoryModel;
import com.hardcastle.honeysuckervendor.Model.UserRequest;
import com.hardcastle.honeysuckervendor.Model.VendorDetails;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by abhijeet on 8/23/2017.
 */

public interface APIInterface {

    @POST("login.php")
    Call<String> checkUserExistence(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("login.php")
    Call<VendorDetails> getUserDetails(@Body String body);

    @POST("pending_req_list.php")
    Call<UserRequest> fetchRequestOnDashboard(@Body String body);

    @POST("vendor_future_trip.php")
    Call<UserRequest> fetchRequestForFutureTrips(@Body String body);

    @POST("vendor_todays_trip.php")
    Call<UserRequest> fetchRequestForTodaysTrips(@Body String body);

    @POST("forget.php")
    Call<String> setNewPasswordToAccount(@Body String body);

    @POST("fetch_driver_list.php")
    Call<DriverProfile> fetchDriverProfile(@Body String body);

    @POST("update_delete_drivers.php")
    Call<String> deleteAndUpdateDriver(@Body String body);

    @POST("register.php")
    Call<String> addDriver(@Body String body);

    @POST("update_info.php")
    Call<VendorDetails> updateVendorInformation(@Body String body);

    @POST("driver_list_for_assign_service.php")
    Call<AvailableDriverDetails> getAvailableDriverDetailsToAssignUserRequest(@Body String body);

    @POST("user_service_status.php")
    Call<AvailableDriverDetails> acceptOrReject(@Body String body);

    @POST("assign_service_to_driver.php")
    Call<AssignedUserRequest> assignDriverToSpecificTrip(@Body String body);

    @POST("vendor_history.php")
    Call<HistoryModel> getHistory(@Body String body);

    @POST("driver_not_responded.php")
    Call<String> driverNotResponding(@Body String body);

    @POST("cancel_request_from_vendor.php")
    Call<String> cancelService(@Body String body);

}
