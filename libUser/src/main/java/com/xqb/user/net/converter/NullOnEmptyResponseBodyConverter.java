package com.xqb.user.net.converter;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class NullOnEmptyResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Factory factory;
    private Type type;
    private Annotation[] annotations;
    private Retrofit retrofit;

    NullOnEmptyResponseBodyConverter(@NonNull Factory factory, Type type, Annotation[] annotations,
                                     Retrofit retrofit) {
        this.factory = factory;
        this.type = type;
        this.annotations = annotations;
        this.retrofit = retrofit;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        if (value.contentLength() == 0) {
            return null;
        }
        return retrofit.<T>nextResponseBodyConverter(factory, type, annotations)
                .convert(value);
    }
}
