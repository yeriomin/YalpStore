# play-store-api [![Build Status](https://travis-ci.org/yeriomin/play-store-api.svg?branch=master)](https://travis-ci.org/yeriomin/play-store-api) [![Release](https://jitpack.io/v/yeriomin/play-store-api.svg)](https://jitpack.io/#yeriomin/play-store-api) [![downloads](https://jitpack.io/v/yeriomin/play-store-api/month.svg)](https://jitpack.io/#yeriomin/play-store-api)

Google Play Store protobuf API wrapper in java

## Include

Get it from [jitpack](https://jitpack.io/#yeriomin/play-store-api). Or...

## Build separately

    git clone https://github.com/yeriomin/play-store-api
    gradlew :assemble
    gradlew :build
    
Protobuf classes generation happens on `assemble` step, tests a ran on `build` step.

## Usage

### First login

```java
        // A device definition is required to log in
        // See resources for a list of available devices
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-honami.properties"));
        } catch (IOException e) {
            System.out.println("device-honami.properties not found");
            return null;
        }
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());
        
        // Provide valid google account info
        PlayStoreApiBuilder builder = new PlayStoreApiBuilder()
            // Extend HttpClientAdapter using a http library of your choice
            .setHttpClient(new HttpClientAdapterImplementation())
            .setDeviceInfoProvider(deviceInfoProvider)
            .setEmail(email)
            .setPassword(password)
        ;
        GooglePlayAPI api = builder.build();
        
        // We are logged in now
        // Save and reuse the generated auth token and gsf id,
        // unless you want to get banned for frequent relogins
        // The token has a very long validity time. Months.
        api.getToken();
        api.getGsfId();
        
        // API wrapper instance is ready
        DetailsResponse response = api.details("com.cpuid.cpu_z");
```
        
### Further logins

```java
        // A device definition is required for routine requests too
        // See resources for a list of available devices
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-honami.properties"));
        } catch (IOException e) {
            System.out.println("device-honami.properties not found");
            return null;
        }
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());
        
        // Provide auth token and gsf id you previously saved
        PlayStoreApiBuilder builder = new PlayStoreApiBuilder()
            // Extend HttpClientAdapter using a http library of your choice
            .setHttpClient(new HttpClientAdapterImplementation())
            .setDeviceInfoProvider(deviceInfoProvider)
            .setToken(token)
            .setGsfId(gsfId)
        ;
        GooglePlayAPI api = builder.build();
        
        // API wrapper instance is ready
        DetailsResponse response = api.details("com.cpuid.cpu_z");
```
        
### Examples

See [tests](https://github.com/yeriomin/play-store-api/blob/master/src/test/java/com/github/yeriomin/playstoreapi/GooglePlayAPITest.java) and [the project which this library was made for](https://github.com/yeriomin/YalpStore) for examples.

### Further studies

Looking through [GooglePlay.proto](https://github.com/yeriomin/play-store-api/blob/master/src/main/proto/GooglePlay.proto) will let you know what responses to expect.
