package com.example.velocityshower;

public enum SpeedSignEnum {
    SPEED_NONE(R.drawable.nospeed),
    SPEED_50(R.drawable.speed50),
    SPEED_60(R.drawable.speed60),
    SPEED_70(R.drawable.speed70),
    SPEED_80(R.drawable.speed80);

    private int image;
    SpeedSignEnum(int image){
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
