package idv.tia201.g1.utils.basic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import idv.tia201.g1.entity.ChatParticipant;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// 嘗試復刻JSONUtil的toJsonStr跟toBean功能
// 感覺不夠嚴謹 但for CacheClient使用 目前是OK的
public class JSONUtil {
    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            }
        });
        customModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            }
        });

        customModule.addSerializer(Timestamp.class, ToStringSerializer.instance);
        customModule.addDeserializer(Timestamp.class, new JsonDeserializer<Timestamp>() {
            @Override
            public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return Timestamp.valueOf(p.getValueAsString());
            }
        });
        objectMapper.registerModule(customModule);
    }

    /**
     * 將傳入的物件轉換成Json字串
     *
     * @param obj 要被轉換的物件
     * @return 轉換後的Json字串
     */
    public static String toJsonStr(Object obj) {
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 將Json字串轉為物件
     *
     * @param json Json格式的字串
     * @param type 指定要轉換的型態
     * @param <R>  回傳的型態 根據type決定
     * @return 轉換後的物件
     */
    public static <R> R toBean(String json, Class<R> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 將物件轉為另一個型態的物件 (實際上應該是LinkedHashMap物件)
     *
     * @param data LinkedHashMap物件
     * @param type 指定要轉換的型態
     * @param <R>  回傳的型態 根據type決定
     * @return 轉換後的物件
     */
    public static <R> R toBean(Object data, Class<R> type) {
        return objectMapper.convertValue(data, type);
    }

    public static <R> List<R> toList(String json, Class<R> type) {
        try {
            List<?> list = objectMapper.readValue(json, List.class);
            List<R> res = new ArrayList<>(list.size());
            for (Object o : list) {
                res.add(toBean(o, type));
            }
            return res;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}