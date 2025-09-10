package com.goarchery.common.factory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.goarchery.common.core.constant.CodeNamedEnum;

import java.io.IOException;
import java.util.Locale;

public class CodeNamedEnumJsonDeserializer extends JsonDeserializer<Enum<?>>
        implements ContextualDeserializer {

    private final Class<? extends Enum<?>> targetType; // 运行时真正的枚举类型

    public CodeNamedEnumJsonDeserializer() {
        this.targetType = null;
    }

    private CodeNamedEnumJsonDeserializer(Class<? extends Enum<?>> targetType) {
        this.targetType = targetType;
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString();
        if (raw == null) return null;
        String s = raw.trim();
        if (targetType == null) {
            // 理论不会发生；兜底按大写常量名
            return Enum.valueOf((Class) Enum.class, s.toUpperCase(Locale.ROOT));
        }

        // 针对实现了 CodeNamedEnum 的枚举，按 code / name / 常量名 解析
        if (CodeNamedEnum.class.isAssignableFrom(targetType)) {
            Object[] constants = targetType.getEnumConstants();
            // 1) code（数字）
            try {
                int v = Integer.parseInt(s);
                for (Object c : constants) {
                    CodeNamedEnum e = (CodeNamedEnum) c;
                    if (e.getCode() != null && e.getCode() == v) return (Enum<?>) c;
                }
            } catch (NumberFormatException ignored) {}

            // 2) name（大小写不敏感）
            for (Object c : constants) {
                CodeNamedEnum e = (CodeNamedEnum) c;
                if (e.getName() != null && e.getName().equalsIgnoreCase(s)) return (Enum<?>) c;
            }

            // 3) 常量名
            String upper = s.toUpperCase(Locale.ROOT);
            for (Object c : constants) {
                if (((Enum<?>) c).name().equals(upper)) return (Enum<?>) c;
            }

            throw JsonMappingException.from(p, "Invalid enum value: " + s + " for " + targetType.getSimpleName());
        }

        // 其他普通枚举：保持默认行为（大小写改成不敏感）
        return Enum.valueOf((Class) targetType, s.toUpperCase(Locale.ROOT));
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JavaType javaType = (property != null) ? property.getType() : ctxt.getContextualType();
        if (javaType != null && Enum.class.isAssignableFrom(javaType.getRawClass())) {
            return new CodeNamedEnumJsonDeserializer((Class<? extends Enum<?>>) javaType.getRawClass());
        }
        return this;
    }
}
