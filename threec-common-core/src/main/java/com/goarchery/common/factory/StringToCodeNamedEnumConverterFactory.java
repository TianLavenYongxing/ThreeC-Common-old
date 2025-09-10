package com.goarchery.common.factory;

import com.goarchery.common.core.constant.CodeNamedEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Locale;

public class StringToCodeNamedEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        if (CodeNamedEnum.class.isAssignableFrom(targetType)) {
            return (Converter<String, T>) new CodeNamedBridgeConverter<>(targetType);
        }
        return (Converter<String, T>) new NameOnlyConverter<>(targetType);
    }

    private static final class CodeNamedBridgeConverter<E extends Enum<E> & CodeNamedEnum>
            implements Converter<String, E> {
        private final Class<E> enumType;
        @SuppressWarnings("unchecked") CodeNamedBridgeConverter(Class<?> raw){ this.enumType=(Class<E>)raw; }
        @Override public E convert(String source){ return CodeNamedEnum.fromString(enumType, source); }
    }

    private static final class NameOnlyConverter<E extends Enum<E>>
            implements Converter<String, E> {
        private final Class<E> enumType;
        @SuppressWarnings("unchecked")
        NameOnlyConverter(Class<?> raw) { this.enumType = (Class<E>) raw; }
        @Override public E convert(String source) {
            if (source == null) return null;
            return Enum.valueOf(enumType, source.trim().toUpperCase(Locale.ROOT));
        }
    }
}
