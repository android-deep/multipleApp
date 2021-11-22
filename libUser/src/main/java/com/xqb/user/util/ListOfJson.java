package com.xqb.user.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public class ListOfJson<T> implements ParameterizedType {

    private Class<?> mType;

    public ListOfJson(Class<T> pType) {
        this.mType = pType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {mType};
    }

    /**
     * Returns the parent / owner type, if this type is an inner type, otherwise
     * {@code null} is returned if this is a top-level type.
     *
     * @return the owner type or {@code null} if this is a top-level type
     * @throws TypeNotPresentException             if one of the type arguments cannot be found
     */
    @Override
    public Type getOwnerType() {
        return null;
    }

    /**
     * Returns the declaring type of this parameterized type.
     * <p/>
     * The raw type of {@code Set<String> field;} is {@code Set}.
     *
     * @return the raw type of this parameterized type
     */
    @Override
    public Type getRawType() {
        return List.class;
    }
}