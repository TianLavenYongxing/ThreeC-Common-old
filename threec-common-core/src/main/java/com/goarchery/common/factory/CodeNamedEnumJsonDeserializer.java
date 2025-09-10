package com.goarchery.common.factory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.goarchery.common.core.constant.CodeNamedEnum;

import java.io.IOException;
import java.util.Locale;

/**
 * 全局通用的枚举反序列化器：
 * - 对实现了 CodeNamedEnum 的枚举：支持 code / name / 常量名；
 * - 对普通枚举：保持按常量名（不区分大小写）解析；
 * - 支持数字或字符串两种 JSON token。
 */
public class CodeNamedEnumJsonDeserializer extends JsonDeserializer<Enum<?>>
        implements ContextualDeserializer {

    private final Class<? extends Enum<?>> targetType;

    public CodeNamedEnumJsonDeserializer() {
        this.targetType = null;
    }

    private CodeNamedEnumJsonDeserializer(Class<? extends Enum<?>> targetType) {
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (targetType == null) {
            // 理论上不会发生：没有上下文类型时无法安全解析
            return null;
        }

        final JsonToken t = p.currentToken();
        // 针对实现了 CodeNamedEnum 的枚举——统一走接口工具方法
        if (CodeNamedEnum.class.isAssignableFrom(targetType)) {
            // 数字：按 code 解析
            if (t == JsonToken.VALUE_NUMBER_INT) {
                int code = p.getIntValue();
                try {
                    return (Enum<?>) CodeNamedEnum.fromCode((Class) targetType, code);
                } catch (IllegalArgumentException ex) {
                    throw JsonMappingException.from(p,
                            "Invalid code: " + code + " for " + targetType.getSimpleName(), ex);
                }
            }
            // 其他 token（字符串等）：按 fromString（code/name/常量名）解析
            String raw = p.getValueAsString();
            if (raw == null) {
                return null; // 或者：抛错；按你的业务决定
            }
            try {
                return (Enum<?>) CodeNamedEnum.fromString((Class) targetType, raw);
            } catch (IllegalArgumentException ex) {
                throw JsonMappingException.from(p,
                        "Invalid value: \"" + raw + "\" for " + targetType.getSimpleName(), ex);
            }
        }
        // 非 CodeNamedEnum：保持常量名解析（大小写不敏感）
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getValueAsString();
            if (s == null) return null;
            String upper = s.trim().toUpperCase(Locale.ROOT);
            try {
                return Enum.valueOf((Class) targetType, upper);
            } catch (IllegalArgumentException ex) {
                throw JsonMappingException.from(p,
                        "Invalid enum constant: \"" + s + "\" for " + targetType.getSimpleName(), ex);
            }
        } else if (t == JsonToken.VALUE_NUMBER_INT) {
            // 对普通枚举不建议用数字（ordinal），这里直接报错更安全
            int n = p.getIntValue();
            throw JsonMappingException.from(p,
                    "Numeric value " + n + " is not supported for " + targetType.getSimpleName()
                            + " (expected enum constant name)");
        }

        // 其他 token 类型不支持
        throw JsonMappingException.from(p,
                "Unsupported JSON token " + t + " for " + targetType.getSimpleName());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JavaType javaType = (property != null) ? property.getType() : ctxt.getContextualType();
        if (javaType != null && Enum.class.isAssignableFrom(javaType.getRawClass())) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> raw = (Class<? extends Enum<?>>) javaType.getRawClass();
            return new CodeNamedEnumJsonDeserializer(raw);
        }
        return this;
    }
}
