package com.app.fypfinal.Info;

public interface Info {
    String TAG = "mytag";
    String NUMBER_REGEX = "^((\\+92)?(0092)?(92)?(0)?)(3)([0-9]{9})$";
    String USER_NODE = "Users";

    String CUSTOMER = "Customer";
    String SALOON_MANAGER = "SaloonManager";

    String PUBNUB_PUBLISH_KEY = "pub-c-3bbb56e3-476d-4f4e-89a4-cc08209ccbd8";
    String PUBNUB_SUBSCRIBE_KEY = "sub-c-9703e626-8120-11ec-8ecc-dee9ee9643e0";
    String PUBNUB_CHANNEL_NAME = "postman_location";

    String PREF_ACCESS_TOKEN = "AccessToken";
    String PREF_DEVICE_ID = "DeviceId";

    String GOOGLE_MAPS_KEY = "AIzaSyCJ5rx8p6sIKQR5nq7NTzj0-1TZbYS1mRc";

    int TYPE_POSTMAN_PARCEL = 1;
    int TYPE_USER_PARCEL = 2;
    int TYPE_USER_RECEIVED_PARCEL = 3;


}
