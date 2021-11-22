package com.ft.mapp.home.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class FakeAppInfo implements Serializable {

    static final long serialVersionUID = 50L;

    @Id
    private Long id;

    @Index(unique = true)
    private int appId;

    private String fakeIcon;

    private String fakeName;

    private String mockLocation;

    private String mockDevice;

@Generated(hash = 1005016837)
public FakeAppInfo(Long id, int appId, String fakeIcon, String fakeName, String mockLocation,
        String mockDevice) {
    this.id = id;
    this.appId = appId;
    this.fakeIcon = fakeIcon;
    this.fakeName = fakeName;
    this.mockLocation = mockLocation;
    this.mockDevice = mockDevice;
}

    @Generated(hash = 1956314818)
    public FakeAppInfo() {
    }

    public String getMockLocation() {
        return mockLocation;
    }

    public void setMockLocation(String mockLocation) {
        this.mockLocation = mockLocation;
    }

    public String getMockDevice() {
        return mockDevice;
    }

    public void setMockDevice(String mockDevice) {
        this.mockDevice = mockDevice;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAppId() {
        return this.appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getFakeIcon() {
        return this.fakeIcon;
    }

    public void setFakeIcon(String fakeIcon) {
        this.fakeIcon = fakeIcon;
    }

    public String getFakeName() {
        return this.fakeName;
    }

    public void setFakeName(String fakeName) {
        this.fakeName = fakeName;
    }

}
