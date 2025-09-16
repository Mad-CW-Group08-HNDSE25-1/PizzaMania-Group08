package com.androidapp.pizzamania.callBack;

public interface OnResultListener<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
