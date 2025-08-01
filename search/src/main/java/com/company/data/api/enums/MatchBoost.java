package com.company.data.api.enums;

public enum MatchBoost {
    PHONE(5.0f),
    DOMAIN(4.0f),
    FACEBOOK(3.0f),
    NAME(2.0f);

    private final float value;
    MatchBoost(float value) { this.value = value; }
    public float value() { return value; }
}