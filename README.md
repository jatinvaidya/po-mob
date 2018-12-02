# Purchase Order Management - Android App
<hr>

**This is NOT yet a fully working PO management app**  

This is a simple android app for the purpose of testing refresh_token with mobile-apps.  

This is based on **Auth0 Android quick start** app. It uses the `OAuth2 Authorization Code Flow with PKCE` 
to request for `refresh_token` along with `access_token` and `id_token`.  

It then simply invokes the po-api with `access_token` and logs the po-api response body to the `Android Studio Console`.  

Since this is only for test purposes, it also logs the `access_token`, `id_token` and `refresh_token` as well to the 
`Android Studio Console`. This is so that we can decode and make sure the claims in the tokens are as expected.
