package com.ll.medium.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private T data;

    public boolean isSuccess() {
        int statusCode = Integer.parseInt(resultCode);

        return statusCode >= 200 && statusCode < 400;
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
