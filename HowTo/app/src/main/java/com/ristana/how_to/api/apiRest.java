package com.ristana.how_to.api;

import com.ristana.how_to.config.Config;
import com.ristana.how_to.entity.ApiResponse;
import com.ristana.how_to.entity.Category;
import com.ristana.how_to.entity.Guide;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hsn on 26/03/2017.
 */

public interface apiRest {
    @GET("device/{tkn}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> addDevice(@Path("tkn")  String tkn);

    @FormUrlEncoded
    @POST("user/register/"+ Config.TOKEN_APP+"/")
    Call<ApiResponse> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("type") String type);

    @GET("user/login/{email}/{password}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> login(@Path("email") String email, @Path("password") String password);

    @GET("user/password/{id}/{old}/{new_}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> changePassword(@Path("id") String id,@Path("old") String old,@Path("new_") String new_);

    @GET("user/reset/{id}/{key}/{new_}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> reset(@Path("id") String id,@Path("key") String key,@Path("new_") String new_);

    @GET("user/check/{id}/{key}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> check(@Path("id") String id,@Path("key") String key);

    @GET("user/name/{id}/{name}/{key}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> editName(@Path("id") String id,@Path("name") String name,@Path("key") String key);

    @GET("user/email/{email}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> sendEmail(@Path("email") String email);

    @GET("user/request/{key}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> request(@Path("key") String key);

    @Multipart
    @POST("user/upload/{id}/{key}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> uploadImage(@Part MultipartBody.Part file,@Path("id") String id,@Path("key") String key);

    @GET("support/add/{email}/{name}/{message}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> addSupport(@Path("email") String email, @Path("name") String name , @Path("message") String message);

    @GET("categories/list/"+Config.TOKEN_APP+"/")
    Call<List<Category>> categorriesList();

    @GET("guides/all/"+Config.TOKEN_APP+"/")
    Call<List<Guide>> guidesAll();

    @GET("guides/next/{id}/"+Config.TOKEN_APP+"/")
    Call<List<Guide>> guidesNext(@Path("id")  Integer id);

    @GET("guides/by/{category}/"+Config.TOKEN_APP+"/")
    Call<List<Guide>> guidesByCategory(@Path("category")  String category);

    @GET("guides/next/{category}/{id}/"+Config.TOKEN_APP+"/")
    Call<List<Guide>> guidesByCategoryNext(@Path("category")  String category,@Path("id") Integer id);

    @GET("guides/get/{id}/"+Config.TOKEN_APP+"/")
    Call<Guide> getGuide(@Path("id") String id);


    @GET("guides/search/{query}/"+Config.TOKEN_APP+"/")
    Call<List<Guide>> guidessByQuery(@Path("query")  String query);

    @GET("comments/add/{user}/{article}/{comment}/"+Config.TOKEN_APP+"/")
    Call<ApiResponse> addComment(@Path("user")  String user,@Path("article") String article,@Path("comment") String comment);

}


