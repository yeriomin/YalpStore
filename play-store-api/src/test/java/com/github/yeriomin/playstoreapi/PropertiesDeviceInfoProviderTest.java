package com.github.yeriomin.playstoreapi;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class PropertiesDeviceInfoProviderTest {

    @Test
    public void isValidEmpty() throws Exception {
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(new Properties());
        Assert.assertFalse(deviceInfoProvider.isValid());
    }

    @Test
    public void isValidValid() throws Exception {
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(getProperties("device-hammerhead.properties"));
        Assert.assertTrue(deviceInfoProvider.isValid());
    }

    @Test
    public void isValidExtra() throws Exception {
        Properties properties = getProperties("device-hammerhead.properties");
        properties.setProperty("Extra", "aaa");
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        Assert.assertTrue(deviceInfoProvider.isValid());
    }

    @Test
    public void isValidMissing() throws Exception {
        Properties properties = getProperties("device-hammerhead.properties");
        properties.remove("Vending.version");
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        Assert.assertFalse(deviceInfoProvider.isValid());
    }

    @Test
    public void isValidAll() throws Exception {
        for (String fileName: new File("src/main/resources").list()) {
            if (!fileName.startsWith("device-") || !fileName.endsWith(".properties")) {
                continue;
            }
            PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
            deviceInfoProvider.setProperties(getProperties(fileName));
            Assert.assertTrue(deviceInfoProvider.isValid());
        }
    }

    private Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream(fileName));
        } catch (IOException e) {
            System.out.println(fileName + " not found");
        }
        return properties;
    }
}
